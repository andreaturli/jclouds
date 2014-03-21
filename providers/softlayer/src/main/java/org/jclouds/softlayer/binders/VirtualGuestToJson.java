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
package org.jclouds.softlayer.binders;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Converts a VirtualGuest into a json string valid for creating a CCI via softlayer api
 * The string is set into the payload of the HttpRequest
 * 
 * @author Andrea Turli
 */
public class VirtualGuestToJson implements Binder {

   private final Json json;

   @Inject
   public VirtualGuestToJson(Json json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      VirtualGuest virtualGuest = VirtualGuest.class.cast(checkNotNull(input, "parameters"));
      request.setPayload(buildJson(virtualGuest));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    *
    * @param virtualGuest
    * @return String
    */
   String buildJson(VirtualGuest virtualGuest) {
      TemplateObject templateObject = null;
      String hostname = checkNotNull(virtualGuest.getHostname(), "hostname");
      String domain = checkNotNull(virtualGuest.getDomain(), "domain");
      int startCpus = checkNotNull(virtualGuest.getStartCpus(), "startCpus");
      int maxMemory = checkNotNull(virtualGuest.getMaxMemory(), "maxMemory");
      String datacenterName = checkNotNull(virtualGuest.getDatacenter().getName(), "datacenterName");
      Set<NetworkComponent> networkComponents = getNetworkComponents(virtualGuest);
      if(virtualGuest.getOperatingSystem() != null) {
         String operatingSystemReferenceCode = checkNotNull(virtualGuest.getOperatingSystem()
                 .getOperatingSystemReferenceCode(), "operatingSystemReferenceCode");
         templateObject = new TemplateObject(hostname, domain, startCpus, maxMemory, true,
                 operatingSystemReferenceCode, null, true, new Datacenter(datacenterName), networkComponents,
                 getBlockDevices(virtualGuest));
      } else if(virtualGuest.getVirtualGuestBlockDeviceTemplateGroup() != null) {
         String globalIdentifier = checkNotNull(virtualGuest.getVirtualGuestBlockDeviceTemplateGroup()
                 .getGlobalIdentifier(), "blockDeviceTemplateGroup.globalIdentifier");
         templateObject = new TemplateObject(hostname, domain, startCpus, maxMemory, true, null,
                 new BlockDeviceTemplateGroup(globalIdentifier), true, new Datacenter(datacenterName),
                 networkComponents, null);
      }
      return json.toJson(ImmutableMap.of("parameters", ImmutableList.<TemplateObject> of(templateObject)));
   }

   private HashSet<BlockDevice> getBlockDevices(VirtualGuest virtualGuest) {
      if(virtualGuest.getVirtualGuestBlockDevices() == null) return null;
      return Sets.newHashSet(Iterables.transform(virtualGuest.getVirtualGuestBlockDevices(),
              new Function<VirtualGuestBlockDevice, BlockDevice>() {
                 @Override
                 public BlockDevice apply(VirtualGuestBlockDevice input) {
                    return new BlockDevice(input.getDevice(), input.getVirtualDiskImage().getCapacity());
                 }
              }));
   }

   private HashSet<NetworkComponent> getNetworkComponents(VirtualGuest virtualGuest) {
      if(virtualGuest.getVirtualGuestNetworkComponents() == null) return null;
      return Sets.newHashSet(Iterables.transform(virtualGuest.getVirtualGuestNetworkComponents(),
              new Function<VirtualGuestNetworkComponent, NetworkComponent>() {
                 @Override
                 public NetworkComponent apply(VirtualGuestNetworkComponent input) {
                    return new NetworkComponent(input.getSpeed());
                 }
              }));
   }

   private static class TemplateObject {
      private String hostname;
      private String domain;
      private int startCpus;
      private int maxMemory;
      private boolean hourlyBillingFlag;
      private BlockDeviceTemplateGroup blockDeviceTemplateGroup;
      private String operatingSystemReferenceCode;
      private boolean localDiskFlag;
      private Datacenter datacenter;
      private Set<NetworkComponent> networkComponents;
      private Set<BlockDevice> blockDevices;

      private TemplateObject(String hostname, String domain, int startCpus, int maxMemory, boolean hourlyBillingFlag,
                         String operatingSystemReferenceCode, BlockDeviceTemplateGroup blockDeviceTemplateGroup,
                         boolean localDiskFlag, Datacenter datacenter, Set<NetworkComponent> networkComponents,
                         Set<BlockDevice> blockDevices) {
         this.hostname = hostname;
         this.domain = domain;
         this.startCpus = startCpus;
         this.maxMemory = maxMemory;
         this.hourlyBillingFlag = hourlyBillingFlag;
         this.operatingSystemReferenceCode = operatingSystemReferenceCode;
         this.blockDeviceTemplateGroup = blockDeviceTemplateGroup;
         this.localDiskFlag = localDiskFlag;
         this.datacenter = datacenter;
         this.networkComponents = networkComponents;
         this.blockDevices = blockDevices;
      }
   }

   private class Datacenter {
      private String name;

      private Datacenter(String name) {
         this.name = name;
      }
   }

   private class NetworkComponent {
      private int maxSpeed;

      private NetworkComponent(int maxSpeed) {
         this.maxSpeed = maxSpeed;
      }
   }

   private class BlockDevice {
      private String device;
      private DiskImage diskImage;

      private BlockDevice(String device, float diskImageCapacity) {
         this.device = device;
         this.diskImage = new DiskImage(diskImageCapacity);
      }
   }

   private class DiskImage {
      private float capacity;

      private DiskImage(float capacity) {
         this.capacity = capacity;
      }
   }

   private class BlockDeviceTemplateGroup {
      private String globalIdentifier;

      private BlockDeviceTemplateGroup(String globalIdentifier) {
         this.globalIdentifier = globalIdentifier;
      }
   }
}
