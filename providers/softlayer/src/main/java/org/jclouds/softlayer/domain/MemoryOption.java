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
 * @author Andrea Turli
 */
public class MemoryOption {

   private final ItemPrice itemPrice;
   private final MemoryTemplate template;

   public MemoryOption(ItemPrice itemPrice, MemoryTemplate template) {
      this.itemPrice = itemPrice;
      this.template = template;
   }

   public int getMaxMemory() {
      return template.getMaxMemory();
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("itemPrice", itemPrice)
              .add("template", template)
              .toString();
   }

   private class MemoryTemplate {
      private final int maxMemory;

      private int getMaxMemory() {
         return maxMemory;
      }

      private MemoryTemplate(int maxMemory) {
         this.maxMemory = maxMemory;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
                 .add("maxMemory", maxMemory)
                 .toString();
      }
   }
}
