package fr.epistudio.epysia.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class PluralRule {

    private static final Pattern OR = Pattern.compile("\\bor\\b");
    private static final Pattern AND = Pattern.compile("\\band\\b");
    private static final Pattern NOT_IN = Pattern.compile("\\s*(?:!=|\\bnot\\s+in\\b)\\s*");
    private static final Pattern IN = Pattern.compile("\\s*(?:==|=|\\bin\\b)\\s*");
    private static final Pattern MODULUS = Pattern.compile("\\s*n\\s*(?:%\\s*(\\d+))?\\s*");
    private static final String RANGE_SEPARATOR = "\\.\\.";

    private final List<List<Condition>> alternatives;

    private PluralRule(final List<List<Condition>> alternatives) {
        this.alternatives = alternatives;
    }

    public static PluralRule parse(final String expression) {
        final List<List<Condition>> alternatives = new ArrayList<>();
        for (final String clause : OR.split(expression)) {
            if (!clause.isBlank()) {
                alternatives.add(conditionsOf(clause));
            }
        }
        return new PluralRule(List.copyOf(alternatives));
    }

    private static List<Condition> conditionsOf(final String clause) {
        final List<Condition> conditions = new ArrayList<>();
        for (final String relation : AND.split(clause)) {
            if (!relation.isBlank()) {
                conditions.add(conditionOf(relation));
            }
        }
        return List.copyOf(conditions);
    }

    private static Condition conditionOf(final String relation) {
        final boolean negated = NOT_IN.matcher(relation).find();
        final String[] halves = (negated ? NOT_IN : IN).split(relation, 2);
        if (halves.length < 2) {
            throw new IllegalArgumentException("Unreadable plural relation: " + relation);
        }
        return new Condition(modulusOf(halves[0]), negated, rangesOf(halves[1]));
    }

    private static long modulusOf(final String operand) {
        final var matcher = MODULUS.matcher(operand);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unreadable plural operand: " + operand);
        }
        return matcher.group(1) == null ? 0L : Long.parseLong(matcher.group(1));
    }

    private static List<long[]> rangesOf(final String values) {
        final List<long[]> ranges = new ArrayList<>();
        for (final String value : values.trim().split(",")) {
            final String[] bounds = value.trim().split(RANGE_SEPARATOR);
            final long low = Long.parseLong(bounds[0].trim());
            ranges.add(new long[]{low, bounds.length > 1 ? Long.parseLong(bounds[1].trim()) : low});
        }
        return List.copyOf(ranges);
    }

    public boolean matches(final long count) {
        return alternatives.stream()
                .anyMatch(group -> group.stream().allMatch(condition -> condition.matches(count)));
    }

    private record Condition(long modulus, boolean negated, List<long[]> ranges) {

        private boolean matches(final long count) {
            final long amount = Math.abs(count);
            final long value = modulus == 0L ? amount : amount % modulus;
            final boolean inside = ranges.stream()
                    .anyMatch(range -> value >= range[0] && value <= range[1]);
            return inside != negated;
        }
    }
}
