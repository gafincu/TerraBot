package main.model.visitor;

import main.model.Bug;
import main.model.FeatureRequest;
import main.model.UiFeedback;

public final class TicketRiskVisitor implements TicketVisitor<Double> {
    private static final double BUG_SEVERITY_WEIGHT = 2.5;
    private static final double FEATURE_VALUE_WEIGHT = 1.5;
    private static final double UI_MAX_SCORE = 10.0;
    private static final double UI_WEIGHT = 0.5;
    private static final double SCORE_SEVERE = 3.0;
    private static final double SCORE_MODERATE = 2.0;
    private static final double SCORE_ALWAYS = 4.0;
    private static final double SCORE_FREQUENT = 3.0;
    private static final double SCORE_OCCASIONAL = 2.0;
    private static final double SCORE_XL = 4.0;
    private static final double SCORE_L = 3.0;
    private static final double SCORE_M = 2.0;
    private static final double SCORE_VERY_HIGH = 4.0;
    private static final double SCORE_HIGH = 3.0;
    private static final double SCORE_MEDIUM = 2.0;

    @Override
    public Double visit(final Bug bug) {
        return getSeverityScore(bug.getSeverity()) * BUG_SEVERITY_WEIGHT
                + getFrequencyScore(bug.getFrequency());
    }

    @Override
    public Double visit(final FeatureRequest fr) {
        return getValueScore(fr.getBusinessValue()) * FEATURE_VALUE_WEIGHT
                + getDemandScore(fr.getCustomerDemand());
    }

    @Override
    public Double visit(final UiFeedback ui) {
        int u = (ui.getUsabilityScore() != null) ? ui.getUsabilityScore() : (int) UI_MAX_SCORE;
        return (UI_MAX_SCORE - u) * UI_WEIGHT + getValueScore(ui.getBusinessValue());
    }

    private double getSeverityScore(final String s) {
        if ("SEVERE".equals(s)) {
            return SCORE_SEVERE;
        }
        if ("MODERATE".equals(s)) {
            return SCORE_MODERATE;
        }
        return 1;
    }

    private double getFrequencyScore(final String f) {
        if ("ALWAYS".equals(f)) {
            return SCORE_ALWAYS;
        }
        if ("FREQUENT".equals(f)) {
            return SCORE_FREQUENT;
        }
        if ("OCCASIONAL".equals(f)) {
            return SCORE_OCCASIONAL;
        }
        return 1;
    }

    private double getValueScore(final String v) {
        if ("XL".equals(v)) {
            return SCORE_XL;
        }
        if ("L".equals(v)) {
            return SCORE_L;
        }
        if ("M".equals(v)) {
            return SCORE_M;
        }
        return 1;
    }

    private double getDemandScore(final String d) {
        if ("VERY_HIGH".equals(d)) {
            return SCORE_VERY_HIGH;
        }
        if ("HIGH".equals(d)) {
            return SCORE_HIGH;
        }
        if ("MEDIUM".equals(d)) {
            return SCORE_MEDIUM;
        }
        return 1;
    }
}
