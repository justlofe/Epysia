package fr.epistudio.epysia.i18n;

public enum PluralCategory {

    ZERO("zero"),
    ONE("one"),
    TWO("two"),
    FEW("few"),
    MANY("many"),
    OTHER("other");

    private final String suffix;

    PluralCategory(final String suffix) {
        this.suffix = suffix;
    }

    public String suffix() {
        return suffix;
    }
}
