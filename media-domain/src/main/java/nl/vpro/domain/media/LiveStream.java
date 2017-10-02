package nl.vpro.domain.media;

import lombok.Getter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.Displayable;
import nl.vpro.util.URLResource;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Getter
public class LiveStream implements Displayable {

    static LiveStream[] values;
    static {
        URLResource.map(
            URI.create("classpath:/livestreams.properties"),
            (m) -> {
                List<LiveStream> result = new ArrayList<>();
                m.forEach((key, value) -> {
                        String[] array = value.trim().split("\\s*,\\s*", 2);
                        result.add(new LiveStream(key, array[0], Channel.valueOf(array[1])));
                    }
                );
                values = result.toArray(new LiveStream[result.size()]);
            }
        ).get();
    }

    private final String name;
    private final String id;
    private final Channel channel;

    LiveStream(String name, String id, Channel channel) {
        this.name = name;
        this.id = id;
        this.channel = channel;
    }

    @Override
    public String getDisplayName() {
        return channel.getDisplayName();
    }

    public static LiveStream[] values() {
        return values;
    }
}
