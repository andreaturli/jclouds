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
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrea Turli
 */
public class ContainerVirtualGuestConfigurationOption {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromContainerVirtualGuestConfigurationOption(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected ProductItemPrice productItemPrice;
      protected VirtualGuest template;

      public T productItemPrice(ProductItemPrice productItemPrice) {
         this.productItemPrice = productItemPrice;
         return self();
      }

      public T template(VirtualGuest template) {
         this.template = template;
         return self();
      }

      public ContainerVirtualGuestConfigurationOption build() {
         return new ContainerVirtualGuestConfigurationOption(productItemPrice, template);
      }

      public T fromContainerVirtualGuestConfigurationOption(ContainerVirtualGuestConfigurationOption in) {
         return this
                 .productItemPrice(in.getProductItemPrice())
                 .template(in.getTemplate());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final ProductItemPrice productItemPrice;
   private final VirtualGuest template;

   @ConstructorProperties({
           "itemPrice", "template"
   })
   public ContainerVirtualGuestConfigurationOption(ProductItemPrice productItemPrice, VirtualGuest template) {
      this.productItemPrice = productItemPrice;
      this.template = template;
   }

   public ProductItemPrice getProductItemPrice() {
      return productItemPrice;
   }

   public VirtualGuest getTemplate() {
      return template;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ContainerVirtualGuestConfigurationOption that = (ContainerVirtualGuestConfigurationOption) o;

      return Objects.equal(this.productItemPrice, that.productItemPrice) &&
              Objects.equal(this.template, that.template);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(productItemPrice, template);
   }

   @Override
   public String toString() {
      return "ContainerVirtualGuestConfigurationOption{" +
              "productItemPrice=" + productItemPrice +
              ", template=" + template +
              '}';
   }
}
