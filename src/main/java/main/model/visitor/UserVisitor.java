package main.model.visitor;

import main.model.Developer;
import main.model.Manager;
import main.model.Reporter;

public interface UserVisitor<R> {
    /**
     * Viziteaza un Manager.
     * @param manager userul.
     * @return rezultat.
     */
    R visit(Manager manager);

    /**
     * Viziteaza un Developer.
     * @param developer userul.
     * @return rezultat.
     */
    R visit(Developer developer);

    /**
     * Viziteaza un Reporter.
     * @param reporter userul.
     * @return rezultat.
     */
    R visit(Reporter reporter);
}
