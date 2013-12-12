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

import java.beans.ConstructorProperties;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.google.common.base.Objects;
import org.jclouds.softlayer.domain.internal.BlockDeviceOption;
import org.jclouds.softlayer.domain.internal.DatacenterOption;
import org.jclouds.softlayer.domain.internal.MemoryOption;
import org.jclouds.softlayer.domain.internal.NetworkComponentsOption;
import org.jclouds.softlayer.domain.internal.OperatingSystemOption;
import org.jclouds.softlayer.domain.internal.ProcessorOption;

/**
 * The guest configuration container is used to provide configuration options for creating computing instances.
 * Each configuration option will include both an itemPrice and a template.
 * The itemPrice value will provide hourly and monthly costs (if either are applicable), and a description of the option.
 * The template will provide a fragment of the request with the properties and values that must be sent when creating a computing instance with the option.
 *
 * @author Andrea Turli
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Container_Virtual_Guest_Configuration"
/>
 */
public class VirtualGuestConfiguration {

   protected final Set<BlockDeviceOption> blockDevices;
   protected final Set<DatacenterOption> datacenters;
   protected final Set<MemoryOption> memory;
   protected final Set<NetworkComponentsOption> networkComponents;
   protected final Set<OperatingSystemOption> operatingSystems;
   protected final Set<ProcessorOption> processors;

   @ConstructorProperties({
           "blockDevices", "datacenters", "memory", "networkComponents", "operatingSystems", "processors"
   })
   protected VirtualGuestConfiguration(Set<BlockDeviceOption> blockDevices, Set<DatacenterOption> datacenters,
                                       Set<MemoryOption> memory, Set<NetworkComponentsOption> networkComponents,
                                       Set<OperatingSystemOption> operatingSystems, Set<ProcessorOption> processors) {
      this.blockDevices = blockDevices;
      this.datacenters = datacenters;
      this.memory = memory;
      this.networkComponents = networkComponents;
      this.operatingSystems = operatingSystems;
      this.processors = processors;
   }

   public Set<BlockDeviceOption> getBlockDeviceOptions() {
      return blockDevices;
   }

   public Set<DatacenterOption> getDatacenters() {
      return datacenters;
   }

   public Set<MemoryOption> getMemory() {
      return memory;
   }

   public Set<NetworkComponentsOption> getNetworkComponents() {
      return networkComponents;
   }

   public Set<OperatingSystemOption> getOperatingSystems() {
      return operatingSystems;
   }

   public Set<ProcessorOption> getProcessors() {
      return processors;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      VirtualGuestConfiguration that = (VirtualGuestConfiguration) o;
      if (blockDevices != null ? !blockDevices.equals(that.blockDevices) : that.blockDevices != null) return false;
      if (datacenters != null ? !datacenters.equals(that.datacenters) : that.datacenters != null) return false;
      if (memory != null ? !memory.equals(that.memory) : that.memory != null) return false;
      if (networkComponents != null ? !networkComponents.equals(that.networkComponents) : that.networkComponents != null)
         return false;
      if (operatingSystems != null ? !operatingSystems.equals(that.operatingSystems) : that.operatingSystems != null)
         return false;
      if (processors != null ? !processors.equals(that.processors) : that.processors != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(blockDevices, datacenters, memory, networkComponents, operatingSystems, processors);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("blockDevices", blockDevices)
              .add("datacenters", datacenters)
              .add("memory", memory)
              .add("networkComponents", networkComponents)
              .add("operatingSystems", operatingSystems)
              .add("processors", processors)
              .toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualGuestConfiguration(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private ImmutableSet.Builder<BlockDeviceOption> blockDevices = ImmutableSet.builder();
      private ImmutableSet.Builder<DatacenterOption> datacenters = ImmutableSet.builder();
      private ImmutableSet.Builder<MemoryOption> memory = ImmutableSet.builder();
      private ImmutableSet.Builder<NetworkComponentsOption> networkComponents = ImmutableSet.builder();
      private ImmutableSet.Builder<OperatingSystemOption> operatingSystems = ImmutableSet.builder();
      private ImmutableSet.Builder<ProcessorOption> processors = ImmutableSet.builder();

      /**
       * @see VirtualGuestConfiguration#getBlockDeviceOptions()
       */
      public T blockDevices(Set<BlockDeviceOption> blockDeviceOptions) {
         this.blockDevices.addAll(blockDeviceOptions);
         return self();
      }

      /**
       * @see VirtualGuestConfiguration#getDatacenters()
       */
      public T datacenters(Set<DatacenterOption> datacenterOptions) {
         this.datacenters.addAll(datacenterOptions);
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestConfiguration#getMemory()
       */
      public T memory(Set<MemoryOption> memoryOptions) {
         this.memory.addAll(memoryOptions);
         return self();
      }

      /**
       * @see VirtualGuestConfiguration#getNetworkComponents()
       */
      public T networkComponents(Set<NetworkComponentsOption> networkComponentsOptions) {
         this.networkComponents.addAll(networkComponentsOptions);
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestConfiguration#getOperatingSystems()
       */
      public T operatingSystems(Set<OperatingSystemOption> operatingSystemOptions) {
         this.operatingSystems.addAll(operatingSystemOptions);
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestConfiguration#getProcessors()
       */
      public T processors(Set<ProcessorOption> processorOptions) {
         this.processors.addAll(processorOptions);
         return self();
      }

      public VirtualGuestConfiguration build() {
         return new VirtualGuestConfiguration(blockDevices.build(), datacenters.build(), memory.build(), networkComponents.build(),
                 operatingSystems.build(), processors.build());
      }

      public T fromVirtualGuestConfiguration(VirtualGuestConfiguration in) {
         return this
                 .blockDevices(in.getBlockDeviceOptions())
                 .datacenters(in.getDatacenters())
                 .memory(in.getMemory())
                 .networkComponents(in.getNetworkComponents())
                 .operatingSystems(in.getOperatingSystems())
                 .processors(in.getProcessors());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}