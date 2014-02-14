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

import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.domain.ContainerVirtualGuestConfiguration;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter.VirtualGuestHasLoginDetailsPresent;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code VirtualGuestApi}
 *
 * @author Adrian Cole, Andrea Turli
 */
@Test(groups = "live")
public class VirtualGuestApiLiveTest extends BaseSoftLayerApiLiveTest {

   private VirtualGuestApi virtualGuestApi;
   private Predicate<VirtualGuest> loginDetailsTester;
   private VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent;
   private long guestLoginDelay = 60 * 60 * 1000;

   private VirtualGuest virtualGuest = null;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      virtualGuestApi = api.getVirtualGuestApi();
   }

   @AfterClass(groups = {"integration", "live"})
   protected void tearDownContext() {
      if (virtualGuest != null) {
         destroyMachine(virtualGuest);
         virtualGuest = null;
      }
   }

   @Override
   protected SoftLayerApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      virtualGuestHasLoginDetailsPresent = injector.getInstance(VirtualGuestHasLoginDetailsPresent.class);
      loginDetailsTester = retry(virtualGuestHasLoginDetailsPresent, guestLoginDelay);
      return injector.getInstance(SoftLayerApi.class);
   }

   @Test
   public void testGetCreateObjectOptions() {
      ContainerVirtualGuestConfiguration configurationOption = api().getCreateObjectOptions();
      assertNotNull(configurationOption);
   }

   @Test
   public void testCreateVirtualMachine() throws Exception {
      VirtualGuest virtualGuestRequest = VirtualGuest.builder()
              .domain("jclouds.org")
              .hostname("virtualGuestApiLiveTest")
              .startCpus(1)
              .maxMemory(1024)
              .operatingSystem(OperatingSystem.builder().id("CENTOS_6_64").operatingSystemReferenceCode("CENTOS_6_64").build())
              .datacenter(Datacenter.builder().name("dal01").build())
              .build();

      virtualGuest = virtualGuestApi.createObject(virtualGuestRequest);
      boolean orderInSystem = loginDetailsTester.apply(virtualGuest);
      checkState(orderInSystem, "order for guest %s doesn't have login details within %sms", virtualGuest,
              Long.toString(guestLoginDelay));
      virtualGuest = virtualGuestApi.getObject(virtualGuest.getId());
      checkVirtualGuest(virtualGuest);
      assertNotNull(virtualGuest.getPrimaryIpAddress(), "primaryIpAddress must be not null");
      assertNotNull(virtualGuest.getPrimaryBackendIpAddress(), "backendIpAddress must be not null");
   }

   private void destroyMachine(final VirtualGuest virtualGuest) {
      checkState(retry(new Predicate<VirtualGuest>() {
         public boolean apply(VirtualGuest guest) {
            guest = api().getObject(virtualGuest.getId());
            return guest.getActiveTransactionCount() == 0;
         }
      }, 5*60*1000).apply(virtualGuest), "%s still has active transactions!", virtualGuest);
      assertTrue(api().deleteObject(virtualGuest.getId()));
   }

   private VirtualGuestApi api() {
      return api.getVirtualGuestApi();
   }

   private void checkVirtualGuest(VirtualGuest vg) {
      if (vg.getActiveTransactionCount() == 0) {
         assertNotNull(vg.getDomain(), "domain must be not null");
         assertNotNull(vg.getFullyQualifiedDomainName(), "fullyQualifiedDomainName must be not null");
         assertNotNull(vg.getHostname(), "hostname must be not null");
         assertTrue(vg.getId() > 0, "id must be greater than 0");
         assertTrue(vg.getMaxCpu() > 0, "maxCpu must be greater than 0");
         assertNotNull(vg.getMaxCpuUnits(), "maxCpuUnits must be not null");
         assertTrue(vg.getMaxMemory() > 0, "maxMemory must be greater than 0");
         assertTrue(vg.getStartCpus() > 0, "startCpus must be greater than 0");
         assertTrue(vg.getStatusId() > 0, "statusId must be greater than 0");
         assertNotNull(vg.getUuid(), "uuid must be not null");
      }
   }

}
