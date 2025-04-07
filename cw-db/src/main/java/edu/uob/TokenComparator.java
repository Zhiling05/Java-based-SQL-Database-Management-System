package edu.uob;

public enum TokenComparator {
    EQ("=="),
    GE(">="),
    LE("<="),
    NE("!="),
    GT(">"),
    LT("<"),
    LIKE("LIKE");

    private String symbol;

    TokenComparator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static TokenComparator fromString(String str) {
        for (TokenComparator comp : TokenComparator.values()) {
            if (comp == LIKE) {
                if (str.trim().equalsIgnoreCase("LIKE")) return comp;
            } else {
                if (str.equals(comp.getSymbol())) return comp;
            }
        }
        throw new IllegalArgumentException("Invalid comparator: " + str);
    }
}