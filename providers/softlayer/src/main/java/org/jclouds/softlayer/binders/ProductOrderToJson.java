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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.Hardware;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.softlayer.domain.VirtualGuest;

/**
 * Converts a ProductOrder into a json string valid for placing an order via the softlayer api The
 * String is set into the payload of the HttpRequest
 * 
 * @author Jason King, Andrea Turli
 */
public class ProductOrderToJson implements Binder {

   private Json json;

   @Inject
   public ProductOrderToJson(Json json) {
      this.json = json;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(input, "order");
      ProductOrder order = ProductOrder.class.cast(input);
      request.setPayload(buildJson(order));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    * 
    * @param order
    * @return
    */
   String buildJson(ProductOrder order) {

      Iterable<Price> prices = Iterables.transform(order.getPrices(), new Function<ProductItemPrice, Price>() {
         @Override
         public Price apply(ProductItemPrice productItemPrice) {
            return new Price(productItemPrice.getId());
         }
      });


      Iterable<HardwareData> hardwareData = Iterables.transform(order.getHardware(),
              new Function<Hardware, HardwareData>() {
                 @Override
                 public HardwareData apply(Hardware Hardware) {
                    return new HardwareData(Hardware.getHostname(), Hardware.getDomain(),
                            Hardware.getBareMetalInstanceFlag());
                 }
              });

      OrderData data = new OrderData(order.getPackageId(), order.getLocation(), Sets.newLinkedHashSet(prices),
              Sets.newLinkedHashSet(hardwareData), order.getQuantity(), order.getUseHourlyPricing());

      return json.toJson(ImmutableMap.of("parameters", ImmutableList.<OrderData> of(data)));
      /*
      return "{\"parameters\":[{" +
              "\"packageId\":50," +
              "\"location\":\"265592\"," +
              "\"prices\":[{\"id\":2164},{\"id\":17432},{\"id\":19},{\"id\":21509},{\"id\":21},{\"id\":55},{\"id\":57},{\"id\":58},{\"id\":1800},{\"id\":905},{\"id\":418},{\"id\":420}]," +
              "\"hardware\":[{\"hostname\":\"mygroup-803\",\"domain\":\"jclouds.org\"," +
              "\"bareMetalInstanceFlag\":true}],\"useHourlyPricing\":true" +
              "}]" +
              "}";
              */
   }

   @SuppressWarnings("unused")
   private static class OrderData {
      //private String complexType = "SoftLayer_Container_Product_Order_Virtual_Guest";
      private long packageId = -1;
      private String location;
      private Set<Price> prices;
      private Set<HardwareData> hardware;
      private long quantity;
      private boolean useHourlyPricing;

      public OrderData(long packageId, String location, Set<Price> prices,
                       Set<HardwareData> hardware,
               long quantity, boolean useHourlyPricing) {
         this.packageId = packageId;
         this.location = location;
         this.prices = prices;
         this.hardware = hardware;
         this.quantity = quantity;
         this.useHourlyPricing = useHourlyPricing;
      }

   }

   @SuppressWarnings("unused")
   private static class HardwareData {
      private String hostname;
      private String domain;
      private int bareMetalInstanceFlag;

      public HardwareData(String hostname, String domain, int bareMetalInstanceFlag) {
         this.hostname = hostname;
         this.domain = domain;
         this.bareMetalInstanceFlag = bareMetalInstanceFlag;
      }
   }

   @SuppressWarnings("unused")
   private static class Price {
      private long id;

      public Price(long id) {
         this.id = id;
      }
   }

}
