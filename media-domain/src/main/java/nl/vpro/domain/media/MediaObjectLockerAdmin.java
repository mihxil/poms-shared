package nl.vpro.domain.media;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
class MediaObjectLockerAdmin implements MediaObjectLockerAdminMXBean {
    /**
     * Number of locks per 'reason'.
     */
    Map<String, AtomicInteger> lockCount = new HashMap<>();

    /**
     * Count per 'reason'.
     */
    Map<String, AtomicInteger> currentCount = new HashMap<>();
    @Getter
    int maxConcurrency = 0;
    @Getter
    int maxDepth = 0;

    @Override
    public Set<String> getLocks() {
        return MediaObjectLocker.LOCKED_MEDIA.keySet();
    }

    @Override
    public int getLockCount() {
        return lockCount.values().stream().mapToInt(AtomicInteger::intValue).sum();

    }

    @Override
    public Map<String, Integer> getLockCounts() {
        return lockCount.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));

    }

    @Override
    public int getCurrentCount() {
        return currentCount.values().stream().mapToInt(AtomicInteger::intValue).sum();
    }

    @Override
    public Map<String, Integer> getCurrentCounts() {
        return currentCount.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));

    }

    @Override
    public String clearMidLock(String mid) {
        return "removed " + MediaObjectLocker.LOCKED_MEDIA.remove(mid);

    }

    @Override
    public String clearMidLocks() {
        int size = MediaObjectLocker.LOCKED_MEDIA.size();
        MediaObjectLocker.LOCKED_MEDIA.clear();
        return "Removed all mid locks (approx. " + size + ")";
    }
}
