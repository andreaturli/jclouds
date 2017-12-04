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

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.openstack.keystone.v2_0.config.Authentication;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v3.AuthenticationApi;
import org.jclouds.openstack.keystone.v3.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v3.domain.Token;
import org.jclouds.openstack.keystone.v3.functions.AuthenticateApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.v3.functions.AuthenticatePasswordCredentials;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.jclouds.openstack.keystone.v3.suppliers.RegionIdToAdminURISupplier;

public final class KeystoneV3Module extends PrivateModule {

   private static final String VERSION = KeystoneVersions.V3.toString();

   @Override
   protected void configure() {
      bind(IdentityService.class).annotatedWith(Names.named(VERSION)).to(IdentityService.class);
      expose(IdentityService.class).annotatedWith(Names.named(VERSION));

      bindHttpApi(binder(), AuthenticationApi.class);
      bind(AuthenticationStrategy.class).annotatedWith(Names.named(VERSION)).to(KeystoneV3AuthenticationStrategy.class);
      expose(AuthenticationStrategy.class).annotatedWith(Names.named(VERSION));

      // TODO KeystoneLocationModule
      install(new KeystoneAuthenticationModule.RegionModule());

      expose(RegionIdToURISupplier.class); //.annotatedWith(Names.named(VERSION));
      expose(RegionIdToAdminURISupplier.class); //.annotatedWith(Names.named(VERSION));
      expose(RegionIdsSupplier.class); //.annotatedWith(Names.named(VERSION));
      expose(ImplicitLocationSupplier.class); //.annotatedWith(Names.named(VERSION));
      expose(LocationsSupplier.class); //.annotatedWith(Names.named(VERSION));
   }

   @Provides
   @Singleton
   @Authentication
   protected Supplier<String> provideAuthenticationTokenCache(final Supplier<Token> supplier)
         throws InterruptedException, ExecutionException, TimeoutException {
      return new Supplier<String>() {
         @Override
         public String get() {
            return supplier.get().id();
         }
      };
   }

   @Provides
   @Singleton
   protected Map<String, Function<Credentials, Token>> provideAuthenticationMethods(Injector i) {
      return authenticationMethods(i);
   }

   protected Map<String, Function<Credentials, Token>> authenticationMethods(Injector i) {
      ImmutableSet.Builder<Function<Credentials, Token>> fns = ImmutableSet.<Function<Credentials, Token>> builder();
      fns.add(i.getInstance(AuthenticatePasswordCredentials.class));
      fns.add(i.getInstance(AuthenticateApiAccessKeyCredentials.class));
      return CredentialTypes.indexByCredentialType(fns.build());
   }

   @Provides
   @Singleton
   protected Function<Credentials, Token> authenticationMethodForCredentialType(
         @Named(KeystoneProperties.CREDENTIAL_TYPE) String credentialType,
         Map<String, Function<Credentials, Token>> authenticationMethods) {
      checkArgument(authenticationMethods.containsKey(credentialType), "credential type %s not in supported list: %s",
            credentialType, authenticationMethods.keySet());
      return authenticationMethods.get(credentialType);
   }

   // TODO: what is the timeout of the session token? modify default accordingly
   // PROPERTY_SESSION_INTERVAL is default to 60 seconds, but we have this here at
   // 11 hours for now.
   @Provides
   @Singleton
   public LoadingCache<Credentials, Token> provideTokenCache(Function<Credentials, Token> getToken) {
      return CacheBuilder.newBuilder().expireAfterWrite(11, TimeUnit.HOURS).build(CacheLoader.from(getToken));
   }

   // Temporary conversion of a cache to a supplier until there is a single-element
   // cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<Token> provideTokenSupplier(final LoadingCache<Credentials, Token> cache,
         @org.jclouds.location.Provider final Supplier<Credentials> creds) {
      return new Supplier<Token>() {
         @Override
         public Token get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }

}
