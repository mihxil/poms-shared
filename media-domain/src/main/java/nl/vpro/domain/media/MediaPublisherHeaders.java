package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface MediaPublisherHeaders {


    String PUBLISH_TO_HEADER = "publishTo";
    String QUEUETIME_HEADER = "queueTime";
    String TRANSACTION_UUID_HEADER = "transactionUUID";
    String REASON_HEADER = "reason";
    String TRIGGERED_BY_HEADER = "triggeredBy";
    String RECENTLY_MODIFIED_BY_HEADER = "recentlyModifiedBy";
    String LAST_MODIFIED_BY_HEADER = "lastModifiedBy";
    String MID = "mid";
    String PREVIOUS_PUBLISHDATE_HEADER = "previousPublishDate";


    @Slf4j
    enum Destination implements Displayable {

        UG("Uitzending gemist"),
        CouchDB_MID("CouchDB Poms (mids)"),
        CouchDB_URN("CouchDB Poms (urns)"),
        CouchDB_API("NPO API CouchDB"),
        ElasticSearch("NPO API Elastic Search"),
        TVVOD("TvVOD"),
        PROJECTM("Project M")
        ;


        private String displayName;

        Destination(String displayName) {
            this.displayName = displayName;
        }

        public static String[] toStringArray(Destination... destinations) {
            if(destinations == null) {
                return null;
            }

            String[] result = new String[destinations.length];
            for(int i = 0; i < destinations.length; i++) {
                result[i] = destinations[i].name();
            }
            return result;
        }

        public static String[] toStringArray(Collection<Destination> destinations) {
            if (destinations == null) {
                return null;
            }
            return toStringArray(destinations.toArray(new Destination[destinations.size()]));
        }

        public static Destination[] arrayOf(String destination) {
            String[] values =
                StringUtils.isEmpty(destination) ?
                    new String[0] :
                    destination.split("\\W+");

            Destination[] result = new Destination[values.length];
            for(int i = 0; i < values.length; i++) {
                try {
                    result[i] = valueOf(values[i]);
                } catch(IllegalArgumentException iae) {
                    log.error(iae.getMessage());
                }
            }
            return result;
        }

        public static Destination[] arrayOfOrNull(String destination) {
            if (StringUtils.isBlank(destination)) {
                return null;
            }
            return arrayOf(destination);
        }


        @Override
        public String getDisplayName() {
            return displayName;
        }
    }
}
