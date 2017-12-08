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
package org.jclouds.openstack.keystone.v3.domain;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Auth {
   
   public abstract Identity identity();

   @SerializedNames({ "identity" })
   public static Auth create(Identity identity) {
      return new AutoValue_Auth(identity);
   }

   @AutoValue
   public abstract static class Identity {
      
      public abstract List<String> methods();
      @Nullable public abstract TokenAuth token();
      public abstract PasswordAuth password();

      @SerializedNames({ "methods", "token", "password" })
      public static Identity create(List<String> methods, TokenAuth token, PasswordAuth password) {
         return new AutoValue_Auth_Identity(methods, token, password);
      }

      @AutoValue
      public abstract static  class TokenAuth {
         
         public abstract String id();

         @SerializedNames({ "id" })
         public static TokenAuth create(String id) {
            return new AutoValue_Auth_Identity_TokenAuth(id);
         }
      }

      @AutoValue
      public abstract static  class PasswordAuth {
         
         public abstract UserAuth user();

         @SerializedNames({ "user" })
         public static PasswordAuth create(UserAuth user) {
            return new AutoValue_Auth_Identity_PasswordAuth(user);
         }

         @AutoValue
         public abstract static  class UserAuth {
            
            public abstract String name();
            public abstract DomainAuth domain();
            public abstract String password();

            @SerializedNames({ "name", "domain", "password" })
            public static UserAuth create(String name, DomainAuth domain, String password) {
               return new AutoValue_Auth_Identity_PasswordAuth_UserAuth(name, domain, password);
            }

            @AutoValue
            public abstract static  class DomainAuth {
               
               @Nullable public abstract String name();

               @SerializedNames({ "name" })
               public static DomainAuth create(String name) {
                  return new AutoValue_Auth_Identity_PasswordAuth_UserAuth_DomainAuth(name);
               }
            }
         }
      }
   }
}
