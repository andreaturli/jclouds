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
package org.jclouds.oauth.v2.filters;

import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.IOException;
import java.security.InvalidKeyException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.domain.Token;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteProcessor;

/**
 * Authorizes new Bearer Tokens at runtime by authorizing claims needed for the http request.
 *
 * <h3>Cache</h3>
 * This maintains a time-based Bearer Token cache. By default expires after 59 minutes
 * (the maximum time a token is valid is 60 minutes).
 * This cache and expiry period is system-wide and does not attend to per-instance expiry time
 * (e.g. "expires_in" from Google Compute -- which is set to the standard 3600 seconds).
 */
public class MACTypeAccessTokenFlow implements OAuthFilter {
   private static final Joiner ON_COMMA = Joiner.on(",");

   private final String audience;
   private final Supplier<Credentials> credentialsSupplier;
   private final OAuthScopes scopes;
   private final long tokenDuration;
   private final LoadingCache<String, Token> tokenCache;
   private final Crypto crypto;

   public static class TestJWTBearerTokenFlow extends MACTypeAccessTokenFlow {

      @Inject TestJWTBearerTokenFlow(AuthorizeToken loader, @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration,
            @Named(AUDIENCE) String audience, @Provider Supplier<Credentials> credentialsSupplier, OAuthScopes scopes) {
         super(loader, tokenDuration, audience, credentialsSupplier, scopes);
      }

      /** Constant time for testing. */
      long currentTimeSeconds() {
         return 0;
      }
   }

   @Inject
   MACTypeAccessTokenFlow(AuthorizeToken loader, @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration,
                    @Named(AUDIENCE) String audience, @Provider Supplier<Credentials> credentialsSupplier, OAuthScopes scopes, Crypto crypto) {
      this.audience = audience;
      this.credentialsSupplier = credentialsSupplier;
      this.scopes = scopes;
      this.tokenDuration = tokenDuration;
      // since the session interval is also the token expiration time requested to the server make the token expire a
      // bit before the deadline to make sure there aren't session expiration exceptions
      long cacheExpirationSeconds = tokenDuration > 30 ? tokenDuration - 30 : tokenDuration;
      this.tokenCache = CacheBuilder.newBuilder().expireAfterWrite(tokenDuration, SECONDS).build(loader);
      this.crypto = crypto;
   }

   static final class AuthorizeToken extends CacheLoader<String, Token> {
      private final AuthorizationApi api;

      @Inject AuthorizeToken(AuthorizationApi api) {
         this.api = api;
      }

      @Override public Token load(String key) throws Exception {
         return null; // api.issue(key);
      }
   }

   @Override public HttpRequest filter(HttpRequest request) throws HttpException {
      long timestamp = currentTimeSeconds();
     /*
     Claims claims = Claims.create( //
            credentialsSupplier.get().identity, // access_token
            ON_COMMA.join(scopes.forRequest(request)), // scope
            audience, // aud
            now + tokenDuration, // exp
            now // iat
      );
     */

     /*
     The client has previously obtained a set of MAC credentials for
     accessing resources on the "http://example.com/" server.  The MAC
     credentials issued to the client include the following attributes:

     MAC key identifier:  h480djs93hd8
     MAC key:  489dks293j39
     MAC algorithm:  hmac-sha-1

     The client constructs the authentication header by calculating a
     timestamp (e.g. the number of seconds since January 1, 1970 00:00:00
     GMT) and generating a random string used as a nonce:

     Timestamp:  1336363200
     Nonce:  dj83hs9s

     The client constructs the normalized request string (the new line
     separator character is represented by "\n" for display purposes only;
     the trailing new line separator signify that no extension value is
     included with the request, explained below):


     1336363200\n
     dj83hs9s\n
     GET\n
       /resource/1?b=1&a=2\n
     example.com\n
     80\n
     \n
       */
     String nonce = "test";
     String httpRequestMethod = "GET";
     String httpRequestURI = "/v1/api/config/workload/";
     String hostname = "portal.brkt.com";
     String port = "443";

     String normalizedRequest = String.format("%s\n%s\n%s\n%s\n%s\n%s\n", timestamp, nonce, httpRequestMethod, httpRequestURI, hostname, port);
     //Token token = tokenCache.getUnchecked(normalizedRequest);

     String signature = null;
     try {
       ByteProcessor<byte[]> hmacSHA256 = asByteProcessor(crypto.hmacSHA256(credentialsSupplier.get().credential.getBytes(Charsets.UTF_8)));
       signature = base64().encode(readBytes(toInputStream(normalizedRequest), hmacSHA256));
     } catch (IOException e) {
       e.printStackTrace();
     } catch (InvalidKeyException e) {
       e.printStackTrace();
     }

     return request.toBuilder().addHeader("Authorization", signature).build();
   }

   long currentTimeSeconds() {
      return System.currentTimeMillis() / 1000;
   }
}
