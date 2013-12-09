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
package org.jclouds.softlayer.domain;

import com.google.common.base.Objects;

public class ItemPrice {

   private final String hourlyRecurringFee;
   private final Item item;
   private final String recurringFee;

   public ItemPrice(String hourlyRecurringFee, Item item, String recurringFee) {
      this.hourlyRecurringFee = hourlyRecurringFee;
      this.item = item;
      this.recurringFee = recurringFee;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("hourlyRecurringFee", hourlyRecurringFee)
              .add("item", item)
              .add("recurringFee", recurringFee)
              .toString();
   }

   public Item getItem() {
      return item;
   }

   protected class Item {
      private final String description;
      private final String id;
      private final SoftwareDescription softwareDescription;
      private final String softwareDescriptionId;
      private final String upgradeItemId;

      protected Item(String description, String id, SoftwareDescription softwareDescription, String softwareDescriptionId, String upgradeItemId) {
         this.description = description;
         this.id = id;
         this.softwareDescription = softwareDescription;
         this.softwareDescriptionId = softwareDescriptionId;
         this.upgradeItemId = upgradeItemId;
      }

      public String getDescription() {
         return description;
      }

      public SoftwareDescription getSoftwareDescription() {
         return softwareDescription;
      }
   }

   protected class SoftwareDescription {
      private final String controlPanel;
      private final String id;
      private final String longDescription;
      private final String manufacturer;
      private final String name;
      private final String operatingSystem;
      private final String referenceCode;
      private final String requiredUser;
      private final String upgradeSoftwareDescriptionId;
      private final String upgradeSwDescId;
      private final String version;
      private final String virtualLicense;
      private final String virtualizationPlatform;

      protected SoftwareDescription(String controlPanel, String id, String longDescription, String manufacturer,
                                    String name, String operatingSystem, String referenceCode, String requiredUser,
                                    String upgradeSoftwareDescriptionId, String upgradeSwDescId, String version,
                                    String virtualLicense, String virtualizationPlatform) {
         this.controlPanel = controlPanel;
         this.id = id;
         this.longDescription = longDescription;
         this.manufacturer = manufacturer;
         this.name = name;
         this.operatingSystem = operatingSystem;
         this.referenceCode = referenceCode;
         this.requiredUser = requiredUser;
         this.upgradeSoftwareDescriptionId = upgradeSoftwareDescriptionId;
         this.upgradeSwDescId = upgradeSwDescId;
         this.version = version;
         this.virtualLicense = virtualLicense;
         this.virtualizationPlatform = virtualizationPlatform;
      }

      public String getId() {
         return id;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
                 .add("controlPanel", controlPanel)
                 .add("id", id)
                 .add("longDescription", longDescription)
                 .add("manufacturer", manufacturer)
                 .add("name", name)
                 .add("operatingSystem", operatingSystem)
                 .add("referenceCode", referenceCode)
                 .add("requiredUser", requiredUser)
                 .add("upgradeSoftwareDescriptionId", upgradeSoftwareDescriptionId)
                 .add("upgradeSwDescId", upgradeSwDescId)
                 .add("version", version)
                 .add("virtualLicense", virtualLicense)
                 .add("virtualizationPlatform", virtualizationPlatform)
                 .toString();
      }
   }
}