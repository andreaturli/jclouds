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
package org.jclouds.softlayer.features;

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.binders.VirtualGuestToJson;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestConfiguration;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Provides asynchronous access to VirtualGuest via their REST API.
 * <p/>
 *
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole, Andrea Turli
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
public interface VirtualGuestApi {
   public static String LIST_GUEST_MASK = "uuid;id;virtualGuests.powerState;virtualGuests.networkVlans;virtualGuests.operatingSystem.passwords;virtualGuests.datacenter;virtualGuests.billingItem;primaryBackendIpAddress;primaryIpAddress";
   public static String GUEST_MASK = "uuid;id;hostname;domain;fullyQualifiedDomainName;powerState;maxCpu;maxMemory;statusId;networkVlans;operatingSystem.passwords;createDate;accountId;datacenter;billingItem;primaryBackendIpAddress;primaryIpAddress;activeTransactionCount";

   /**
    * @return an account's associated virtual guest objects.
    */
   @Named("VirtualGuests:list")
   @GET
   @Path("/SoftLayer_Account/VirtualGuests")
   @QueryParams(keys = "objectMask", values = LIST_GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<VirtualGuest> listVirtualGuests();

   /**
    * Enables the creation of computing instances on an account.
    * @param virtualGuest this data type presents the structure in which all virtual guests will be presented.
    * @return the new Virtual Guest
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/createObject" />
    */
   @Named("VirtualGuests:create")
   @POST
   @Path("SoftLayer_Virtual_Guest")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualGuest createObject(@BinderParam(VirtualGuestToJson.class) VirtualGuest virtualGuest);

   /**
    * @param id
    *           id of the virtual guest
    * @return virtual guest or null if not found
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/getObject" />
    */
   @Named("VirtualGuests:get")
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/getObject")
   @QueryParams(keys = "objectMask", values = GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualGuest getObject(@PathParam("id") long id);

   /**
    * Delete a computing instance
    * @param id the id of the virtual guest.
    * @return the result of the deletion
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/deleteObject" />
    */
   @Named("VirtualGuests:delete")
   @GET
   @Path("SoftLayer_Virtual_Guest/{id}/deleteObject")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   boolean deleteObject(@PathParam("id") long id);

   /**
    * Determine options available when creating a computing instance
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Virtual_Guest/getCreateObjectOptions" />
    */
   @Named("VirtualGuests:getObjectOptions")
   @GET
   @Path("SoftLayer_Virtual_Guest/getCreateObjectOptions")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualGuestConfiguration getCreateObjectOptions();

   /**
    * hard reboot the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/rebootHard.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   Void rebootHardVirtualGuest(@PathParam("id") long id);

   /**
    * pause the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/pause.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   Void pauseVirtualGuest(@PathParam("id") long id);

   /**
    * resume the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/resume.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   Void resumeVirtualGuest(@PathParam("id") long id);
}
