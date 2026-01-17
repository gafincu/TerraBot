package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import main.model.visitor.TicketVisitor;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id", "type", "title", "businessPriority", "status",
        "reportedBy", "createdAt", "assignedTo", "assignedAt",
        "solvedAt", "comments"
})
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

    public Ticket() {}

    public abstract <R> R accept(TicketVisitor<R> visitor);

    // --- Getters ---
    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getBusinessPriority() { return businessPriority; }
    public String getInitialPriority() { return initialPriority; }
    public String getStatus() { return status; }
    public String getExpertiseArea() { return expertiseArea; }
    public String getReportedBy() { return reportedBy; }
    public String getCreatedAt() { return createdAt; }
    public String getAssignedTo() { return assignedTo; }
    public String getAssignedAt() { return assignedAt; }
    public String getSolvedAt() { return solvedAt; }
    public List<Comment> getComments() { return comments; }
    public List<HistoryLog> getHistory() { return history; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    public void setBusinessPriority(String businessPriority) {
        this.businessPriority = businessPriority;
        if (this.initialPriority == null) {
            this.initialPriority = businessPriority;
        }
    }

    public void setInitialPriority(String initialPriority) { this.initialPriority = initialPriority; }

    public void setStatus(String status) { this.status = status; }
    public void setExpertiseArea(String expertiseArea) { this.expertiseArea = expertiseArea; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setAssignedAt(String assignedAt) { this.assignedAt = assignedAt; }
    public void setSolvedAt(String solvedAt) { this.solvedAt = solvedAt; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}