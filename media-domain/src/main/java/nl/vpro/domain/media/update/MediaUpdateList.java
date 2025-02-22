package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.vpro.util.IntegerVersion;
import nl.vpro.util.IntegerVersionSpecific;


/**
 * @author Michiel Meeuwissen
 */
@XmlRootElement(name = "list")
@XmlType(name = "mediaListResultType")
@XmlSeeAlso({
    ProgramUpdate.class,
    GroupUpdate.class,
    SegmentUpdate.class,
    MemberUpdate.class,
    MemberRefUpdate.class,
    LocationUpdate.class,
    String.class
})
@XmlAccessorType(XmlAccessType.NONE)
public class MediaUpdateList<T> implements Iterable<T>, IntegerVersionSpecific {

    @XmlAttribute
    @Getter
    @Setter
    protected IntegerVersion version;

    protected List<T> list;

    @XmlAttribute
    @Getter
    protected long offset;

    @XmlAttribute
    @Getter
    protected long totalCount;

    @XmlAttribute
    @Getter
    protected Integer max;

    @XmlAttribute
    @Getter
    protected String order;


    public MediaUpdateList() {
        super();
    }

    public MediaUpdateList(final List<T> list, long totalCount, long offset, Integer max, String order, IntegerVersion version) {
        this.list = Collections.unmodifiableList(list);
        this.offset = offset;
        this.totalCount =  totalCount;
        this.max = max;
        this.order = order;
        this.version = version;
    }


    public MediaUpdateList(T... list) {
        this.list = Collections.unmodifiableList(Arrays.asList(list));
        this.offset = 0;
        this.totalCount = list.length;
        this.max = null;
        this.order = null;
    }


    @XmlElement(name = "item")
    public List<T> getList() {
        return list;
    }
    public void setList(List<T> l) {
        this.list = l == null ? null : Collections.unmodifiableList(l);
    }


    //@Override
    public int size() {
        return list == null ? 0 : list.size();
    }

    @XmlAttribute
    public int getSize() {
        return size();
    }

    @Override
    public String toString() {
        return "" + list;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return list == null ? Collections.<T>emptyList().iterator() : list.iterator();
    }

    public Stream<T> stream() {
        return list == null ? Stream.empty() : list.stream();
    }


    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent != null) {
            if (parent instanceof IntegerVersionSpecific) {
                version = ((IntegerVersionSpecific) parent).getVersion();
            }
        }
    }
}
