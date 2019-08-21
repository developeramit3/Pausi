package com.t.pausi.Bean;

import com.t.pausi.Pojo.AgentDetails;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.Pojo.PropertyImage;

import java.util.List;

public class DataHolder {
    public String property_image;
    public String property_price;
    public String property_sale_type;
    public String deals_name;
    public String year_name;
    public List<PropertyImage> propertyImageList;
    private PropertyDetails propertyDetails;
    private AgentDetails agentDetails;
    public AgentDetails getAgentDetails() {
        return agentDetails;
    }

    public void setAgentDetails(AgentDetails agentDetails) {
        this.agentDetails = agentDetails;
    }



    public PropertyDetails getPropertyDetails() {
        return propertyDetails;
    }

    public void setPropertyDetails(PropertyDetails propertyDetails) {
        this.propertyDetails = propertyDetails;
    }

    public String getYear_name() {
        return year_name;
    }

    public void setYear_name(String year_name) {
        this.year_name = year_name;
    }


    public String getDeals_name() {
        return deals_name;
    }

    public void setDeals_name(String deals_name) {
        this.deals_name = deals_name;
    }


    public List<PropertyImage> getPropertyImageList() {
        return propertyImageList;
    }

    public void setPropertyImageList(List<PropertyImage> propertyImageList) {
        this.propertyImageList = propertyImageList;
    }

    public String getProperty_image() {
        return property_image;
    }

    public void setProperty_image(String property_image) {
        this.property_image = property_image;
    }

    public String getProperty_price() {
        return property_price;
    }

    public void setProperty_price(String property_price) {
        this.property_price = property_price;
    }

    public String getProperty_sale_type() {
        return property_sale_type;
    }

    public void setProperty_sale_type(String property_sale_type) {
        this.property_sale_type = property_sale_type;
    }


}
