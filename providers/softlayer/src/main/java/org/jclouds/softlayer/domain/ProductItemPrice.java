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
package org.jclouds.softlayer.domain;

import com.google.common.base.Objects;

/**
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item_Price"
 * @author Andrea Turli
 */
public class ProductItemPrice {

   private final int id;
   private final float hourlyRecurringFee;
   private final String recurringFee;
   private final ProductItem item;


   public ProductItemPrice(int id, int hourlyRecurringFee, String recurringFee, ProductItem item) {
      this.id = id;
      this.hourlyRecurringFee = hourlyRecurringFee;
      this.recurringFee = recurringFee;
      this.item = item;
   }

   public int getId() {
      return id;
   }

   public float getHourlyRecurringFee() {
      return hourlyRecurringFee;
   }

   public String getRecurringFee() {
      return recurringFee;
   }

   public ProductItem getItem() {
      return item;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProductItemPrice that = (ProductItemPrice) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.hourlyRecurringFee, that.hourlyRecurringFee) &&
              Objects.equal(this.recurringFee, that.recurringFee) &&
              Objects.equal(this.item, that.item);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, hourlyRecurringFee, recurringFee, item);
   }

   @Override
   public String toString() {
      return "ProductItemPrice{" +
              "id=" + id +
              ", hourlyRecurringFee=" + hourlyRecurringFee +
              ", recurringFee='" + recurringFee + '\'' +
              ", item=" + item +
              '}';
   }
}
