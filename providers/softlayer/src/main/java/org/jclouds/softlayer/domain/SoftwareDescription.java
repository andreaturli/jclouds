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
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;

public class SoftwareDescription {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSoftwareDescription(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String longDescription;
      protected String manufacturer;
      protected String name;
      protected int operatingSystem;
      protected String referenceCode;
      protected String requiredUser;
      protected String version;
      protected int controlPanel;
      protected String upgradeSoftwareDescriptionId;
      protected String upgradeSwDescId;
      protected String virtualLicense;
      protected String virtualizationPlatform;

      /**
       * @see SoftwareDescription#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see SoftwareDescription#getLongDescription()
       */
      public T longDescription(String longDescription) {
         this.longDescription = longDescription;
         return self();
      }

      /**
       * @see SoftwareDescription#getManufacturer()
       */
      public T manufacturer(String manufacturer) {
         this.manufacturer = manufacturer;
         return self();
      }

      /**
       * @see SoftwareDescription#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see SoftwareDescription#getOperatingSystem()
       */
      public T operatingSystem(int operatingSystem) {
         this.operatingSystem = operatingSystem;
         return self();
      }

      /**
       * @see SoftwareDescription#getReferenceCode()
       */
      public T referenceCode(String referenceCode) {
         this.referenceCode = referenceCode;
         return self();
      }

      /**
       * @see SoftwareDescription#getRequiredUser()
       */
      public T requiredUser(String requiredUser) {
         this.requiredUser = requiredUser;
         return self();
      }

      /**
       * @see SoftwareDescription#getVersion()
       */
      public T version(String version) {
         this.version = version;
         return self();
      }

      public T controlPanel(int controlPanel) {
         this.controlPanel = controlPanel;
         return self();
      }

      public T upgradeSoftwareDescriptionId(String upgradeSoftwareDescriptionId) {
         this.upgradeSoftwareDescriptionId = upgradeSoftwareDescriptionId;
         return self();
      }

      public T upgradeSwDescId(String upgradeSwDescId) {
         this.upgradeSwDescId = upgradeSwDescId;
         return self();
      }

      public T virtualLicense(String virtualLicense) {
         this.virtualLicense = virtualLicense;
         return self();
      }

      public T virtualizationPlatform(String virtualizationPlatform) {
         this.virtualizationPlatform = virtualizationPlatform;
         return self();
      }

      public SoftwareDescription build() {
         return new SoftwareDescription(id, longDescription, manufacturer, name, operatingSystem, referenceCode,
                 requiredUser, version, controlPanel, upgradeSoftwareDescriptionId, upgradeSwDescId, virtualLicense,
                 virtualizationPlatform);
      }

      public T fromSoftwareDescription(SoftwareDescription in) {
         return this
                 .id(in.getId())
                 .longDescription(in.getLongDescription())
                 .manufacturer(in.getManufacturer())
                 .name(in.getName())
                 .operatingSystem(in.getOperatingSystem())
                 .referenceCode(in.getReferenceCode())
                 .requiredUser(in.getRequiredUser())
                 .version(in.getVersion())
                 .controlPanel(in.getControlPanel())
                 .upgradeSoftwareDescriptionId(in.getUpgradeSoftwareDescriptionId())
                 .upgradeSwDescId(in.getUpgradeSwDescId())
                 .virtualLicense(in.getVirtualLicense())
                 .virtualizationPlatform(in.getVirtualizationPlatform());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String longDescription;
   private final String manufacturer;
   private final String name;
   private final int operatingSystem;
   private final String referenceCode;
   private final String requiredUser;
   private final String version;
   private final int controlPanel;
   private final String upgradeSoftwareDescriptionId;
   private final String upgradeSwDescId;
   private final String virtualLicense;
   private final String virtualizationPlatform;

   @ConstructorProperties({
           "id", "longDescription", "manufacturer", "name", "operatingSystem", "referenceCode", "requiredUser",
           "version", "controlPanel", "upgradeSoftwareDescriptionId", "upgradeSwDescId", "virtualLicense",
           "virtualizationPlatform"
   })
   protected SoftwareDescription(int id, @Nullable String longDescription, @Nullable String manufacturer,
                                 @Nullable String name, @Nullable int operatingSystem, @Nullable String referenceCode,
                                 @Nullable String requiredUser, @Nullable String version, int controlPanel,
                                 String upgradeSoftwareDescriptionId, String upgradeSwDescId, String virtualLicense,
                                 String virtualizationPlatform) {
      this.id = id;
      this.longDescription = longDescription;
      this.manufacturer = manufacturer;
      this.name = name;
      this.operatingSystem = operatingSystem;
      this.referenceCode = referenceCode;
      this.requiredUser = requiredUser;
      this.version = version;
      this.controlPanel = controlPanel;
      this.upgradeSoftwareDescriptionId = upgradeSoftwareDescriptionId;
      this.upgradeSwDescId = upgradeSwDescId;
      this.virtualLicense = virtualLicense;
      this.virtualizationPlatform = virtualizationPlatform;
   }

   public int getId() {
      return id;
   }

   public String getLongDescription() {
      return longDescription;
   }

   public String getManufacturer() {
      return manufacturer;
   }

   public String getName() {
      return name;
   }

   public int getOperatingSystem() {
      return operatingSystem;
   }

   public String getReferenceCode() {
      return referenceCode;
   }

   public String getRequiredUser() {
      return requiredUser;
   }

   public String getVersion() {
      return version;
   }

   public int getControlPanel() {
      return controlPanel;
   }

   public String getUpgradeSoftwareDescriptionId() {
      return upgradeSoftwareDescriptionId;
   }

   public String getUpgradeSwDescId() {
      return upgradeSwDescId;
   }

   public String getVirtualLicense() {
      return virtualLicense;
   }

   public String getVirtualizationPlatform() {
      return virtualizationPlatform;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SoftwareDescription that = (SoftwareDescription) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.longDescription, that.longDescription) &&
              Objects.equal(this.manufacturer, that.manufacturer) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.operatingSystem, that.operatingSystem) &&
              Objects.equal(this.referenceCode, that.referenceCode) &&
              Objects.equal(this.requiredUser, that.requiredUser) &&
              Objects.equal(this.version, that.version) &&
              Objects.equal(this.controlPanel, that.controlPanel) &&
              Objects.equal(this.upgradeSoftwareDescriptionId, that.upgradeSoftwareDescriptionId) &&
              Objects.equal(this.upgradeSwDescId, that.upgradeSwDescId) &&
              Objects.equal(this.virtualLicense, that.virtualLicense) &&
              Objects.equal(this.virtualizationPlatform, that.virtualizationPlatform);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, longDescription, manufacturer, name, operatingSystem, referenceCode,
              requiredUser, version, controlPanel, upgradeSoftwareDescriptionId, upgradeSwDescId,
              virtualLicense, virtualizationPlatform);
   }

   @Override
   public String toString() {
      return "SoftwareDescription{" +
              "id=" + id +
              ", longDescription='" + longDescription + '\'' +
              ", manufacturer='" + manufacturer + '\'' +
              ", name='" + name + '\'' +
              ", operatingSystem=" + operatingSystem +
              ", referenceCode='" + referenceCode + '\'' +
              ", requiredUser='" + requiredUser + '\'' +
              ", version='" + version + '\'' +
              ", controlPanel='" + controlPanel + '\'' +
              ", upgradeSoftwareDescriptionId='" + upgradeSoftwareDescriptionId + '\'' +
              ", upgradeSwDescId='" + upgradeSwDescId + '\'' +
              ", virtualLicense='" + virtualLicense + '\'' +
              ", virtualizationPlatform='" + virtualizationPlatform + '\'' +
              '}';
   }
}
