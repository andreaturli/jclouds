/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.softlayer.compute;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;
import org.jclouds.softlayer.SoftLayerApi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

/**
 * @author Andrea Turli
 */
@Singleton
public class SoftLayerComputeService extends BaseComputeService {
   protected final SoftLayerApi api;
   /*
   protected final LoadingCache<ZoneAndName, SecurityGroup> securityGroupMap;
   protected final LoadingCache<String, SshKeyPair> keyPairCache;
   protected final Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByZoneId;
   protected final GroupNamingConvention.Factory namingConvention;
   protected final Supplier<LoadingCache<String, Zone>> zoneIdToZone;
   */

   @Inject
   protected SoftLayerComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
                                     @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
                                     @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
                                     GetImageStrategy getImageStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
                                     CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
                                     DestroyNodeStrategy destroyNodeStrategy, ResumeNodeStrategy startNodeStrategy,
                                     SuspendNodeStrategy stopNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
                                     @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
                                     @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
                                     @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
                                     @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
                                     InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
                                     RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
                                     PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
                                     @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                     SoftLayerApi api,
                                     /*LoadingCache<ZoneAndName, SecurityGroup> securityGroupMap,
                                     LoadingCache<String, SshKeyPair> keyPairCache,
                                     Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByZoneId,
                                     GroupNamingConvention.Factory namingConvention,
                                     Supplier<LoadingCache<String, Zone>> zoneIdToZone,*/
                                     Optional<ImageExtension> imageExtension,
                                     Optional<SecurityGroupExtension> securityGroupExtension) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
               getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               startNodeStrategy, stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
               persistNodeCredentials, timeouts, userExecutor, imageExtension, securityGroupExtension);
      this.api = checkNotNull(api, "api");
      /*
      this.zoneIdToZone = checkNotNull(zoneIdToZone, "zoneIdToZone");
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache");
      this.orphanedGroupsByZoneId = checkNotNull(orphanedGroupsByZoneId, "orphanedGroupsByZoneId");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
      */
   }

}
