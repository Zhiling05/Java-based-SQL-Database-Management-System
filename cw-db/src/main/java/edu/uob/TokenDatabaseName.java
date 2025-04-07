package edu.uob;

import java.util.HashSet;
import java.util.Set;

public class TokenDatabaseName {
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(Set.of(
            "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES",
            "CREATE", "TABLE", "DATABASE", "DROP", "ALTER",
            "UPDATE", "DELETE", "JOIN", "AND", "OR", "LIKE",
            "TRUE", "FALSE", "NULL"
    ));

    private String name;

    public TokenDatabaseName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Missing database name.");
        }
        if (!TokenPlainText.isValidPlainText(name)) {
            throw new IllegalArgumentException("Database name can only contain letters and digits.");
        }
        if (RESERVED_KEYWORDS.contains(name.toUpperCase())) {
            throw new IllegalArgumentException("The database name cannot use reserved keywords: " + name);
        }
        this.name = name.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public static boolean isValid(String str) {
        return str != null && !str.isEmpty() && str.matches("^[a-zA-Z0-9]+$");
    }
}
