package main.model.visitor;

import main.model.Developer;
import main.model.HistoryLog;
import main.model.Manager;
import main.model.Reporter;
import main.model.Ticket;

public final class HistoryVisibilityVisitor implements UserVisitor<Boolean> {
    private final Ticket ticket;
    private final String username;

    public HistoryVisibilityVisitor(final Ticket ticket, final String username) {
        this.ticket = ticket;
        this.username = username;
    }

    @Override
    public Boolean visit(final Manager manager) {
        return true;
    }

    @Override
    public Boolean visit(final Developer developer) {
        if (username.equals(ticket.getAssignedTo())) {
            return true;
        }

        for (HistoryLog log : ticket.getHistory()) {
            if (username.equals(log.getBy())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean visit(final Reporter reporter) {
        return false;
    }
}
