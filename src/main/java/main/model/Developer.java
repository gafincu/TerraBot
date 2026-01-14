package main.model;

public class Developer extends User {
    private String hireDate;
    private String seniority;
    private String expertiseArea;

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public String getSeniority() { return seniority; }
    public void setSeniority(String seniority) { this.seniority = seniority; }

    public String getExpertiseArea() { return expertiseArea; }
    public void setExpertiseArea(String expertiseArea) { this.expertiseArea = expertiseArea; }
}