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

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import org.jclouds.openstack.keystone.filters.AuthenticationRequest;
import org.jclouds.openstack.keystone.filters.KeystoneAuthenticationFilter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.KEYSTONE_VERSION;

public class KeystoneFactoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(KeystoneCredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
        bind(KeystoneVersions.class).toProvider(KeystoneVersionFromPropertyOrDefault.class);

        KeystoneVersions keystoneVersion = new KeystoneVersionFromPropertyOrDefault().get();
        if (KeystoneVersions.V2 == keystoneVersion) {
            install(new org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule());
            install(new org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule());
            install(new org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule());
            bind(AuthenticationStrategy.class).to(KeystoneV2AuthenticationStrategy.class);
        } else if (KeystoneVersions.V3 == keystoneVersion) {
            install(new org.jclouds.openstack.keystone.v3.config.AuthenticationApiModule());
            install(new org.jclouds.openstack.keystone.v3.config.KeystoneAuthenticationModule());
            install(new org.jclouds.openstack.keystone.v3.config.KeystoneAuthenticationModule.RegionModule());
            bind(AuthenticationStrategy.class).to(KeystoneV3AuthenticationStrategy.class);
        }
//        install(new FactoryModuleBuilder()
//                .implement(Key.get(AuthenticationStrategy.class, named("v2")), KeystoneV2AuthenticationStrategy.class)
//                .implement(Key.get(AuthenticationStrategy.class, named("v3")), KeystoneV3AuthenticationStrategy.class)
//                .build(AuthenticationStrategyFactory.class));
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
