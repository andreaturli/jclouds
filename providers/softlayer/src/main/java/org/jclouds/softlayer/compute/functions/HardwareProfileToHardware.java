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
package org.jclouds.softlayer.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.internal.HardwareProfile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.compute.domain.Volume.*;
import static org.jclouds.compute.domain.Volume.Type.*;
import static org.jclouds.softlayer.domain.internal.HardwareProfile.*;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.matches;

/**
 * Converts a set of {@code HardwareProfile} to Hardware. All cores have a speed of 2.0Ghz
 *
 * The HardwareId will be a comma separated list containing the prices ids: cpus,ram,volume
 * 
 * @author Andrea Turli
 */
@Singleton
public class HardwareProfileToHardware implements Function<HardwareProfile, Hardware> {

   private static final String GUEST_DISK_CATEGORY_REGEX =  "guest_disk[0-9]";
   private static final String FIRST_GUEST_DISK = "guest_disk0";
   private static final String STORAGE_AREA_NETWORK = "SAN";

   private static final String RAM_CATEGORY = "ram";

   private static final String CPU_DESCRIPTION_REGEX = "(Private )?[0-9]+ x ([.0-9]+) GHz Core[s]?";
   private static final double DEFAULT_CORE_SPEED = 2.0;

   private final Pattern cpuDescriptionRegex;
   private final Pattern diskCategoryRegex;

   @Inject
   public HardwareProfileToHardware() {
      this(Pattern.compile(CPU_DESCRIPTION_REGEX), Pattern.compile(GUEST_DISK_CATEGORY_REGEX));
   }

   public HardwareProfileToHardware(Pattern cpuDescriptionRegex, Pattern diskCategoryRegex) {
      this.cpuDescriptionRegex = checkNotNull(cpuDescriptionRegex, "cpuDescriptionRegex");
      this.diskCategoryRegex = checkNotNull(diskCategoryRegex, "diskCategoryRegex");
   }

   @Override
   public Hardware apply(HardwareProfile hardwareProfile) {
      return new HardwareBuilder().ids(hardwareProfile.getId())
              .processors(ImmutableList.of(new Processor(hardwareProfile.getCpus(), hardwareProfile.getCoreSpeed())))
              .ram(hardwareProfile.getMemory())
              .hypervisor("XenServer")
              .volumes(Lists.<Volume>newArrayList(new VolumeImpl(
                      hardwareProfile.getId(),
                      hardwareProfile.getDiskType() == DiskType.LOCAL ? LOCAL : SAN,
                      hardwareProfile.getDiskCapacity(), null,
                      true,
                      false)))
              .build();
   }

}
