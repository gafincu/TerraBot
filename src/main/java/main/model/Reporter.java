package main.model;

import main.model.visitor.UserVisitor;

public final class Reporter extends User {

    public Reporter() {
    }

    @Override
    public <R> R accept(final UserVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
