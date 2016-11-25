package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {
    lastModified(Type.LONG),
    sortTitle(Type.STRING),
    creationDate(Type.LONG),
    locations(Type.LONG) {
        @Override
        public String field() {
            return "locationCount";
        }
    },
    sortDate(Type.LONG),
    mid(Type.STRING),
    type(Type.STRING),
    lastModifiedBy(Type.STRING),
    publishStop(Type.LONG),
    publishStart(Type.LONG),
    publishDate(Type.LONG);

    private final Type t;

    MediaSortField(Type type) {
        this.t = type;
    }
    @Override
    public Type type() {
        return t;
    }
}
