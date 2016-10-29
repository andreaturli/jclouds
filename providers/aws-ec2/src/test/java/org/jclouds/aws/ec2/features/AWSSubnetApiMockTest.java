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

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.aws.ec2.options.CreateSubnetOptions;
import org.jclouds.ec2.domain.Subnet;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "AWSSubnetApiMockTest", singleThreaded = true)
public class AWSSubnetApiMockTest extends BaseAWSEC2ApiMockTest {

   public void createSubnetInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<CreateSubnetResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-09-15/\">\n" +
              "  <requestId>7a62c49f-347e-4fc4-9331-6e8eEXAMPLE</requestId>\n" +
              "  <subnet>\n" +
              "    <subnetId>subnet-9d4a7b6c</subnetId>\n" +
              "    <state>pending</state>\n" +
              "    <vpcId>vpc-1a2b3c4d</vpcId>\n" +
              "    <cidrBlock>10.0.1.0/24</cidrBlock> \n" +
              "    <availableIpAddressCount>251</availableIpAddressCount>\n" +
              "    <availabilityZone>us-east-1a</availabilityZone>\n" +
              "    <tagSet/>\n" +
              "  </subnet>\n" +
              "</CreateSubnetResponse>"));

      Subnet result = subnetApi().createSubentInRegion(DEFAULT_REGION, "vpc-1a2b3c4d", "10.0.1.0/24");
      assertEquals(result.getVpcId(), "vpc-1a2b3c4d");
      assertEquals(result.getCidrBlock(), "10.0.1.0/24");
      assertEquals(result.getAvailabilityZone(), "us-east-1a");
      assertEquals(result.getSubnetId(), "subnet-9d4a7b6c");
      assertEquals(result.getSubnetState().value(), "pending");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateSubnet&VpcId=vpc-1a2b3c4d&CidrBlock=10.0.1.0/24");
   }

   public void createSubnetInRegion_options() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setBody("<CreateSubnetResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-09-15/\">\n" +
                    "  <requestId>7a62c49f-347e-4fc4-9331-6e8eEXAMPLE</requestId>\n" +
                    "  <subnet>\n" +
                    "    <subnetId>subnet-9d4a7b6c</subnetId>\n" +
                    "    <state>pending</state>\n" +
                    "    <vpcId>vpc-1a2b3c4d</vpcId>\n" +
                    "    <cidrBlock>10.0.1.0/24</cidrBlock> \n" +
                    "    <availableIpAddressCount>251</availableIpAddressCount>\n" +
                    "    <availabilityZone>us-east-1a</availabilityZone>\n" +
                    "    <tagSet/>\n" +
                    "  </subnet>\n" +
                    "</CreateSubnetResponse>"));

      Subnet result = subnetApi().createSubentInRegion(DEFAULT_REGION, "vpc-1a2b3c4d", "10.0.1.0/24",
            new CreateSubnetOptions().dryRun().availabilityZone("us-east-1a"));
      assertEquals(result.getVpcId(), "vpc-1a2b3c4d");
      assertEquals(result.getCidrBlock(), "10.0.1.0/24");
      assertEquals(result.getAvailabilityZone(), "us-east-1a");
      assertEquals(result.getSubnetId(), "subnet-9d4a7b6c");
      assertEquals(result.getSubnetState().value(), "pending");
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateSubnet&VpcId=vpc-1a2b3c4d&CidrBlock=10.0.1.0/24&DryRun=true&AvailabilityZone=us-east-1a");
   }

   private AWSSubnetApi subnetApi() {
      return api().getAWSSubnetApi().get();
   }
}
