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

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;
import java.net.URI;
import java.util.Properties;

import org.jclouds.numergy.compute.config.NumergyCloudComputeServiceContextModule;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.config.NovaHttpApiModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ProviderMetadata} for Numergy Cloud Compute Services.
 */
@AutoService(ProviderMetadata.class)
public class NumergyCloudComputeProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public NumergyCloudComputeProviderMetadata() {
      super(builder());
   }

   public NumergyCloudComputeProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = NovaApiMetadata.defaultProperties();
      // deallocating ip addresses can take a while
      properties.setProperty(TIMEOUT_NODE_TERMINATED, 60 * 1000 + "");

      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);

      properties.setProperty(AUTO_ALLOCATE_FLOATING_IPS, "true");
      properties.setProperty(AUTO_GENERATE_KEYPAIRS, "true");
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[24].[01][04],locationId=tr2");


      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("numergy-compute")
         .name("Numergy Cloud Compute Services")
         .apiMetadata(new NovaApiMetadata().toBuilder()
                  .identityName("${tenantName}:${accessKey}")
                  .credentialName("${secretKey}")
                  .version("2")
                  .endpointName("identity service url ending in /v2.0/")
                  .defaultEndpoint("https://cloud.numergy.com/identity/v2.0/")
                  .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                              .add(AuthenticationApiModule.class)
                                              .add(KeystoneAuthenticationModule.class)
                                              .add(RegionModule.class)
                                              .add(NovaParserModule.class)
                                              .add(NovaHttpApiModule.class)
                                              .add(NumergyCloudComputeServiceContextModule.class).build())
                  .build())
         .homepage(URI.create("https://cloud.numergy.com"))
         .console(URI.create("https://cloud.numergy.com/horizon"))
         .linkedServices("numergy-compute")
         .iso3166Codes("FR")
         .endpoint("https://cloud.numergy.com/identity/v2.0/")
         .defaultProperties(NumergyCloudComputeProviderMetadata.defaultProperties());
      }

      @Override
      public NumergyCloudComputeProviderMetadata build() {
         return new NumergyCloudComputeProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
