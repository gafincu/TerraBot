package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.HistoryLog;
import main.model.Ticket;

public final class UndoChangeStatusCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public UndoChangeStatusCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        int ticketId = node.get("ticketID").asInt();
        String timestamp = node.get("timestamp").asText();

        Ticket ticket = Database.getInstance().findTicket(ticketId);
        if (ticket == null) {
            return;
        }

        if (!username.equals(ticket.getAssignedTo())) {
            ObjectNode err = mapper.createObjectNode();
            err.put("command", "undoChangeStatus");
            err.put("username", username);
            err.put("timestamp", timestamp);
            err.put("error", "Ticket " + ticketId + " is not assigned to developer "
                    + username + ".");
            output.add(err);
            return;
        }

        String currentStatus = ticket.getStatus();
        String prevStatus = null;

        if ("CLOSED".equals(currentStatus)) {
            prevStatus = "RESOLVED";
        } else if ("RESOLVED".equals(currentStatus)) {
            prevStatus = "IN_PROGRESS";
        } else if ("IN_PROGRESS".equals(currentStatus)) {
            return;
        }

        if (prevStatus != null) {
            ticket.setStatus(prevStatus);

            if ("IN_PROGRESS".equals(prevStatus)) {
                ticket.setSolvedAt(null);
            }

            HistoryLog log = new HistoryLog(username, timestamp, "STATUS_CHANGED");
            log.setFrom(currentStatus);
            log.setTo(prevStatus);
            ticket.getHistory().add(log);
        }
    }
}
