package nl.vpro.domain.classification.bind;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.classification.Term;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlType(name = "termType", namespace = Xmlns.MEDIA_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@JsonSerialize(using = TermWrapperJsonString.Serializer.class)
@JsonDeserialize(using = TermWrapper.Deserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TermWrapper extends AbstractTermWrapper {


    public TermWrapper() {
    }

    public TermWrapper(String name) {
        super(name);
    }

    public TermWrapper(Term term) {
        super(term);
    }
    public static class Deserializer extends TermWrapperJsonString.Deserializer<TermWrapper> {

        @Override
        protected TermWrapper getTermWrapper() {
            return new TermWrapper();
        }
    }
}
