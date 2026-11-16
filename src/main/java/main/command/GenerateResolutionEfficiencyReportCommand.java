package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;
import main.model.visitor.ResolutionEfficiencyVisitor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class GenerateResolutionEfficiencyReportCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public GenerateResolutionEfficiencyReportCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        LocalDate reportDate = LocalDate.parse(timestamp);
        Database db = Database.getInstance();

        List<Ticket> allTickets = db.getTickets();

        int bug = 0, feat = 0, ui = 0;
        int low = 0, med = 0, high = 0, crit = 0;

        double effBug = 0, effFeat = 0, effUi = 0;
        int cntBug = 0, cntFeat = 0, cntUi = 0;

        ResolutionEfficiencyVisitor visitor = new ResolutionEfficiencyVisitor();

        for (Ticket t : allTickets) {
            String solvedStr = t.getSolvedAt();
            boolean hasSolvedDate = (solvedStr != null && !solvedStr.trim().isEmpty());

            if (hasSolvedDate) {
                try {
                    LocalDate solved = LocalDate.parse(solvedStr.trim());
                    if (!solved.isBefore(reportDate)) {
                        continue;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }

            if ("BUG".equals(t.getType()) && !hasSolvedDate) {
                continue;
            }

            if ("BUG".equals(t.getType())) {
                bug++;
            } else if ("FEATURE_REQUEST".equals(t.getType())) {
                feat++;
            } else if ("UI_FEEDBACK".equals(t.getType())) {
                ui++;
            }

            switch (t.getBusinessPriority()) {
                case "LOW": low++; break;
                case "MEDIUM": med++; break;
                case "HIGH": high++; break;
                case "CRITICAL": crit++; break;
                default: break;
            }

            long days = 1;
            String assignedStr = t.getAssignedAt();
            boolean hasAssignedDate = (assignedStr != null && !assignedStr.trim().isEmpty());

            if (hasAssignedDate && hasSolvedDate) {
                try {
                    LocalDate start = LocalDate.parse(assignedStr.trim());
                    LocalDate end = LocalDate.parse(solvedStr.trim());
                    days = ChronoUnit.DAYS.between(start, end);
                    if (days == 0) {
                        days = 1;
                    }
                } catch (Exception e) {
                    days = 1;
                }
            }

            Double weightedComplexity = t.accept(visitor);
            double efficiency = weightedComplexity / days;

            if ("BUG".equals(t.getType())) {
                effBug += efficiency;
                cntBug++;
            } else if ("FEATURE_REQUEST".equals(t.getType())) {
                effFeat += efficiency;
                cntFeat++;
            } else if ("UI_FEEDBACK".equals(t.getType())) {
                effUi += efficiency;
                cntUi++;
            }
        }

        ObjectNode report = mapper.createObjectNode();
        report.put("totalTickets", bug + feat + ui);

        ObjectNode byType = mapper.createObjectNode();
        byType.put("BUG", bug);
        byType.put("FEATURE_REQUEST", feat);
        byType.put("UI_FEEDBACK", ui);
        report.set("ticketsByType", byType);

        ObjectNode byPrio = mapper.createObjectNode();
        byPrio.put("LOW", low);
        byPrio.put("MEDIUM", med);
        byPrio.put("HIGH", high);
        byPrio.put("CRITICAL", crit);
        report.set("ticketsByPriority", byPrio);

        ObjectNode effMap = mapper.createObjectNode();
        effMap.put("BUG", cntBug == 0 ? 0.0 : round(effBug / cntBug));
        effMap.put("FEATURE_REQUEST", cntFeat == 0 ? 0.0 : round(effFeat / cntFeat));
        effMap.put("UI_FEEDBACK", cntUi == 0 ? 0.0 : round(effUi / cntUi));
        report.set("efficiencyByType", effMap);

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "generateResolutionEfficiencyReport");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("report", report);
        output.add(res);
    }

    private double round(final double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
