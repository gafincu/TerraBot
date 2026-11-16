package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.visitor.TicketVisitor;

public final class FeatureRequest extends Ticket {
    private String businessValue;
    private String customerDemand;

    @Override
    public <R> R accept(final TicketVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @JsonIgnore
    public String getBusinessValue() {
        return businessValue;
    }

    @JsonIgnore
    public String getCustomerDemand() {
        return customerDemand;
    }

    @JsonProperty("businessValue")
    public void setBusinessValue(final String businessValue) {
        this.businessValue = businessValue;
    }

    @JsonProperty("customerDemand")
    public void setCustomerDemand(final String customerDemand) {
        this.customerDemand = customerDemand;
    }
}
