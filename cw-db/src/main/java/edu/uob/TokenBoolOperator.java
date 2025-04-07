package edu.uob;

public enum TokenBoolOperator {
    AND,
    OR;

    public static TokenBoolOperator fromString(String str) {
        if (str.equalsIgnoreCase("AND")) return AND;
        if (str.equalsIgnoreCase("OR")) return OR;
        throw new IllegalArgumentException("Invalid bool operator: " + str);
    }
}
