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

import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "NumergyComputeServiceContextLiveTest")
public class NumergyComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public NumergyComputeServiceContextLiveTest() {
      provider = "numergy-compute";
   }

   @Test
   public void testLaunchNode() throws RunNodesException {
      int numNodes = 2;
      final String group = "group-" + new Random().nextInt(100);
      ComputeServiceContext context = ContextBuilder.newBuilder("numergy-compute").credentials(identity, credential)
              .modules(ImmutableSet.of(new SLF4JLoggingModule(),
                      new SshjSshClientModule()))
              .build(ComputeServiceContext.class);

      TemplateBuilder templateBuilder = context.getComputeService().templateBuilder();

      Template template = templateBuilder.build();
      // test passing custom options
      NovaTemplateOptions options = template.getOptions().as(NovaTemplateOptions.class);
      options.inboundPorts(22, 8080)
             .runScript(AdminAccess.standard());

      // Numergy-specific
      options.overrideLoginUser("stack");
      options.floatingIpPoolNames(ImmutableSet.of("PublicNetwork-02"));

      Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup(group, numNodes, template);

      for (NodeMetadata node : nodes) {
         try {
            SshClient client = context.utils().sshForNode().apply(node);
            client.connect();
            ExecResponse hello = client.exec("sudo apt-get update");
            logger.debug(hello.getOutput().trim());
         } finally {
            context.getComputeService().destroyNode(node.getId());
         }
      }


   }

}
