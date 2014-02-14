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
import static java.lang.Math.round;
import static org.jclouds.compute.domain.Volume.*;
import static org.jclouds.util.Predicates2.retry;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
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
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.ContainerVirtualGuestConfiguration;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.jclouds.softlayer.domain.VirtualDiskImage;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;

/**
 * defines the connection between the {@link SoftLayerApi} implementation and
 * the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualGuest, Hardware, OperatingSystem, Datacenter> {

   public static final String BOOTABLE_DEVICE = "0";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SoftLayerApi api;
   private final Supplier<ContainerVirtualGuestConfiguration> createObjectOptionsSupplier;
   private final Predicate<VirtualGuest> loginDetailsTester;
   private final long guestLoginDelay;

   @Inject
   public SoftLayerComputeServiceAdapter(SoftLayerApi api,
         VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent,
         @Memoized Supplier<ContainerVirtualGuestConfiguration> createObjectOptionsSupplier,
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
              .name(template.getLocation().getId())
              .build();
      Float diskCapacity = template.getHardware().getVolumes().get(0).getSize();
      Type type = template.getHardware().getVolumes().get(0).getType();
      VirtualGuestBlockDevice blockDevice = VirtualGuestBlockDevice.builder()
              .device(BOOTABLE_DEVICE)
              .virtualDiskImage(VirtualDiskImage.builder()
                      .capacity(diskCapacity)
                      .typeId(type.ordinal())
                      .build())
              .build();

      VirtualGuest virtualGuest = VirtualGuest.builder()
              .domain(domainName)
              .hostname(name)
              .startCpus((int) template.getHardware().getProcessors().get(0).getCores())
              .maxMemory(template.getHardware().getRam())
              .operatingSystem(operatingSystem)
              .datacenter(datacenter)
              .blockDevices(blockDevice)
              .localDiskFlag(isLocalDisk(blockDevice))
              .build();

      logger.debug(">> creating new virtualGuest (%s)", virtualGuest);
      VirtualGuest result = api.getVirtualGuestApi().createObject(virtualGuest);
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

   private boolean isLocalDisk(VirtualGuestBlockDevice guestBlockDevice) {
      return guestBlockDevice.getVirtualDiskImage().getTypeId() == Type.LOCAL.ordinal();
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      ContainerVirtualGuestConfiguration virtualGuestConfiguration = createObjectOptionsSupplier.get();
      Builder<Hardware> hardware = ImmutableSet.builder();
      for (Integer cpus : virtualGuestConfiguration.getCpusOfProcessors()) {
         for (Integer memory : virtualGuestConfiguration.getMemories()) {
            for (VirtualGuestBlockDevice blockDevice : virtualGuestConfiguration.getVirtualGuestBlockDevices()) {
               if (blockDevice.getDevice().equals("0")) {
                  float capacity = blockDevice.getVirtualDiskImage().getCapacity();
                  Type type = blockDevice.getVirtualGuest().isLocalDiskFlag() ? Type.LOCAL : Type.SAN;
                  String id = String.format("cpu=%s,memory=%s,disk=%s,type=%s", cpus, memory, round(capacity), type);
                  hardware.add(new HardwareBuilder()
                          .ids(id)
                          .ram(memory)
                          .processors(ImmutableList.of(new Processor(cpus, 0)))
                          .hypervisor("XenServer")
                          .volumes(ImmutableList.<Volume>of(
                                  new VolumeImpl(blockDevice.getId() + "",
                                          type,
                                          capacity,
                                          blockDevice.getDevice(),
                                          blockDevice.getBootableFlag() == 1,
                                          true)))
                          .build());
               }
            }
         }
      }
      return hardware.build();
   }

   @Override
   public Set<OperatingSystem> listImages() {
      Set<OperatingSystem> result = Sets.newHashSet();
      Set<SoftwareDescription> unfiltered = api.getSoftwareDescriptionApi().getAllObjects();
      Set<OperatingSystem> osAvailable = createObjectOptionsSupplier.get().getVirtualGuestOperatingSystems();
      for (OperatingSystem os : osAvailable) {
         final String operatingSystemReferenceCode = os.getOperatingSystemReferenceCode();
         result.addAll(
                 FluentIterable.from(unfiltered)
                         .filter(new Predicate<SoftwareDescription>() {
                            @Override
                            public boolean apply(SoftwareDescription input) {
                               return input.getOperatingSystem() == 1 && input.getReferenceCode().equals
                                       (operatingSystemReferenceCode);
                            }
                         })
                         .transform(new Function<SoftwareDescription, OperatingSystem>() {
                            @Override
                            public OperatingSystem apply(SoftwareDescription input) {
                               return OperatingSystem.builder()
                                       .id(input.getName())
                                       .softwareLicense(SoftwareLicense.builder().softwareDescription(input).build())
                                       .build();
                            }
                         })
                         .toSet()
         );
      }
      return result;
   }

   @Override
   public OperatingSystem getImage(final String id) {
      return find(listImages(), new Predicate<OperatingSystem>() {

         @Override
         public boolean apply(OperatingSystem input) {
            return input.equals(id);
         }

      }, null);
   }

   @Override
   public Iterable<VirtualGuest> listNodes() {
      return api.getVirtualGuestApi().listVirtualGuests();
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
      Set<Datacenter> datacenterAvailable = createObjectOptionsSupplier.get().getVirtualGuestDatacenters();
      for (Datacenter datacenter : datacenterAvailable) {
         final String datacenterName = datacenter.getName();
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
