package edu.uob;

import java.util.Set;
import  java.util.HashSet;

public class TokenTableName {
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(Set.of(
            "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES",
            "CREATE", "TABLE", "DATABASE", "DROP", "ALTER",
            "UPDATE", "DELETE", "JOIN", "AND", "OR", "LIKE",
            "TRUE", "FALSE", "NULL"
    ));

    private String name;

    public TokenTableName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be empty.");
        }

        if (!TokenPlainText.isValidPlainText(name)) {
            throw new IllegalArgumentException("Table name can only contain letters and digits: " + name);
        }

        if (RESERVED_KEYWORDS.contains(name.toUpperCase())) {
            throw new IllegalArgumentException("The table name cannot use reserved keywords: " + name);
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
