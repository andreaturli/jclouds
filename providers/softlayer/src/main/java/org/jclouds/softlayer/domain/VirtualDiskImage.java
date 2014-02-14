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

import java.beans.ConstructorProperties;

/**
 * Class VirtualDiskImage
 *
 * @author Andrea Turli
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Disk_Image"/>
 */
public class VirtualDiskImage {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualDiskImage(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String uuid;
      protected float capacity;
      protected String units;
      protected int typeId;
      protected String description;
      protected String name;
      protected int storageRepositoryId;

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getUuid()
       */
      public T uuid(String uuid) {
         this.uuid = uuid;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getCapacity()
       */
      public T capacity(float capacity) {
         this.capacity = capacity;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getUnits()
       */
      public T units(String units) {
         this.units = units;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getTypeId()
       */
      public T typeId(int typeId) {
         this.typeId = typeId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImage#getStorageRepositoryId()
       */
      public T storageRepositoryId(int storageRepositoryId) {
         this.storageRepositoryId = storageRepositoryId;
         return self();
      }


      public VirtualDiskImage build() {
         return new VirtualDiskImage(id, uuid, capacity, units, typeId, description, name,
                 storageRepositoryId);
      }

      public T fromVirtualDiskImage(VirtualDiskImage in) {
         return this
                 .id(in.getId())
                 .uuid(in.getUuid())
                 .capacity(in.getCapacity())
                 .units(in.getUnits())
                 .typeId(in.getTypeId())
                 .description(in.getDescription())
                 .name(in.getName())
                 .storageRepositoryId(in.getStorageRepositoryId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String uuid;
   private final float capacity;
   private final String units;
   private final int typeId;
   private final String description;
   private final String name;
   private final int storageRepositoryId;

   @ConstructorProperties({
           "id", "uuid", "capacity", "units", "typeId", "description", "name", "storageRepositoryId"
   })
   public VirtualDiskImage(int id, String uuid, float capacity, String units, int typeId, String description, String name, int storageRepositoryId) {
      this.id = id;
      this.uuid = uuid;
      this.capacity = capacity;
      this.units = units;
      this.typeId = typeId;
      this.description = description;
      this.name = name;
      this.storageRepositoryId = storageRepositoryId;
   }

   public int getId() {
      return id;
   }

   public String getUuid() {
      return uuid;
   }

   public float getCapacity() {
      return capacity;
   }

   public String getUnits() {
      return units;
   }

   public int getTypeId() {
      return typeId;
   }

   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }

   public int getStorageRepositoryId() {
      return storageRepositoryId;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualDiskImage that = (VirtualDiskImage) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.uuid, that.uuid) &&
              Objects.equal(this.capacity, that.capacity) &&
              Objects.equal(this.units, that.units) &&
              Objects.equal(this.typeId, that.typeId) &&
              Objects.equal(this.description, that.description) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.storageRepositoryId, that.storageRepositoryId);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, uuid, capacity, units, typeId, description,
              name, storageRepositoryId);
   }

   @Override
   public String toString() {
      return "VirtualDiskImage{" +
              "id=" + id +
              ", uuid='" + uuid + '\'' +
              ", capacity=" + capacity +
              ", units='" + units + '\'' +
              ", typeId=" + typeId +
              ", description='" + description + '\'' +
              ", name='" + name + '\'' +
              ", storageRepositoryId=" + storageRepositoryId +
              '}';
   }
}
