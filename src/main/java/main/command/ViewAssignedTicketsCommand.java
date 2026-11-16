package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;
import java.util.ArrayList;
import java.util.List;

public final class ViewAssignedTicketsCommand implements Command {
    private static final int PRIO_CRITICAL = 4;
    private static final int PRIO_HIGH = 3;
    private static final int PRIO_MEDIUM = 2;
    private static final int PRIO_LOW = 1;

    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ViewAssignedTicketsCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();

        List<Ticket> assigned = new ArrayList<>();
        for (Ticket t : Database.getInstance().getTickets()) {
            if (username.equals(t.getAssignedTo())) {
                assigned.add(t);
            }
        }

        assigned.sort((t1, t2) -> {
            int p1 = getPrioVal(t1.getBusinessPriority());
            int p2 = getPrioVal(t2.getBusinessPriority());
            if (p1 != p2) {
                return p2 - p1;
            }
            return Integer.compare(t1.getId(), t2.getId());
        });

        ArrayNode ticketsArray = mapper.createArrayNode();
        for (Ticket t : assigned) {
            ObjectNode tNode = mapper.createObjectNode();
            tNode.put("id", t.getId());
            tNode.put("type", t.getType());
            tNode.put("title", t.getTitle());
            tNode.put("businessPriority", t.getBusinessPriority());
            tNode.put("status", t.getStatus());
            tNode.put("createdAt", t.getCreatedAt());
            tNode.put("assignedAt", t.getAssignedAt());
            tNode.put("reportedBy", t.getReportedBy());

            tNode.set("comments", mapper.valueToTree(t.getComments()));

            ticketsArray.add(tNode);
        }

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "viewAssignedTickets");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("assignedTickets", ticketsArray);
        output.add(res);
    }

    private int getPrioVal(final String p) {
        if ("CRITICAL".equals(p)) {
            return PRIO_CRITICAL;
        }
        if ("HIGH".equals(p)) {
            return PRIO_HIGH;
        }
        if ("MEDIUM".equals(p)) {
            return PRIO_MEDIUM;
        }
        return PRIO_LOW;
    }
}
