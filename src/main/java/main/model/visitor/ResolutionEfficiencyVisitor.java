package main.model.visitor;

import main.model.Bug;
import main.model.FeatureRequest;
import main.model.UiFeedback;

public final class ResolutionEfficiencyVisitor implements TicketVisitor<Double> {
    private static final double BUG_FACTOR = 4.286;
    private static final double FR_FACTOR = 6.0;
    private static final double UI_FACTOR = 7.3529;
    private static final double MAX_USABILITY = 10.0;

    // Helper Constants
    private static final double VAL_1 = 1.0;
    private static final double VAL_2 = 2.0;
    private static final double VAL_3 = 3.0;
    private static final double VAL_4 = 4.0;

    @Override
    public Double visit(final Bug bug) {
        double complexity = getSeverityScore(bug.getSeverity())
                + getFrequencyScore(bug.getFrequency())
                + getPriorityScore(bug.getBusinessPriority());
        return complexity * BUG_FACTOR;
    }

    @Override
    public Double visit(final FeatureRequest fr) {
        double complexity = getValueScore(fr.getBusinessValue())
                + getDemandScore(fr.getCustomerDemand())
                + getPriorityScore(fr.getBusinessPriority());
        return complexity * FR_FACTOR;
    }

    @Override
    public Double visit(final UiFeedback ui) {
        int u = (ui.getUsabilityScore() != null) ? ui.getUsabilityScore() : 1;
        double complexity = (MAX_USABILITY - u)
                + getValueScore(ui.getBusinessValue())
                + getPriorityScore(ui.getBusinessPriority());
        return complexity * UI_FACTOR;
    }

    private double getPriorityScore(final String p) {
        if (p == null) {
            return VAL_1;
        }
        switch (p) {
            case "LOW": return VAL_1;
            case "MEDIUM": return VAL_2;
            case "HIGH": return VAL_3;
            case "CRITICAL": return VAL_4;
            default: return VAL_1;
        }
    }

    private double getSeverityScore(final String s) {
        if (s == null) {
            return VAL_1;
        }
        switch (s) {
            case "MINOR": return VAL_1;
            case "MODERATE": return VAL_2;
            case "SEVERE": return VAL_3;
            default: return VAL_1;
        }
    }

    private double getFrequencyScore(final String f) {
        if (f == null) {
            return VAL_1;
        }
        switch (f) {
            case "RARE": return VAL_1;
            case "OCCASIONAL": return VAL_2;
            case "FREQUENT": return VAL_3;
            case "ALWAYS": return VAL_4;
            default: return VAL_1;
        }
    }

    private double getValueScore(final String v) {
        if (v == null) {
            return VAL_1;
        }
        switch (v) {
            case "S": return VAL_1;
            case "M": return VAL_2;
            case "L": return VAL_3;
            case "XL": return VAL_4;
            default: return VAL_1;
        }
    }

    private double getDemandScore(final String d) {
        if (d == null) {
            return VAL_1;
        }
        switch (d) {
            case "LOW": return VAL_1;
            case "MEDIUM": return VAL_2;
            case "HIGH": return VAL_3;
            case "VERY_HIGH": return VAL_4;
            default: return VAL_1;
        }
    }
}
