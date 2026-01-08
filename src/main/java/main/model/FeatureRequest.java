package main.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FeatureRequest extends Ticket {
    private String businessValue;
    private String customerDemand;
}