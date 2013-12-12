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

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.jclouds.softlayer.domain.ItemPrice;

public class ProcessorOption {
   private final ItemPrice itemPrice;
   private final ProcessorTemplate template;

   public ProcessorOption(ItemPrice itemPrice, ProcessorTemplate template) {
      this.itemPrice = itemPrice;
      this.template = template;
   }

   public double getStartCpus() {
      return template.startCpus;
   }

   public double getCoreSpeed() {
      String description = itemPrice.getItem().getDescription();
      String sub = description.substring(description.indexOf("x") + 2);
      return Double.parseDouble(Iterables.get(Splitter.on(CharMatcher.WHITESPACE).split(sub), 0));
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("itemPrice", itemPrice)
              .add("template", template)
              .toString();
   }

   private class ProcessorTemplate {
      private final double startCpus;

      private ProcessorTemplate(double startCpus) {
         this.startCpus = startCpus;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
                 .add("startCpus", startCpus)
                 .toString();
      }
   }
}