package main.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Milestone {
    // --- Câmpuri Persistente ---
    private String name;
    private List<String> blockingFor;
    private String dueDate;
    private String createdAt;
    private List<Integer> tickets;
    private List<String> assignedDevs;
    private String createdBy;
    private String status;

    // --- Câmpuri Transient (calculate la afișare) ---
    private Boolean isBlocked;
    private Long daysUntilDue;
    private Long overdueBy;
    private List<Integer> openTickets;
    private List<Integer> closedTickets;
    private Double completionPercentage;
    private List<Repartition> repartition;

    public Milestone() {}

    // --- Clasa Internă Repartition ---
    public static class Repartition {
        private String developer;
        private List<Integer> assignedTickets;

        public Repartition(String developer, List<Integer> assignedTickets) {
            this.developer = developer;
            this.assignedTickets = assignedTickets;
        }

        public String getDeveloper() { return developer; }
        public List<Integer> getAssignedTickets() { return assignedTickets; }
    }

    // --- Getters & Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getBlockingFor() { return blockingFor; }
    public void setBlockingFor(List<String> blockingFor) { this.blockingFor = blockingFor; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<Integer> getTickets() { return tickets; }
    public void setTickets(List<Integer> tickets) { this.tickets = tickets; }

    public List<String> getAssignedDevs() { return assignedDevs; }
    public void setAssignedDevs(List<String> assignedDevs) { this.assignedDevs = assignedDevs; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Getters/Setters pentru câmpuri calculate
    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean blocked) { isBlocked = blocked; }

    public Long getDaysUntilDue() { return daysUntilDue; }
    public void setDaysUntilDue(Long daysUntilDue) { this.daysUntilDue = daysUntilDue; }

    public Long getOverdueBy() { return overdueBy; }
    public void setOverdueBy(Long overdueBy) { this.overdueBy = overdueBy; }

    public List<Integer> getOpenTickets() { return openTickets; }
    public void setOpenTickets(List<Integer> openTickets) { this.openTickets = openTickets; }

    public List<Integer> getClosedTickets() { return closedTickets; }
    public void setClosedTickets(List<Integer> closedTickets) { this.closedTickets = closedTickets; }

    public Double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(Double completionPercentage) { this.completionPercentage = completionPercentage; }

    public List<Repartition> getRepartition() { return repartition; }
    public void setRepartition(List<Repartition> repartition) { this.repartition = repartition; }
}