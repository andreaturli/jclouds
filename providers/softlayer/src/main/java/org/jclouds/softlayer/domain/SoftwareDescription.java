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
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

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

      public SoftwareDescription build() {
         return new SoftwareDescription(id, longDescription, manufacturer, name, operatingSystem, referenceCode,
                 requiredUser, version);
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
                 .version(in.getVersion());
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

   @ConstructorProperties({
           "id", "longDescription", "manufacturer", "name", "operatingSystem", "referenceCode", "requiredUser", "version"
   })
   protected SoftwareDescription(int id, @Nullable String longDescription, @Nullable String manufacturer,
                                 @Nullable String name, @Nullable int operatingSystem, @Nullable String referenceCode,
                                 @Nullable String requiredUser, @Nullable String version) {
      this.id = id;
      this.longDescription = longDescription;
      this.manufacturer = manufacturer;
      this.name = name;
      this.operatingSystem = operatingSystem;
      this.referenceCode = referenceCode;
      this.requiredUser = requiredUser;
      this.version = version;
   }

   @Nullable
   public int getId() {
      return this.id;
   }

   @Nullable
   public String getLongDescription() {
      return longDescription;
   }

   @Nullable
   public String getManufacturer() {
      return manufacturer;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public int getOperatingSystem() {
      return operatingSystem;
   }

   @Nullable
   public String getReferenceCode() {
      return referenceCode;
   }

   @Nullable
   public String getRequiredUser() {
      return requiredUser;
   }

   @Nullable
   public String getVersion() {
      return version;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SoftwareDescription that = SoftwareDescription.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("longDescription", longDescription)
              .add("manufacturer", manufacturer)
              .add("name", name)
              .add("operatingSystem", operatingSystem)
              .add("referenceCode", referenceCode)
              .add("requiredUser", requiredUser)
              .add("version", version)
              .toString();
   }
}
