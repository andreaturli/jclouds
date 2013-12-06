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
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.jclouds.softlayer.domain.VirtualGuest;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

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

   @Override
   public Image apply(VirtualGuest from) {

      checkNotNull(from, "from");
      ImageBuilder builder = new ImageBuilder().ids(from.getId() + "")
              .name(from.getOperatingSystem().getSoftwareLicense().getSoftwareDescription().getReferenceCode())
              .status(Image.Status.AVAILABLE);

      SoftwareLicense softwareLicense = from.getOperatingSystem().getSoftwareLicense();
      if (softwareLicense == null) {
         return builder.operatingSystem(OperatingSystem.builder().description(from.getOperatingSystem().getId() + "")
                 .build())
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

      OperatingSystem os = OperatingSystem.builder()
              .description(longDescription)
              .family(osFamily)
              .version(from.getOperatingSystem().getSoftwareLicense().getSoftwareDescription().getVersion())
              .is64Bit(Objects.equal(bits, 64))
              .build();
      return builder.operatingSystem(os).build();
   }
}