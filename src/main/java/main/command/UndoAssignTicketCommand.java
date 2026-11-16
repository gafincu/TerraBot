package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.database.Database;
import main.model.HistoryLog;
import main.model.Ticket;

public final class UndoAssignTicketCommand implements Command {
    private final JsonNode node;

    public UndoAssignTicketCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        int tId = node.get("ticketID").asInt();
        String username = node.has("username") ? node.get("username").asText() : null;
        String timestamp = node.has("timestamp") ? node.get("timestamp").asText() : null;

        Ticket t = Database.getInstance().findTicket(tId);

        if (t != null && "IN_PROGRESS".equals(t.getStatus())) {
            t.setStatus("OPEN");
            t.setAssignedTo("");
            t.setAssignedAt("");
            t.setSolvedAt("");

            // LOG: DE-ASSIGNED
            if (username != null && timestamp != null) {
                HistoryLog log = new HistoryLog(username, timestamp, "DE-ASSIGNED");
                t.getHistory().add(log);
            }
        }
    }
}
