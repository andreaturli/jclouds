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
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item"
 * @author Andrea Turli
 */
public class ProductItem {

   private final int id;
   private final String description;
   private final String SoftwareDescriptionId;
   private final SoftwareDescription softwareDescription;


   public ProductItem(int id, String description, String softwareDescriptionId, SoftwareDescription softwareDescription) {
      this.id = id;
      this.description = description;
      SoftwareDescriptionId = softwareDescriptionId;
      this.softwareDescription = softwareDescription;
   }

   public int getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public String getSoftwareDescriptionId() {
      return SoftwareDescriptionId;
   }

   public SoftwareDescription getSoftwareDescription() {
      return softwareDescription;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProductItem that = (ProductItem) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.description, that.description) &&
              Objects.equal(this.SoftwareDescriptionId, that.SoftwareDescriptionId) &&
              Objects.equal(this.softwareDescription, that.softwareDescription);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, description, SoftwareDescriptionId, softwareDescription);
   }

   @Override
   public String toString() {
      return "ProductItem{" +
              "id=" + id +
              ", description='" + description + '\'' +
              ", SoftwareDescriptionId='" + SoftwareDescriptionId + '\'' +
              ", softwareDescription=" + softwareDescription +
              '}';
   }
}
