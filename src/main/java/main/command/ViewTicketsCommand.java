package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Milestone;
import main.model.Ticket;
import main.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class ViewTicketsCommand implements Command {
    private static final int PRIORITY_BUMP_INTERVAL = 3;
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ViewTicketsCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();
        User user = db.findUser(username);

        updatePriorities(db, timestamp);

        List<Ticket> visible = new ArrayList<>();

        if (user != null) {
            if ("MANAGER".equals(user.getRole())) {
                visible.addAll(db.getTickets());
            } else if ("REPORTER".equals(user.getRole())) {
                for (Ticket t : db.getTickets()) {
                    if (username.equals(t.getReportedBy())) {
                        visible.add(t);
                    }
                }
            } else if ("DEVELOPER".equals(user.getRole())) {
                for (Milestone m : db.getMilestones()) {
                    if (m.getAssignedDevs().contains(username)) {
                        for (Integer tId : m.getTickets()) {
                            Ticket t = db.findTicket(tId);
                            if (t != null && "OPEN".equals(t.getStatus())) {
                                visible.add(t);
                            }
                        }
                    }
                }
            }
        }

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "viewTickets");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("tickets", mapper.valueToTree(visible));
        output.add(res);
    }

    private void updatePriorities(final Database db, final String currentTimestamp) {
        LocalDate current = LocalDate.parse(currentTimestamp);

        for (Milestone m : db.getMilestones()) {
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

            LocalDate created = LocalDate.parse(m.getCreatedAt());
            LocalDate due = LocalDate.parse(m.getDueDate());
            long daysFromStart = ChronoUnit.DAYS.between(created, current);
            long daysUntilDue = ChronoUnit.DAYS.between(current, due);

            for (Integer tId : m.getTickets()) {
                Ticket t = db.findTicket(tId);
                if (t == null || "CLOSED".equals(t.getStatus())
                        || "RESOLVED".equals(t.getStatus())) {
                    continue;
                }

                if (daysUntilDue <= 1) {
                    t.setBusinessPriority("CRITICAL");
                } else {
                    int increases = (int) (daysFromStart / PRIORITY_BUMP_INTERVAL);
                    if (increases > 0) {
                        applyPriorityFromBase(t, increases);
                    } else if (t.getInitialPriority() != null) {
                        t.setBusinessPriority(t.getInitialPriority());
                    }
                }
            }
        }
    }

    private void applyPriorityFromBase(final Ticket t, final int times) {
        String base = t.getInitialPriority();
        if (base == null) {
            base = t.getBusinessPriority();
        }

        String[] levels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        int currentIdx = 0;
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].equals(base)) {
                currentIdx = i;
                break;
            }
        }

        int newIdx = Math.min(currentIdx + times, levels.length - 1);
        t.setBusinessPriority(levels[newIdx]);
    }
}
