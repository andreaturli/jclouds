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
package org.jclouds.softlayer.domain.internal;


import com.google.common.base.Objects;
import org.jclouds.softlayer.domain.ItemPrice;

import java.util.List;

public class NetworkComponentsOption {

   private final ItemPrice itemPrice;
   private final NetworkComponentsTemplate template;

   public NetworkComponentsOption(ItemPrice itemPrice, NetworkComponentsTemplate template) {
      this.itemPrice = itemPrice;
      this.template = template;
   }

   public int getMaxSpeed() {
     if(!template.networkComponents.isEmpty()) {
        return template.networkComponents.get(0).maxSpeed;
     }
      return 0;
   }


   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("itemPrice", itemPrice)
              .add("template", template)
              .toString();
   }

   private class NetworkComponentsTemplate {
      private final List<NetworkComponentsDetails> networkComponents;

      private NetworkComponentsTemplate(List<NetworkComponentsDetails> networkComponents) {
         this.networkComponents = networkComponents;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
                 .add("networkComponents", networkComponents)
                 .toString();
      }
   }

   private class NetworkComponentsDetails {
      private final int maxSpeed;


      private NetworkComponentsDetails(int maxSpeed) {
         this.maxSpeed = maxSpeed;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
                 .add("maxSpeed", maxSpeed)
                 .toString();
      }
   }
}