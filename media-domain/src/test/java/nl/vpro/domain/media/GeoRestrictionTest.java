/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Instant;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class GeoRestrictionTest extends ObjectTest<GeoRestriction> {

    @DataPoint
    public static GeoRestriction nullArgument = null;

    @DataPoint
    public static GeoRestriction beneluxNoTime = new GeoRestriction(Region.BENELUX);

    @DataPoint
    public static GeoRestriction nlNoTime = new GeoRestriction(Region.NL);

    @DataPoint
    public static GeoRestriction benelux = new GeoRestriction(Region.BENELUX, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));

    @DataPoint
    public static GeoRestriction nl = new GeoRestriction(Region.NL, Instant.ofEpochMilli(3), Instant.ofEpochMilli(3));

    @Test
    public void testEquals() throws Exception {
        assertThat(new GeoRestriction(Region.BENELUX)).isEqualTo(new GeoRestriction(Region.BENELUX));
    }

    @Test
    public void testEqualsOnTime() throws Exception {
        assertThat(new GeoRestriction(Region.BENELUX)).isNotEqualTo(new GeoRestriction(Region.BENELUX, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2)));
    }
}
