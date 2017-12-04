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
package org.jclouds.openstack.keystone.config;

import javax.inject.Inject;
import javax.inject.Named;

public class IdentityService {

    @Inject
    @Named("v2")
    AuthenticationStrategy authenticationStrategyV2;

    @Inject
    @Named("v3")
    AuthenticationStrategy authenticationStrategyV3;

    @Inject KeystoneVersions keystoneVersion;

    public String authenticate() {
        if (keystoneVersion == KeystoneVersions.V2) {
            return authenticationStrategyV2.authenticate();
        } else if (keystoneVersion == KeystoneVersions.V3) {
            return authenticationStrategyV3.authenticate();
        } else {
            throw new IllegalStateException("Can't find authentication strategy for " + keystoneVersion);
        }
    }
}
