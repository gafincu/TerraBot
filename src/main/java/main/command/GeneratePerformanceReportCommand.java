package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Developer;
import main.model.Ticket;
import main.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GeneratePerformanceReportCommand implements Command {
    private static final double SENIOR_BONUS = 1.5;
    private static final double MID_BONUS = 1.2;
    private static final double BASE_SCORE_MULTIPLIER = 10.0;

    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public GeneratePerformanceReportCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();

        List<ObjectNode> devStats = new ArrayList<>();
        List<User> users = new ArrayList<>(db.getUsers());
        users.sort(Comparator.comparing(User::getUsername));

        for (User u : users) {
            if ("DEVELOPER".equals(u.getRole())) {
                Developer dev = (Developer) u;

                long totalTickets = 0;
                long totalDays = 0;

                for (Ticket t : db.getTickets()) {
                    if ("CLOSED".equals(t.getStatus())
                            && dev.getUsername().equals(t.getAssignedTo())) {
                        if (t.getAssignedAt() != null && t.getSolvedAt() != null
                                && !t.getAssignedAt().isEmpty() && !t.getSolvedAt().isEmpty()) {
                            LocalDate start = LocalDate.parse(t.getAssignedAt());
                            LocalDate end = LocalDate.parse(t.getSolvedAt());
                            totalDays += (ChronoUnit.DAYS.between(start, end) + 1);
                            totalTickets++;
                        }
                    }
                }

                ObjectNode stat = mapper.createObjectNode();
                stat.put("username", dev.getUsername());
                stat.put("completedTickets", totalTickets);

                double avgTime = (totalTickets == 0) ? 0.0 : (double) totalDays / totalTickets;
                stat.put("averageResolutionTime", round(avgTime));

                double score = 0.0;
                if (totalTickets > 0) {
                    double seniorityBonus = "SENIOR".equals(dev.getSeniority()) ? SENIOR_BONUS
                            : ("MID".equals(dev.getSeniority()) ? MID_BONUS : 1.0);
                    score = (totalTickets * BASE_SCORE_MULTIPLIER
                            / (avgTime + 1)) * seniorityBonus;
                }
                stat.put("performanceScore", round(score));

                devStats.add(stat);
            }
        }

        ObjectNode report = mapper.createObjectNode();
        report.set("developers", mapper.valueToTree(devStats));

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "generatePerformanceReport");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("report", report);
        output.add(res);
    }

    private double round(final double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
