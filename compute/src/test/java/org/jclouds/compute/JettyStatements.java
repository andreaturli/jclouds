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
package org.jclouds.compute;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import java.net.URI;

import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.extractTargzAndFlattenIntoDirectory;
import static org.jclouds.scriptbuilder.domain.Statements.literal;

public class JettyStatements {

   public static final URI JETTY_URL = URI.create(System.getProperty("test.jetty-url",
         "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.12.v20180830/jetty-distribution-9.4.12.v20180830.tar.gz));

   public static final String JETTY_HOME = "/usr/local/jetty";
   
   public static final int port = 8080;

   public static Statement version() {
      return exec(String.format("head -1 %s/VERSION.txt | cut -f1 -d ' '", JETTY_HOME));
   }

   public static Statement install() {
      return new StatementList(
            AdminAccess.builder().adminUsername("web").build(),
            InstallJDK.fromOpenJDK(),
            authorizePortInIpTables(),
            exec("sudo mkdir -p " + JETTY_HOME),
            exec("chown -R web " + JETTY_HOME),
            extractTargzAndFlattenIntoDirectory(JETTY_URL, JETTY_HOME));
   }

   private static Statement authorizePortInIpTables() {
      return new StatementList(
            exec("sudo iptables -I INPUT 1 -p tcp --dport " + port + " -j ACCEPT"),
            exec("sudo iptables-save"));
   }
   
   public static Statement start() {
      return new StatementList(
            literal(String.format("JETTY_PORT=%d nohup %s/bin/jetty.sh start > start.log 2>&1 < /dev/null &", port, JETTY_HOME)),
            literal("test $? && sleep 1")); // in case it is slow starting the proc
   }
   
   public static Statement stop() {
      return literal(JETTY_HOME + "/bin/jetty.sh stop");
   }
}
