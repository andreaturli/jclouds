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
package org.jclouds.softlayer.features;

import org.jclouds.softlayer.domain.BlockDeviceTemplateGroup;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;

import java.util.Set;

/**
 * Provides synchronous access to Account.
 * <p/>
 * 
 * @see AccountAsyncClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Jason King
 * @deprecated This will be renamed to AccountApi in 1.7.0.
 */
public interface AccountClient {

   /**
    * @return return all the active packages.
    */
   Set<ProductPackage> getActivePackages();


   /**
    * @return return all the active packages's id and name.
    * @see #getActivePackages()
    */
   Set<ProductPackage> getReducedActivePackages();


   /**
    * @return return all the private images for the account
    * @see #getPrivateImages()
    */
   Set<BlockDeviceTemplateGroup> getPrivateImages();
}
