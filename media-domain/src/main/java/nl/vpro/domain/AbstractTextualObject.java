package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public abstract class AbstractTextualObject<T extends OwnedText<T>, D extends OwnedText<D>, TO extends AbstractTextualObject<T, D, TO>> 
    extends AbstractTextualObjectUpdate<T, D, TO>
    implements TextualObject<T, D, TO> {


}
