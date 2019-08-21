package com.t.pausi.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PropertyDetailList {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("property_name")
    @Expose
    private String propertyName;
    @SerializedName("property_price")
    @Expose
    private String propertyPrice;
    @SerializedName("modified_price")
    @Expose
    private String modifiedPrice;
    @SerializedName("property_type")
    @Expose
    private String propertyType;
    @SerializedName("sq_feet")
    @Expose
    private String sqFeet;
    @SerializedName("beds")
    @Expose
    private String beds;
    @SerializedName("baths")
    @Expose
    private String baths;
    @SerializedName("build_year")
    @Expose
    private String buildYear;
    @SerializedName("acre_area")
    @Expose
    private String acreArea;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("address_2")
    @Expose
    private String address2;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("sale_type")
    @Expose
    private String saleType;
    @SerializedName("property_description")
    @Expose
    private String propertyDescription;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("property_amenities")
    @Expose
    private List<PropertyAmenity> propertyAmenities = null;
    @SerializedName("notified")
    @Expose
    private String notified;
    @SerializedName("property_images")
    @Expose
    private List<PropertyImage> propertyImages = null;
    @SerializedName("similar_listing_list")
    @Expose
    private List<SimilarListingList> similarListingList = null;
    @SerializedName("agent_details")
    @Expose
    private AgentDetails agentDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getModifiedPrice() {
        return modifiedPrice;
    }

    public void setModifiedPrice(String modifiedPrice) {
        this.modifiedPrice = modifiedPrice;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getSqFeet() {
        return sqFeet;
    }

    public void setSqFeet(String sqFeet) {
        this.sqFeet = sqFeet;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getBaths() {
        return baths;
    }

    public void setBaths(String baths) {
        this.baths = baths;
    }

    public String getBuildYear() {
        return buildYear;
    }

    public void setBuildYear(String buildYear) {
        this.buildYear = buildYear;
    }

    public String getAcreArea() {
        return acreArea;
    }

    public void setAcreArea(String acreArea) {
        this.acreArea = acreArea;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
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

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<PropertyAmenity> getPropertyAmenities() {
        return propertyAmenities;
    }

    public void setPropertyAmenities(List<PropertyAmenity> propertyAmenities) {
        this.propertyAmenities = propertyAmenities;
    }

    public String getNotified() {
        return notified;
    }

    public void setNotified(String notified) {
        this.notified = notified;
    }

    public List<PropertyImage> getPropertyImages() {
        return propertyImages;
    }

    public void setPropertyImages(List<PropertyImage> propertyImages) {
        this.propertyImages = propertyImages;
    }

    public List<SimilarListingList> getSimilarListingList() {
        return similarListingList;
    }

    public void setSimilarListingList(List<SimilarListingList> similarListingList) {
        this.similarListingList = similarListingList;
    }

    public AgentDetails getAgentDetails() {
        return agentDetails;
    }

    public void setAgentDetails(AgentDetails agentDetails) {
        this.agentDetails = agentDetails;
    }
}
