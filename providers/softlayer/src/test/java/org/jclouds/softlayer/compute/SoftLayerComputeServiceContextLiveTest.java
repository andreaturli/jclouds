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

import com.google.common.collect.ImmutableSet;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.inject.Named;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", testName = "SoftLayerComputeServiceContextLiveTest")
public class SoftLayerComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public SoftLayerComputeServiceContextLiveTest() {
      provider = "softlayer";
   }

   @Test
   public void testLaunchClusterWithMinDisk() throws RunNodesException {
      int numNodes = 1;
      final String name = "node";

      ComputeServiceContext context = ContextBuilder.newBuilder("softlayer").credentials(identity, credential)
              .modules(ImmutableSet.of(new SLF4JLoggingModule(),
                      new SshjSshClientModule()))
              .build(ComputeServiceContext.class);

      for(Hardware hardware : context.getComputeService().listHardwareProfiles()) {
         logger.info("hardware: ", hardware);
      }

      for(Image image : context.getComputeService().listImages()) {
         logger.info("image: ", image);
      }

      Image image = context.getComputeService().getImage("UBUNTU_8_64");
      logger.info("UBUNTU_8_64 image: ", image);

      for(ComputeMetadata node : context.getComputeService().listNodes()) {
         logger.info("node: ", node);
      }

      TemplateBuilder templateBuilder = context.getComputeService().templateBuilder();
      //templateBuilder.minDisk(15d);
      //templateBuilder.hardwareId("cpu=1,memory=4096,disk=100,type=SAN");
      //templateBuilder.hardwareId("cpu=1,memory=4096,disk=100,type=LOCAL");
      templateBuilder.imageId("UBUNTU_12_64");
      //templateBuilder.imageId("7bcd78dc-eb11-4e1b-8d93-111c62ed5fd1");
      templateBuilder.locationId("dal01");
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

         context.getComputeService().destroyNode(node.getId());
      }
   }

}
