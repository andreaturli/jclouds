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
import com.google.common.collect.Iterables;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class VirtualGuestBlockDeviceTemplateGroup
 *
 * @author Andrea Turli
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Block_Device_Template_Group"/>
 */
public class VirtualGuestBlockDeviceTemplateGroup {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualGuestBlockDeviceTemplateGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected String globalIdentifier;
      protected int statusId;
      protected int accountId;
      protected int parentId;
      protected String summary;
      protected Set<VirtualGuestBlockDeviceTemplateGroup> children = ImmutableSet.of();
      protected Set<VirtualGuestBlockDeviceTemplate> blockDevices = ImmutableSet.of();

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getGlobalIdentifier()
       */
      public T globalIdentifier(String globalIdentifier) {
         this.globalIdentifier = globalIdentifier;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getStatusId()
       */
      public T statusId(int statusId) {
         this.statusId = statusId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getAccountId()
       */
      public T accountId(int accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see VirtualGuestBlockDeviceTemplateGroup#getParentId()
       */
      public T parentId(int parentId) {
         this.parentId = parentId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getSummary()
       */
      public T summary(String summary) {
         this.summary = summary;
         return self();
      }

      /**
       * @see VirtualGuestBlockDeviceTemplateGroup#getChildren()
       */
      public T children(Set<VirtualGuestBlockDeviceTemplateGroup> children) {
         this.children = ImmutableSet.copyOf(checkNotNull(children, "children"));
         return self();
      }

      public T children(VirtualGuestBlockDeviceTemplateGroup... in) {
         return children(ImmutableSet.copyOf(in));
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup#getBlockDevices()
       */
      public T blockDevices(Set<VirtualGuestBlockDeviceTemplate> blockDevices) {
         this.blockDevices = ImmutableSet.copyOf(checkNotNull(blockDevices, "blockDevices"));
         return self();
      }

      public T blockDevices(VirtualGuestBlockDeviceTemplate... in) {
         return blockDevices(ImmutableSet.copyOf(in));
      }

      public VirtualGuestBlockDeviceTemplateGroup build() {
         return new VirtualGuestBlockDeviceTemplateGroup(id, name, globalIdentifier, statusId, accountId, parentId,
                 summary, children, blockDevices);
      }

      public T fromVirtualGuestBlockDeviceTemplateGroup(VirtualGuestBlockDeviceTemplateGroup in) {
         return this
                 .id(in.getId())
                 .name(in.getName())
                 .statusId(in.getStatusId())
                 .accountId(in.getAccountId())
                 .parentId(in.getParentId())
                 .summary(in.getSummary())
                 .children(in.getChildren())
                 .blockDevices(in.getBlockDevices());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final String globalIdentifier;
   private final int statusId;
   private final int accountId;
   private final int parentId;
   private final String summary;
   private final Set<VirtualGuestBlockDeviceTemplateGroup> children;
   private final Set<VirtualGuestBlockDeviceTemplate> blockDevices;

   @ConstructorProperties({
           "id", "name", "globalIdentifier", "statusId", "accountId", "parentId", "summary", "children", "blockDevices"
   })
   protected VirtualGuestBlockDeviceTemplateGroup(int id, String name, String globalIdentifier, int statusId,
                                                  int accountId,  int parentId, @Nullable String summary,
                                                  @Nullable Set<VirtualGuestBlockDeviceTemplateGroup> children,
                                                  @Nullable Set<VirtualGuestBlockDeviceTemplate> blockDevices) {
      this.id = id;
      this.name = name;
      this.globalIdentifier = globalIdentifier;
      this.statusId = statusId;
      this.accountId = accountId;
      this.parentId = parentId;
      this.summary = summary;
      this.children = children == null ? ImmutableSet.<VirtualGuestBlockDeviceTemplateGroup>of() :
              ImmutableSet.copyOf(children);
      this.blockDevices = blockDevices == null ? ImmutableSet.<VirtualGuestBlockDeviceTemplate>of() :
              ImmutableSet.copyOf(blockDevices);
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getGlobalIdentifier() {
      return globalIdentifier;
   }

   public int getStatusId() {
      return statusId;
   }

   public int getAccountId() {
      return accountId;
   }

   public int getParentId() {
      return parentId;
   }

   public String getSummary() {
      return summary;
   }

   public Set<VirtualGuestBlockDeviceTemplateGroup> getChildren() {
      return children;
   }

   public Set<VirtualGuestBlockDeviceTemplate> getBlockDevices() {
      return blockDevices;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualGuestBlockDeviceTemplateGroup that = (VirtualGuestBlockDeviceTemplateGroup) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.globalIdentifier, that.globalIdentifier) &&
              Objects.equal(this.statusId, that.statusId) &&
              Objects.equal(this.accountId, that.accountId) &&
              Objects.equal(this.parentId, that.parentId) &&
              Objects.equal(this.summary, that.summary) &&
              Objects.equal(this.children, that.children) &&
              Objects.equal(this.blockDevices, that.blockDevices);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, globalIdentifier, statusId, accountId, parentId, summary, children,
              blockDevices);
   }

   @Override
   public String toString() {
      return "VirtualGuestBlockDeviceTemplateGroup{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", globalIdentifier='" + globalIdentifier + '\'' +
              ", statusId=" + statusId +
              ", accountId='" + accountId + '\'' +
              ", parentId='" + parentId + '\'' +
              ", summary=" + summary +
              ", children=" + Iterables.toString(children) +
              ", blockDevices=" + Iterables.toString(blockDevices) +
              "}";
   }
}
