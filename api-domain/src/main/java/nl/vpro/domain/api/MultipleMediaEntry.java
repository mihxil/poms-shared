package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "multipleMediaEntryType", propOrder = { })
@JsonPropertyOrder({})
public class MultipleMediaEntry extends  MultipleEntry<MediaObject> {

    public MultipleMediaEntry() {

    }
    public MultipleMediaEntry(String id, MediaObject m) {
        super(id, m);
    }
}
