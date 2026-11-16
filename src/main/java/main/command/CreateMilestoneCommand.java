package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.HistoryLog;
import main.model.Milestone;
import main.model.Ticket;
import main.model.User;

import java.util.ArrayList;
import java.util.List;

public final class CreateMilestoneCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public CreateMilestoneCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();
        User user = db.findUser(username);

        if (user == null || !"MANAGER".equals(user.getRole())) {
            ObjectNode err = mapper.createObjectNode();
            err.put("command", "createMilestone");
            err.put("username", username);
            err.put("timestamp", timestamp);
            String role = (user == null) ? "null" : user.getRole();
            err.put("error", "The user does not have permission to execute this command: "
                    + "required role MANAGER; user role " + role + ".");
            output.add(err);
            return;
        }

        try {
            Milestone m = new Milestone();
            m.setName(node.get("name").asText());
            m.setDueDate(node.get("dueDate").asText());
            m.setCreatedBy(username);
            m.setCreatedAt(timestamp);
            m.setStatus("ACTIVE");

            if (node.has("tickets")) {
                List<Integer> tIds = new ArrayList<>();
                for (JsonNode t : node.get("tickets")) {
                    tIds.add(t.asInt());
                }
                m.setTickets(tIds);
            } else {
                m.setTickets(new ArrayList<>());
            }

            for (Integer tId : m.getTickets()) {
                for (Milestone existing : db.getMilestones()) {
                    if (existing.getTickets().contains(tId)) {
                        ObjectNode err = mapper.createObjectNode();
                        err.put("command", "createMilestone");
                        err.put("username", username);
                        err.put("timestamp", timestamp);
                        err.put("error", "Tickets " + tId + " already assigned to milestone "
                                + existing.getName() + ".");
                        output.add(err);
                        return;
                    }
                }
            }

            if (node.has("blockingFor")) {
                List<String> blocks = new ArrayList<>();
                for (JsonNode b : node.get("blockingFor")) {
                    blocks.add(b.asText());
                }
                m.setBlockingFor(blocks);
            } else {
                m.setBlockingFor(new ArrayList<>());
            }

            if (node.has("assignedDevs")) {
                List<String> devs = new ArrayList<>();
                for (JsonNode d : node.get("assignedDevs")) {
                    devs.add(d.asText());
                }
                m.setAssignedDevs(devs);
            } else {
                m.setAssignedDevs(new ArrayList<>());
            }

            for (Integer tId : m.getTickets()) {
                Ticket t = db.findTicket(tId);
                if (t != null) {
                    HistoryLog log = new HistoryLog(username, timestamp, "ADDED_TO_MILESTONE");
                    log.setMilestone(m.getName());
                    t.getHistory().add(log);
                }
            }

            for (String devName : m.getAssignedDevs()) {
                User dev = db.findUser(devName);
                if (dev != null) {
                    String msg = "New milestone " + m.getName() + " has been created with due date "
                            + m.getDueDate() + ".";
                    dev.addNotification(msg);
                }
            }

            db.getMilestones().add(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
