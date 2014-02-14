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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

/**
 * Class VirtualGuestBlockDevice
 *
 * @author Andrea Turli
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Block_Device"/>
 */
public class VirtualGuestBlockDevice {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualGuestBlockDevice(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String uuid;
      protected int statusId;
      protected String mountType;
      protected String mountMode;
      protected int bootableFlag;
      protected String device;
      protected VirtualDiskImage virtualDiskImage;
      protected VirtualGuest guest;

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getUuid()
       */
      public T uuid(String uuid) {
         this.uuid = uuid;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getStatusId()
       */
      public T statusId(int statusId) {
         this.statusId = statusId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getMountType()
       */
      public T mountType(String mountType) {
         this.mountType = mountType;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getMountMode()
       */
      public T mountMode(String mountMode) {
         this.mountMode = mountMode;
         return self();
      }

      /**
       * @see VirtualGuestBlockDevice#getBootableFlag()
       */
      public T bootableFlag(int bootableFlag) {
         this.bootableFlag = bootableFlag;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getDevice()
       */
      public T device(String device) {
         this.device = device;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDevice#getVirtualDiskImage()
       */
      public T virtualDiskImage(VirtualDiskImage virtualDiskImage) {
         this.virtualDiskImage = virtualDiskImage;
         return self();
      }

      public T guest(VirtualGuest guest) {
         this.guest = guest;
         return self();
      }

      public VirtualGuestBlockDevice build() {
         return new VirtualGuestBlockDevice(id, uuid, statusId, mountType, mountMode, bootableFlag, device,
                 virtualDiskImage, guest);
      }

      public T fromVirtualGuestBlockDevice(VirtualGuestBlockDevice in) {
         return this
               .id(in.getId())
               .uuid(in.getUuid())
               .statusId(in.getStatusId())
               .mountMode(in.getMountMode())
               .mountType(in.getMountType())
               .bootableFlag(in.getBootableFlag())
               .device(in.getDevice())
               .virtualDiskImage(in.getVirtualDiskImage())
               .guest(in.getVirtualGuest());
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
   private final int statusId;
   private final String mountType;
   private final String mountMode;
   private final int bootableFlag;
   private final String device;
   private final VirtualDiskImage virtualDiskImage;
   private final VirtualGuest guest;

   @ConstructorProperties({
           "id", "uuid", "statusId", "mountType", "mountMode", "bootableFlag", "device", "diskImage", "guest"
   })
   protected VirtualGuestBlockDevice(int id, String uuid, int statusId, String mountType, String mountMode,
                                     int bootableFlag, String device, VirtualDiskImage virtualDiskImage,
                                     VirtualGuest guest) {
      this.id = id;
      this.uuid = uuid;
      this.statusId = statusId;
      this.mountType = mountType;
      this.mountMode = mountMode;
      this.bootableFlag = bootableFlag;
      this.device = device;
      this.virtualDiskImage = virtualDiskImage;
      this.guest = guest;
   }

   public int getId() {
      return id;
   }

   public String getUuid() {
      return uuid;
   }

   public int getStatusId() {
      return statusId;
   }

   public String getMountType() {
      return mountType;
   }

   public String getMountMode() {
      return mountMode;
   }

   public int getBootableFlag() {
      return bootableFlag;
   }

   public String getDevice() {
      return device;
   }

   public VirtualDiskImage getVirtualDiskImage() {
      return virtualDiskImage;
   }

   public VirtualGuest getVirtualGuest() {
      return guest;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualGuestBlockDevice that = (VirtualGuestBlockDevice) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.uuid, that.uuid) &&
              Objects.equal(this.statusId, that.statusId) &&
              Objects.equal(this.mountType, that.mountType) &&
              Objects.equal(this.mountMode, that.mountMode) &&
              Objects.equal(this.bootableFlag, that.bootableFlag) &&
              Objects.equal(this.device, that.device) &&
              Objects.equal(this.virtualDiskImage, that.virtualDiskImage) &&
              Objects.equal(this.guest, that.guest);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, uuid, statusId, mountType, mountMode, bootableFlag,
              device, virtualDiskImage, guest);
   }

   @Override
   public String toString() {
      return "VirtualGuestBlockDevice{" +
              "id=" + id +
              ", uuid='" + uuid + '\'' +
              ", statusId=" + statusId +
              ", mountType='" + mountType + '\'' +
              ", mountMode='" + mountMode + '\'' +
              ", bootableFlag=" + bootableFlag +
              ", device=" + device +
              ", virtualDiskImage=" + virtualDiskImage +
              ", guest=" + guest +
              '}';
   }
}
