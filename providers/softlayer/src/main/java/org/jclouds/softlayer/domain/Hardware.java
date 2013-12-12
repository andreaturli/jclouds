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
package org.jclouds.softlayer.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * Class Hardware
 *
 * @author Andrea Turli
 * @see <a href="http://sldn.softlayer.com/reference/datatypes/SoftLayer_Hardware"/>
 */
public class Hardware {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAddress(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected int accountId;
      protected int bareMetalInstanceFlag;
      protected String domain;
      protected String hostname;
      protected int hardwareStatusId;

      /**
       * @see Hardware#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see Hardware#getAccountId()
       */
      public T accountId(int accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see Hardware#getBareMetalInstanceFlag()
       */
      public T bareMetalInstanceFlag(int bareMetalInstanceFlag) {
         this.bareMetalInstanceFlag = bareMetalInstanceFlag;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Hardware#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      public T hostname(String hostname) {
         this.hostname = hostname;
         return self();
      }

      public T hardwareStatusId(int hardwareStatusId) {
         this.hardwareStatusId = hardwareStatusId;
         return self();
      }

      public Hardware build() {
         return new Hardware(id, accountId, bareMetalInstanceFlag, domain, hostname, hardwareStatusId);
      }

      public T fromAddress(Hardware in) {
         return this
                 .id(in.getId())
                 .accountId(in.getAccountId())
                 .bareMetalInstanceFlag(in.getBareMetalInstanceFlag())
                 .domain(in.getDomain())
                 .hostname(in.getHostname())
                 .hardwareStatusId(in.getHardwareStatusId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final int accountId;
   private final int bareMetalInstanceFlag;
   private final String domain;
   private final String hostname;
   private final int hardwareStatusId;

   @ConstructorProperties({
           "id", "accountId", "bareMetalInstanceFlag", "domain", "hostname", "hardwareStatusId"
   })
   protected Hardware(int id, int accountId, int bareMetalInstanceFlag, String domain, String hostname,
                      int hardwareStatusId) {
      this.id = id;
      this.accountId = accountId;
      this.bareMetalInstanceFlag = bareMetalInstanceFlag;
      this.domain = domain;
      this.hostname = hostname;
      this.hardwareStatusId = hardwareStatusId;
   }

   /**
    * @return The unique id of the hardware.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The accountId of the hardware.
    */
   public int getAccountId() {
      return this.accountId;
   }

   /**
    * @return The bareMetalInstanceFlag of the hardware.
    */
   public int getBareMetalInstanceFlag() {
      return this.bareMetalInstanceFlag;
   }

   /**
    * @return The domain of the hardware.
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return The hostname of the hardware.
    */
   @Nullable
   public String getHostname() {
      return this.hostname;
   }

   /**
    * @return The hardware status Id of the hardware.
    */
   public int getHardwareStatusId() {
      return hardwareStatusId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Hardware that = Hardware.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("accountId", accountId)
              .add("bareMetalInstanceFlag", bareMetalInstanceFlag)
              .add("domain", domain)
              .add("hostname", hostname)
              .add("hardwareStatusId", hardwareStatusId)
              .toString();
   }
}
