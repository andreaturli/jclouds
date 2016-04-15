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
package org.jclouds.googlecomputeengine.compute.functions;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.googlecomputeengine.domain.MachineType;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

public final class MachineTypeToHardware implements Function<MachineType, Hardware> {

   @Override
   public Hardware apply(MachineType input) {
      return new HardwareBuilder()
              .id(input.name())
              .providerId(input.id())
              .name(input.name())
              .hypervisor("kvm")
              .processor(new Processor(input.guestCpus(), 1.0))
              .providerId(input.id())
              .ram(input.memoryMb())
              .uri(input.selfLink())
              .volumes(collectVolumes(input))
              .supportsImage(Predicates.<Image>alwaysTrue())
              .build();
   }

   private Iterable<Volume> collectVolumes(MachineType input) {
      ImmutableSet.Builder<Volume> volumes = ImmutableSet.builder();
      for (MachineType.ScratchDisk disk : input.scratchDisks()) {
         volumes.add(new VolumeBuilder()
                 .type(Volume.Type.LOCAL)
                 .size(Float.valueOf(disk.diskGb()))
                 .bootDevice(true)
                 .durable(false).build());
      }
      return volumes.build();
   }
}
