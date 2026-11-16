package main.command;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Interfata pentru comenzi.
 */
public interface Command {
    /**
     * Executa comanda.
     * @param output nodul JSON pentru output.
     */
    void execute(ArrayNode output);
}
