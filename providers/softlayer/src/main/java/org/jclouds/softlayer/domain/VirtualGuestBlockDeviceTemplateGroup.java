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

import java.util.Set;

/**
 * The virtual block device template group data type presents the structure in which a group of archived image
 * templates will be presented. A virtual block device template group, also known as an image template group,
 * represents an image of a virtual guest instance.
 *
 * @author Andrea Turli
 * @see <a href="http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Block_Device_Template_Group"
/>
 */
public class VirtualGuestBlockDeviceTemplateGroup {

   private final String accountId;
   private final Set<BlockDeviceTemplateGroup> children;
   private final String globalIdentifier;


   public VirtualGuestBlockDeviceTemplateGroup(String accountId, Set<BlockDeviceTemplateGroup> children, String globalIdentifier) {
      this.accountId = accountId;
      this.children = children;
      this.globalIdentifier = globalIdentifier;
   }

   public String getAccountId() {
      return accountId;
   }

   public Set<BlockDeviceTemplateGroup> getChildren() {
      return children;
   }

   public String getGlobalIdentifier() {
      return globalIdentifier;
   }
}
