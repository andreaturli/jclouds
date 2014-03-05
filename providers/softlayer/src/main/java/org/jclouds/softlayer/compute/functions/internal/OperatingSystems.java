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
package org.jclouds.softlayer.compute.functions.internal;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import static com.google.common.collect.Iterables.getLast;

/**
 * @author Andrea Turli
 */
public class OperatingSystems {

   public static Function<String, Integer> bits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String operatingSystemReferenceCode) {
            if (operatingSystemReferenceCode != null) {
               return Integer.parseInt(getLast(Splitter.on("_").split(operatingSystemReferenceCode)));
            }
            return null;
         }
      };
   }

   public static Function<String, String> version() {
      return new Function<String, String>() {
         @Override
         public String apply(final String version) {
            return parseVersion(version);
         }
      };
   }

   private static String parseVersion(String version) {
      if(version.contains("-")) {
         String rawVersion = version.substring(0,
                 version.lastIndexOf("-"));
         if(Iterables.size(Splitter.on(".").split(rawVersion)) == 3) {
            return rawVersion.substring(0, rawVersion.lastIndexOf("."));
         } else {
            return rawVersion;
         }
      } else if(version.contains(" ")) {
         version.substring(0,
                 version.indexOf(" "));
      }
      return null;
   }

}
