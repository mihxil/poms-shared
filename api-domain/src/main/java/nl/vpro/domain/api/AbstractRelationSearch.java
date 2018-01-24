package nl.vpro.domain.api;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.collect.Iterators;

import nl.vpro.domain.media.Relation;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractRelationSearch extends AbstractSearch
    implements Predicate<Relation>, Iterable<AbstractTextMatcher<?>> {
    @Valid
    private TextMatcherList types;

    @Valid
    private TextMatcherList broadcasters;

    @Valid
    private ExtendedTextMatcherList values;

    @Valid
    private TextMatcherList uriRefs;

    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(types, broadcasters, values, uriRefs);
    }

    @Override
    public boolean test(@Nullable Relation relation) {
        return relation != null
                && (types == null || types.test(relation.getType()))
                && (broadcasters == null || broadcasters.test(relation.getBroadcaster()))
                && (values == null || values.test(relation.getText()))
                && (uriRefs == null || uriRefs.test(relation.getUriRef()));
    }

    public boolean searchEqualsOrNarrows(AbstractRelationSearch that) {
        return that == null ||
            (that.broadcasters == null || TextMatcherList.searchEquals(broadcasters, that.broadcasters)) &&
            (that.types == null || TextMatcherList.searchEquals(types, that.types)) &&
            (that.values == null || ExtendedTextMatcherList.searchEquals(values, that.values)) &&
            (that.uriRefs == null || TextMatcherList.searchEquals(uriRefs, that.uriRefs));
    }

    public TextMatcherList getTypes() {
        return types;
    }

    public void setTypes(TextMatcherList types) {
        this.types = types;
    }

    public TextMatcherList getBroadcasters() {
        return broadcasters;
    }

    public void setBroadcasters(TextMatcherList broadcasters) {
        this.broadcasters = broadcasters;
    }

    public ExtendedTextMatcherList getValues() {
        return values;
    }

    public void setValues(ExtendedTextMatcherList values) {
        this.values = values;
    }

    public TextMatcherList getUriRefs() {
        return uriRefs;
    }

    public void setUriRefs(TextMatcherList uriRefs) {
        this.uriRefs = uriRefs;
    }

    @Override
    public Iterator<AbstractTextMatcher<?>> iterator() {
        return Iterators.concat(iterator(types), iterator(broadcasters), iterator(values), iterator(uriRefs));
    }

    private <T extends AbstractTextMatcher<S>, S extends MatchType> Iterator<T> iterator(
        AbstractTextMatcherList<T, S> list) {
        if (list == null) {
            return Collections.emptyIterator();
        } else {
            return list.iterator();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractRelationSearch that = (AbstractRelationSearch) o;

        if (types != null ? !types.equals(that.types) : that.types != null) return false;
        if (broadcasters != null ? !broadcasters.equals(that.broadcasters) : that.broadcasters != null) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;
        return uriRefs != null ? uriRefs.equals(that.uriRefs) : that.uriRefs == null;

    }

    @Override
    public int hashCode() {
        int result = types != null ? types.hashCode() : 0;
        result = 31 * result + (broadcasters != null ? broadcasters.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        result = 31 * result + (uriRefs != null ? uriRefs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AbstractRelationSearch{" +
            "types=" + types +
            ", broadcasters=" + broadcasters +
            ", values=" + values +
            ", uriRefs=" + uriRefs +
            '}';
    }
}
