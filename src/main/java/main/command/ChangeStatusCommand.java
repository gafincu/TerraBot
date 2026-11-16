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

import java.time.LocalDate;
import java.util.List;

public final class ChangeStatusCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ChangeStatusCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        int ticketId = node.get("ticketID").asInt();
        String timestamp = node.get("timestamp").asText();

        Database db = Database.getInstance();
        Ticket ticket = db.findTicket(ticketId);
        if (ticket == null) {
            return;
        }

        if (!username.equals(ticket.getAssignedTo())) {
            ObjectNode err = mapper.createObjectNode();
            err.put("command", "changeStatus");
            err.put("username", username);
            err.put("timestamp", timestamp);
            err.put("error", "Ticket " + ticketId + " is not assigned to developer "
                    + username + ".");
            output.add(err);
            return;
        }

        String oldStatus = ticket.getStatus();
        String newStatus = null;

        if ("IN_PROGRESS".equals(oldStatus)) {
            newStatus = "RESOLVED";
        } else if ("RESOLVED".equals(oldStatus)) {
            newStatus = "CLOSED";
        } else if ("CLOSED".equals(oldStatus)) {
            return;
        }

        if (newStatus != null) {
            ticket.setStatus(newStatus);
            if ("RESOLVED".equals(newStatus)) {
                ticket.setSolvedAt(timestamp);
            }

            HistoryLog log = new HistoryLog(username, timestamp, "STATUS_CHANGED");
            log.setFrom(oldStatus);
            log.setTo(newStatus);
            ticket.getHistory().add(log);

            if ("CLOSED".equals(newStatus)) {
                checkMilestoneUnblocking(db, ticket, timestamp);
            }
        }
    }

    private void checkMilestoneUnblocking(final Database db, final Ticket ticket,
                                          final String timestamp) {
        Milestone currentMilestone = null;
        for (Milestone m : db.getMilestones()) {
            if (m.getTickets().contains(ticket.getId())) {
                currentMilestone = m;
                break;
            }
        }

        if (currentMilestone == null) {
            return;
        }

        boolean allClosed = true;
        for (Integer tId : currentMilestone.getTickets()) {
            Ticket t = db.findTicket(tId);
            if (t != null && !"CLOSED".equals(t.getStatus())) {
                allClosed = false;
                break;
            }
        }

        if (allClosed) {
            currentMilestone.setStatus("COMPLETED");
            List<String> blockedNames = currentMilestone.getBlockingFor();
            LocalDate current = LocalDate.parse(timestamp);

            for (String blockedName : blockedNames) {
                Milestone blockedM = db.getMilestones().stream()
                        .filter(m -> m.getName().equals(blockedName))
                        .findFirst().orElse(null);

                if (blockedM != null && "ACTIVE".equals(blockedM.getStatus())) {
                    boolean stillBlocked = false;
                    for (Milestone other : db.getMilestones()) {
                        if (other.getBlockingFor().contains(blockedName)
                                && "ACTIVE".equals(other.getStatus())) {
                            stillBlocked = true;
                            break;
                        }
                    }

                    if (!stillBlocked) {
                        notifyUnblocked(db, blockedM, current);
                    }
                }
            }
        }
    }

    private void notifyUnblocked(final Database db, final Milestone blockedM,
                                 final LocalDate current) {
        LocalDate due = LocalDate.parse(blockedM.getDueDate());
        if (current.isAfter(due)) {
            String msg = "Milestone " + blockedM.getName()
                    + " was unblocked after due date. All active tickets are now CRITICAL.";

            for (String devName : blockedM.getAssignedDevs()) {
                User dev = db.findUser(devName);
                if (dev != null) {
                    if (!dev.getNotifications().contains(msg)) {
                        dev.addNotification(msg);
                    }
                }
            }

            for (Integer tId : blockedM.getTickets()) {
                Ticket t = db.findTicket(tId);
                if (t != null && ("OPEN".equals(t.getStatus())
                        || "IN_PROGRESS".equals(t.getStatus()))) {
                    t.setBusinessPriority("CRITICAL");
                }
            }
        }
    }
}
