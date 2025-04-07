package edu.uob;

import java.util.HashSet;
import java.util.Set;

public class TokenAttributeName {
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(Set.of(
            "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES",
            "CREATE", "TABLE", "DATABASE", "DROP", "ALTER",
            "UPDATE", "DELETE", "JOIN", "AND", "OR", "LIKE",
            "TRUE", "FALSE", "NULL"
    ));

    private String name;

    public TokenAttributeName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The attribute name cannot be empty.");
        }

        if (!TokenPlainText.isValidPlainText(name)) {
            throw new IllegalArgumentException("Attribute names can only contain letters and digits: " + name);
        }
        if (RESERVED_KEYWORDS.contains(name.toUpperCase())) {
            throw new IllegalArgumentException("Attribute names cannot use reversed keywords: " + name);
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isValid(String str) {
        return str != null && !str.isEmpty() && str.matches("^[a-zA-Z0-9]+$");
    }
}
