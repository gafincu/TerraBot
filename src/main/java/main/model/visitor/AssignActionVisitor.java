package main.model.visitor;

import main.database.Database;
import main.model.Developer;
import main.model.Manager;
import main.model.Milestone;
import main.model.Reporter;
import main.model.Ticket;

import java.util.Optional;

public final class AssignActionVisitor implements UserVisitor<Optional<String>> {
    private final Ticket ticket;
    private final Database db;

    public AssignActionVisitor(final Ticket ticket, final Database db) {
        this.ticket = ticket;
        this.db = db;
    }

    @Override
    public Optional<String> visit(final Manager manager) {
        return Optional.of("The user does not have permission to execute this command: "
                + "required role DEVELOPER; user role MANAGER.");
    }

    @Override
    public Optional<String> visit(final Reporter reporter) {
        return Optional.of("The user does not have permission to execute this command: "
                + "required role DEVELOPER; user role REPORTER.");
    }

    @Override
    public Optional<String> visit(final Developer dev) {
        String req = ticket.getExpertiseArea();
        String devExp = dev.getExpertiseArea();
        boolean match = "FULLSTACK".equals(devExp)
                || (req != null && req.equals(devExp))
                || ("DB".equals(req) && "BACKEND".equals(devExp));

        if (!match) {
            String requirements = "DB".equals(req) ? "BACKEND, DB, FULLSTACK" : req;
            return Optional.of("Developer " + dev.getUsername() + " cannot assign ticket "
                    + ticket.getId() + " due to expertise area. Required: " + requirements
                    + "; Current: " + devExp + ".");
        }

        String p = ticket.getBusinessPriority();
        String s = dev.getSeniority();
        boolean isJuniorBlocked = "JUNIOR".equals(s)
                && ("HIGH".equals(p) || "CRITICAL".equals(p));
        boolean isMidBlocked = "MID".equals(s) && "CRITICAL".equals(p);

        if (isJuniorBlocked || isMidBlocked) {
            String reqS = "CRITICAL".equals(p) && "MID".equals(s) ? "SENIOR" : "MID, SENIOR";
            return Optional.of("Developer " + dev.getUsername() + " cannot assign ticket "
                    + ticket.getId() + " due to seniority level. Required: " + reqS
                    + "; Current: " + s + ".");
        }

        Milestone m = db.getMilestones().stream()
                .filter(mile -> mile.getTickets().contains(ticket.getId()))
                .findFirst().orElse(null);
        if (m != null) {
            if (!m.getAssignedDevs().contains(dev.getUsername())) {
                return Optional.of("Developer " + dev.getUsername()
                        + " is not assigned to milestone " + m.getName() + ".");
            }
            boolean isBlocked = db.getMilestones().stream()
                    .anyMatch(other -> "ACTIVE".equals(other.getStatus())
                            && other.getBlockingFor().contains(m.getName()));
            if (isBlocked) {
                return Optional.of("Cannot assign ticket " + ticket.getId()
                        + " from blocked milestone " + m.getName() + ".");
            }
        }

        return Optional.empty();
    }
}
