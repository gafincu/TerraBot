package main.model;

import main.model.visitor.UserVisitor;

public class Reporter extends User {

    public Reporter() {}

    @Override
    public <R> R accept(UserVisitor<R> visitor) {
        return visitor.visit(this);
    }
}