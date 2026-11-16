package main.database;

import main.model.Milestone;
import main.model.Ticket;
import main.model.User;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    private static final int TESTING_PHASE_DAYS = 12;
    private static Database instance = null;

    private final List<User> users;
    private final List<Ticket> tickets;
    private final List<Milestone> milestones;
    private String appStartDate = null;

    private Database() {
        users = new ArrayList<>();
        tickets = new ArrayList<>();
        milestones = new ArrayList<>();
    }

    /**
     * Returneaza instanta Singleton a bazei de date.
     *
     * @return instanta Database
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Reseteaza baza de date.
     */
    public static void reset() {
        instance = new Database();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    /**
     * Gaseste un user dupa username.
     *
     * @param username numele utilizatorului
     * @return User sau null
     */
    public User findUser(final String username) {
        return users.stream().filter(u -> u.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    /**
     * Gaseste un tichet dupa ID.
     *
     * @param id id-ul tichetului
     * @return Ticket sau null
     */
    public Ticket findTicket(final int id) {
        return tickets.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    /**
     * Verifica daca aplicatia este in faza de testare.
     *
     * @param currentTimestamp data curenta
     * @return true daca e in testing phase
     */
    public boolean isTestingPhase(final String currentTimestamp) {
        if (appStartDate == null) {
            appStartDate = currentTimestamp;
        }

        boolean hasActiveMilestones = milestones.stream()
                .anyMatch(m -> "ACTIVE".equals(m.getStatus()));
        if (hasActiveMilestones) {
            return false;
        }

        LocalDate start = LocalDate.parse(appStartDate);
        LocalDate current = LocalDate.parse(currentTimestamp);
        long daysDiff = ChronoUnit.DAYS.between(start, current);

        return daysDiff >= 0 && daysDiff < TESTING_PHASE_DAYS;
    }
}
