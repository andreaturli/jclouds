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
package org.jclouds.numergy.compute.functions;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageToOperatingSystem;
import org.jclouds.openstack.nova.v2_0.domain.Image;

import com.google.common.base.Objects;

public class NumergyImageToOperatingSystem extends ImageToOperatingSystem {

   public static final Pattern DEFAULT_PATTERN = Pattern.compile("([0-9.]+)_(x[0-9.]+)_([0-9.]+)");
   // Windows Server 2008 R2 x64
   public static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows (.*) (x[86][64])");
   public static final String UNSPECIFIED = "unspecified";

   @javax.annotation.Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public NumergyImageToOperatingSystem(Map<OsFamily, Map<String, String>> osVersionMap) {
      super(osVersionMap);
   }

   @Override
   public OperatingSystem apply(Image from) {
      String osVersion = null;

      String imageName = Objects.firstNonNull(from.getName(), UNSPECIFIED);

      boolean is64Bit = true;

      OsFamily osFamily = OsFamily.WINDOWS;
      if (!imageName.endsWith("-DEPRECATED")) {
         if (imageName.indexOf("Windows") != -1) {
            Matcher matcher = WINDOWS_PATTERN.matcher(from.getName());
            if (matcher.find()) {
               osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(1), osVersionMap);
               is64Bit = matcher.group(2).equals("x64");
            }
         } else {
            Matcher matcher = DEFAULT_PATTERN.matcher(imageName);
            if (matcher.find() && matcher.groupCount() >= 1) {
               String in = matcher.group(1);
               if (imageName.startsWith("debian")) {
                  osFamily = OsFamily.DEBIAN;
                  in = in.contains(".") ? in.substring(0, in.lastIndexOf('.')) : in;
               } else if (imageName.startsWith("centos")) {
                  osFamily = OsFamily.CENTOS;
                  if (in.endsWith("7")) {
                     in = new StringBuilder().append(in).append(".0").toString();
                  } else if (in.endsWith("65")) {
                     in = "centos6.5";
                  } else if (in.endsWith("60")) {
                     in = "centos6.0";
                  }
               } else if (imageName.startsWith("ubuntu")) {
                  osFamily = OsFamily.UBUNTU;
               } else if (imageName.startsWith("rhel")) {
                  osFamily = OsFamily.RHEL;
               } else {
                  logger.trace("could not parse operating system family for image(%s): %s", from.getId(), imageName);
                  osFamily = OsFamily.LINUX;
               }
               osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, in, osVersionMap);
               is64Bit = matcher.group(3).equals("64");
            }
         }
      }
      return new OperatingSystem(osFamily, imageName, osVersion, null, imageName, is64Bit);
   }

}
