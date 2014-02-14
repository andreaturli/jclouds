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

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

/**
 * @author Andrea Turli
 */
public class ContainerVirtualGuestConfiguration {

   public static final String SWAP_DEVICE = "1";

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromContainerVirtualGuestConfiguration(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Set<ContainerVirtualGuestConfigurationOption> blockDevices;
      protected Set<ContainerVirtualGuestConfigurationOption> datacenters;
      protected Set<ContainerVirtualGuestConfigurationOption> memory;
      protected Set<ContainerVirtualGuestConfigurationOption> networkComponents;
      protected Set<ContainerVirtualGuestConfigurationOption> operatingSystems;
      protected Set<ContainerVirtualGuestConfigurationOption> processors;

      public T blockDevices(Set<ContainerVirtualGuestConfigurationOption> blockDevices) {
         this.blockDevices = ImmutableSet.copyOf(checkNotNull(blockDevices, "blockDevices"));
         return self();
      }

      public T blockDevices(ContainerVirtualGuestConfigurationOption... in) {
         return blockDevices(ImmutableSet.copyOf(in));
      }

      public T datacenters(Set<ContainerVirtualGuestConfigurationOption> datacenters) {
         this.datacenters = ImmutableSet.copyOf(checkNotNull(datacenters, "datacenters"));
         return self();
      }

      public T datacenters(ContainerVirtualGuestConfigurationOption... in) {
         return datacenters(ImmutableSet.copyOf(in));
      }

      public T memory(Set<ContainerVirtualGuestConfigurationOption> memory) {
         this.memory = ImmutableSet.copyOf(checkNotNull(memory, "memory"));
         return self();
      }

      public T memory(ContainerVirtualGuestConfigurationOption... in) {
         return memory(ImmutableSet.copyOf(in));
      }

      public T networkComponents(Set<ContainerVirtualGuestConfigurationOption> networkComponents) {
         this.networkComponents = ImmutableSet.copyOf(checkNotNull(networkComponents, "networkComponents"));
         return self();
      }

      public T networkComponents(ContainerVirtualGuestConfigurationOption... in) {
         return networkComponents(ImmutableSet.copyOf(in));
      }

      public T operatingSystems(Set<ContainerVirtualGuestConfigurationOption> operatingSystems) {
         this.operatingSystems = ImmutableSet.copyOf(checkNotNull(operatingSystems, "operatingSystems"));
         return self();
      }

      public T operatingSystems(ContainerVirtualGuestConfigurationOption... in) {
         return operatingSystems(ImmutableSet.copyOf(in));
      }

      public T processors(Set<ContainerVirtualGuestConfigurationOption> processors) {
         this.processors = ImmutableSet.copyOf(checkNotNull(processors, "processors"));
         return self();
      }

      public T processors(ContainerVirtualGuestConfigurationOption... in) {
         return processors(ImmutableSet.copyOf(in));
      }

      public ContainerVirtualGuestConfiguration build() {
         return new ContainerVirtualGuestConfiguration(blockDevices, datacenters, memory, networkComponents,
                 operatingSystems, processors);
      }

      public T fromContainerVirtualGuestConfiguration(ContainerVirtualGuestConfiguration in) {
         return this
                 .blockDevices(in.getBlockDevices())
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

   private final Set<ContainerVirtualGuestConfigurationOption> blockDevices;
   private final Set<ContainerVirtualGuestConfigurationOption> datacenters;
   private final Set<ContainerVirtualGuestConfigurationOption> memory;
   private final Set<ContainerVirtualGuestConfigurationOption> networkComponents;
   private final Set<ContainerVirtualGuestConfigurationOption> operatingSystems;
   private final Set<ContainerVirtualGuestConfigurationOption> processors;

   @ConstructorProperties({
           "blockDevices", "datacenters", "memory", "networkComponents", "operatingSystems", "processors"
   })
   public ContainerVirtualGuestConfiguration(Set<ContainerVirtualGuestConfigurationOption> blockDevices,
                                             Set<ContainerVirtualGuestConfigurationOption> datacenters,
                                             Set<ContainerVirtualGuestConfigurationOption> memory,
                                             Set<ContainerVirtualGuestConfigurationOption> networkComponents,
                                             Set<ContainerVirtualGuestConfigurationOption> operatingSystems,
                                             Set<ContainerVirtualGuestConfigurationOption> processors) {
      this.blockDevices = blockDevices;
      this.datacenters = datacenters;
      this.memory = memory;
      this.networkComponents = networkComponents;
      this.operatingSystems = operatingSystems;
      this.processors = processors;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getBlockDevices() {
      return blockDevices;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getDatacenters() {
      return datacenters;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getMemory() {
      return memory;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getNetworkComponents() {
      return networkComponents;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getOperatingSystems() {
      return operatingSystems;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getProcessors() {
      return processors;
   }

   public Set<Integer> getCpusOfProcessors() {
      if(processors.isEmpty()) return ImmutableSet.of();
      return Sets.newHashSet(Iterables.transform(processors, new Function<ContainerVirtualGuestConfigurationOption,
              Integer>() {
         @Override
         public Integer apply(ContainerVirtualGuestConfigurationOption input) {
            return input.getTemplate().getStartCpus();
         }
      }));
   }

   public Set<Integer> getMemories() {
      if(memory.isEmpty()) return ImmutableSet.of();
      return Sets.newHashSet(Iterables.transform(memory, new Function<ContainerVirtualGuestConfigurationOption,
              Integer>() {
         @Override
         public Integer apply(ContainerVirtualGuestConfigurationOption input) {
            return input.getTemplate().getMaxMemory();
         }
      }));
   }

   public Set<Datacenter> getVirtualGuestDatacenters() {
      if(datacenters.isEmpty()) return ImmutableSet.of();
      return Sets.newHashSet(Iterables.transform(datacenters, new Function<ContainerVirtualGuestConfigurationOption,
              Datacenter>() {
         @Override
         public Datacenter apply(ContainerVirtualGuestConfigurationOption input) {
            return input.getTemplate().getDatacenter();
         }
      }));
   }

   public Set<OperatingSystem> getVirtualGuestOperatingSystems() {
      if(operatingSystems.isEmpty()) return ImmutableSet.of();
      return Sets.newHashSet(Iterables.transform(operatingSystems,
              new Function<ContainerVirtualGuestConfigurationOption,
              OperatingSystem>() {
         @Override
         public OperatingSystem apply(ContainerVirtualGuestConfigurationOption input) {
            return OperatingSystem.builder()
                    .id(input.getTemplate().getOperatingSystemReferenceCode())
                    .operatingSystemReferenceCode(input.getTemplate().getOperatingSystemReferenceCode())
                    .build();
         }
      }));
   }

   public Set<VirtualGuestBlockDevice> getVirtualGuestBlockDevices() {
      if(blockDevices.isEmpty()) return ImmutableSet.of();
      Set<VirtualGuestBlockDevice> virtualGuestBlockDevices = Sets.newHashSet();
      for (final ContainerVirtualGuestConfigurationOption configurationOption : blockDevices) {

         virtualGuestBlockDevices.addAll(FluentIterable.from(configurationOption.getTemplate().getVirtualGuestBlockDevices())
                 .filter(new Predicate<VirtualGuestBlockDevice>() {
                    @Override
                    public boolean apply(VirtualGuestBlockDevice input) {
                       return !input.getDevice().equals(SWAP_DEVICE);
                    }
                 })
                 .transform(new Function<VirtualGuestBlockDevice, VirtualGuestBlockDevice>() {
                    @Override
                    public VirtualGuestBlockDevice apply(VirtualGuestBlockDevice input) {
                       return input.toBuilder().guest(configurationOption.getTemplate()).build();
                    }
                 })
                 .toSet());
      }
      return virtualGuestBlockDevices;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ContainerVirtualGuestConfiguration that = (ContainerVirtualGuestConfiguration) o;

      return Objects.equal(this.blockDevices, that.blockDevices) &&
              Objects.equal(this.datacenters, that.datacenters) &&
              Objects.equal(this.memory, that.memory) &&
              Objects.equal(this.networkComponents, that.networkComponents) &&
              Objects.equal(this.operatingSystems, that.operatingSystems) &&
              Objects.equal(this.processors, that.processors);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(blockDevices, datacenters, memory, networkComponents, operatingSystems,
              processors);
   }

   @Override
   public String toString() {
      return "ContainerVirtualGuestConfiguration{" +
              "blockDevices=" + blockDevices +
              ", datacenters=" + datacenters +
              ", memory=" + memory +
              ", networkComponents=" + networkComponents +
              ", operatingSystems=" + operatingSystems +
              ", processors=" + processors +
              '}';
   }
}
