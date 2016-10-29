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

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.options.CreateSubnetOptions;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.features.SubnetApi;
import org.jclouds.ec2.xml.SubnetHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon EC2 via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html"
 *      >doc</a>
 */
@SinceApiVersion("2011-01-01")
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AWSSubnetApi extends SubnetApi {

   /**
    * Creates a subnet in an existing VPC.
    * 
    * @param region
    * @param vpcId The ID of the VPC.
    * @param cidrBlock The network range for the subnet, in CIDR notation. For example, 10.0.0.0/24.
    * @param options
    * @return AWS Subnet
    */
   @Named("CreateSubnet")
   @POST
   @Path("/")
   @XMLResponseParser(SubnetHandler.class)
   @FormParams(keys = ACTION, values = "CreateSubnet")
   Subnet createSubentInRegion(
           @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
           @FormParam("VpcId") String vpcId, @FormParam("CidrBlock") String cidrBlock,
           CreateSubnetOptions... options);

}
