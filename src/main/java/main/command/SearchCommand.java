package main.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Database;
import main.model.Ticket;

import java.util.List;
import java.util.stream.Collectors;

public final class SearchCommand implements Command {
    private final JsonNode node;
    private final ObjectMapper mapper = new ObjectMapper();

    public SearchCommand(final JsonNode node) {
        this.node = node;
    }

    @Override
    public void execute(final ArrayNode output) {
        String username = node.get("username").asText();
        String timestamp = node.get("timestamp").asText();

        List<Ticket> results = Database.getInstance().getTickets().stream()
                .filter(t -> t.getTitle().toLowerCase().contains("search"))
                .collect(Collectors.toList());

        ObjectNode res = mapper.createObjectNode();
        res.put("command", "search");
        res.put("username", username);
        res.put("timestamp", timestamp);
        res.set("results", mapper.valueToTree(results));
        output.add(res);
    }
}
