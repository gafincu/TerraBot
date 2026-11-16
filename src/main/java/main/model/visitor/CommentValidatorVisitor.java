package main.model.visitor;

import main.model.Developer;
import main.model.Manager;
import main.model.Reporter;
import main.model.Ticket;

import java.util.Optional;

public final class CommentValidatorVisitor implements UserVisitor<Optional<String>> {
    private final Ticket ticket;
    private final String username;

    public CommentValidatorVisitor(final Ticket ticket, final String username) {
        this.ticket = ticket;
        this.username = username;
    }

    @Override
    public Optional<String> visit(final Manager manager) {
        return Optional.empty();
    }

    @Override
    public Optional<String> visit(final Developer developer) {
        if (!username.equals(ticket.getAssignedTo())) {
            return Optional.of("Ticket " + ticket.getId()
                    + " is not assigned to the developer " + username + ".");
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> visit(final Reporter reporter) {
        if ("CLOSED".equals(ticket.getStatus())) {
            return Optional.of("Reporters cannot comment on CLOSED tickets.");
        }

        if (!username.equals(ticket.getReportedBy())) {
            return Optional.of("Reporter " + username + " cannot comment on ticket "
                    + ticket.getId() + ".");
        }
        return Optional.empty();
    }
}
