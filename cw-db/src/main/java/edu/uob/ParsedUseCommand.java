package edu.uob;

public class ParsedUseCommand {
    public static String parse(String command) {
        if (!command.toUpperCase().startsWith("USE ")) {
            throw new IllegalArgumentException("USE statement format error.");
        }

        String dbName = command.substring(3).trim();
        if (dbName.isEmpty()) {
            throw new IllegalArgumentException("Database name missing in USE command");
        }

        if (!TokenDatabaseName.isValid(dbName)) {
            throw new IllegalArgumentException("Invalid database name: " + dbName);
        }

        TokenDatabaseName tokenDBName = new TokenDatabaseName(dbName);
        return tokenDBName.getName();
    }
}
