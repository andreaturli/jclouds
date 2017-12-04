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
package org.jclouds.openstack.keystone.config;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.KEYSTONE_VERSION;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.openstack.keystone.filters.AuthenticationRequest;
import org.jclouds.openstack.keystone.filters.KeystoneAuthenticationFilter;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;

public final class KeystoneModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(KeystoneCredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      bind(KeystoneVersions.class).toProvider(KeystoneVersionFromPropertyOrDefault.class);

      install(new KeystoneV2Module());
      install(new KeystoneV3Module());
   }

   @Singleton
   public static class KeystoneVersionFromPropertyOrDefault implements Provider<KeystoneVersions> {
      @Inject(optional = true)
      @Named(KEYSTONE_VERSION)
      String keystoneVersion = KeystoneVersions.V3.toString();

      @Override
      public KeystoneVersions get() {
         return KeystoneVersions.fromValue(keystoneVersion);
      }
   }

   @Singleton
   public static class CredentialTypeFromPropertyOrDefault implements Provider<KeystoneCredentialType> {
      @Inject(optional = true)
      @Named(CREDENTIAL_TYPE)
      String credentialType = KeystoneCredentialType.PASSWORD_CREDENTIALS.toString();

      @Override
      public KeystoneCredentialType get() {
         return KeystoneCredentialType.fromValue(credentialType);
      }
   }

   @Provides
   @Singleton
   protected Map<KeystoneCredentialType, Class<? extends KeystoneAuthenticationFilter>> authenticationRequestMap() {
      return ImmutableMap.<KeystoneCredentialType, Class<? extends KeystoneAuthenticationFilter>> of(
            KeystoneCredentialType.PASSWORD_CREDENTIALS, AuthenticationRequest.class,
            KeystoneCredentialType.API_ACCESS_KEY_CREDENTIALS, AuthenticationRequest.class);
   }

   @Provides
   @Singleton
   protected KeystoneAuthenticationFilter keystoneAuthenticationFilterForCredentialType(
         KeystoneCredentialType credentialType,
         Map<KeystoneCredentialType, Class<? extends KeystoneAuthenticationFilter>> authenticationRequests,
         Injector injector) {
      if (!authenticationRequests.containsKey(credentialType)) {
         throw new IllegalArgumentException("Unsupported credential type: " + credentialType);
      }
      return injector.getInstance(authenticationRequests.get(credentialType));
   }

}
