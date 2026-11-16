package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;
import main.model.visitor.CustomerImpactVisitor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class GenerateCustomerImpactReportCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public GenerateCustomerImpactReportCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();

        List<Ticket> activeTickets = new ArrayList<>();
        for (Ticket t : db.getTickets()) {
            if ("LOW".equals(t.getBusinessPriority())) {
                continue;
            }
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

        CustomerImpactVisitor visitor = new CustomerImpactVisitor();
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

            Double score = t.accept(visitor);

            if ("BUG".equals(t.getType())) {
                sumBug += score;
            } else if ("FEATURE_REQUEST".equals(t.getType())) {
                sumFeat += score;
            } else if ("UI_FEEDBACK".equals(t.getType())) {
                sumUi += score;
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

        ObjectNode impact = mapper.createObjectNode();
        impact.put("BUG", bug == 0 ? 0.0 : round(sumBug / bug));
        impact.put("FEATURE_REQUEST", feat == 0 ? 0.0 : round(sumFeat / feat));
        impact.put("UI_FEEDBACK", ui == 0 ? 0.0 : round(sumUi / ui));

        report.set("customerImpactByType", impact);

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "generateCustomerImpactReport");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("report", report);
        output.add(res);
    }

    private double round(final double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
