package main.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {
    private String hireDate;
    private List<String> subordinates;
}