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
package org.jclouds.softlayer.reference;

/**
 * Configuration properties and constants used in SoftLayer connections.
 * 
 * @author Adrian Cole, Andrea Turli
 */
public interface SoftLayerConstants {

   public static final String SOFTLAYER_PROVIDER_NAME = "softlayer";

   /**
    * number of milliseconds to wait for an order to arrive on the api.
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY = "jclouds.softlayer.virtualguest" +
           ".login_details_delay";

   /**
    * number of milliseconds to wait for a virtualguest to be without active transactions
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY = "jclouds.softlayer" +
           ".virtualguest.active_transactions_delay";

   /**
    * by default, list images will now consider the public images
    */
   public static final String PROPERTY_SOFTLAYER_INCLUDE_PUBLIC_IMAGES = "jclouds.softlayer.include_public_images";

   /**
    * Uplink port speed for new guests (10, 100, 1000)
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_PORT_SPEED = "jclouds.softlayer.virtualguest.port-speed";

   /**
    * Default Disk type (SAN, LOCAL)
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK_TYPE = "jclouds.softlayer.virtualguest.disk-type";
}
