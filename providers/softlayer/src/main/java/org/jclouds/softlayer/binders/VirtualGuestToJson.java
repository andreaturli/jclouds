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
package org.jclouds.softlayer.binders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.VirtualGuest;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Converts a VirtualGuest into a json string valid for creating a CCI via softlayer api
 * The string is set into the payload of the HttpRequest
 *
 * @author Andrea Turli
 */
public class VirtualGuestToJson implements Binder {

   private Json json;

   @Inject
   public VirtualGuestToJson(Json json) {
      this.json = json;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(input, "parameters");
      VirtualGuest virtualGuest = VirtualGuest.class.cast(input);
      request.setPayload(buildJson(virtualGuest));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    *
    * @param virtualGuest
    * @return String
    */
   String buildJson(VirtualGuest virtualGuest) {
      ObjectData.Builder<?> dataBuilder = ObjectData.builder();
      dataBuilder.hostname(virtualGuest.getHostname())
              .domain(virtualGuest.getDomain())
              .startCpus(virtualGuest.getStartCpus())
              .maxMemory(virtualGuest.getMaxMemory())
              .hourlyBillingFlag(true)
              .localDiskFlag(true)
              .datacenterName(virtualGuest.getDatacenter().getName())
              .networkComponents(null) // TODO
              // Disallowed when referenceCode is provided, as the template will specify the operating system.
              .globalIdentifier(virtualGuest.getUuid());

      if (virtualGuest.getOperatingSystem() != null && virtualGuest.getOperatingSystem().getSoftwareLicense() != null
              && virtualGuest.getOperatingSystem().getSoftwareLicense().getSoftwareDescription() != null) {
         String referenceCode = virtualGuest.getOperatingSystem().getSoftwareLicense().getSoftwareDescription()
                 .getReferenceCode();
         dataBuilder.operatingSystemReferenceCode(referenceCode);
      }
      return json.toJson(ImmutableMap.of("parameters", ImmutableList.<ObjectData> of(dataBuilder.build())));
   }

}