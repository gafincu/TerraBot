package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;
import main.model.visitor.TicketRiskVisitor;

import java.util.ArrayList;
import java.util.List;

public final class GenerateTicketRiskReportCommand implements Command {
    private static final double LOW_RISK_THRESHOLD = 4.0;
    private static final double MODERATE_RISK_THRESHOLD = 9.0;
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public GenerateTicketRiskReportCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();

        List<Ticket> activeTickets = new ArrayList<>();
        for (Ticket t : db.getTickets()) {
            if ("OPEN".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus())) {
                activeTickets.add(t);
            }
        }

        ObjectNode report = mapper.createObjectNode();
        report.put("totalTickets", activeTickets.size());

        ObjectNode byType = mapper.createObjectNode();
        ObjectNode byPrio = mapper.createObjectNode();

        int bug = 0, feat = 0, ui = 0;
        int low = 0, med = 0, high = 0, crit = 0;

        TicketRiskVisitor visitor = new TicketRiskVisitor();
        double sumBug = 0, sumFeat = 0, sumUi = 0;

        for (Ticket t : activeTickets) {
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

            Double riskScore = t.accept(visitor);

            if ("BUG".equals(t.getType())) {
                sumBug += riskScore;
            } else if ("FEATURE_REQUEST".equals(t.getType())) {
                sumFeat += riskScore;
            } else if ("UI_FEEDBACK".equals(t.getType())) {
                sumUi += riskScore;
            }
        }

        byType.put("BUG", bug);
        byType.put("FEATURE_REQUEST", feat);
        byType.put("UI_FEEDBACK", ui);

        byPrio.put("LOW", low);
        byPrio.put("MEDIUM", med);
        byPrio.put("HIGH", high);
        byPrio.put("CRITICAL", crit);

        report.set("ticketsByType", byType);
        report.set("ticketsByPriority", byPrio);

        ObjectNode riskMap = mapper.createObjectNode();
        riskMap.put("BUG", getRiskLabel(bug == 0 ? 0 : sumBug / bug));
        riskMap.put("FEATURE_REQUEST", getRiskLabel(feat == 0 ? 0 : sumFeat / feat));
        riskMap.put("UI_FEEDBACK", getRiskLabel(ui == 0 ? 0 : sumUi / ui));

        report.set("riskByType", riskMap);

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "generateTicketRiskReport");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("report", report);
        output.add(res);
    }

    private String getRiskLabel(final double score) {
        if (score < LOW_RISK_THRESHOLD) {
            return "LOW";
        }
        if (score <= MODERATE_RISK_THRESHOLD) {
            return "MODERATE";
        }
        return "MAJOR";
    }
}
