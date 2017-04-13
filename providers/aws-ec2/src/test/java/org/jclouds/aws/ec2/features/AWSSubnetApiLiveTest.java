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
package org.jclouds.aws.ec2.features;

import static org.testng.Assert.assertTrue;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.domain.Subnet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

/**
 * Tests behavior of {@code VPCApi}
 */
@Test(groups = "live", singleThreaded = true)
public class AWSSubnetApiLiveTest extends BaseComputeServiceContextLiveTest {

   public AWSSubnetApiLiveTest() {
      provider = "aws-ec2";
   }

   private AWSEC2Api api;
   private Subnet subnet;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      api = view.unwrapApi(AWSEC2Api.class);
   }

   @Test
   public void testCreateSubnetInRegion() {
//      subnet = subnetApi().createSubnetInRegion();
//      assertNotNull(subnet);
   }

   @Test(dependsOnMethods = "testCreateSubnetInRegion")
   public void testGet() {
      FluentIterable<Subnet> subnets = subnetApi().list();
      assertTrue(subnets.toList().size() == 1);
   }

   @Test(dependsOnMethods = "testCreateSubnetInRegion")
   public void testList() {
      FluentIterable<Subnet> subnets = subnetApi().list();
      assertTrue(subnets.toList().size() == 1);
   }

   @Test(dependsOnMethods = {"testList", "testGet"}, alwaysRun = true)
   public void testDelete() {
      if (subnet != null) {
//         assertTrue(subnetApi().deleteSubnet(null, vpc.id()));
      }
   }

   private AWSSubnetApi subnetApi() {
      Optional<? extends AWSSubnetApi> subnetOption = api.getAWSSubnetApi();
      if (!subnetOption.isPresent()) Assert.fail();
      return subnetOption.get();
   }

}
