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

/**
 * @author Andrea Turli
 */
public class VirtualDiskImageSoftware {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualDiskImageSoftware(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected int softwareDescriptionId;
      protected SoftwareDescription softwareDescription;

      /**
       * @see VirtualDiskImageSoftware#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see VirtualDiskImageSoftware#getSoftwareDescriptionId()
       */
      public T softwareDescriptionId(int softwareDescriptionId) {
         this.softwareDescriptionId = softwareDescriptionId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImageSoftware#getSoftwareDescription()
       */
      public T softwareDescription(SoftwareDescription softwareDescription) {
         this.softwareDescription = softwareDescription;
         return self();
      }

      public VirtualDiskImageSoftware build() {
         return new VirtualDiskImageSoftware(id, softwareDescriptionId, softwareDescription);
      }

      public T fromVirtualDiskImageSoftware(VirtualDiskImageSoftware in) {
         return this
                 .id(in.getId())
                 .softwareDescriptionId(in.getSoftwareDescriptionId())
                 .softwareDescription(in.getSoftwareDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final int softwareDescriptionId;
   private final SoftwareDescription softwareDescription;

   @ConstructorProperties({"id", "softwareDescriptionId", "softwareDescription"})
   public VirtualDiskImageSoftware(int id, int softwareDescriptionId, @Nullable SoftwareDescription softwareDescription) {
      this.id = id;
      this.softwareDescriptionId = softwareDescriptionId;
      this.softwareDescription = softwareDescription;
   }

   public int getId() {
      return this.id;
   }

   public int getSoftwareDescriptionId() {
      return this.softwareDescriptionId;
   }

   @Nullable
   public SoftwareDescription getSoftwareDescription() {
      return this.softwareDescription;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VirtualDiskImageSoftware that = VirtualDiskImageSoftware.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
              .add("id", id).add("softwareDescriptionId", softwareDescriptionId).add("softwareDescription",
                      softwareDescription);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
