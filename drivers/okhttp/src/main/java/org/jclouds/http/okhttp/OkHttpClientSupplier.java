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
package org.jclouds.http.okhttp;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.http.HttpUtils;
import org.jclouds.http.okhttp.OkHttpClientSupplier.NewOkHttpClient;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;

/**
 * Provides the OkHttp client used for all requests. This could be used to
 * designate a custom SSL context or limit TLS ciphers.
 * <p>
 * Note that it should configured it in the Guice module designated as
 * <code>@ConfiguresHttpApi</code>.
 */
@Beta
@ImplementedBy(NewOkHttpClient.class)
public interface OkHttpClientSupplier extends Supplier<OkHttpClient> {

   static final class NewOkHttpClient implements OkHttpClientSupplier {

      private final HostnameVerifier verifier;
      private final Supplier<SSLContext> untrustedSSLContextProvider;
      private final HttpUtils utils;

      @Inject
      public NewOkHttpClient(HttpUtils utils, @Named("untrusted") HostnameVerifier verifier,
                             @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider) {
         this.verifier = verifier;
         this.untrustedSSLContextProvider = untrustedSSLContextProvider;
         this.utils = utils;
      }

      @Override
      public OkHttpClient get() {
         OkHttpClient client = new OkHttpClient();
         client.setConnectTimeout(utils.getConnectionTimeout(), TimeUnit.MILLISECONDS);
         client.setReadTimeout(utils.getSocketOpenTimeout(), TimeUnit.MILLISECONDS);
         // do not follow redirects since https redirects don't work properly
         // ex. Caused by: java.io.IOException: HTTPS hostname wrong: should be
         // <adriancole.s3int0.s3-external-3.amazonaws.com>
         client.setFollowRedirects(false);

         if (utils.relaxHostname()) {
            client.setHostnameVerifier(verifier);
         }
         if (utils.trustAllCerts()) {
            client.setSslSocketFactory(untrustedSSLContextProvider.get().getSocketFactory());
         }
         return client;
      }
   }
}
