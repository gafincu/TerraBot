package main.factory;

import com.fasterxml.jackson.databind.JsonNode;
import main.model.Ticket;

public final class TicketFactory {

    private TicketFactory() {
    }

    /**
     * Factory method pentru crearea tichetelor.
     */
    public static Ticket createTicket(final JsonNode params) {
        return new TicketBuilder().fromJson(params).build();
    }
}
