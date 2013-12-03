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
package org.jclouds.softlayer.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.jclouds.softlayer.domain.VirtualGuest;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.regex.Matcher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrea Turli
 */
@Singleton
public class VirtualGuestToImage implements Function<VirtualGuest, Image> {

   private static final String CENTOS = "CentOS";
   private static final String DEBIAN = "Debian GNU/Linux";
   private static final String FEDORA = "Fedora Release";
   private static final String RHEL = "Red Hat Enterprise Linux";
   private static final String UBUNTU = "Ubuntu Linux";
   private static final String WINDOWS = "Windows Server";
   private static final String CLOUD_LINUX = "CloudLinux";
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   /**
    * Parses the item description to determine the OSFamily
    *
    * @return the @see OsFamily or OsFamily.UNRECOGNIZED
    */
   public static Function<String, OsFamily> osFamily() {
      return new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(final String description) {
            if (description != null) {
               if (description.startsWith(CENTOS)) return OsFamily.CENTOS;
               else if (description.startsWith(DEBIAN)) return OsFamily.DEBIAN;
               else if (description.startsWith(FEDORA)) return OsFamily.FEDORA;
               else if (description.startsWith(RHEL)) return OsFamily.RHEL;
               else if (description.startsWith(UBUNTU)) return OsFamily.UBUNTU;
               else if (description.startsWith(WINDOWS)) return OsFamily.WINDOWS;
               else if (description.startsWith(CLOUD_LINUX)) return OsFamily.CLOUD_LINUX;
            }
            return OsFamily.UNRECOGNIZED;
         }
      };
   }

   public static Function<String, Integer> osBits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String description) {
            if (description != null) {
               return description.contains("64") == true ? 64 : 32;
            }
            return null;
         }
      };
   }

   /**
    * Parses the item description to determine the os version
    *
    * @return the version or null if the version cannot be determined
    */
   public static Function<String, String> osVersion() {
      return new Function<String, String>() {
         @Override
         public String apply(final String description) {
            OsFamily family = osFamily().apply(description);

            if (Objects.equal(family, OsFamily.CENTOS)) return parseVersion(description, CENTOS);
            else if (Objects.equal(family, OsFamily.DEBIAN)) return parseVersion(description, DEBIAN);
            else if (Objects.equal(family, OsFamily.FEDORA)) return parseVersion(description, FEDORA);
            else if (Objects.equal(family, OsFamily.RHEL)) return parseVersion(description, RHEL);
            else if (Objects.equal(family, OsFamily.UBUNTU)) return parseVersion(description, UBUNTU);
            else if (Objects.equal(family, OsFamily.WINDOWS)) return parseVersion(description, WINDOWS);
            else if (Objects.equal(family, OsFamily.CLOUD_LINUX)) return parseVersion(description, CLOUD_LINUX);

            return null;
         }
      };
   }

   private static String parseVersion(String description, String os) {
      String noOsName = description.replaceFirst(os, "").trim();
      return noOsName.split(" ")[0];
   }

   @Override
   public Image apply(VirtualGuest from) {

      checkNotNull(from, "from");
      ImageBuilder builder = new ImageBuilder().ids(from.getId() + "")
              .name(from.getHostname())
              .status(Image.Status.AVAILABLE);

      // TODO improve: maybe we can reuse the from.getOperatingSystem().getId() to get all the details
      SoftwareLicense softwareLicense = from.getSoftwareLicense();
      if (softwareLicense == null) {
         return builder.operatingSystem(OperatingSystem.builder().description(from.getOperatingSystem().getId()).build())
                 .build();
      }
      final String longDescription = softwareLicense.getSoftwareDescription().getLongDescription();
      OsFamily osFamily = osFamily().apply(longDescription);
      if (osFamily == OsFamily.UNRECOGNIZED) {
         logger.debug("Cannot determine os family for item: %s", from);
      }
      Integer bits = osBits().apply(longDescription);
      if (bits == null) {
         logger.debug("Cannot determine os bits for item: %s", from);
      }
      String osVersion = osVersion().apply(longDescription);
      if (osVersion == null) {
         logger.debug("Cannot determine os version for item: %s", from);
      }
      OperatingSystem os = OperatingSystem.builder()
              .description(longDescription)
              .family(osFamily)
              .version(osVersion)
              .is64Bit(Objects.equal(bits, 64))
              .build();
      return builder.operatingSystem(os).build();
   }
}
