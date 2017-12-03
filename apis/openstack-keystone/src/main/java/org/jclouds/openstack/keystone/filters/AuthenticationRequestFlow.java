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
package org.jclouds.openstack.keystone.filters;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.keystone.config.IdentityService;
import org.jclouds.openstack.v2_0.reference.AuthHeaders;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AuthenticationRequestFlow implements KeystoneAuthenticationFilter {

   // private final Supplier<Credentials> credentialsSupplier;
   // private final String apiVersion;
   // private final IdentityService.Factory factory;
   // private final AuthenticationApi api;

   public static final String KEYSTONE_VERSION = "2";
   private final IdentityService identityService;

   @Inject
   AuthenticationRequestFlow(@Named(KEYSTONE_VERSION) IdentityService identityService) {
      this.identityService = identityService;
      // @Provider Supplier<Credentials> credentialsSupplier, //, @ApiVersion String
      // apiVersion,
      // AuthenticationApi api) {
      // IdentityService.Factory factory) {
      // this.credentialsSupplier = credentialsSupplier;
      // this.apiVersion = apiVersion;
      // this.factory = factory;
      // this.api = api;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      String tokenId = identityService.authenticate();
      return request.toBuilder().addHeader(AuthHeaders.AUTH_TOKEN, tokenId).build();
   }

}
