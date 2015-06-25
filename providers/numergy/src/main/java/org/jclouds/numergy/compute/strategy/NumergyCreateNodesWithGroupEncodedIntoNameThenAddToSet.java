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
package org.jclouds.numergy.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.jclouds.ssh.SshKeys.fingerprintPrivateKey;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.functions.AllocateAndAddFloatingIpToNode;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class NumergyCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
        ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet {

   @javax.annotation.Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public NumergyCreateNodesWithGroupEncodedIntoNameThenAddToSet(CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy, ListNodesStrategy listNodesStrategy, GroupNamingConvention.Factory namingConvention, CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, AllocateAndAddFloatingIpToNode createAndAddFloatingIpToNode, LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupCache, LoadingCache<RegionAndName, KeyPair> keyPairCache, NovaApi novaApi) {
      super(addNodeWithTagStrategy, listNodesStrategy, namingConvention, customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory, userExecutor, createAndAddFloatingIpToNode, securityGroupCache, keyPairCache, novaApi);
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
                                                 Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      Template mutableTemplate = template.clone();

      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(mutableTemplate.getOptions());

      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      String region = mutableTemplate.getLocation().getId();

      if (templateOptions.shouldAutoAssignFloatingIp()) {
         checkArgument(novaApi.getFloatingIPApi(region).isPresent(),
                 "Floating IPs are required by options, but the extension is not available! options: %s",
                 templateOptions);
      }

      boolean keyPairExtensionPresent = novaApi.getKeyPairApi(region).isPresent();
      if (templateOptions.shouldGenerateKeyPair()) {
         checkArgument(keyPairExtensionPresent,
                 "Key Pairs are required by options, but the extension is not available! options: %s", templateOptions);
         KeyPair keyPair = keyPairCache.getUnchecked(RegionAndName.fromRegionAndName(region, namingConvention.create()
                 .sharedNameForGroup(group)));
         keyPairCache.asMap().put(RegionAndName.fromRegionAndName(region, keyPair.getName()), keyPair);
         templateOptions.keyPairName(keyPair.getName());
      } else if (templateOptions.getKeyPairName() != null) {
         checkArgument(keyPairExtensionPresent,
                 "Key Pairs are required by options, but the extension is not available! options: %s", templateOptions);
         if (templateOptions.getLoginPrivateKey() != null) {
            String pem = templateOptions.getLoginPrivateKey();
            KeyPair keyPair = KeyPair.builder().name(templateOptions.getKeyPairName())
                    .fingerprint(fingerprintPrivateKey(pem)).privateKey(pem).build();
            keyPairCache.asMap().put(RegionAndName.fromRegionAndName(region, keyPair.getName()), keyPair);
         }
      }

      templateOptions.userMetadata(ComputeServiceConstants.NODE_GROUP_KEY, group);

      return doExecute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }

   private Map<?, ListenableFuture<Void>> doExecute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
                                                 Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      Map<String, ListenableFuture<Void>> responses = newLinkedHashMap();
      for (String name : getNextNames(group, template, count)) {
         responses.put(name, Futures.transform(createNodeInGroupWithNameAndTemplate(group, name, template),
                 customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory.create(template.getOptions(), goodNodes,
                         badNodes, customizationResponses), userExecutor));
      }
      return responses;
   }


}
