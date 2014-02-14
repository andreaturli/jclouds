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
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.SoftwareLicense;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getLast;

@Singleton
public class OperatingSystemToImage implements Function<OperatingSystem, Image> {

   private static final String CENTOS = "CENTOS";
   private static final String DEBIAN = "DEBIAN";
   private static final String RHEL = "REDHAT";
   private static final String UBUNTU = "UBUNTU";
   private static final String WINDOWS = "WIN_";
   private static final String CLOUD_LINUX = "CLOUDLINUX";
   private static final String VYATTACE = "VYATTACE";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public static Function<String, OsFamily> osFamily() {
      return new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(final String description) {
            if (description != null) {
               if (description.startsWith(CENTOS)) return OsFamily.CENTOS;
               else if (description.startsWith(DEBIAN)) return OsFamily.DEBIAN;
               else if (description.startsWith(RHEL)) return OsFamily.RHEL;
               else if (description.startsWith(UBUNTU)) return OsFamily.UBUNTU;
               else if (description.startsWith(WINDOWS)) return OsFamily.WINDOWS;
               else if (description.startsWith(CLOUD_LINUX)) return OsFamily.CLOUD_LINUX;
               else if (description.startsWith(VYATTACE)) return OsFamily.LINUX;
            }
            return OsFamily.UNRECOGNIZED;
         }
      };
   }

   public static Function<String, Integer> osBits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String operatingSystemReferenceCode) {
            if (operatingSystemReferenceCode != null) {
                  return Integer.parseInt(getLast(Splitter.on("_").split(operatingSystemReferenceCode)));
            }
            return null;
         }
      };
   }

   public static Function<String, String> osVersion() {
      return new Function<String, String>() {
         @Override
         public String apply(final String version) {
            return parseVersion(version);
         }
      };
   }

   private static String parseVersion(String version/*, String os*/) {
      if(version.contains("-")) {
         String rawVersion = version.substring(0,
              version.lastIndexOf("-"));
         if(Iterables.size(Splitter.on(".").split(rawVersion)) == 3) {
            return rawVersion.substring(0, rawVersion.lastIndexOf("."));
         } else {
            return rawVersion;
         }
      } else if(version.contains(" ")) {
         version.substring(0,
                 version.indexOf(" "));
      }
      return null;
   }

   @Override
   public Image apply(OperatingSystem operatingSystem) {
      checkNotNull(operatingSystem, "operatingSystem");
      SoftwareLicense softwareLicense = checkNotNull(operatingSystem.getSoftwareLicense(),
              "softwareLicense");
      String operatingSystemReferenceCode = softwareLicense.getSoftwareDescription().getReferenceCode();
      OsFamily osFamily = osFamily().apply(operatingSystemReferenceCode);
      if (osFamily == OsFamily.UNRECOGNIZED) {
         logger.debug("Cannot determine os family for item: %s", operatingSystem);
      }
      String osVersion = osVersion().apply(softwareLicense.getSoftwareDescription().getVersion());
      if (osVersion == null) {
         logger.debug("Cannot determine os version for item: %s", operatingSystem);
      }
      Integer bits = osBits().apply(operatingSystemReferenceCode);
      if (bits == null) {
         logger.debug("Cannot determine os bits for item: %s", operatingSystem);
      }

      org.jclouds.compute.domain.OperatingSystem os = org.jclouds.compute.domain.OperatingSystem.builder()
              .description(softwareLicense.getSoftwareDescription().getLongDescription())
              .family(osFamily)
              .version(osVersion)
              .is64Bit(Objects.equal(bits, 64))
              .build();

      return new ImageBuilder()
              .ids(operatingSystemReferenceCode)
              .description(operatingSystemReferenceCode)
              .operatingSystem(os)
              .status(Image.Status.AVAILABLE)
              .build();
   }
}
