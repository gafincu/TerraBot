package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.visitor.TicketVisitor;

public final class UiFeedback extends Ticket {
    private String uiElementId;
    private String businessValue;
    private Integer usabilityScore;
    private String suggestedFix;
    private String screenshotUrl;

    @Override
    public <R> R accept(final TicketVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @JsonIgnore
    public String getUiElementId() {
        return uiElementId;
    }

    @JsonIgnore
    public String getBusinessValue() {
        return businessValue;
    }

    @JsonIgnore
    public Integer getUsabilityScore() {
        return usabilityScore;
    }

    @JsonIgnore
    public String getSuggestedFix() {
        return suggestedFix;
    }

    @JsonIgnore
    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    @JsonProperty("uiElementId")
    public void setUiElementId(final String uiElementId) {
        this.uiElementId = uiElementId;
    }

    @JsonProperty("businessValue")
    public void setBusinessValue(final String businessValue) {
        this.businessValue = businessValue;
    }

    @JsonProperty("usabilityScore")
    public void setUsabilityScore(final Integer usabilityScore) {
        this.usabilityScore = usabilityScore;
    }

    @JsonProperty("suggestedFix")
    public void setSuggestedFix(final String suggestedFix) {
        this.suggestedFix = suggestedFix;
    }

    @JsonProperty("screenshotUrl")
    public void setScreenshotUrl(final String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }
}
