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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ViewMilestonesCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public ViewMilestonesCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();
        Database db = Database.getInstance();
        User user = db.findUser(username);

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "viewMilestones");
        res.put("username", username);
        res.put("timestamp", timestamp);

        List<Milestone> visibleMilestones = new ArrayList<>();

        if (user != null) {
            if ("MANAGER".equals(user.getRole())) {
                for (Milestone m : db.getMilestones()) {
                    if (m.getCreatedBy().equals(username)) {
                        visibleMilestones.add(m);
                    }
                }
            } else if ("DEVELOPER".equals(user.getRole())) {
                for (Milestone m : db.getMilestones()) {
                    if (m.getAssignedDevs().contains(username)) {
                        visibleMilestones.add(m);
                    }
                }
            }
        }

        visibleMilestones.sort(Comparator.comparing(Milestone::getDueDate)
                .thenComparing(Milestone::getName));

        LocalDate currentCmdDate = LocalDate.parse(timestamp);

        for (Milestone m : visibleMilestones) {
            List<Integer> open = new ArrayList<>();
            List<Integer> closed = new ArrayList<>();

            for (Integer tId : m.getTickets()) {
                Ticket t = db.findTicket(tId);
                if (t != null && "CLOSED".equals(t.getStatus())) {
                    closed.add(tId);
                } else {
                    open.add(tId);
                }
            }
            m.setOpenTickets(open);
            m.setClosedTickets(closed);

            if (m.getTickets().isEmpty()) {
                m.setCompletionPercentage(0.0);
            } else {
                double val = (double) closed.size() / m.getTickets().size();
                BigDecimal bd = BigDecimal.valueOf(val);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                m.setCompletionPercentage(bd.doubleValue());
            }

            boolean isCompleted = !m.getTickets().isEmpty() && open.isEmpty();
            m.setStatus(isCompleted ? "COMPLETED" : "ACTIVE");

            LocalDate calculationDate = currentCmdDate;

            if (isCompleted) {
                LocalDate lastClosedDate = null;
                for (Integer tId : m.getTickets()) {
                    Ticket t = db.findTicket(tId);
                    if (t != null) {
                        for (HistoryLog log : t.getHistory()) {
                            if ("STATUS_CHANGED".equals(log.getAction())
                                    && "CLOSED".equals(log.getTo())) {
                                LocalDate d = LocalDate.parse(log.getTimestamp());
                                if (lastClosedDate == null || d.isAfter(lastClosedDate)) {
                                    lastClosedDate = d;
                                }
                            }
                        }
                    }
                }
                if (lastClosedDate != null) {
                    calculationDate = lastClosedDate;
                }
            }

            LocalDate due = LocalDate.parse(m.getDueDate());
            long diff = ChronoUnit.DAYS.between(calculationDate, due);

            if (diff >= 0) {
                m.setDaysUntilDue(diff + 1);
                m.setOverdueBy(0L);
            } else {
                m.setDaysUntilDue(0L);
                m.setOverdueBy(Math.abs(diff) + 1);
            }

            boolean isBlocked = false;
            for (Milestone other : db.getMilestones()) {
                if (other.getBlockingFor().contains(m.getName())
                        && "ACTIVE".equals(other.getStatus())) {
                    isBlocked = true;
                    break;
                }
            }
            m.setIsBlocked(isBlocked);

            List<Milestone.Repartition> repart = new ArrayList<>();
            for (String devName : m.getAssignedDevs()) {
                List<Integer> devTickets = new ArrayList<>();
                for (Integer tId : m.getTickets()) {
                    Ticket t = db.findTicket(tId);
                    if (t != null && devName.equals(t.getAssignedTo())) {
                        devTickets.add(tId);
                    }
                }
                repart.add(new Milestone.Repartition(devName, devTickets));
            }
            repart.sort((r1, r2) -> {
                int sizeCmp = Integer.compare(r1.getAssignedTickets().size(),
                        r2.getAssignedTickets().size());
                if (sizeCmp != 0) {
                    return sizeCmp;
                }
                return r1.getDeveloper().compareTo(r2.getDeveloper());
            });
            m.setRepartition(repart);
        }

        res.set("milestones", mapper.valueToTree(visibleMilestones));
        output.add(res);
    }
}
