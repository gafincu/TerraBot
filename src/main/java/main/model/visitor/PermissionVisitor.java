package main.model.visitor;

import main.model.Developer;
import main.model.Manager;
import main.model.Reporter;

public final class PermissionVisitor implements UserVisitor<Boolean> {
    private final String commandType;

    public PermissionVisitor(final String commandType) {
        this.commandType = commandType;
    }

    @Override
    public Boolean visit(final Manager manager) {
        if ("createMilestone".equals(commandType)) {
            return true;
        }
        return "viewTicketHistory".equals(commandType);
    }

    @Override
    public Boolean visit(final Developer developer) {
        if ("assignTicket".equals(commandType)) {
            return true;
        }
        if ("viewTicketHistory".equals(commandType)) {
            return true;
        }
        if ("changeStatus".equals(commandType)) {
            return true;
        }
        return "undoAssignTicket".equals(commandType);
    }

    @Override
    public Boolean visit(final Reporter reporter) {
        return false;
    }
}
