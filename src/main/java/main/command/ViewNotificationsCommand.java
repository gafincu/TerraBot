package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Milestone;
import main.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class ViewNotificationsCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ViewNotificationsCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();

        Database db = Database.getInstance();
        User user = db.findUser(username);

        if (user != null && "DEVELOPER".equals(user.getRole())) {
            checkDueDateNotifications(db, user, timestamp);
        }

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "viewNotifications");
        res.put("username", username);
        res.put("timestamp", timestamp);

        List<String> userNotes = new ArrayList<>();
        if (user != null) {
            userNotes.addAll(user.getNotifications());
            user.getNotifications().clear();
        }

        res.set("notifications", mapper.valueToTree(userNotes));
        output.add(res);
    }

    private void checkDueDateNotifications(final Database db, final User dev,
                                           final String currentTimestamp) {
        LocalDate current = LocalDate.parse(currentTimestamp);

        for (Milestone m : db.getMilestones()) {
            if (!m.getAssignedDevs().contains(dev.getUsername())) {
                continue;
            }
            if (!"ACTIVE".equals(m.getStatus())) {
                continue;
            }

            boolean isBlocked = false;
            for (Milestone other : db.getMilestones()) {
                if (other.getBlockingFor().contains(m.getName())
                        && "ACTIVE".equals(other.getStatus())) {
                    isBlocked = true;
                    break;
                }
            }
            if (isBlocked) {
                continue;
            }

            LocalDate due = LocalDate.parse(m.getDueDate());
            long daysBetween = ChronoUnit.DAYS.between(current, due);

            if (daysBetween <= 1 && daysBetween >= -1) {
                String msg = "Milestone " + m.getName()
                        + " is due tomorrow. All unresolved tickets are now CRITICAL.";

                if (!m.isNotifiedDueTomorrow()) {
                    notifyAllAssigned(db, m, msg);
                    m.setNotifiedDueTomorrow(true);
                } else {
                    if (!dev.getNotifications().contains(msg)) {
                        dev.addNotification(msg);
                    }
                }
            }
        }
    }

    private void notifyAllAssigned(final Database db, final Milestone m, final String msg) {
        for (String devName : m.getAssignedDevs()) {
            User u = db.findUser(devName);
            if (u != null) {
                if (!u.getNotifications().contains(msg)) {
                    u.addNotification(msg);
                }
            }
        }
    }
}
