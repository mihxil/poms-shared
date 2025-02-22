package nl.vpro.domain.api.thesaurus;

import lombok.NoArgsConstructor;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.media.gtaa.*;

@XmlRootElement(name = "thesaurusItems")
@XmlType(name = "thesaurusItemsType")
@NoArgsConstructor
@XmlSeeAlso({
        GTAAPerson.class,
        GTAATopic.class,
        GTAAGenre.class,
        GTAAGeographicName.class,
        GTAAMaker.class,
        GTAAName.class,
        ThesaurusItem.class
})public class ThesaurusResult<T extends ThesaurusObject> extends Result<T> {

    public ThesaurusResult(List<T> list, Integer max) {
        super(list, 0L, max, null);
    }



}
