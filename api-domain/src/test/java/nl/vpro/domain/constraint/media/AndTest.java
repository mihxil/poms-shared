/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.JAXB;

import org.junit.Test;

import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class AndTest {

    @Test
    public void testAndBinding() throws Exception {
        And in = new And(new And());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:and xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:and/>\n" +
                "</local:and>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(And.class);
    }

    @Test
    public void testOrBinding() throws Exception {
        And in = new And(new Or());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:and xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:or/>\n" +
                "</local:and>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Or.class);
    }

    @Test
    public void testNotBinding() throws Exception {
        And in = new And(new Not());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:and xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:not/>\n" +
                "</local:and>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Not.class);
    }

    @Test
    public void testAvTypeBinding() throws Exception {
        And in = new And(new AvTypeConstraint());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:and xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avType/>\n" +
                "</local:and>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvTypeConstraint.class);
    }

    @Test
    public void testAvFileFormatBinding() throws Exception {
        And in = new And(new AvFileFormatConstraint());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:and xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avFileFormat/>\n" +
                "</local:and>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvFileFormatConstraint.class);
    }

    @Test
    public void testAvFileExtensionBinding() throws Exception {
        And in = new And(new AVFileExtensionConstraint());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:and xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avFileExtension/>\n" +
                "</local:and>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AVFileExtensionConstraint.class);
    }

    @Test
    public void testApplyWhenEmpty() {
        And constraint = new And();
        assertThat(constraint.test(null)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        And constraint = new And(
            MediaConstraints.alwaysFalse(),
            MediaConstraints.alwaysTrue(),
            MediaConstraints.alwaysFalse()
        );
        assertThat(constraint.test(null)).isFalse();

        assertThat(constraint.testWithReason(null).applies()).isFalse();
        assertThat(constraint.testWithReason(null).getReason()).isEqualTo("And");
        assertThat(constraint.testWithReason(null).getDescription(Locales.DUTCH)).isEqualTo("(Voldoet nooit en Voldoet nooit)");
        JAXB.marshal(constraint.testWithReason(null), System.out);

    }

    @Test
    public void testApplyWhenTrue() {
        And constraint = new And(
            MediaConstraints.alwaysTrue()
        );
        assertThat(constraint.test(null)).isTrue();
    }
}
