package org.jclouds.softlayer.binders;

import org.jclouds.softlayer.domain.internal.NetworkComponentsOption;

import java.util.Set;

public class ObjectData {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromObjectData(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String hostname;
      protected String domain;
      protected int startCpus;
      protected int maxMemory;
      protected boolean hourlyBillingFlag;
      protected String operatingSystemReferenceCode;
      protected boolean localDiskFlag;
      protected String datacenterName;
      protected Set<NetworkComponentsOption> networkComponents;
      protected String globalIdentifier;

      public T hostname(String hostname) {
         this.hostname = hostname;
         return self();
      }

      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      public T startCpus(int startCpus) {
         this.startCpus = startCpus;
         return self();
      }

      public T maxMemory(int maxMemory) {
         this.maxMemory = maxMemory;
         return self();
      }

      public T hourlyBillingFlag(boolean hourlyBillingFlag) {
         this.hourlyBillingFlag = hourlyBillingFlag;
         return self();
      }

      public T operatingSystemReferenceCode(String operatingSystemReferenceCode) {
         this.operatingSystemReferenceCode = operatingSystemReferenceCode;
         return self();
      }

      public T localDiskFlag(boolean localDiskFlag) {
         this.localDiskFlag = localDiskFlag;
         return self();
      }

      public T datacenterName(String datacenterName) {
         this.datacenterName = datacenterName;
         return self();
      }

      public T networkComponents(Set<NetworkComponentsOption> networkComponents) {
         this.networkComponents = networkComponents;
         return self();
      }

      public T globalIdentifier(String globalIdentifier) {
         this.globalIdentifier = globalIdentifier;
         return self();
      }

      public ObjectData build() {
         return new ObjectData(hostname, domain, startCpus, maxMemory, hourlyBillingFlag, operatingSystemReferenceCode,
                 localDiskFlag, datacenterName, networkComponents, globalIdentifier);
      }

      public T fromObjectData(ObjectData in) {
         return this
                 .hostname(in.getHostname())
                 .domain(in.getDomain())
                 .startCpus(in.getStartCpus())
                 .maxMemory(in.getMaxMemory())
                 .hourlyBillingFlag(in.isHourlyBillingFlag())
                 .operatingSystemReferenceCode(in.getOperatingSystemReferenceCode())
                 .localDiskFlag(in.isLocalDiskFlag())
                 .datacenterName(in.getDatacenter().getName())
                 .networkComponents(in.getNetworkComponents())
                 .globalIdentifier(in.getBlockDeviceTemplateGroup().getGlobalIdentifier());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String hostname;
   private final String domain;
   private final int startCpus;
   private final int maxMemory;
   private final boolean hourlyBillingFlag;
   private final String operatingSystemReferenceCode;
   private final boolean localDiskFlag;
   private final Datacenter datacenter;
   private final Set<NetworkComponentsOption> networkComponents;
   private final BlockDeviceTemplateGroup blockDeviceTemplateGroup;

   protected ObjectData(String hostname, String domain, int startCpus, int maxMemory, boolean hourlyBillingFlag,
                        String operatingSystemReferenceCode, boolean localDiskFlag, String datacenterName,
                        Set<NetworkComponentsOption> networkComponents, String globalIdentifier) {
      this.hostname = hostname;
      this.domain = domain;
      this.startCpus = startCpus;
      this.maxMemory = maxMemory;
      this.hourlyBillingFlag = hourlyBillingFlag;
      this.operatingSystemReferenceCode = operatingSystemReferenceCode;
      this.localDiskFlag = localDiskFlag;
      this.datacenter = new Datacenter(datacenterName);
      this.networkComponents = networkComponents;
      this.blockDeviceTemplateGroup = new BlockDeviceTemplateGroup(globalIdentifier);
   }

   public String getHostname() {
      return hostname;
   }

   public String getDomain() {
      return domain;
   }

   public int getStartCpus() {
      return startCpus;
   }

   public int getMaxMemory() {
      return maxMemory;
   }

   public boolean isHourlyBillingFlag() {
      return hourlyBillingFlag;
   }

   public String getOperatingSystemReferenceCode() {
      return operatingSystemReferenceCode;
   }

   public boolean isLocalDiskFlag() {
      return localDiskFlag;
   }

   public Datacenter getDatacenter() {
      return datacenter;
   }

   public Set<NetworkComponentsOption> getNetworkComponents() {
      return networkComponents;
   }

   public BlockDeviceTemplateGroup getBlockDeviceTemplateGroup() {
      return blockDeviceTemplateGroup;
   }

   private static class Datacenter {

      private String name;

      private Datacenter(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }

   }

   private class BlockDeviceTemplateGroup {
      private final String globalIdentifier;

      public BlockDeviceTemplateGroup(String globalIdentifier) {
         this.globalIdentifier = globalIdentifier;
      }

      public String getGlobalIdentifier() {
         return globalIdentifier;
      }
   }
}