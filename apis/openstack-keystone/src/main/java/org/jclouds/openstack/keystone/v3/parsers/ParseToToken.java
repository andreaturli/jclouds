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
package org.jclouds.openstack.keystone.v3.parsers;

import javax.annotation.Nullable;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.openstack.keystone.v3.domain.Token;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class ParseToToken implements Function<HttpResponse, Token> {

   private final Injector injector;

   @Inject
   public ParseToToken(Injector injector) {
      this.injector = injector;
   }

   @Nullable
   @Override
   public Token apply(@Nullable HttpResponse from) {
      ParseFirstJsonValueNamed transformer = new ParseFirstJsonValueNamed(injector.getInstance(GsonWrapper.class),
              TypeLiteral.get(Token.class), "token");
      Token token = (Token) transformer.apply(from);
//      return Token.create(ImmutableList.<String>of(), null, null, ImmutableList.<String>of(), User.create("", "", null, User.Domain.create("", "")), null);
      String xSubjectToken = Iterables.getOnlyElement(from.getHeaders().get("X-Subject-Token"));
      return token.toBuilder().id(xSubjectToken).build();
   }
}
