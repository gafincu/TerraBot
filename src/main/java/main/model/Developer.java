package main.model;

import main.model.visitor.UserVisitor;

public final class Developer extends User {
    private String hireDate;
    private String seniority;
    private String expertiseArea;

    public Developer() {
    }

    @Override
    public <R> R accept(final UserVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(final String hireDate) {
        this.hireDate = hireDate;
    }

    public String getSeniority() {
        return seniority;
    }

    public void setSeniority(final String seniority) {
        this.seniority = seniority;
    }

    public String getExpertiseArea() {
        return expertiseArea;
    }

    public void setExpertiseArea(final String expertiseArea) {
        this.expertiseArea = expertiseArea;
    }
}
