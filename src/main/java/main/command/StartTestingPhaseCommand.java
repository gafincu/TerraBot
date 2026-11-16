package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Milestone;
import main.model.User;

public final class StartTestingPhaseCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public StartTestingPhaseCommand(final JsonNode node) {
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
            err.put("command", "startTestingPhase");
            err.put("username", username);
            err.put("timestamp", timestamp);
            String role = (user == null) ? "null" : user.getRole();
            err.put("error", "The user does not have permission to execute this command: "
                    + "required role MANAGER; user role " + role + ".");
            output.add(err);
            return;
        }

        boolean hasActive = false;
        for (Milestone m : db.getMilestones()) {
            if ("ACTIVE".equals(m.getStatus())) {
                hasActive = true;
                break;
            }
        }

        if (hasActive) {
            ObjectNode err = mapper.createObjectNode();
            err.put("command", "startTestingPhase");
            err.put("username", username);
            err.put("timestamp", timestamp);
            err.put("error", "Cannot start testing phase while there are active milestones.");
            output.add(err);
            return;
        }

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "startTestingPhase");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.put("message", "Testing phase started. Reporters can now report bugs.");
        output.add(res);
    }
}
