package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Comment;
import main.model.Ticket;
import java.util.List;

public final class UndoAddCommentCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public UndoAddCommentCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        int ticketId = node.get("ticketID").asInt();
        String timestamp = node.get("timestamp").asText();

        Ticket ticket = Database.getInstance().findTicket(ticketId);

        if (ticket != null) {
            if (ticket.getReportedBy().isEmpty()) {
                ObjectNode err = mapper.createObjectNode();
                err.put("command", "undoAddComment");
                err.put("username", username);
                err.put("timestamp", timestamp);
                err.put("error", "Comments are not allowed on anonymous tickets.");
                output.add(err);
                return;
            }

            List<Comment> comments = ticket.getComments();
            for (int i = comments.size() - 1; i >= 0; i--) {
                if (comments.get(i).getAuthor().equals(username)) {
                    comments.remove(i);
                    break;
                }
            }
        }
    }
}
