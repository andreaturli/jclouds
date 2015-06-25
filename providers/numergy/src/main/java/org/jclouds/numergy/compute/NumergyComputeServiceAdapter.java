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
package org.jclouds.numergy.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.NeutronApiMetadata;
import org.jclouds.openstack.neutron.v2.domain.FirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.FirewallRule;
import org.jclouds.openstack.neutron.v2.extensions.FWaaSApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v2_0.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v2_0.compute.functions.ServerInRegionToNodeMetadata;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ServerInRegion;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class NumergyComputeServiceAdapter extends NovaComputeServiceAdapter {

   public static final String JCLOUDS_FW_POLICY_PATTERN = "jclouds-fw-policy-%s";
   public static final String JCLOUDS_FW_RULE_PATTERN = "jclouds-fw-rule-%s_port-%d";

   private FWaaSApi fWaaSApi;
   private final GroupNamingConvention nodeNamingConvention;
   private final Supplier<URI> endpoint;
   private final Supplier<Credentials> creds;

   @Inject
   public NumergyComputeServiceAdapter(NovaApi novaApi, @Region Supplier<Set<String>> regionIds,
                                       RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate,
                                       LoadingCache<RegionAndName, KeyPair> keyPairCache, GroupNamingConvention.Factory namingConvention,
                                       @Provider Supplier<URI> endpoint, @Provider Supplier<Credentials> creds) {
      super(novaApi, regionIds, removeFloatingIpFromNodeAndDeallocate, keyPairCache);
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.endpoint = checkNotNull(endpoint, "endpoint");
      this.creds = checkNotNull(creds, "creds");
   }

   @Override
   public NodeAndInitialCredentials<ServerInRegion> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      NodeAndInitialCredentials<ServerInRegion> nodeAndInitialCredentials = super.createNodeWithGroupEncodedIntoName(group, name, template);

      String regionId = template.getLocation().getId();

      Server server = novaApi.getServerApi(regionId).get(nodeAndInitialCredentials.getNode().getServer().getId());
      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(template.getOptions());

      Set<String> addresses = FluentIterable.from(newHashSet(server.getAddresses().values()))
              .filter(ServerInRegionToNodeMetadata.isPrivateAddress)
              .transform(ServerInRegionToNodeMetadata.AddressToStringTransformationFunction.INSTANCE)
              .filter(ServerInRegionToNodeMetadata.isInet4Address).toSet();

      NeutronApi neutronApi = ContextBuilder.newBuilder(new NeutronApiMetadata())
              .endpoint(endpoint.get().toASCIIString())
              .credentials(creds.get().identity, creds.get().credential)
              .modules(ImmutableSet.<Module>of(
                      new SshjSshClientModule(),
                      new SLF4JLoggingModule(),
                      new BouncyCastleCryptoModule()))
              .buildApi(NeutronApi.class);
      fWaaSApi = neutronApi.getFWaaSApi(regionId).get();

      FirewallPolicy firewallPolicy = fWaaSApi.createFirewallPolicy(FirewallPolicy.builder()
              .name(String.format(JCLOUDS_FW_POLICY_PATTERN, name))
              .build());


      for (String address : addresses) {
         for (int inboundPort : templateOptions.getInboundPorts()) {
            FirewallRule firewallRule = fWaaSApi.createFirewallRule(FirewallRule.builder()
                    .name(String.format(JCLOUDS_FW_RULE_PATTERN, name, inboundPort))
                    .destinationIpAddress(address)
                    .destinationPort(String.valueOf(inboundPort))
                    .enabled(true)
                    .action("allow")
                    .protocol("tcp")
                    .build());
            fWaaSApi.insertFirewallRuleToPolicy(firewallPolicy.getId(), firewallRule.getId());
         }
      }
      return nodeAndInitialCredentials;
   }

   @Override
   public void destroyNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      if (novaApi.getFloatingIPApi(regionAndId.getRegion()).isPresent()) {
         try {
            removeFloatingIpFromNodeAndDeallocate.apply(regionAndId);
         } catch (RuntimeException e) {
            logger.warn(e, "<< error removing and deallocating ip from node(%s): %s", id, e.getMessage());
         }
      }

      Server server = novaApi.getServerApi(regionAndId.getRegion()).get(regionAndId.getId());
      novaApi.getServerApi(regionAndId.getRegion()).delete(regionAndId.getId());

      String nodeName = server.getName();

      for (FirewallPolicy firewallPolicy : fWaaSApi.listFirewallPolicies().concat().toList()) {
         if (!firewallPolicy.isShared() && firewallPolicy.getName().equals(String.format(JCLOUDS_FW_POLICY_PATTERN, nodeName))) {
            List<String> firewallRuleIds = firewallPolicy.getFirewallRules();
            fWaaSApi.deleteFirewallPolicy(firewallPolicy.getId());
            for (String firewallRuleId : firewallRuleIds) {
               fWaaSApi.deleteFirewallRule(firewallRuleId);
            }
         }
      }
   }
}
