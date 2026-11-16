package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;
import main.model.User;
import main.model.visitor.HistoryVisibilityVisitor;

import java.util.ArrayList;
import java.util.List;

public final class ViewTicketHistoryCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ViewTicketHistoryCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();
        User user = db.findUser(username);

        List<ObjectNode> historyList = new ArrayList<>();

        if (user != null) {
            for (Ticket t : db.getTickets()) {
                HistoryVisibilityVisitor visitor = new HistoryVisibilityVisitor(t, username);
                Boolean isVisible = user.accept(visitor);

                if (Boolean.TRUE.equals(isVisible) && !t.getHistory().isEmpty()) {
                    ObjectNode tNode = mapper.createObjectNode();
                    tNode.put("id", t.getId());
                    tNode.put("title", t.getTitle());
                    tNode.put("status", t.getStatus());
                    tNode.set("actions", mapper.valueToTree(t.getHistory()));
                    tNode.set("comments", mapper.valueToTree(t.getComments()));
                    historyList.add(tNode);
                }
            }
        }

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "viewTicketHistory");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("ticketHistory", mapper.valueToTree(historyList));
        output.add(res);
    }
}
