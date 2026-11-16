package main.model.visitor;

import main.database.Database;
import main.model.Developer;
import main.model.Manager;
import main.model.Milestone;
import main.model.Reporter;
import main.model.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class TicketVisibilityVisitor implements UserVisitor<List<Ticket>> {
    private final Database db;

    public TicketVisibilityVisitor() {
        this.db = Database.getInstance();
    }

    @Override
    public List<Ticket> visit(final Manager manager) {
        return new ArrayList<>(db.getTickets());
    }

    @Override
    public List<Ticket> visit(final Developer developer) {
        List<Ticket> visible = new ArrayList<>();
        String username = developer.getUsername();

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
        return visible;
    }

    @Override
    public List<Ticket> visit(final Reporter reporter) {
        return db.getTickets().stream()
                .filter(t -> t.getReportedBy().equals(reporter.getUsername()))
                .collect(Collectors.toList());
    }
}
