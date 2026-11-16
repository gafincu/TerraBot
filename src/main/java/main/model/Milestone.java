package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Milestone {
    private String name;
    private List<String> blockingFor;
    private String dueDate;
    private String createdAt;
    private List<Integer> tickets;
    private List<String> assignedDevs;
    private String createdBy;
    private String status;

    private Boolean isBlocked;
    private Long daysUntilDue;
    private Long overdueBy;
    private List<Integer> openTickets;
    private List<Integer> closedTickets;
    private Double completionPercentage;
    private List<Repartition> repartition;

    @JsonIgnore
    private boolean notifiedDueTomorrow = false;

    public Milestone() {
    }

    public static final class Repartition {
        private String developer;
        private List<Integer> assignedTickets;

        public Repartition(final String developer, final List<Integer> assignedTickets) {
            this.developer = developer;
            this.assignedTickets = assignedTickets;
        }

        public String getDeveloper() {
            return developer;
        }

        public List<Integer> getAssignedTickets() {
            return assignedTickets;
        }
    }

    @JsonIgnore
    public boolean isNotifiedDueTomorrow() {
        return notifiedDueTomorrow;
    }

    public void setNotifiedDueTomorrow(final boolean notifiedDueTomorrow) {
        this.notifiedDueTomorrow = notifiedDueTomorrow;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getBlockingFor() {
        return blockingFor;
    }

    public void setBlockingFor(final List<String> blockingFor) {
        this.blockingFor = blockingFor;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(final String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(final List<Integer> tickets) {
        this.tickets = tickets;
    }

    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    public void setAssignedDevs(final List<String> assignedDevs) {
        this.assignedDevs = assignedDevs;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(final Boolean blocked) {
        isBlocked = blocked;
    }

    public Long getDaysUntilDue() {
        return daysUntilDue;
    }

    public void setDaysUntilDue(final Long daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    public Long getOverdueBy() {
        return overdueBy;
    }

    public void setOverdueBy(final Long overdueBy) {
        this.overdueBy = overdueBy;
    }

    public List<Integer> getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(final List<Integer> openTickets) {
        this.openTickets = openTickets;
    }

    public List<Integer> getClosedTickets() {
        return closedTickets;
    }

    public void setClosedTickets(final List<Integer> closedTickets) {
        this.closedTickets = closedTickets;
    }

    public Double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(final Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public List<Repartition> getRepartition() {
        return repartition;
    }

    public void setRepartition(final List<Repartition> repartition) {
        this.repartition = repartition;
    }
}
