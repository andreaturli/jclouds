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
package org.jclouds.softlayer.compute;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.VirtualGuestConfiguration;
import org.jclouds.softlayer.features.BaseSoftLayerApiLiveTest;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.inject.Named;
import java.util.Random;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", testName = "SoftLayerExperimentLiveTest")
public class SoftLayerExperimentLiveTest extends BaseSoftLayerApiLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   ComputeServiceContext context;

   @BeforeClass
   public void setUp() {
      context = ContextBuilder.newBuilder("softlayer").modules(
               ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule())).build(
               ComputeServiceContext.class);
   }

   @Test
   public void testLaunchClusterWithMinDisk() throws RunNodesException {
      int numNodes = 1;
      final String name = "node";

      for (Hardware hardware : context.getComputeService().listHardwareProfiles()) {
         System.out.println(hardware);
      }

      //for(VirtualGuestConfiguration virtualGuestConfiguration : context.unwrapApi(SoftLayerApi.class).getVirtualGuestApi().getCreateObjectOptions()) {
      //   System.out.println(virtualGuestConfiguration);
      //}

      TemplateBuilder templateBuilder = context.getComputeService().templateBuilder();
      templateBuilder.minDisk(100d);
      templateBuilder.imageId("UBUNTU_12_64");
      Template template = templateBuilder.build();
      // test passing custom options
      template.getOptions().as(SoftLayerTemplateOptions.class).domainName("live.org");

      Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup(name, numNodes, template);
      assertEquals(numNodes, nodes.size(), "wrong number of nodes");
      for (NodeMetadata node : nodes) {
         logger.debug("Created Node: %s", node);
         SshClient client = context.utils().sshForNode().apply(node);
         client.connect();
         ExecResponse hello = client.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      }
      context.getComputeService().destroyNodesMatching(new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return input.getId().contains(name);
         }
      });
   }

}
