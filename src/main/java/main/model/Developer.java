package main.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Developer extends User {
    private String hireDate;
    private String seniority; // JUNIOR, MID, SENIOR
    private String expertiseArea; // FRONTEND, BACKEND, FULLSTACK etc.
}