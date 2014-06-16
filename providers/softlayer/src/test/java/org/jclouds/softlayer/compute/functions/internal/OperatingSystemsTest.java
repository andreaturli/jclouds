package org.jclouds.softlayer.compute.functions.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

@Test(singleThreaded = true, groups = "unit")
public class OperatingSystemsTest {

   @Test
   public void testOsFamily() {
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.CENTOS), OsFamily.CENTOS);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.DEBIAN), OsFamily.DEBIAN);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.RHEL), OsFamily.RHEL);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.UBUNTU), OsFamily.UBUNTU);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.WINDOWS), OsFamily.WINDOWS);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.CLOUD_LINUX), OsFamily.CLOUD_LINUX);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.VYATTACE), OsFamily.LINUX);
   }

   @Test
   public void testOsBits() {
      assertEquals(OperatingSystems.bits().apply("UBUNTU_12_64").intValue(), 64);
      assertEquals(OperatingSystems.bits().apply("UBUNTU_12_32").intValue(), 32);
   }

   @Test
   public void testOsVersion() {
      assertEquals(OperatingSystems.version().apply("12.04-64 Minimal for VSI"), "12.04");
      assertEquals(OperatingSystems.version().apply("STD 32 bit"), "STD");
   }

}
