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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Extends the SoftLayer_Software_Component data type to include operating system specific properties.
 *
 * @author Jason King, Andrea Turli
 * @see <a href="http://sldn.softlayer.com/reference/datatypes/SoftLayer_Software_Component_OperatingSystem"/>
 */
public class OperatingSystem {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromOperatingSystem(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected SoftwareLicense softwareLicense;
      protected String operatingSystemReferenceCode;
      protected Set<Password> passwords = ImmutableSet.of();

      /**
       * @see OperatingSystem#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see OperatingSystem#getSoftwareLicense()
       */
      public T softwareLicense(SoftwareLicense softwareLicense) {
         this.softwareLicense = softwareLicense;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.OperatingSystem#getOperatingSystemReferenceCode()
       */
      public T operatingSystemReferenceCode(String operatingSystemReferenceCode) {
         this.operatingSystemReferenceCode = operatingSystemReferenceCode;
         return self();
      }

      /**
       * @see OperatingSystem#getPasswords()
       */
      public T passwords(Set<Password> passwords) {
         this.passwords = ImmutableSet.copyOf(checkNotNull(passwords, "passwords"));
         return self();
      }

      public T passwords(Password... in) {
         return passwords(ImmutableSet.copyOf(in));
      }

      public OperatingSystem build() {
         return new OperatingSystem(id, softwareLicense, operatingSystemReferenceCode, passwords);
      }

      public T fromOperatingSystem(OperatingSystem in) {
         return this
               .id(in.getId())
               .passwords(in.getPasswords());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final SoftwareLicense softwareLicense;
   private final String operatingSystemReferenceCode;
   private final Set<Password> passwords;

   @ConstructorProperties({
         "id", "softwareLicense", "operatingSystemReferenceCode", "passwords"
   })
   protected OperatingSystem(String id, @Nullable SoftwareLicense softwareLicense,
                             @Nullable String operatingSystemReferenceCode, @Nullable Set<Password> passwords) {
      this.id = id;
      this.softwareLicense = softwareLicense;
      this.operatingSystemReferenceCode = operatingSystemReferenceCode;
      this.passwords = passwords == null ? ImmutableSet.<Password>of() : ImmutableSet.copyOf(passwords);
   }

   /**
    * @return An ID number identifying this Software Component (Software Installation)
    */
   public String getId() {
      return this.id;
   }

   public SoftwareLicense getSoftwareLicense() {
      return softwareLicense;
   }

   public String getOperatingSystemReferenceCode() {
      return operatingSystemReferenceCode;
   }

   /**
    * @return Username/Password pairs used for access to this Software Installation.
    */
   public Set<Password> getPasswords() {
      return this.passwords;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OperatingSystem that = (OperatingSystem) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.softwareLicense, that.softwareLicense) &&
              Objects.equal(this.operatingSystemReferenceCode, that.operatingSystemReferenceCode) &&
              Objects.equal(this.passwords, that.passwords);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, softwareLicense, operatingSystemReferenceCode, passwords);
   }

   @Override
   public String toString() {
      return "OperatingSystem{" +
              "id='" + id + '\'' +
              ", softwareLicense=" + softwareLicense +
              ", operatingSystemReferenceCode=" + operatingSystemReferenceCode +
              ", passwords=" + passwords +
              '}';
   }
}
