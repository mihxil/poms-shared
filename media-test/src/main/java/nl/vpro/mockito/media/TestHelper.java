/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media;

import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Schedule;
import nl.vpro.mockito.media.answer.FirstArgument;
import nl.vpro.mockito.media.matcher.*;

public class TestHelper {

    public static <T> FirstArgument firstArgument(Class<T> clazz) {
        return new FirstArgument<T>();
    }

    public static FirstArgument withSameMediaObject() {
        return new FirstArgument<MediaObject>();
    }

    public static FirstArgument withSameSchedule() {
        return new FirstArgument<Schedule>();
    }

    public static <T> Answer argument(final int pos, Class<T> clazz) {
        return new Answer<T>() {
            @Override
            public T answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (T)args[pos];
            }
        };
    }

    public static Object anyMediaObject() {
        return ArgumentMatchers.argThat(new IsAnyMediaObject());
    }

    public static MediaObject anyProgram() {
        return ArgumentMatchers.argThat(new IsAnyProgram());
    }

    public static MediaObject anyGroup() {
        return ArgumentMatchers.argThat(new IsAnyGroup());
    }

    public static MediaObject anySegment() {
        return ArgumentMatchers.argThat(new IsAnySegment());
    }

    public static Object anySchedule() {
        return ArgumentMatchers.argThat(new IsAnySchedule());
    }
}
