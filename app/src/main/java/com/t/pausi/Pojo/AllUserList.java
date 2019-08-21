package com.t.pausi.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllUserList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("id_proof")
    @Expose
    private String idProof;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country_code")
    @Expose
    private Object countryCode;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("social_id")
    @Expose
    private String socialId;
    @SerializedName("register_id")
    @Expose
    private Object registerId;
    @SerializedName("service_id")
    @Expose
    private Object serviceId;
    @SerializedName("broker")
    @Expose
    private String broker;
    @SerializedName("affordable")
    @Expose
    private String affordable;
    @SerializedName("deals")
    @Expose
    private String deals;
    @SerializedName("broker_title")
    @Expose
    private String brokerTitle;
    @SerializedName("offer")
    @Expose
    private String offer;
    @SerializedName("close_deals")
    @Expose
    private String closeDeals;
    @SerializedName("agent_since")
    @Expose
    private String agentSince;
    @SerializedName("agent_join_year")
    @Expose
    private String agentJoinYear;
    @SerializedName("ios_device_token")
    @Expose
    private String iosDeviceToken;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("sold_date")
    @Expose
    private String soldDate;
    @SerializedName("assign_user_id")
    @Expose
    private String assignUserId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Object countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public Object getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Object registerId) {
        this.registerId = registerId;
    }

    public Object getServiceId() {
        return serviceId;
    }

    public void setServiceId(Object serviceId) {
        this.serviceId = serviceId;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getAffordable() {
        return affordable;
    }

    public void setAffordable(String affordable) {
        this.affordable = affordable;
    }

    public String getDeals() {
        return deals;
    }

    public void setDeals(String deals) {
        this.deals = deals;
    }

    public String getBrokerTitle() {
        return brokerTitle;
    }

    public void setBrokerTitle(String brokerTitle) {
        this.brokerTitle = brokerTitle;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getCloseDeals() {
        return closeDeals;
    }

    public void setCloseDeals(String closeDeals) {
        this.closeDeals = closeDeals;
    }

    public String getAgentSince() {
        return agentSince;
    }

    public void setAgentSince(String agentSince) {
        this.agentSince = agentSince;
    }

    public String getAgentJoinYear() {
        return agentJoinYear;
    }

    public void setAgentJoinYear(String agentJoinYear) {
        this.agentJoinYear = agentJoinYear;
    }

    public String getIosDeviceToken() {
        return iosDeviceToken;
    }

    public void setIosDeviceToken(String iosDeviceToken) {
        this.iosDeviceToken = iosDeviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(String soldDate) {
        this.soldDate = soldDate;
    }

    public String getAssignUserId() {
        return assignUserId;
    }

    public void setAssignUserId(String assignUserId) {
        this.assignUserId = assignUserId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
