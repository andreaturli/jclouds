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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
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
import org.jclouds.softlayer.domain.VirtualDiskImageSoftware;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplate;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.tryFind;
import static java.lang.Math.round;
import static java.lang.String.format;
import static org.jclouds.compute.domain.Volume.Type;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_INCLUDE_PUBLIC_IMAGES;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;
import static org.jclouds.util.Predicates2.retry;

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
   private final long activeTransactionsDelay;
   private final boolean includePublicImages;

   @Inject
   public SoftLayerComputeServiceAdapter(SoftLayerApi api,
         VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent,
         @Memoized Supplier<ContainerVirtualGuestConfiguration> createObjectOptionsSupplier,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY) long guestLoginDelay,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY) long activeTransactionsDelay,
         @Named(PROPERTY_SOFTLAYER_INCLUDE_PUBLIC_IMAGES) boolean includePublicImages) {
      this.api = checkNotNull(api, "api");
      this.guestLoginDelay = guestLoginDelay;
      this.activeTransactionsDelay = activeTransactionsDelay;
      this.includePublicImages = includePublicImages;
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

      final String imageId = template.getImage().getId();
      Set<OperatingSystem> operatingSystemsAvailable = createObjectOptionsSupplier.get()
              .getVirtualGuestOperatingSystems();
      Optional<OperatingSystem> optionalOS = tryFind(from(operatingSystemsAvailable)
              .filter(new Predicate<OperatingSystem>() {
                 @Override
                 public boolean apply(OperatingSystem input) {
                    return input.getId().contains(imageId);
                 }
              }), Predicates.notNull());
      OperatingSystem operatingSystem = null;
      VirtualGuestBlockDeviceTemplateGroup blockDeviceTemplateGroup = null;
      if(optionalOS.isPresent()) {
         operatingSystem = optionalOS.get();
      } else {
            blockDeviceTemplateGroup = VirtualGuestBlockDeviceTemplateGroup.builder()
                 .globalIdentifier(imageId)
                 .build();
      }
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

      VirtualGuest.Builder virtualGuestBuilder = VirtualGuest.builder()
              .domain(domainName)
              .hostname(name)
              .startCpus((int) template.getHardware().getProcessors().get(0).getCores())
              .maxMemory(template.getHardware().getRam())
              .datacenter(datacenter)
              .blockDevices(blockDevice)
              .localDiskFlag(isLocalDisk(blockDevice));
      VirtualGuest virtualGuest = null;
      if(operatingSystem != null) {
         virtualGuest = virtualGuestBuilder.operatingSystem(operatingSystem).build();
      } else if(blockDeviceTemplateGroup != null) {
         virtualGuest = virtualGuestBuilder.blockDeviceTemplateGroup(blockDeviceTemplateGroup).build();
      }

      logger.debug(">> creating new VirtualGuest(%s)", virtualGuest);
      VirtualGuest result = api.getVirtualGuestApi().createObject(virtualGuest);
      logger.trace("<< VirtualGuest(%s)", result.getId());

      logger.debug(">> awaiting login details for virtualGuest(%s)", result.getId());
      boolean orderInSystem = loginDetailsTester.apply(result);
      logger.trace("<< VirtualGuest(%s) complete(%s)", result.getId(), orderInSystem);

      if(!orderInSystem) {
         logger.warn("VirtualGuest(%s) doesn't have login details within %sms so it will be destroyed.", result,
              Long.toString(guestLoginDelay));
         api.getVirtualGuestApi().deleteObject(result.getId());
         throw new IllegalStateException(format("VirtualGuest(%s) is being destroyed as it doesn't have login details" +
                 " after %sms. Please, try by increasing `jclouds.softlayer.virtualguest.login_details_delay` and " +
                 " try again", result, Long.toString(guestLoginDelay)));
      }
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
                  String id = format("cpu=%s,memory=%s,disk=%s,type=%s", cpus, memory, round(capacity), type);
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
      Set<OperatingSystem> operatingSystemsAvailable = createObjectOptionsSupplier.get()
              .getVirtualGuestOperatingSystems();
      for (OperatingSystem os : operatingSystemsAvailable) {
         final String osReferenceCode = os.getOperatingSystemReferenceCode();
         final String osId = os.getId();
         result.addAll(
                 from(unfiltered)
                         .filter(new Predicate<SoftwareDescription>() {
                            @Override
                            public boolean apply(SoftwareDescription input) {
                               return isOperatingSystem(input) && input.getReferenceCode().equals(osReferenceCode);
                            }
                         })
                         .transform(new SoftwareDescriptionToOperatingSystem(osId, osReferenceCode))
                         .toSet()
         );
      }

      // list private images and transform them to OperatingSystem
      Set<VirtualGuestBlockDeviceTemplateGroup> privateImages = api.getAccountApi().getBlockDeviceTemplateGroups();
      Map<String, SoftwareDescription> privateImagesSoftwareDescriptions = extractSoftwareDescriptions(privateImages);
      for (Map.Entry<String, SoftwareDescription> entry : privateImagesSoftwareDescriptions.entrySet()) {
         OperatingSystem os = getOperatingSystem(entry);
         if (os != null) {
            result.add(os);
         }
      }
      // list public images and transform them to OperatingSystem
      if(includePublicImages) {
         Set<VirtualGuestBlockDeviceTemplateGroup> publicImages = api.getVirtualGuestBlockDeviceTemplateGroupApi().getPublicImages();
         Map<String, SoftwareDescription> publicImagesSoftwareDescriptions = extractSoftwareDescriptions(publicImages);
         for (Map.Entry<String, SoftwareDescription> entry :
                 publicImagesSoftwareDescriptions.entrySet()) {
            OperatingSystem os = getOperatingSystem(entry);
            if(os != null) {
               result.add(os);
            }
         }
      }
      return result;
   }

   private OperatingSystem getOperatingSystem(Map.Entry<String, SoftwareDescription> entry) {
      SoftwareDescription softwareDescription = entry.getValue();
      if (isOperatingSystem(softwareDescription)) {
         String uuid = entry.getKey();
         return OperatingSystem.builder()
                 .id(uuid)
                 .softwareLicense(SoftwareLicense.builder().softwareDescription(softwareDescription).build())
                 .operatingSystemReferenceCode(softwareDescription.getReferenceCode())
                 .build();
      }
      return null;
   }

   private Map<String, SoftwareDescription> extractSoftwareDescriptions(Set<VirtualGuestBlockDeviceTemplateGroup> images) {
      Map<String, SoftwareDescription> softwareDescriptions = Maps.newHashMap();
      for (VirtualGuestBlockDeviceTemplateGroup image : images) {
         final String globalIdentifier = image.getGlobalIdentifier();
         for(VirtualGuestBlockDeviceTemplateGroup child : image.getChildren()) {
            for(VirtualGuestBlockDeviceTemplate blockDeviceTemplate : child.getBlockDevices()) {
               for(VirtualDiskImageSoftware softwareReference : blockDeviceTemplate.getDiskImage().getSoftwareReferences()) {
                  softwareDescriptions.put(globalIdentifier, softwareReference.getSoftwareDescription());
               }
            }
         }
      }
      return softwareDescriptions;
   }

   @Override
   public OperatingSystem getImage(final String id) {
      return find(listImages(), new Predicate<OperatingSystem>() {

         @Override
         public boolean apply(OperatingSystem input) {
            return input.getId().equals(id);
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
      }, activeTransactionsDelay).apply(guest), "%s still has active transactions!", guest);
      logger.debug(">> canceling virtualGuest with globalIdentifier(%s)", id);
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

   private boolean isOperatingSystem(SoftwareDescription input) {
      return input.getOperatingSystem() == 1;
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

   private static class SoftwareDescriptionToOperatingSystem implements Function<SoftwareDescription, OperatingSystem> {
      private final String osId;
      private final String operatingSystemReferenceCode;

      public SoftwareDescriptionToOperatingSystem(String osId, String operatingSystemReferenceCode) {
         this.osId = osId;
         this.operatingSystemReferenceCode = operatingSystemReferenceCode;
      }

      @Override
      public OperatingSystem apply(SoftwareDescription input) {
         return OperatingSystem.builder().id(osId)
                 .softwareLicense(SoftwareLicense.builder().softwareDescription(input).build())
                 .operatingSystemReferenceCode(operatingSystemReferenceCode)
                 .build();
      }
   }
}
