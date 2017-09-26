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

import java.net.URI;
import java.util.List;

import org.jclouds.json.SerializedNames;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Endpoint {

   public abstract String id();

   public abstract String region();

   public abstract String regionId();

   public abstract String serviceId();

   public abstract URI url();

   public abstract boolean enabled();

   public abstract List<Link> links();
//   public abstract String interface();

   @SerializedNames({"id", "region", "region_id", "service_id", "url", "enabled", "links"})
   public static Endpoint create(String id, String region, String regionId, String serviceId, URI url, boolean enabled, List<Link> links) {
      return new AutoValue_Endpoint(id, region, regionId, serviceId, url, enabled,
              links == null ? ImmutableList.<Link>of() : ImmutableList.copyOf(links));
   }

   Endpoint() {
   }

}
