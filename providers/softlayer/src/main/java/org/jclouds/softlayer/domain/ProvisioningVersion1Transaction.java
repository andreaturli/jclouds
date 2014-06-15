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

import com.google.common.base.Objects;

/**
 * @see <a href="http://sldn.softlayer.com/reference/datatypes/SoftLayer_Provisioning_Version1_Transaction"/>
 */
public class ProvisioningVersion1Transaction {

   private final int id;
   private final int guestId;
   private final int hardwareId;
   private final int elapsedSeconds;
   private final long pendingTransactionCount;

   public ProvisioningVersion1Transaction(int id, int guestId, int hardwareId, int elapsedSeconds, long pendingTransactionCount) {
      this.id = id;
      this.guestId = guestId;
      this.hardwareId = hardwareId;
      this.elapsedSeconds = elapsedSeconds;
      this.pendingTransactionCount = pendingTransactionCount;
   }

   public int getId() {
      return id;
   }

   public int getGuestId() {
      return guestId;
   }

   public int getHardwareId() {
      return hardwareId;
   }

   public int getElapsedSeconds() {
      return elapsedSeconds;
   }

   public long getPendingTransactionCount() {
      return pendingTransactionCount;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProvisioningVersion1Transaction that = (ProvisioningVersion1Transaction) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.guestId, that.guestId) &&
              Objects.equal(this.hardwareId, that.hardwareId) &&
              Objects.equal(this.elapsedSeconds, that.elapsedSeconds) &&
              Objects.equal(this.pendingTransactionCount, that.pendingTransactionCount);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, guestId, hardwareId, elapsedSeconds, pendingTransactionCount);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .addValue(id)
              .addValue(guestId)
              .addValue(hardwareId)
              .addValue(elapsedSeconds)
              .addValue(pendingTransactionCount)
              .toString();
   }
}
