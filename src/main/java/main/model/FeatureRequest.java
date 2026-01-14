package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FeatureRequest extends Ticket {
    @JsonIgnore private String businessValue;
    @JsonIgnore private String customerDemand;

    public String getBusinessValue() { return businessValue; }
    public void setBusinessValue(String businessValue) { this.businessValue = businessValue; }

    public String getCustomerDemand() { return customerDemand; }
    public void setCustomerDemand(String customerDemand) { this.customerDemand = customerDemand; }
}