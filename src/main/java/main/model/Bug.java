package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.visitor.TicketVisitor;

public final class Bug extends Ticket {
    private String expectedBehavior;
    private String actualBehavior;
    private String frequency;
    private String severity;
    private String environment;
    private Integer errorCode;

    @Override
    public <R> R accept(final TicketVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @JsonIgnore
    public String getExpectedBehavior() {
        return expectedBehavior;
    }

    @JsonIgnore
    public String getActualBehavior() {
        return actualBehavior;
    }

    @JsonIgnore
    public String getFrequency() {
        return frequency;
    }

    @JsonIgnore
    public String getSeverity() {
        return severity;
    }

    @JsonIgnore
    public String getEnvironment() {
        return environment;
    }

    @JsonIgnore
    public Integer getErrorCode() {
        return errorCode;
    }

    @JsonProperty("expectedBehavior")
    public void setExpectedBehavior(final String expectedBehavior) {
        this.expectedBehavior = expectedBehavior;
    }

    @JsonProperty("actualBehavior")
    public void setActualBehavior(final String actualBehavior) {
        this.actualBehavior = actualBehavior;
    }

    @JsonProperty("frequency")
    public void setFrequency(final String frequency) {
        this.frequency = frequency;
    }

    @JsonProperty("severity")
    public void setSeverity(final String severity) {
        this.severity = severity;
    }

    @JsonProperty("environment")
    public void setEnvironment(final String environment) {
        this.environment = environment;
    }

    @JsonProperty("errorCode")
    public void setErrorCode(final Integer errorCode) {
        this.errorCode = errorCode;
    }
}
