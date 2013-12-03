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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole, Andrea Turli
 */
@Singleton
public class VirtualGuestToNodeMetadata implements Function<VirtualGuest, NodeMetadata> {

   public static final Map<VirtualGuest.State, Status> serverStateToNodeStatus = ImmutableMap
         .<VirtualGuest.State, Status> builder().put(VirtualGuest.State.HALTED, Status.PENDING)
         .put(VirtualGuest.State.PAUSED, Status.SUSPENDED).put(VirtualGuest.State.RUNNING, Status.RUNNING)
         .put(VirtualGuest.State.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   private final Supplier<Set<? extends Location>> locations;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   VirtualGuestToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public NodeMetadata apply(VirtualGuest from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.hostname(from.getHostname());
      if (from.getDatacenter() != null)
         builder.location(from(locations.get()).firstMatch(
               LocationPredicates.idEquals(from.getDatacenter().getId() + "")).orNull());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getHostname()));

      Image image = new VirtualGuestToImage().apply(from);
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
         builder.hardware(new VirtualGuestToHardware().apply(from));
      }
      builder.status(serverStateToNodeStatus.get(from.getPowerState().getKeyName()));

      // These are null for 'bad' guest orders in the HALTED state.
      if (from.getPrimaryIpAddress() != null)
         builder.publicAddresses(ImmutableSet.<String> of(from.getPrimaryIpAddress()));
      if (from.getPrimaryBackendIpAddress() != null)
         builder.privateAddresses(ImmutableSet.<String> of(from.getPrimaryBackendIpAddress()));
      return builder.build();
   }

}
