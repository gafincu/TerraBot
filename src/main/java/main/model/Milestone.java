package main.model; // <--- Simplificat

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Milestone {
    private String name;
    // ... restul codului identic, doar pachetul diferă
}