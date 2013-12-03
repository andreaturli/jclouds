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
package org.jclouds.softlayer.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.util.Predicates2.retry;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.compute.functions.OperatingSystemOptionToImage;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.BlockDeviceOption;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.DatacenterOption;
import org.jclouds.softlayer.domain.MemoryOption;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.OperatingSystemOption;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.ProcessorOption;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.jclouds.softlayer.domain.VirtualGuestConfiguration;

/**
 * defines the connection between the {@link SoftLayerApi} implementation and
 * the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualGuest, Hardware, OperatingSystemOption, Datacenter> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SoftLayerApi api;
   private final Supplier<VirtualGuestConfiguration> createObjectOptionsSupplier;
   private final Predicate<VirtualGuest> loginDetailsTester;
   private final long guestLoginDelay;

   @Inject
   public SoftLayerComputeServiceAdapter(SoftLayerApi api,
         VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent,
         @Memoized Supplier<VirtualGuestConfiguration> createObjectOptionsSupplier,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY) long guestLoginDelay) {
      this.api = checkNotNull(api, "api");
      this.guestLoginDelay = guestLoginDelay;
      this.createObjectOptionsSupplier = checkNotNull(createObjectOptionsSupplier, "createObjectOptionsSupplier");
      checkArgument(guestLoginDelay > 500, "guestOrderDelay must be in milliseconds and greater than 500");
      this.loginDetailsTester = retry(virtualGuestHasLoginDetailsPresent, guestLoginDelay);
   }

   @Override
   public NodeAndInitialCredentials<VirtualGuest> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(SoftLayerTemplateOptions.class),
            "options class %s should have been assignable from SoftLayerTemplateOptions", template.getOptions()
                  .getClass());

      String domainName = template.getOptions().as(SoftLayerTemplateOptions.class).getDomainName();

      final OperatingSystem operatingSystem = OperatingSystem.builder()
              .id(template.getImage().getId())
              .build();
      final Datacenter datacenter = Datacenter.builder()
              .id(Integer.valueOf(template.getLocation().getId()))
              .name((String) template.getLocation().getMetadata().get("name"))
              .build();
      VirtualGuest newGuest = VirtualGuest.builder()
              .domain(domainName)
              .hostname(name)
              .startCpus((int) template.getHardware().getProcessors().get(0).getCores())
              .maxMemory(template.getHardware().getRam())
              .operatingSystem(operatingSystem)
              .datacenter(datacenter)
              .build();
      logger.debug(">> creating new virtualGuest (%s)", newGuest);
      VirtualGuest result = api.getVirtualGuestApi().createObject(newGuest);
      logger.trace("<< virtualGuest(%s)", result.getId());

      logger.debug(">> awaiting login details for virtualGuest(%s)", result.getId());
      boolean orderInSystem = loginDetailsTester.apply(result);
      logger.trace("<< virtualGuest(%s) complete(%s)", result.getId(), orderInSystem);

      checkState(orderInSystem, "order for guest %s doesn't have login details within %sms", result,
            Long.toString(guestLoginDelay));
      result = api.getVirtualGuestApi().getObject(result.getId());

      Password pw = get(result.getOperatingSystem().getPasswords(), 0);
      return new NodeAndInitialCredentials<VirtualGuest>(result, result.getId() + "", LoginCredentials.builder().user(pw.getUsername()).password(
            pw.getPassword()).build());
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      VirtualGuestConfiguration virtualGuestConfiguration = createObjectOptionsSupplier.get();
      Builder<Hardware> hardware = ImmutableSet.builder();
      for (ProcessorOption processor : virtualGuestConfiguration.getProcessors()) {
         for (MemoryOption memory : virtualGuestConfiguration.getMemoryOptions()) {
            for (BlockDeviceOption blockDevice : virtualGuestConfiguration.getBlockDeviceOptions()) {
            String id = String.format("cpu=%s,memory=%s,disk=%s", processor.getStartCpus(), memory.getMaxMemory(),
                    blockDevice.getSize());
            hardware.add(new HardwareBuilder()
                    .ids(id)
                    .ram(memory.getMaxMemory())
                    .processors(ImmutableList.of(new Processor(processor.getStartCpus(), 0)))
                    .hypervisor("XenServer")
                    .volumes(ImmutableList.<Volume>of(new VolumeImpl(blockDevice.getSize(), true, true)))
                    .build());
            }
         }
      }
      return hardware.build();
   }

   @Override
   public Set<OperatingSystemOption> listImages() {
      return createObjectOptionsSupplier.get().getOperatingSystems();
   }

   // cheat until we have a getProductItem command
   @Override
   public OperatingSystemOption getImage(final String id) {
      return find(listImages(), new Predicate<OperatingSystemOption>() {

         @Override
         public boolean apply(OperatingSystemOption input) {
            return OperatingSystemOptionToImage.imageId().apply(input).equals(id);
         }

      }, null);
   }

   @Override
   public Iterable<VirtualGuest> listNodes() {
      return filter(api.getVirtualGuestApi().listVirtualGuests(), new Predicate<VirtualGuest>() {

         @Override
         public boolean apply(VirtualGuest arg0) {
            boolean hasBillingItem = arg0.getBillingItemId() != -1;
            if (hasBillingItem)
               return true;
            logger.trace("guest invalid, as it has no billing item %s", arg0);
            return false;
         }

      });
   }

   @Override
   public Iterable<VirtualGuest> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VirtualGuest>() {

         @Override
         public boolean apply(VirtualGuest server) {
            return contains(ids, server.getId());
         }
      });
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      Set<Datacenter> result = Sets.newHashSet();
      Set<Datacenter> unfiltered = api.getDatacenterApi().listDatacenters();
      Set<DatacenterOption> datacenterAvailable = createObjectOptionsSupplier.get().getDatacenters();
      for (DatacenterOption datacenterOption : datacenterAvailable) {
         final String datacenterName = datacenterOption.getName();
         result.addAll(Sets.newHashSet(filter(unfiltered,
                 new Predicate<Datacenter>() {
            @Override
            public boolean apply(Datacenter input) {
               return input.getName().equals(datacenterName);
            }
         })));
      }
      return result;
   }

   @Override
   public VirtualGuest getNode(String id) {
      long serverId = Long.parseLong(id);
      return api.getVirtualGuestApi().getObject(serverId);
   }

   @Override
   public void destroyNode(String id) {
      VirtualGuest guest = getNode(id);
      if (guest == null)
         return;
      logger.debug(">> awaiting virtualGuest(%s) without active transactions", guest.getId());
      checkState(retry(new Predicate<VirtualGuest>() {
         public boolean apply(VirtualGuest guest) {
               guest = getNode(guest.getId() + "");
               return guest.getActiveTransactionCount() == 0;
         }
      }, 180000).apply(guest), "%s still has active transactions!", guest);
      logger.debug(">> canceling server with globalIdentifier(%s)", id);
      checkState(api.getVirtualGuestApi().deleteObject(guest.getId()), "server(%s) still there after deleting!?", id);
   }

   @Override
   public void rebootNode(String id) {
      api.getVirtualGuestApi().rebootHardVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void resumeNode(String id) {
      api.getVirtualGuestApi().resumeVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void suspendNode(String id) {
      api.getVirtualGuestApi().pauseVirtualGuest(Long.parseLong(id));
   }

   public static class VirtualGuestHasLoginDetailsPresent implements Predicate<VirtualGuest> {
      private final SoftLayerApi client;

      @Inject
      public VirtualGuestHasLoginDetailsPresent(SoftLayerApi client) {
         this.client = checkNotNull(client, "client was null");
      }

      @Override
      public boolean apply(VirtualGuest guest) {
         checkNotNull(guest, "virtual guest was null");

         VirtualGuest newGuest = client.getVirtualGuestApi().getObject(guest.getId());
         boolean hasBackendIp = newGuest.getPrimaryBackendIpAddress() != null;
         boolean hasPrimaryIp = newGuest.getPrimaryIpAddress() != null;
         boolean hasPasswords = newGuest.getOperatingSystem() != null
               && newGuest.getOperatingSystem().getPasswords().size() > 0;

         return hasBackendIp && hasPrimaryIp && hasPasswords;
      }
   }
}
