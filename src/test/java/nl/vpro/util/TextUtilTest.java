/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.util;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import static nl.vpro.util.TextUtil.isValid;
import static nl.vpro.util.TextUtil.sanitize;
import static org.fest.assertions.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class TextUtilTest {

    @Test
    public void testPatternOnTag() {
        assertThat(isValid("Tekst met <bold>html</bold>")).isFalse();
    }

    @Test
    public void testPatternOnLowerThen() {
        assertThat(isValid("a < b")).isTrue();
    }

    @Test
    public void testPatternOnValidAmpersand() {
        assertThat(isValid("Pauw & Witteman;")).isTrue();
    }

    @Test
    public void testPatternOnNumericEscape() {
        assertThat(isValid("&#233;")).isFalse();
    }

    @Test
    public void testPatternOnTextEscape() {
        assertThat(isValid("&eacute;")).isFalse();
    }

    @Test
    public void testPatternOnTextEscapeWithUC() {
        assertThat(isValid("&Ograve;")).isFalse();
    }

    @Test
    public void testPatternOnTextEscapeWithDigits() {
        assertThat(isValid("&frac14;")).isFalse();
    }

    @Test
    public void testPatternOnLineBreak() {
        assertThat(isValid("Text with line\u2028break.")).isFalse();
    }

    @Test
    public void testSanitizePreserveInputWithSmallerThen() {
        assertThat(sanitize("a < b")).isEqualTo("a < b");
    }

    @Test
    public void testSanitizePreserveInputWithAmpersand() {
        assertThat(sanitize("a & b")).isEqualTo("a & b");
    }

    @Test
    public void testSanitizeOnHtmlInput() {
        assertThat(sanitize("<p>Hello world</p><br>")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnHtmlFantasy() {
        assertThat(sanitize("<fantasy>Hello world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnAmpersands() {
        assertThat(sanitize("Hello&nbsp;world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnIllegalLineBreak() {
        assertThat(sanitize("Hello\u2028world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnHtmlEscapedNbsp() {
        assertThat(sanitize("Hello&nbsp;world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnDoubleEscapedInput() {
        assertThat(sanitize("A &amp;amp; B")).isEqualTo("A & B");
    }

    @Test
    public void testSanitizeOnDoubleEscapedNbsp() {
        assertThat(sanitize("Hello&amp;nbsp;world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeUnicodeChar() throws UnsupportedEncodingException {
        String result = sanitize("KRO De Re&#252;nie");
        assertThat(result.getBytes("UTF8")).isEqualTo("KRO De Reünie".getBytes("UTF8"));
    }
}
