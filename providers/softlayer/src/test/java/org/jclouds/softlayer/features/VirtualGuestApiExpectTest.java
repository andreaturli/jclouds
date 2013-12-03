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
package org.jclouds.softlayer.features;

import com.google.common.collect.Iterables;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.parse.CreateVirtualGuestResponseTest;
import org.jclouds.softlayer.parse.GetVirtualGuestResponseTest;
import org.jclouds.softlayer.parse.ListVirtualGuestsResponseTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import java.text.SimpleDateFormat;

import static org.testng.Assert.*;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "VirtualGuestApiExpectTest")
public class VirtualGuestApiExpectTest extends BaseSoftLayerApiExpectTest {

   public void testGetVirtualGuestWhenResponseIs2xx() {

      HttpRequest getVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/3001812/getObject?objectMask=id%3Bhostname%3Bdomain%3BpowerState%3BnetworkVlans%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem%3BprimaryBackendIpAddress%3BprimaryIpAddress%3BactiveTransactionCount")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getVirtualGuestResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtual_guest_get.json")).build();

      SoftLayerApi api = requestSendsResponse(getVirtualGuest, getVirtualGuestResponse);

      assertEquals(api.getVirtualGuestApi().getObject(3001812).toString(),
              new GetVirtualGuestResponseTest().expected().toString());
   }

   public void testGetVirtualGuestWhenResponseIs4xx() {

      HttpRequest getObjectRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/3001812/getObject?objectMask=" +
                      "id%3Bhostname%3Bdomain%3BpowerState%3BnetworkVlans%3BoperatingSystem.passwords%3Bdatacenter%3B" +
                      "billingItem%3BprimaryBackendIpAddress%3BprimaryIpAddress%3BactiveTransactionCount")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getObjectResponse = HttpResponse.builder().statusCode(404).build();

      SoftLayerApi api = requestSendsResponse(getObjectRequest, getObjectResponse);

      assertNull(api.getVirtualGuestApi().getObject(3001812));
   }

   public void testCreateObjectWhenResponseIs2xx() {

      HttpRequest createVirtualGuest = HttpRequest.builder().method("POST")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .payload(payloadFromResourceWithContentType("/virtual_guest_create.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createVirtualGuestResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtual_guest_create_response.json")).build();

      VirtualGuestApi api = requestSendsResponse(createVirtualGuest, createVirtualGuestResponse).getVirtualGuestApi();
      VirtualGuest virtualGuest = createVirtualGuest();
      VirtualGuest result = api.createObject(virtualGuest);
      assertEquals(result, new CreateVirtualGuestResponseTest().expected());
   }

   public void testCreateObjectWhenResponseIs4xx() {

      HttpRequest createVirtualGuest = HttpRequest.builder().method("POST")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .payload(payloadFromResourceWithContentType("/virtual_guest_create.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();

      SoftLayerApi api = requestSendsResponse(createVirtualGuest, createVirtualGuestResponse);

      VirtualGuest virtualGuest = createVirtualGuest();

      assertNull(api.getVirtualGuestApi().createObject(virtualGuest));
   }

   public void testListVirtualGuestsWhenResponseIs2xx() {

      HttpRequest listVirtualGuestsRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests?objectMask=virtualGuests.powerState%3BvirtualGuests.networkVlans%3BvirtualGuests.operatingSystem.passwords%3BvirtualGuests.datacenter%3BvirtualGuests.billingItem")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listVirtualGuestsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtual_guest_list.json")).build();

      SoftLayerApi api = requestSendsResponse(listVirtualGuestsRequest, listVirtualGuestsResponse);

      assertEquals(api.getVirtualGuestApi().listVirtualGuests().toString(),
              new ListVirtualGuestsResponseTest().expected().toString());
   }

   public void testListVirtualGuestsWhenResponseIs4xx() {

      HttpRequest listVirtualGuestsRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests?objectMask=virtualGuests" +
                      ".powerState%3BvirtualGuests.networkVlans%3BvirtualGuests.operatingSystem.passwords%3BvirtualGuests.datacenter%3BvirtualGuests.billingItem")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listVirtualGuestsResponse = HttpResponse.builder().statusCode(404).build();

      SoftLayerApi api = requestSendsResponse(listVirtualGuestsRequest, listVirtualGuestsResponse);

      assertTrue(Iterables.isEmpty(api.getVirtualGuestApi().listVirtualGuests()));
   }

   private VirtualGuest createVirtualGuest() {
      return VirtualGuest.builder()
              .domain("example.com")
              .hostname("host1")
              .id(3001813)
              .maxMemory(1024)
              .startCpus(1)
              .operatingSystem(OperatingSystem.builder().id("UBUNTU_LATEST").build())
              .datacenter(Datacenter.builder().name("test").build())
              .build();
   }
}
