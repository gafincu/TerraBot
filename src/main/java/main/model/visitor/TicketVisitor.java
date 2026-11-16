package main.model.visitor;

import main.model.Bug;
import main.model.FeatureRequest;
import main.model.UiFeedback;

public interface TicketVisitor<R> {
    /**
     * Viziteaza un Bug.
     * @param bug tichetul.
     * @return rezultat.
     */
    R visit(Bug bug);

    /**
     * Viziteaza un FeatureRequest.
     * @param featureRequest tichetul.
     * @return rezultat.
     */
    R visit(FeatureRequest featureRequest);

    /**
     * Viziteaza un UiFeedback.
     * @param uiFeedback tichetul.
     * @return rezultat.
     */
    R visit(UiFeedback uiFeedback);
}
