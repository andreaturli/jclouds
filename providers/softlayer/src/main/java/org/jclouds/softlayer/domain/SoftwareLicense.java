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

public class SoftwareLicense {

   private final int id;
   private final SoftwareDescription softwareDescription;
   private final int softwareDescriptionId;

   @ConstructorProperties({
           "id", "softwareDescription", "softwareDescriptionId"
   })
   protected SoftwareLicense(int id, @Nullable SoftwareDescription softwareDescription, int softwareDescriptionId) {
      this.id = id;
      this.softwareDescription = softwareDescription;
      this.softwareDescriptionId = softwareDescriptionId;
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSoftwareLicense(this);
   }

   public int getId() {
      return this.id;
   }

   @Nullable
   public SoftwareDescription getSoftwareDescription() {
      return this.softwareDescription;
   }

   /**
    * @return A longer location description.
    */
   @Nullable
   public int getSoftwareDescriptionId() {
      return this.softwareDescriptionId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SoftwareLicense that = SoftwareLicense.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
              .add("id", id).add("softwareDescription", softwareDescription).add("softwareDescriptionId", softwareDescriptionId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected int id;
      protected SoftwareDescription softwareDescription;
      protected int softwareDescriptionId;

      protected abstract T self();

      /**
       * @see SoftwareLicense#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftwareLicense#getSoftwareDescription() ()
       */
      public T softwareDescription(SoftwareDescription softwareDescription) {
         this.softwareDescription = softwareDescription;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftwareLicense#getSoftwareDescriptionId() ()
       */
      public T softwareDescriptionId(int softwareDescriptionId) {
         this.softwareDescriptionId = softwareDescriptionId;
         return self();
      }

      public SoftwareLicense build() {
         return new SoftwareLicense(id, softwareDescription, softwareDescriptionId);
      }

      public T fromSoftwareLicense(SoftwareLicense in) {
         return this
                 .id(in.getId())
                 .softwareDescription(in.getSoftwareDescription())
                 .softwareDescriptionId(in.getSoftwareDescriptionId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}