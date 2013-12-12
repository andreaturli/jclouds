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
 *
 *
 * @author Andrea Turli
 */
public class BlockDeviceTemplateGroup {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromBlockDeviceTemplateGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String globalIdentifier;

      public T id(String id) {
         this.id = id;
         return self();
      }

      public T name(String name) {
         this.name = name;
         return self();
      }

      public T globalIdentifier(String globalIdentifier) {
         this.globalIdentifier = globalIdentifier;
         return self();
      }

      public BlockDeviceTemplateGroup build() {
         return new BlockDeviceTemplateGroup(id, name, globalIdentifier);
      }

      public T fromBlockDeviceTemplateGroup(BlockDeviceTemplateGroup in) {
         return this
                 .id(in.getId())
                 .name(in.getName())
                 .globalIdentifier(in.getGlobalIdentifier());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private String globalIdentifier;

   public BlockDeviceTemplateGroup(String id, String name, String globalIdentifier) {
      this.id = id;
      this.name = name;
      this.globalIdentifier = globalIdentifier;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getGlobalIdentifier() {
      return globalIdentifier;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("name", name)
              .add("globalIdentifier", globalIdentifier)
              .toString();
   }

}
