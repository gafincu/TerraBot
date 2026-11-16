package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.HistoryLog;
import main.model.Ticket;
import main.model.User;
import main.model.visitor.AssignValidatorVisitor;

import java.util.Optional;

public final class AssignTicketCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public AssignTicketCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        int ticketId = node.get("ticketID").asInt();
        String timestamp = node.get("timestamp").asText();

        Database db = Database.getInstance();
        User user = db.findUser(username);
        Ticket ticket = db.findTicket(ticketId);

        if (ticket == null || user == null) {
            return;
        }

        if (!"DEVELOPER".equals(user.getRole())) {
            return;
        }

        if (!"OPEN".equals(ticket.getStatus())) {
            addError(output, username, timestamp, "Only OPEN tickets can be assigned.");
            return;
        }

        AssignValidatorVisitor visitor = new AssignValidatorVisitor(ticket, db);
        Optional<String> error = user.accept(visitor);

        if (error.isPresent()) {
            addError(output, username, timestamp, error.get());
        } else {
            performAssignment(ticket, username, timestamp);
        }
    }

    private void addError(final ArrayNode output, final String user,
                          final String time, final String errorMsg) {
        ObjectNode err = mapper.createObjectNode();
        err.put("command", "assignTicket");
        err.put("username", user);
        err.put("timestamp", time);
        err.put("error", errorMsg);
        output.add(err);
    }

    private void performAssignment(final Ticket ticket, final String username,
                                   final String timestamp) {
        String oldStatus = ticket.getStatus();
        ticket.setStatus("IN_PROGRESS");
        ticket.setAssignedTo(username);
        ticket.setAssignedAt(timestamp);

        HistoryLog assignLog = new HistoryLog(username, timestamp, "ASSIGNED");
        ticket.getHistory().add(assignLog);

        if (!oldStatus.equals("IN_PROGRESS")) {
            HistoryLog statusLog = new HistoryLog(username, timestamp, "STATUS_CHANGED");
            statusLog.setFrom(oldStatus);
            statusLog.setTo("IN_PROGRESS");
            ticket.getHistory().add(statusLog);
        }
    }
}
