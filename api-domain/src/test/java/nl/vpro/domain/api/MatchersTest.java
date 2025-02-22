package nl.vpro.domain.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class MatchersTest {
    @Test
    public void testTokenizedPredicate() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isTrue();
    }

    @Test
    public void testTokenizedPredicate2() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo bar")).isTrue();
    }

    @Test
    public void testTokenizedPredicate3() {
        TextMatcher matcher = new TextMatcher("foo bar");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isTrue();
    }

    @Test
    public void testTokenizedPredicate4() {
        TextMatcher matcher = new TextMatcher("foo bar");
        assertThat(Matchers.tokenizedPredicate(matcher).test("xxx")).isFalse();
    }

    @Test
    public void testTokenizedPredicateNot() {
        TextMatcher matcher = new TextMatcher("foo", Match.NOT);
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isFalse();
    }

    @Test
    public void testTokenizedPredicates() {
        TextMatcher matcher1 = new TextMatcher("foo");
        TextMatcher matcher2 = new TextMatcher("bar");
        assertThat(Matchers.tokenizedListPredicate(new TextMatcherList(Match.SHOULD, matcher1, matcher2)).test("foo")).isTrue();
        assertThat(Matchers.tokenizedListPredicate(new TextMatcherList(Match.MUST, matcher1, matcher2)).test("foo")).isFalse();
    }

    @Test
    public void testUntokenizedPredicate() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(matcher.test("foo")).isTrue();
        assertThat(matcher.test("Foo")).isFalse();
    }

    @Test
    public void testUntokenizedPredicateNot() {
        TextMatcher matcher = new TextMatcher("foo", Match.NOT);
        assertThat(matcher.test("foo")).isFalse();
    }

    @Test
    public void testUntokenizedLowercasePredicate() {
        ExtendedTextMatcher matcher = new ExtendedTextMatcher("foo", Match.MUST, StandardMatchType.TEXT, false);
        assertThat(matcher.test("foo")).isTrue();
        assertThat(matcher.test("Foo")).isTrue();
    }

    @Test
    public void testUntokenizedPredicates() {
        TextMatcher matcher1 = new TextMatcher("foo");
        TextMatcher matcher2 = new TextMatcher("bar");
        assertThat(matcher2.test("foo")).isFalse();
        assertThat(Matchers.listPredicate(new TextMatcherList(Match.SHOULD, matcher1, matcher2)).test("foo")).isTrue();
        assertThat(Matchers.listPredicate(new TextMatcherList(matcher1, matcher2)).test("foo")).isFalse();
    }

    @Test
    public void testListPredicateWithShould() {
        TextMatcherList textMatchers = new TextMatcherList(Match.SHOULD, new TextMatcher("SEASON"), new TextMatcher("SERIES"));
        assertThat(Matchers.listPredicate(textMatchers).test("SEASON")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("ALBUM")).isFalse();
    }

    @Test
    public void testListPredicateWithNots() {
        TextMatcherList textMatchers = new TextMatcherList(new TextMatcher("SEASON", Match.NOT), new TextMatcher("SERIES", Match.NOT));
        assertThat(Matchers.listPredicate(textMatchers).test("SEASON")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("ALBUM")).isTrue();
    }

    @Test
    public void testListPredicateWithMust() {
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("AA.*", StandardMatchType.REGEX), new TextMatcher(".*BB", StandardMatchType.REGEX));
        assertThat(Matchers.listPredicate(textMatchers).test("AAxxxxBBB")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("foobar")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("AAxxxx")).isFalse();
    }

    @Test
    public void testListPredicateWithOneNot() {
        Function<String, String> STRING = input -> input;
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB", Match.NOT));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isFalse();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isTrue();
    }

    @Test
    public void testListPredicateWithOne() {
        Function<String, String> STRING = input -> input;
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB"));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isTrue();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isFalse();
    }

    @Test
    public void testListPredicateWithMore() {
        Function<String, String> STRING = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input;

            }
        };
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB", Match.NOT), new TextMatcher("CC", Match.NOT));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("CC", "BB"))).isFalse();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isTrue();
    }

    @Test
    public void testListPredicateWithMore2() {
        Function<String, String> STRING = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input;

            }
        };
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB"), new TextMatcher("CC", Match.NOT));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("CC", "BB"))).isFalse();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isFalse();
    }
}
