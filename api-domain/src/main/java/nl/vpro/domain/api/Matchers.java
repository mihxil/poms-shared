package nl.vpro.domain.api;


import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;


/**
 * Utilities related to matchers.
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Matchers {

    static final MatchType TEXT = new MatchType() {
        @Override
        public boolean eval (String value, String input, boolean caseSensitive){
            return value == null ? input == null : (caseSensitive ? value.equals(input) : value.equalsIgnoreCase(input));
        }

        @Override
        public Validation valid (String value){
            return Validation.valid();
        }
    };
    static final MatchType REGEX = new MatchType() {

        @Override
        public boolean eval (String value, String input,boolean caseSensitive) {
            return value == null ? input == null : input != null && Pattern.compile(value, (caseSensitive ? 0 : Pattern.CASE_INSENSITIVE)).matcher(input).matches();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public Validation valid (String value) {
            if (StringUtils.isNotBlank(value)) {
                try {
                    Pattern.compile(value);
                } catch (PatternSyntaxException pe) {
                    return Validation.invalid("Invalid regex: " + value + " " + pe.getMessage());
                }
                java.util.regex.Matcher matcher = REGEX_PATTERN.matcher(value);
                if (matcher.find()) {
                    return Validation.invalid("Regular expression can not start with a special character");
                }
            }
            return Validation.valid();
        }
    };
    static final MatchType WILDCARD = new MatchType() {
        @Override
        public boolean eval(String value, String input, boolean caseSensitive) {
            return value == null ? input == null : input != null && getWildcardPattern(value, caseSensitive).matcher(input).matches();

        }

        @Override
        public Validation valid(String value) {
            if (StringUtils.isNotBlank(value)) {
                if (value.startsWith("?") || value.startsWith("*")) {
                    return Validation.invalid("Wildcard can not start with either ? or *");
                }
            }
            return Validation.valid();
        }
    };

    private Matchers() {
    }

    public static Predicate<String> tokenizedPredicate(final AbstractTextMatcher m) {
        return new Predicate<String>() {
            private Set<String> tokenizedValue;


            @Override
            public boolean test(@Nullable String s) {
                final String value = m.getValue();
                if(s == null) {
                    return value == null;
                }
                Set<String> tokens = new HashSet<>(tokenize(s));
                for(String token : getTokenizedValue()) {
                    if(tokens.contains(token)) {
                        return m.getMatch() != Match.NOT;
                    }
                }
                return m.getMatch() == Match.NOT;
            }

            @Override
            public String toString() {
                return (m.getMatch() == Match.NOT ? "!=" : "=") + tokenize(m.getValue());
            }

            protected Set<String> getTokenizedValue() {
                if(tokenizedValue == null) {
                    tokenizedValue = new HashSet<>(tokenize(m.getValue()));
                }
                return tokenizedValue;
            }

            protected Collection<String> tokenize(String string) {
                if(string == null) {
                    return Collections.emptyList();
                }
                List<String> list = new ArrayList<>(Arrays.asList(string.toLowerCase().split("\\s*\\b\\s*")));
                Iterator<String> i = list.iterator();
                while(i.hasNext()) {
                    if(org.apache.commons.lang3.StringUtils.isEmpty(i.next())) {
                        i.remove();
                    }
                }
                return list;

            }

        };
    }


    protected static <S extends MatchType> Predicate<String> listPredicate(final AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S> textMatchers, final boolean tokenized) {
        return listPredicate(textMatchers, input -> {
            if(tokenized) {
                return Matchers.tokenizedPredicate(input);
            } else {
                return input;
            }
        });
    }

    public static <S extends MatchType> Predicate<String> listPredicate(final AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S>  textMatchers) {
        return listPredicate(textMatchers, false);
    }

    public static <S extends MatchType> Predicate<String> tokenizedListPredicate(final AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S>  textMatchers) {
        return listPredicate(textMatchers, true);
    }

    protected static <S extends MatchType> Predicate<String> listPredicate(final AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S> textMatchers, final Function<AbstractTextMatcher, Predicate<String>> predicater) {
        if(textMatchers == null) {
            return a -> true;
        }
        return new Predicate<String>() {
            @Override
            public boolean test(@Nullable String input) {
                switch(textMatchers.getMatch()) {
                    case SHOULD:
                        // OR
                        for(AbstractTextMatcher t : textMatchers) {
                            if(predicater.apply(t).test(input)) {
                                return true;
                            }
                        }
                        return false;
                    default:
                        // AND
                        for(AbstractTextMatcher t : textMatchers) {
                            if(!predicater.apply(t).test(input)) {
                                return textMatchers.getMatch() == Match.NOT;
                            }
                        }
                        return textMatchers.getMatch() != Match.NOT;
                }
            }
        };
    }


    public static <T, S extends MatchType> Predicate<Collection<T>> toPredicate(final AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S> textMatchers, final Function<T, String> textValueGetter) {
        if(textMatchers == null) {
            return i -> true;
        }

        return new Predicate<Collection<T>>() {
            @Override
            public boolean test(@Nullable Collection<T> collection) {
                if(collection == null) {
                    collection = Collections.emptyList();
                }
                switch (textMatchers.getMatch()) {
                    case SHOULD:
                        for (AbstractTextMatcher textMatcher : textMatchers) {
                            if (textMatcher.getMatch() == Match.NOT) {
                                boolean matchedall = true;
                                for (T item : collection) {
                                    String value = textValueGetter.apply(item);
                                    if (!textMatcher.test(value)) {
                                        matchedall = false;
                                        break;
                                    }
                                }
                                return matchedall;

                            } else {
                                for (T item : collection) {
                                    String value = textValueGetter.apply(item);
                                    if (textMatcher.test(value)) {
                                        return true;
                                    }
                                }
                            }
                        }
                        // OR
                        return false;
                    default:
                        // AND
                        for (AbstractTextMatcher textMatcher : textMatchers) {
                            if (textMatcher.getMatch() == Match.NOT) {
                                for (T item : collection) {
                                    String value = textValueGetter.apply(item);
                                    if (! textMatcher.test(value)) {
                                        return false;
                                    }
                                }

                            } else {
                                boolean found = false;
                                for (T item : collection) {
                                    String value = textValueGetter.apply(item);
                                    if (textMatcher.test(value)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    return false;
                                }

                            }
                        }
                        return true;
                }

            }
        };
    }


}



