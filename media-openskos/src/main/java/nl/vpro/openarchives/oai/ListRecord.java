package nl.vpro.openarchives.oai;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class ListRecord {

    public static ListRecord empty() {
        ListRecord result = new ListRecord();
        result.records = new ArrayList<>();
        return result;
    }

    @XmlElement(required = false)
    private ResumptionToken resumptionToken;

    @XmlElement(name = "record")
    private List<Record> records;
}
