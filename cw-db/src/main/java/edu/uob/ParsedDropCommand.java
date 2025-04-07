package edu.uob;

public class ParsedDropCommand {
    public static DropCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("DROP statement fromat error.");
        }
        command = command.trim();
        if (!command.toUpperCase().startsWith("DROP ")) {
            throw new IllegalArgumentException("DROP statement fromat error.");
        }
        String parts = command.substring("DROP ".length()).trim();
        if (parts.toUpperCase().startsWith("DATABASE ")) {
            String dbName = parts.substring("DATABASE ".length()).trim();
            if (!TokenDatabaseName.isValid(dbName)) {
                throw new IllegalArgumentException("Invalid database name : " + dbName);
            }
            new TokenDatabaseName(dbName);
            return new DropDatabaseCommand(dbName.toLowerCase());
        } else if (parts.toUpperCase().startsWith("TABLE ")) {
            String tableName = parts.substring("TABLE ".length()).trim();
            if (!TokenTableName.isValid(tableName)) {
                throw new IllegalArgumentException("Invalid table name : " + tableName);
            }
            new TokenTableName(tableName);
            return new DropTableCommand(tableName.toLowerCase());
        } else {
            throw new IllegalArgumentException("DROP statement format error.");
        }
    }

    public static abstract class DropCommand {
        public abstract CommandType getType();
    }

    public enum CommandType {
        DROP_DATABASE, DROP_TABLE
    }

    public static class DropDatabaseCommand extends DropCommand {
        private final String databaseName;

        public DropDatabaseCommand(String databaseName) {
            this.databaseName = databaseName;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        @Override
        public CommandType getType() {
            return CommandType.DROP_DATABASE;
        }

        @Override
        public String toString() {
            return "DROP DATABASE " + databaseName;
        }
    }

    public static class DropTableCommand extends DropCommand {
        private final String tableName;

        public DropTableCommand(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return tableName;
        }

        @Override
        public CommandType getType() {
            return CommandType.DROP_TABLE;
        }

        @Override
        public String toString() {
            return "DROP TABLE " + tableName;
        }
    }
}

