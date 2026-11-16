package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import main.model.visitor.TicketVisitor;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Bug.class, name = "BUG"),
        @JsonSubTypes.Type(value = FeatureRequest.class, name = "FEATURE_REQUEST"),
        @JsonSubTypes.Type(value = UiFeedback.class, name = "UI_FEEDBACK")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Ticket {

    protected int id;
    protected String type;
    protected String title;

    @JsonIgnore
    protected String description;

    protected String businessPriority;

    @JsonIgnore
    protected String initialPriority;

    protected String status;

    @JsonIgnore
    protected String expertiseArea;

    protected String reportedBy;
    protected String createdAt;
    protected String assignedTo;
    protected String assignedAt;
    protected String solvedAt;

    protected List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    protected List<HistoryLog> history = new ArrayList<>();

    public Ticket() {
    }

    public abstract <R> R accept(TicketVisitor<R> visitor);

    /**
     * Returneaza ID-ul tichetului.
     * @return id.
     */
    public final int getId() {
        return id;
    }

    public final String getType() {
        return type;
    }

    public final String getTitle() {
        return title;
    }

    public final String getDescription() {
        return description;
    }

    public final String getBusinessPriority() {
        return businessPriority;
    }

    public final String getInitialPriority() {
        return initialPriority;
    }

    public final String getStatus() {
        return status;
    }

    public final String getExpertiseArea() {
        return expertiseArea;
    }

    public final String getReportedBy() {
        return reportedBy;
    }

    public final String getCreatedAt() {
        return createdAt;
    }

    public final String getAssignedTo() {
        return assignedTo;
    }

    public final String getAssignedAt() {
        return assignedAt;
    }

    public final String getSolvedAt() {
        return solvedAt;
    }

    public final List<Comment> getComments() {
        return comments;
    }

    public final List<HistoryLog> getHistory() {
        return history;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final void setTitle(final String title) {
        this.title = title;
    }

    public final void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Seteaza prioritatea de business.
     * @param businessPriority prioritatea.
     */
    public final void setBusinessPriority(final String businessPriority) {
        this.businessPriority = businessPriority;
        if (this.initialPriority == null) {
            this.initialPriority = businessPriority;
        }
    }

    public final void setInitialPriority(final String initialPriority) {
        this.initialPriority = initialPriority;
    }

    public final void setStatus(final String status) {
        this.status = status;
    }

    public final void setExpertiseArea(final String expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    public final void setReportedBy(final String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public final void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public final void setAssignedTo(final String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public final void setAssignedAt(final String assignedAt) {
        this.assignedAt = assignedAt;
    }

    public final void setSolvedAt(final String solvedAt) {
        this.solvedAt = solvedAt;
    }

    public final void setComments(final List<Comment> comments) {
        this.comments = comments;
    }
}
