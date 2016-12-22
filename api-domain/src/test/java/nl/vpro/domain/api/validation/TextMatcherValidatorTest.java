package nl.vpro.domain.api.validation;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.api.StandardMatchType;
import nl.vpro.domain.api.TextMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author rico
 * @since 3.1
 */
public class TextMatcherValidatorTest {

    private final TextMatcherValidator validator = new TextMatcherValidator();

    private final ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeBuildercontext = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext.class);

    @Before
    public void setup() {
        reset(context, builder);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addNode(anyString())).thenReturn(nodeBuildercontext);
    }

    @Test
    public void EmptyTextMatcher() {
        TextMatcher textMatcher = new TextMatcher("");
        assertThat(validator.isValid(textMatcher, context)).isTrue();
    }

    @Test
    public void TextTextMatcher() {
        TextMatcher textMatcher = new TextMatcher("value");
        assertThat(validator.isValid(textMatcher, context)).isTrue();
    }

    @Test
    public void RegexTextMatcher() {
        TextMatcher textMatcher = new TextMatcher("achter.*", null, StandardMatchType.REGEX);
        assertThat(validator.isValid(textMatcher, context)).isTrue();
    }

    @Test
    public void InvalidRegexTextMatcher() {
        TextMatcher textMatcher = new TextMatcher("achter*[", null, StandardMatchType.REGEX);
        assertThat(validator.isValid(textMatcher, context)).isFalse();
    }

    @Test
    public void UnsupportedRegexTextMatcher() {
        TextMatcher textMatcher = new TextMatcher(".*aap", null, StandardMatchType.REGEX);
        assertThat(validator.isValid(textMatcher, context)).isFalse();
    }

    @Test
    public void UnsupportedWildcardTextMatcher() {
        TextMatcher textMatcher = new TextMatcher("*boe", null, StandardMatchType.WILDCARD);
        assertThat(validator.isValid(textMatcher, context)).isFalse();
    }
}
