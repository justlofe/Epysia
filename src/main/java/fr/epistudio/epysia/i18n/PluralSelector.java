package fr.epistudio.epysia.i18n;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PluralSelector {

    public static final String RULE_PREFIX = "plural.";

    private static final String DEFAULT_ONE = "n == 1";

    private final Map<PluralCategory, PluralRule> rules;

    private PluralSelector(final Map<PluralCategory, PluralRule> rules) {
        this.rules = rules;
    }

    public static PluralSelector from(final Map<String, String> translations) {
        final Map<PluralCategory, PluralRule> rules = new LinkedHashMap<>();
        for (final PluralCategory category : PluralCategory.values()) {
            if (category != PluralCategory.OTHER) {
                ruleFor(translations, category).ifPresent(rule -> rules.put(category, rule));
            }
        }
        return new PluralSelector(rules.isEmpty() ? englishFallback() : Map.copyOf(rules));
    }

    private static java.util.Optional<PluralRule> ruleFor(final Map<String, String> translations,
                                                          final PluralCategory category) {
        final String expression = translations.getOrDefault(RULE_PREFIX + category.suffix(), "");
        if (expression.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(PluralRule.parse(expression));
    }

    private static Map<PluralCategory, PluralRule> englishFallback() {
        return Map.of(PluralCategory.ONE, PluralRule.parse(DEFAULT_ONE));
    }

    public PluralCategory categoryOf(final long count) {
        for (final PluralCategory category : PluralCategory.values()) {
            final PluralRule rule = rules.get(category);
            if (rule != null && rule.matches(count)) {
                return category;
            }
        }
        return PluralCategory.OTHER;
    }
}
