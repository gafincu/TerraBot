package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.command.*;
import main.database.Database;
import main.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App {
    private App() {
    }

    private static final String INPUT_USERS_FIELD = "input/database/users.json";
    private static final ObjectWriter WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public static void run(final String inputPath, final String outputPath) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode outputs = mapper.createArrayNode();

        // Resetare DB la fiecare rulare
        Database.reset();
        Database db = Database.getInstance();

        try {
            // Încărcare utilizatori
            File usersFile = new File(INPUT_USERS_FIELD);
            if (usersFile.exists()) {
                List<User> users = mapper.readValue(usersFile, new TypeReference<List<User>>() {});
                db.getUsers().addAll(users);
            }

            // Procesare comenzi
            File inputFile = new File(inputPath);
            JsonNode commandsArray = mapper.readTree(inputFile);

            if (commandsArray.isArray()) {
                for (JsonNode commandNode : commandsArray) {
                    String commandName = commandNode.get("command").asText();
                    Command command = null;

                    switch (commandName) {
                        case "reportTicket":
                            command = new ReportTicketCommand(commandNode);
                            break;
                        case "createMilestone":
                            command = new CreateMilestoneCommand(commandNode);
                            break;
                        case "viewMilestones":
                            command = new ViewMilestonesCommand(commandNode);
                            break;
                        case "assignTicket":
                            command = new AssignTicketCommand(commandNode);
                            break;
                        case "viewTickets":
                            command = new ViewTicketsCommand(commandNode);
                            break;
                        case "undoAssignTicket":
                            command = new UndoAssignTicketCommand(commandNode);
                            break;
                        case "viewAssignedTickets":
                            command = new ViewAssignedTicketsCommand(commandNode);
                            break;
                        case "addComment":
                            command = new AddCommentCommand(commandNode);
                            break;
                        case "undoAddComment":
                            command = new UndoAddCommentCommand(commandNode);
                            break;
                        case "changeStatus":
                            command = new ChangeStatusCommand(commandNode);
                            break;
                        case "viewTicketHistory":
                            command = new ViewTicketHistoryCommand(commandNode);
                            break;
                        case "lostInvestors":
                            // Folosim clasa separată LostInvestorsCommand dacă ai creat-o,
                            // sau lăsăm o implementare simplă aici pentru a nu opri execuția brusc.
                            // Dacă ai creat fișierul LostInvestorsCommand.java:
                            // command = new LostInvestorsCommand(commandNode);

                            // Dacă NU ai creat fișierul, poți folosi acest bloc pentru a scrie și ieși:
                            try {
                                File outputFile = new File(outputPath);
                                if (outputFile.getParentFile() != null) outputFile.getParentFile().mkdirs();
                                WRITER.writeValue(outputFile, outputs);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;

                        default:
                            System.err.println("Unknown command: " + commandName);
                            break;
                    }

                    if (command != null) {
                        command.execute(outputs);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Scrierea finală a output-ului
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