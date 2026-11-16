package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Comment;
import main.model.Ticket;
import main.model.User;
import main.model.visitor.CommentValidatorVisitor;

import java.util.Optional;

public final class AddCommentCommand implements Command {
    private static final int MIN_COM_LEN = 10;
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public AddCommentCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        int ticketId = node.get("ticketID").asInt();
        String commentText = node.get("comment").asText();
        String timestamp = node.get("timestamp").asText();

        Database db = Database.getInstance();
        Ticket ticket = db.findTicket(ticketId);
        User user = db.findUser(username);

        if (ticket == null || user == null) {
            return;
        }

        if (ticket.getReportedBy().isEmpty()) {
            addError(output, username, timestamp, "Comments are not allowed on anonymous tickets.");
            return;
        }

        if (commentText.length() < MIN_COM_LEN) {
            addError(output, username, timestamp,
                    "Comment must be at least 10 characters long.");
            return;
        }

        CommentValidatorVisitor visitor = new CommentValidatorVisitor(ticket, username);
        Optional<String> error = user.accept(visitor);

        if (error.isPresent()) {
            addError(output, username, timestamp, error.get());
            return;
        }

        Comment c = new Comment(username, commentText, timestamp);
        ticket.getComments().add(c);
    }

    private void addError(final ArrayNode output, final String username,
                          final String timestamp, final String msg) {
        ObjectNode err = mapper.createObjectNode();
        err.put("command", "addComment");
        err.put("username", username);
        err.put("timestamp", timestamp);
        err.put("error", msg);
        output.add(err);
    }
}
