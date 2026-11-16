package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.command.Command;
import main.database.Database;
import main.factory.CommandFactory;
import main.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class App {
    private App() {
    }

    private static final String INPUT_USERS_FIELD = "input/database/users.json";
    private static final ObjectWriter WRITER = new ObjectMapper().writer()
            .withDefaultPrettyPrinter();

    /**
     * Metoda principala de rulare a aplicatiei.
     *
     * @param inputPath  calea catre fisierul de input
     * @param outputPath calea catre fisierul de output
     */
    public static void run(final String inputPath, final String outputPath) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode outputs = mapper.createArrayNode();

        Database.reset();
        Database db = Database.getInstance();

        try {
            File usersFile = new File(INPUT_USERS_FIELD);
            if (usersFile.exists()) {
                List<User> users = mapper.readValue(usersFile,
                        new TypeReference<List<User>>() {
                        });
                db.getUsers().addAll(users);
            }

            File inputFile = new File(inputPath);
            JsonNode commandsArray = mapper.readTree(inputFile);

            if (commandsArray.isArray()) {
                CommandFactory commandFactory = CommandFactory.getInstance();

                for (JsonNode commandNode : commandsArray) {
                    String commandName = commandNode.get("command").asText();

                    try {
                        Command command = commandFactory.createCommand(commandName, commandNode);

                        if (command != null) {
                            command.execute(outputs);

                            if ("lostInvestors".equals(commandName)) {
                                writeOutput(outputPath, outputs);
                                return;
                            }
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        System.err.println("Eroare la executia comenzii: " + commandName);
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeOutput(outputPath, outputs);
    }

    private static void writeOutput(final String outputPath, final ArrayNode outputs) {
        try {
            File outputFile = new File(outputPath);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            WRITER.writeValue(outputFile, outputs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
