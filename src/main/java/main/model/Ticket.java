package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Ticket {
    private int id;
    private String type;
    private String title;
    private String description;
    private String businessPriority; // LOW, MEDIUM, HIGH, CRITICAL
    private String status = "OPEN";
    private String expertiseArea;
    private String reportedBy;
    private String createdAt;

    // Câmpuri interne gestionate de aplicație
    private String assignedTo = "";
    private String assignedAt = "";
    private String solvedAt = "";
    private List<Object> comments = new ArrayList<>();
}