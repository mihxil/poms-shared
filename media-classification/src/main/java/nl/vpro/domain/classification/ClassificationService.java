package nl.vpro.domain.classification;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public interface ClassificationService {
    
    /**
     * Returns the Term with the given id.
     * @param termId
     * @throws TermNotFoundException if no such term
     */

    Term getTerm(String termId) throws TermNotFoundException;

    List<Term> getTermsByReference(String reference);

    boolean hasTerm(String termId);

    Collection<Term> values();

    Collection<Term> valuesOf(String termId);

    ClassificationScheme getClassificationScheme();

    Instant getLastModified();

}
