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

import java.util.List;


public class BlockDeviceOption {

   private final ItemPrice itemPrice;
   private final BlockDeviceTemplate template;

   public BlockDeviceOption(ItemPrice itemPrice, BlockDeviceTemplate template) {
      this.itemPrice = itemPrice;
      this.template = template;
   }

   public Float getSize() {
      final BlockDeviceTemplate.BlockDevice blockDevice = template.blockDevices.get(0);
      if(blockDevice == null)
         return (float) 0;
      else
         return blockDevice.diskImage.capacity;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("itemPrice", itemPrice)
              .add("template", template)
              .toString();
   }

   public class BlockDeviceTemplate {

      private final List<BlockDevice> blockDevices;
      private final boolean localDiskFlag;

      public BlockDeviceTemplate(List<BlockDevice> blockDevices, boolean localDiskFlag) {
         this.blockDevices = blockDevices;
         this.localDiskFlag = localDiskFlag;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
                 .add("blockDevices", blockDevices)
                 .add("localDiskFlag", localDiskFlag)
                 .toString();
      }

      private class BlockDevice {
         private final String device;
         private final DiskImage diskImage;

         private BlockDevice(String device, DiskImage diskImage) {
            this.device = device;
            this.diskImage = diskImage;
         }

         @Override
         public String toString() {
            return Objects.toStringHelper(this)
                    .add("device", device)
                    .add("diskImage", diskImage)
                    .toString();
         }
      }

      private class DiskImage {
         private final Float capacity;

         private DiskImage(Float capacity) {
            this.capacity = capacity;
         }

         @Override
         public String toString() {
            return Objects.toStringHelper(this)
                    .add("capacity", capacity)
                    .toString();
         }
      }
   }
}
