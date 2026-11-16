package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.factory.TicketFactory;
import main.model.Ticket;
import main.model.User;

public final class ReportTicketCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReportTicketCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();

        User user = db.findUser(username);

        if (user == null && !username.isEmpty()) {
            addError(output, username, timestamp, "The user " + username + " does not exist.");
            return;
        }

        if (!db.isTestingPhase(timestamp)) {
            addError(output, username, timestamp,
                    "Tickets can only be reported during testing phases.");
            return;
        }

        JsonNode params = node.get("params");
        String type = params.get("type").asText();
        String reportedBy = params.has("reportedBy") ? params.get("reportedBy").asText() : "";

        if (reportedBy.isEmpty() && !"BUG".equals(type)) {
            addError(output, username, timestamp,
                    "Anonymous reports are only allowed for tickets of type BUG.");
            return;
        }

        Ticket ticket = TicketFactory.createTicket(params);
        if (ticket != null) {
            ticket.setId(db.getTickets().size());
            ticket.setCreatedAt(timestamp);
            ticket.setComments(new java.util.ArrayList<>());

            ticket.setType(type);
            ticket.setStatus("OPEN");
            ticket.setAssignedTo("");
            ticket.setAssignedAt("");
            ticket.setSolvedAt("");

            if (params.has("title")) {
                ticket.setTitle(params.get("title").asText());
            }
            if (params.has("description")) {
                ticket.setDescription(params.get("description").asText());
            }
            if (params.has("expertiseArea")) {
                ticket.setExpertiseArea(params.get("expertiseArea").asText());
            }
            if (params.has("businessPriority")) {
                ticket.setBusinessPriority(params.get("businessPriority").asText());
            }

            if (reportedBy.isEmpty()) {
                ticket.setBusinessPriority("LOW");
                ticket.setReportedBy("");
            } else {
                ticket.setReportedBy(reportedBy);
            }

            db.getTickets().add(ticket);
        }
    }

    private void addError(final ArrayNode output, final String username,
                          final String timestamp, final String msg) {
        ObjectNode err = mapper.createObjectNode();
        err.put("command", "reportTicket");
        err.put("username", username);
        err.put("timestamp", timestamp);
        err.put("error", msg);
        output.add(err);
    }
}
