package main.factory;

import com.fasterxml.jackson.databind.JsonNode;
import main.command.AddCommentCommand;
import main.command.AppStabilityReportCommand;
import main.command.AssignTicketCommand;
import main.command.ChangeStatusCommand;
import main.command.Command;
import main.command.CreateMilestoneCommand;
import main.command.GenerateCustomerImpactReportCommand;
import main.command.GeneratePerformanceReportCommand;
import main.command.GenerateResolutionEfficiencyReportCommand;
import main.command.GenerateTicketRiskReportCommand;
import main.command.LostInvestorsCommand;
import main.command.ReportTicketCommand;
import main.command.SearchCommand;
import main.command.StartTestingPhaseCommand;
import main.command.UndoAddCommentCommand;
import main.command.UndoAssignTicketCommand;
import main.command.UndoChangeStatusCommand;
import main.command.ViewAssignedTicketsCommand;
import main.command.ViewMilestonesCommand;
import main.command.ViewNotificationsCommand;
import main.command.ViewTicketHistoryCommand;
import main.command.ViewTicketsCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CommandFactory {

    private static CommandFactory instance;
    private final Map<String, Function<JsonNode, Command>> commandRegistry;

    private CommandFactory() {
        commandRegistry = new HashMap<>();
        registerCommands();
    }

    /**
     * Returneaza instanta Singleton a fabricii.
     *
     * @return instanta CommandFactory
     */
    public static synchronized CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }
        return instance;
    }

    private void registerCommands() {
        commandRegistry.put("lostInvestors", LostInvestorsCommand::new);
        commandRegistry.put("startTestingPhase", StartTestingPhaseCommand::new);
        commandRegistry.put("createMilestone", CreateMilestoneCommand::new);

        commandRegistry.put("reportTicket", ReportTicketCommand::new);
        commandRegistry.put("viewTickets", ViewTicketsCommand::new);
        commandRegistry.put("viewTicketHistory", ViewTicketHistoryCommand::new);
        commandRegistry.put("assignTicket", AssignTicketCommand::new);
        commandRegistry.put("undoAssignTicket", UndoAssignTicketCommand::new);
        commandRegistry.put("viewAssignedTickets", ViewAssignedTicketsCommand::new);
        commandRegistry.put("changeStatus", ChangeStatusCommand::new);
        commandRegistry.put("undoChangeStatus", UndoChangeStatusCommand::new);
        commandRegistry.put("search", SearchCommand::new);

        commandRegistry.put("addComment", AddCommentCommand::new);
        commandRegistry.put("undoAddComment", UndoAddCommentCommand::new);

        commandRegistry.put("viewMilestones", ViewMilestonesCommand::new);

        commandRegistry.put("viewNotifications", ViewNotificationsCommand::new);
        commandRegistry.put("generatePerformanceReport", GeneratePerformanceReportCommand::new);
        commandRegistry.put("generateTicketRiskReport", GenerateTicketRiskReportCommand::new);
        commandRegistry.put("generateResolutionEfficiencyReport",
                GenerateResolutionEfficiencyReportCommand::new);
        commandRegistry.put("generateCustomerImpactReport",
                GenerateCustomerImpactReportCommand::new);
        commandRegistry.put("appStabilityReport", AppStabilityReportCommand::new);
    }

    /**
     * Creeaza o comanda pe baza numelui.
     *
     * @param commandName numele comenzii
     * @param node        nodul JSON cu parametri
     * @return instanta Command sau null
     */
    public Command createCommand(final String commandName, final JsonNode node) {
        Function<JsonNode, Command> creator = commandRegistry.get(commandName);

        if (creator != null) {
            return creator.apply(node);
        }

        return null;
    }
}
