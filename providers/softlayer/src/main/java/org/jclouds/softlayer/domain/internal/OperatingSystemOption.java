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

public class OperatingSystemOption {

   private final ItemPrice itemPrice;
   private final OperatingSystemTemplate template;

   public OperatingSystemOption(ItemPrice itemPrice, OperatingSystemTemplate template) {
      this.itemPrice = itemPrice;
      this.template = template;
   }

   public String getDescritpion() {
      return itemPrice.getItem().getDescription();
   }

   public String getOperatingSystemReferenceCode() {
      return template.getOperatingSystemReferenceCode();
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("itemPrice", itemPrice)
              .add("template", template)
              .toString();
   }

   public String getSoftwareDescriptionId() {
      if (itemPrice.getItem().getSoftwareDescriptionId() == null) {
         return null;
      }
      return itemPrice.getItem().getSoftwareDescriptionId();
   }
}

class OperatingSystemTemplate {
   private final String operatingSystemReferenceCode;

   private OperatingSystemTemplate(String operatingSystemReferenceCode) {
      this.operatingSystemReferenceCode = operatingSystemReferenceCode;
   }

   String getOperatingSystemReferenceCode() {
      return operatingSystemReferenceCode;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("operatingSystemReferenceCode", operatingSystemReferenceCode)
              .toString();
   }
}