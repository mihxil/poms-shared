package nl.vpro.domain.media.search;

import java.util.*;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.Member;

/**
 * @author Michiel Meeuwissen
 * @since 1.5
 */
@XmlRootElement(name = "list")
@XmlType(
    name = "mediaListItemResultType")
@XmlSeeAlso({
    MediaListItem.class,
    Member.class
})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MediaList<T> implements Iterable<T> {

    protected List<T> list;

    @XmlAttribute
    protected long totalCount;

    @XmlAttribute
    protected Long offset;

    @XmlAttribute
    protected Integer max;

    @XmlAttribute
    protected MediaSortField sort;

    @XmlAttribute
    protected String order;

    public MediaList() {
        super();
    }

    public MediaList(final List<T> list) {
        this.list = list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        this.totalCount = this.list.size();
        this.offset = 0L;
        this.max = null;
        this.sort = null;
        this.order = null;
    }

    public MediaList(long offset, Integer max, long totalCount, Pager.Direction direction, final List<T> list) {
        this.list = list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        this.totalCount = totalCount;
        this.offset = offset;
        this.max = max;
        this.order = direction == null ? null : direction.toString();
    }


    public MediaList(MediaPager pager, long totalCount, final List<T> list) {
        this.list = list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        this.totalCount = totalCount;
        this.offset = pager.getOffset();
        this.max = pager.getMax();
        this.sort = pager.getSort();
        this.order = pager.getOrder() == null ? null : pager.getOrder().toString();
    }

    public MediaList(MediaPager pager, long totalCount, T... list) {
        this.list = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(list)));
        this.totalCount = totalCount;
        this.offset = pager.getOffset();
        this.max = pager.getMax();
        this.sort = pager.getSort();
        this.order = pager.getOrder() == null ? null : pager.getOrder().toString();
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

    protected void setSize(int size) {
        // ignore
    }

    public long getTotalCount() {
        return totalCount;
    }

    public String getOrder() {
        return order;
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
}
