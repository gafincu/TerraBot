package main.model;

import main.model.visitor.UserVisitor;
import java.util.List;

public class Manager extends User {
    private String hireDate;
    private List<String> subordinates;

    public Manager() {}

    @Override
    public <R> R accept(UserVisitor<R> visitor) {
        // Aici se face dispatch-ul dinamic. Java știe că 'this' este Manager.
        return visitor.visit(this);
    }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public List<String> getSubordinates() { return subordinates; }
    public void setSubordinates(List<String> subordinates) { this.subordinates = subordinates; }
}