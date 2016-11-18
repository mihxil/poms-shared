/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import org.junit.Test;

import nl.vpro.validation.ImageURIValidator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class ImageURIValidatorTest {

    ImageURIValidator validator = new ImageURIValidator();

    /**
     * Legacy. Once there was a bug which distributed uri's like this
     */
    @Test
    public void testIsValidWithDot() throws Exception {
        String uri = "urn:vpro.image:12345";
        assertThat(validator.isValid(uri, null)).isTrue();
    }

    @Test
    public void testIsValidWithColon() throws Exception {
        String uri = "urn:vpro:image:12345";
        assertThat(validator.isValid(uri, null)).isTrue();
    }

    @Test
    public void testIsValidWhenInvalid() throws Exception {
        String uri = "urn:vpro:images:12345";
        assertThat(validator.isValid(uri, null)).isFalse();
    }
}
