package main.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class HistoryLog {
    private String milestone;
    private String by;
    private String timestamp;
    private String action;
    private String from;
    private String to;

    public HistoryLog() {
    }

    public HistoryLog(final String by, final String timestamp, final String action) {
        this.by = by;
        this.timestamp = timestamp;
        this.action = action;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(final String milestone) {
        this.milestone = milestone;
    }

    public String getBy() {
        return by;
    }

    public void setBy(final String by) {
        this.by = by;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(final String to) {
        this.to = to;
    }
}
