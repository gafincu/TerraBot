package main.factory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.Ticket;

public final class TicketBuilder {
    private Ticket ticket;
    private final ObjectMapper mapper = new ObjectMapper();

    public TicketBuilder() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Construiește tichetul din JSON.
     */
    public TicketBuilder fromJson(final JsonNode params) {
        try {
            this.ticket = mapper.treeToValue(params, Ticket.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Returnează tichetul construit.
     */
    public Ticket build() {
        return ticket;
    }
}
