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
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.PrivateModule;
import org.jclouds.openstack.keystone.filters.AuthenticationRequestFlow;
import org.jclouds.openstack.keystone.filters.KeystoneAuthenticationFilter;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.name.Names;

public final class KeystoneModule extends AbstractModule {

   @Override
   protected void configure() {
////      bindHttpApi(binder(), AuthenticationApi.class);
      bind(KeystoneCredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
//      install(new FactoryModuleBuilder()
//              .implement(IdentityService.class, KeystoneV2IdentityService.class)
//              .build(IdentityService.Factory.class));

      install(new PrivateModule() {
         @Override
         protected void configure() {
            // bind IdentityService annotated as English to IdentityService PRIVATELY
//                bind(IdentityService.class).annotatedWith(English.class).to(IdentityService.class);
            bind(IdentityService.class).annotatedWith(Names.named("2")).to(IdentityService.class);

            // expose IdentityService annotated as English GLOBALLY. this fulfills injection point (1)
            expose(IdentityService.class).annotatedWith(Names.named("2")); //.annotatedWith(English.class);

            bindHttpApi(binder(), AuthenticationApi.class);
            bind(AuthenticationStrategy.class).to(KeystoneV2AuthenticationStrategy.class);
         }
      });
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
            KeystoneCredentialType.PASSWORD_CREDENTIALS, AuthenticationRequestFlow.class,
            KeystoneCredentialType.API_ACCESS_KEY_CREDENTIALS, AuthenticationRequestFlow.class);
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
