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

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

class KeystoneV2AuthenticationStrategy implements AuthenticationStrategy {

   private final AuthenticationApi api;
   private final Supplier<Credentials> credentialsSupplier;

   @Inject
   KeystoneV2AuthenticationStrategy(AuthenticationApi api, @Provider Supplier<Credentials> credentialsSupplier) {
      this.api = api;
      this.credentialsSupplier = credentialsSupplier;
   }

   @Override
   public String authenticate() {
      Optional<String> tenantName = Optional.absent();
      String usernameOrAccessKey = null;
      if (!tenantName.isPresent() && credentialsSupplier.get().identity.indexOf(':') != -1) {
         tenantName = Optional.of(
               credentialsSupplier.get().identity.substring(0, credentialsSupplier.get().identity.lastIndexOf(':')));
         usernameOrAccessKey = credentialsSupplier.get().identity
               .substring(credentialsSupplier.get().identity.lastIndexOf(':') + 1);
      }

      Access access = api.authenticateWithTenantNameAndCredentials(tenantName.orNull(), PasswordCredentials
            .createWithUsernameAndPassword(usernameOrAccessKey, credentialsSupplier.get().credential));
      if (access != null && access.getToken() != null) {
         return access.getToken().getId();
      }
      return null;
   }
}
