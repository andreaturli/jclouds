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
package org.jclouds.openstack.keystone.v3.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v3.AuthenticationApi;
import org.jclouds.openstack.keystone.v3.domain.PasswordCredentials;
import org.jclouds.openstack.keystone.v3.domain.Token;
import org.jclouds.openstack.keystone.v3.functions.internal.BaseAuthenticator;

import com.google.common.base.Optional;

@CredentialType(CredentialTypes.PASSWORD_CREDENTIALS)
@Singleton
public class AuthenticatePasswordCredentials extends BaseAuthenticator<PasswordCredentials> {

   protected final AuthenticationApi api;

   @Inject
   public AuthenticatePasswordCredentials(AuthenticationApi api) {
      this.api = api;
   }

   @Override
   protected Token authenticateWithTenantName(Optional<String> tenantName, PasswordCredentials apiAccessKeyCredentials) {
      return api.create(tenantName.orNull(), apiAccessKeyCredentials);
   }

   @Override
   protected Token authenticateWithTenantId(Optional<String> tenantId, PasswordCredentials apiAccessKeyCredentials) {
//      return api.authenticateWithTenantIdAndCredentials(tenantId.orNull(), apiAccessKeyCredentials);
      return null;
   }

   @Override
   public PasswordCredentials createCredentials(String identity, String credential) {
      return PasswordCredentials.createWithUsernameAndPassword(identity, credential);
   }

   @Override
   public String toString() {
      return "authenticatePasswordCredentials()";
   }
}
