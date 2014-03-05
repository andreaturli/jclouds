/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.softlayer.compute.extensions;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.domain.ProvisioningVersion1Transaction;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

/**
 * Nova implementation of {@link org.jclouds.compute.extensions.ImageExtension}
 *
 * @author Andrea Turli
 */
@Singleton
public class SoftLayerImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SoftLayerApi softLayerApi;
   private final ListeningExecutorService userExecutor;
   private final Supplier<Set<? extends Location>> locations;
   private final Predicate<AtomicReference<Image>> imageAvailablePredicate;

   @Inject
   public SoftLayerImageExtension(SoftLayerApi softLayerApi, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                  @Memoized Supplier<Set<? extends Location>> locations,
                                  @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<AtomicReference<Image>> imageAvailablePredicate) {
      this.softLayerApi = checkNotNull(softLayerApi, "softLayerApi");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.locations = checkNotNull(locations, "locations");
      this.imageAvailablePredicate = checkNotNull(imageAvailablePredicate, "imageAvailablePredicate");
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      VirtualGuest guest = softLayerApi.getVirtualGuestApi().getObject(Long.parseLong(id));
      if (guest == null)
         throw new NoSuchElementException("Cannot find virtual guest with id: " + id);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, " softlayer only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      VirtualGuest virtualGuest = softLayerApi.getVirtualGuestApi().getObject(Long.parseLong(cloneTemplate
              .getSourceNodeId()));
      Set<VirtualGuestBlockDevice> blockDevices = virtualGuest.getVirtualGuestBlockDevices();
      ProvisioningVersion1Transaction provisioningVersion1Transaction = softLayerApi.getVirtualGuestApi()
              .createArchiveTransaction(cloneTemplate.getSourceNodeId(), blockDevices);

      logger.info(">> Registered new Image %s, waiting for it to become available.", provisioningVersion1Transaction);
      final AtomicReference<Image> image = null;
      String locationId = softLayerApi.getVirtualGuestApi().getObject(Long.parseLong(cloneTemplate.getSourceNodeId())
      ).getDatacenter().getName();
      Atomics.newReference(new ImageBuilder().location(find(locations.get(), idEquals(locationId)))
              //.id(provisioningVersion1Transaction.getId())
              //.providerId(targetImageZoneAndId.getId())
              .description(cloneTemplate.getName())
              .operatingSystem(OperatingSystem.builder().description(cloneTemplate.getName()).build())
              .status(Image.Status.PENDING).build());
      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            if (imageAvailablePredicate.apply(image))
               return image.get();
            // TODO: get rid of the expectation that the image will be available, as it is very brittle
            throw new UncheckedTimeoutException("Image was not created within the time limit: " + image.get());
         }
      });
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         softLayerApi.getVirtualGuestBlockDeviceTemplateGroupApi().deleteObject(id);
         softLayerApi.getVirtualGuestBlockDeviceTemplateGroupApi().getObject(id);
         } catch (Exception e) {
         if(e.getMessage().contains("SoftLayer_Exception_ObjectNotFound")) {
            return true;
         } else {
            return false;
         }
      }
      return false;
   }

}
