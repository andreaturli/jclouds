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
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.BlockDeviceTemplateGroup;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrea Turli
 */
@Singleton
public class BlockDeviceTemplateGroupToImage implements Function<BlockDeviceTemplateGroup, Image> {

   /**
    * Pattern to capture the number of bits e.g. "a (amd64) os"
    */
   private static final Pattern OS_BITS_PATTERN = Pattern.compile(".*(amd64|x64|64-bit).*");

   private static final String CENTOS = "CentOS";
   private static final String DEBIAN = "Debian";
   private static final String FEDORA = "Fedora";
   private static final String RHEL = "rhel";
   private static final String UBUNTU = "Ubuntu";
   private static final String WINDOWS = "Windows";
   private static final String CLOUD_LINUX = "CloudLinux";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Image apply(BlockDeviceTemplateGroup blockDeviceTemplateGroup) {
      checkNotNull(blockDeviceTemplateGroup, "blockDeviceTemplateGroup");
      String name = checkNotNull(blockDeviceTemplateGroup.getName(), "blockDeviceTemplateGroup.name");

      OsFamily osFamily = osFamily().apply(name);
      if (osFamily == OsFamily.UNRECOGNIZED) {
         logger.debug("Cannot determine os family for item: %s", blockDeviceTemplateGroup);
      }
      Integer bits = osBits().apply(name);
      if (bits == null) {
         logger.debug("Cannot determine os bits for item: %s", blockDeviceTemplateGroup);
      }
      String osVersion = osVersion().apply(name);
      if (osVersion == null) {
         logger.debug("Cannot determine os version for item: %s", blockDeviceTemplateGroup);
      }
      OperatingSystem os = OperatingSystem.builder()
              .description(name)
              .family(osFamily)
              .version(osVersion)
              .is64Bit(Objects.equal(bits, 64))
              .build();

      return new ImageBuilder()
              .ids(blockDeviceTemplateGroup.getId())
              .description(name)
              .operatingSystem(os)
              .status(Image.Status.AVAILABLE)
              .build();
   }

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
               if (description.contains(CENTOS)) return OsFamily.CENTOS;
               else if (description.contains(DEBIAN)) return OsFamily.DEBIAN;
               else if (description.contains(FEDORA)) return OsFamily.FEDORA;
               else if (description.contains(RHEL)) return OsFamily.RHEL;
               else if (description.contains(UBUNTU)) return OsFamily.UBUNTU;
               else if (description.contains(WINDOWS)) return OsFamily.WINDOWS;
               else if (description.contains(CLOUD_LINUX)) return OsFamily.CLOUD_LINUX;
            }

            return OsFamily.UNRECOGNIZED;
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
         public String apply(final String name) {
            OsFamily family = osFamily().apply(name);

            if (Objects.equal(family, OsFamily.CENTOS)) return parseVersion(name, CENTOS);
            else if (Objects.equal(family, OsFamily.DEBIAN)) return parseVersion(name, DEBIAN);
            else if (Objects.equal(family, OsFamily.FEDORA)) return parseVersion(name, FEDORA);
            else if (Objects.equal(family, OsFamily.RHEL)) return parseVersion(name, RHEL);
            else if (Objects.equal(family, OsFamily.UBUNTU)) return parseVersion(name, UBUNTU);
            else if (Objects.equal(family, OsFamily.WINDOWS)) return parseVersion(name, WINDOWS);
            else if (Objects.equal(family, OsFamily.CLOUD_LINUX)) return parseVersion(name, CLOUD_LINUX);

            return null;
         }
      };
   }

   private static String parseVersion(String description, String os) {
      String noOsName = description.replaceFirst(os, "").trim();
      return noOsName.split(" ")[0];
   }

   /**
    * Parses the item description to determine the number of OS bits
    * Expects the number to be in parenthesis and to contain the word "bit".
    * The following return 64: "A (64 bit) OS", "A (64bit) OS"
    *
    * @return the number of bits or null if the number of bits cannot be determined
    */
   public static Function<String, Integer> osBits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String name) {
            if (name != null) {
               Matcher m = OS_BITS_PATTERN.matcher(name);
               if (m.matches()) {
                  return 64;
               }
            }
            return 32;
         }
      };
   }

}
