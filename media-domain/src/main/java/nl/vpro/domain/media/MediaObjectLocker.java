package nl.vpro.domain.media;

import lombok.Lombok;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import nl.vpro.services.TransactionService;

/**
 * Tool to make sure that the 'authority' related dropboxes don't run at the same time for the same mid.
 *
 * It may be better to (also) introduce more decent hibernate locking (MSE-3751)
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class MediaObjectLocker {


    /**
     * Map mid -> ReentrantLock
     */
    static Map<String, ReentrantLock>       LOCKED_MEDIA  = new ConcurrentHashMap<>();


    /**
     * Map key -> ReentrantLock
     */
    static Map<Serializable, ReentrantLock> LOCKED_OBJECTS= new ConcurrentHashMap<>();


    private static final MediaObjectLockerAdmin JMX_INSTANCE = new MediaObjectLockerAdmin();



    static {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(JMX_INSTANCE, new ObjectName("nl.vpro.media:name=objectLocker"));
        } catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }


    /**
     * Adding this annotation of a method with a {@link String} or {@link MediaIdentifiable} object will 'lock' the identifier, and will make sure
     * that not other code doing the same will run simultaneously.
     *
     * Much code like this will be get a mediaobject using this mid, change it and then commit the mediaobject.
     *
     * If another thread is changing the mediaobject in between those event, those changes will be lost.
     *
     * This can therefore be avoided using this annotations (or equivalently by using {@link #withMidLock(String, String, Callable)}

     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mid {
        int argNumber() default 0;
        String reason() default "";
        String method() default "";
    }


    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sid {
        int argNumber() default 0;
        String reason() default "";
    }



    public static <T> T withMidLock(
        @Nonnull TransactionService transactionService,
        String mid,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withMidLock(
            mid,
            reason,
            () -> transactionService.executeInNewTransaction(callable));
    }


    public static <T> T getWithMidLock(
        String mid,
        @Nonnull String reason,
        @Nonnull Supplier<T> callable) {
        return withMidLock(mid, reason, callable::get);
    }


     public static void withMidLock(
         String mid,
         @Nonnull String reason,
         @Nonnull Runnable runnable) {
        withMidLock(mid, reason, () -> {
            runnable.run();
            return null;
        });

     }

    public static <T> T withMidLock(
        String mid,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withObjectLock(mid, reason, callable, LOCKED_MEDIA);
    }


    public static <T> T withMidsLock(
        Iterable<String> mids,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withObjectsLock(mids, reason, callable, LOCKED_MEDIA);
    }

     public static void withMidsLock(
         Iterable<String> mid,
         @Nonnull String reason,
         @Nonnull Runnable runnable) {
         withMidsLock(mid, reason, () -> {
            runnable.run();
            return null;
        });

     }


    public static <T> T withKeyLock(
        Serializable id,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withObjectLock(id, reason, callable, LOCKED_OBJECTS);
    }

    @SneakyThrows
    private static <T, K extends Serializable> T withObjectLock(
        K key,
        @Nonnull String reason,
        @Nonnull Callable<T> callable,
        @Nonnull Map<K, ReentrantLock> locks) {
        if (key == null) {
            //log.warn("Calling with null mid: {}", reason, new Exception());
            log.warn("Calling with null key: {}", reason);
            return callable.call();
        }
        long nanoStart = System.nanoTime();
        ReentrantLock lock = aquireLock(nanoStart, key, reason, locks);
        try {
            return callable.call();
        } finally {
            releaseLock(nanoStart, key, reason, locks, lock);
        }
    }

    @SneakyThrows
    private static <T, K extends Serializable> T withObjectsLock(
        @Nonnull Iterable<K> keys,
        @Nonnull String reason,
        @Nonnull Callable<T> callable,
        @Nonnull Map<K, ReentrantLock> locks) {

        final long nanoStart = System.nanoTime();
        final List<ReentrantLock> lockList = new ArrayList<>();
        final List<K> copyOfKeys = new ArrayList<>();
        for (K key : keys) {
            if (key != null) {
                lockList.add(aquireLock(nanoStart, key, reason, locks));
                copyOfKeys.add(key);
            }
        }
        try {
            return callable.call();
        } finally {
            int i = 0;
            for (K key : copyOfKeys) {
                releaseLock(nanoStart, key, reason, locks, lockList.get(i++));
            }
        }
    }

    private static  <K extends Serializable> ReentrantLock aquireLock(long nanoStart, K key, @Nonnull  String reason,  final @Nonnull Map<K, ReentrantLock> locks) {
        ReentrantLock lock;
        boolean alreadyWaiting = false;
        synchronized (locks) {
            lock = locks.computeIfAbsent(key, (m) -> {
                    log.trace("New lock for " + m);
                    return new ReentrantLock();
                }
            );
            if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
                log.debug("There are already threads ({}) for {}, waiting", lock.getQueueLength(), key);
                JMX_INSTANCE.maxConcurrency = Math.max(lock.getQueueLength(), JMX_INSTANCE.maxConcurrency);
                alreadyWaiting = true;
            }
        }

        lock.lock();
        if (alreadyWaiting) {
            log.debug("Released and continuing {}", key);
        }

        JMX_INSTANCE.maxDepth = Math.max(JMX_INSTANCE.maxDepth, lock.getHoldCount());
        log.trace("{} holdcount {}", Thread.currentThread().hashCode(), lock.getHoldCount());
        if (lock.getHoldCount() == 1) {
            JMX_INSTANCE.lockCount.computeIfAbsent(reason, (s) -> new AtomicInteger(0)).incrementAndGet();
            JMX_INSTANCE.currentCount.computeIfAbsent(reason, (s) -> new AtomicInteger()).incrementAndGet();
            Duration aquireTime = Duration.ofNanos(System.nanoTime() - nanoStart);
            log.debug("Acquired lock for {}  ({}) in {}", key, reason, aquireTime);
        }
        return lock;
    }
    private static  <K extends Serializable> void releaseLock(long nanoStart, K key, @Nonnull  String reason,  final @Nonnull Map<K, ReentrantLock> locks, @Nonnull ReentrantLock lock) {
        synchronized (locks) {
            if (lock.getHoldCount() == 1) {
                if (!lock.hasQueuedThreads()) {
                    log.trace("Removed " + key);
                    locks.remove(key);
                }
                JMX_INSTANCE.currentCount.computeIfAbsent(reason, (s) -> new AtomicInteger()).decrementAndGet();
                log.debug("Released lock for {} ({}) in {}", key, reason, Duration.ofNanos(System.nanoTime() - nanoStart));
            }
            lock.unlock();
            locks.notifyAll();
        }
    }


}
