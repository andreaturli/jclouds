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
package org.jclouds.softlayer.domain.internal;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrea Turli
 */
public class HardwareProfile {

   public enum DiskType {
      LOCAL, SAN
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHardwareProfile(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected double cpus;
      protected double coreSpeed;
      protected int memory;
      protected float diskCapacity;
      protected DiskType diskType;
      protected int nicSpeed;

      public T id(String id) {
         this.id = id;
         return self();
      }

      public T cpus(double cpus) {
         this.cpus = cpus;
         return self();
      }

      public T coreSpeed(double coreSpeed) {
         this.coreSpeed = coreSpeed;
         return self();
      }

      public T memory(int memory) {
         this.memory = memory;
         return self();
      }

      public T diskCapacity(float diskCapacity) {
         this.diskCapacity = diskCapacity;
         return self();
      }

      public T diskType(DiskType diskType) {
         this.diskType = diskType;
         return self();
      }

      public T nicSpeed(int nicSpeed) {
         this.nicSpeed = nicSpeed;
         return self();
      }

      public HardwareProfile build() {
         return new HardwareProfile(id, cpus, coreSpeed, memory, diskCapacity, diskType, nicSpeed);
      }

      public T fromHardwareProfile(HardwareProfile in) {
         return this
                 .id(in.getId())
               .cpus(in.getCpus())
               .memory(in.getMemory())
               .diskCapacity(in.getDiskCapacity())
               .diskType(in.getDiskType())
               .nicSpeed(in.getNicSpeed());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final double cpus;
   private final double coreSpeed;
   private final int memory;
   private final float diskCapacity;
   private final DiskType diskType;
   private final int nicSpeed;

   public HardwareProfile(String id, double cpus, double coreSpeed, int memory, float diskCapacity, DiskType diskType,
                          int nicSpeed) {
      this.id = id;
      this.cpus = cpus;
      this.coreSpeed = coreSpeed;
      this.memory = memory;
      this.diskCapacity = diskCapacity;
      this.diskType = diskType;
      this.nicSpeed = nicSpeed;
   }

   public String getId() {
      return id;
   }

   public double getCpus() {
      return cpus;
   }

   public double getCoreSpeed() {
      return coreSpeed;
   }

   public int getMemory() {
      return memory;
   }

   public float getDiskCapacity() {
      return diskCapacity;
   }

   public DiskType getDiskType() {
      return diskType;
   }

   public int getNicSpeed() {
      return nicSpeed;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      HardwareProfile that = HardwareProfile.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("cpus", cpus)
              .add("coreSpeed", coreSpeed)
              .add("memory", memory)
              .add("diskCapacity", diskCapacity)
              .add("diskType", diskType)
              .add("nicSpeed", nicSpeed)
              .toString();
   }
}
