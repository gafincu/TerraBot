package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;
import main.model.visitor.CustomerImpactVisitor;
import main.model.visitor.TicketRiskVisitor;

import java.util.List;

public final class AppStabilityReportCommand implements Command {
    private static final double IMPACT_THRESHOLD = 50.0;
    private static final double LOW_RISK = 4.0;
    private static final double MODERATE_RISK = 9.0;

    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public AppStabilityReportCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();

        List<Ticket> allTickets = db.getTickets();

        boolean hasActive = allTickets.stream()
                .anyMatch(t -> "OPEN".equals(t.getStatus())
                        || "IN_PROGRESS".equals(t.getStatus()));

        String stabilityStatus;
        if (!hasActive) {
            stabilityStatus = "STABLE";
        } else {
            TicketRiskVisitor riskVisitor = new TicketRiskVisitor();
            CustomerImpactVisitor impactVisitor = new CustomerImpactVisitor();

            boolean hasSignificantRisk = false;
            boolean allLowRisk = true;
            boolean allLowImpact = true;

            for (Ticket t : allTickets) {
                Double riskScore = t.accept(riskVisitor);
                Double impactScore = t.accept(impactVisitor);

                String riskLabel = getRiskLabel(riskScore);

                if ("MAJOR".equals(riskLabel)) {
                    hasSignificantRisk = true;
                }
                if (!"LOW".equals(riskLabel)) {
                    allLowRisk = false;
                }

                if (impactScore >= IMPACT_THRESHOLD) {
                    allLowImpact = false;
                }
            }

            if (hasSignificantRisk) {
                stabilityStatus = "UNSTABLE";
            } else if (allLowRisk && allLowImpact) {
                stabilityStatus = "STABLE";
            } else {
                stabilityStatus = "PARTIALLY STABLE";
            }
        }

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "appStabilityReport");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.put("stabilityStatus", stabilityStatus);
        output.add(res);
    }

    private String getRiskLabel(final double score) {
        if (score < LOW_RISK) {
            return "LOW";
        }
        if (score < MODERATE_RISK) {
            return "MODERATE";
        }
        return "MAJOR";
    }
}
