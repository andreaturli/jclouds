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

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
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
   private static final Pattern OS_BITS_PATTERN = Pattern.compile(".*(amd64|x64|64-bit|x86_64).*");

   private static final String CENTOS = "CentOS";
   private static final String DEBIAN = "Debian";
   private static final String FEDORA = "Fedora";
   private static final String RHEL = "rhel";
   private static final String UBUNTU = "Ubuntu";
   private static final String WINDOWS = "Windows";
   private static final String CLOUD_LINUX = "CloudLinux";

   private static final Pattern CENTOS_PATTERN = Pattern.compile(".*(CentOS|centos|Centos).*");
   private static final Pattern DEBIAN_PATTERN = Pattern.compile(".*(Debian).*");
   private static final Pattern FEDORA_PATTERN = Pattern.compile(".*(Fedora).*");
   private static final Pattern RHEL_PATTERN = Pattern.compile(".*(RedHat|rhel).*|Red Hat.*");
   private static final Pattern UBUNTU_PATTERN = Pattern.compile(".*(Ubuntu|ubuntu).*|ubuntu.*");
   private static final Pattern WINDOWS_2008_PATTERN =
           Pattern.compile(".*(Microsoft Windows|win2k8R2dc|w2k8R2dc|Win2k3).*|(w2k8R2dc|Windows Server).*");

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
              .ids(blockDeviceTemplateGroup.getGlobalIdentifier())
              .name(name)
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
         public OsFamily apply(String name) {
            if (name != null) {
               if(CENTOS_PATTERN.matcher(name).matches()) { return OsFamily.CENTOS; }
               if(DEBIAN_PATTERN.matcher(name).matches()) { return OsFamily.DEBIAN; }
               if(FEDORA_PATTERN.matcher(name).matches()) { return OsFamily.FEDORA; }
               if(RHEL_PATTERN.matcher(name).matches()) { return OsFamily.RHEL; }
               if(UBUNTU_PATTERN.matcher(name).matches()) { return OsFamily.UBUNTU; }
               if(WINDOWS_2008_PATTERN.matcher(name).matches()) { return OsFamily.WINDOWS; }
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
       // for 100G OS VERSION BITS like
      if(Iterables.size(Splitter.on(CharMatcher.WHITESPACE).split(description)) == 4) {
         return Iterables.get(Splitter.on(CharMatcher.WHITESPACE).split(description), 2);
      }
      // for RightImage_OS_VERSION_BITS and SoftLayer_OS_VERSION_BITS like OR
      // RightImage_WINDOWSVERSION and WINDOWSVERSION_rightlink_*
      if(!os.equals(WINDOWS)) {
         if(Iterables.size(Splitter.on("_").split(description)) == 4) {
            return Iterables.get(Splitter.on("_").split(description), 2);
         }
      } else {
         if(WINDOWS_2008_PATTERN.matcher(description).matches()) { return "2008"; }
      }
      return "unrecognized";
   }

   public static Function<String, Integer> osBits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String name) {
            if (name != null) {
               Matcher m = OS_BITS_PATTERN.matcher(name);
               if (m.matches()) { return 64; }
            }
            return 32;
         }
      };
   }

}
