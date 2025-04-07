package edu.uob;

public class ParsedInsertCommand {

    public static InsertCommand parse(String command) {
        if(command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("INSERT statement format error.");
        }
        command = command.trim();
        String upper = command.toUpperCase();
        if(!upper.startsWith("INSERT INTO ")) {
            throw new IllegalArgumentException("INSERT statement format error.");
        }
        int valuesIndex = upper.indexOf(" VALUES");
        if(valuesIndex == -1) {
            throw new IllegalArgumentException("Missing 'VALUES' keyword.");
        }

        int tableNameStartIndex = "INSERT INTO ".length();
        if(valuesIndex <= tableNameStartIndex) {
            throw new IllegalArgumentException("Missing table name.");
        }
        String tableName = command.substring(tableNameStartIndex, valuesIndex).trim();
        if(tableName.isEmpty()) {
            throw new IllegalArgumentException("Missing table name.");
        }
        if(!TokenTableName.isValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        String parts = command.substring(valuesIndex + " VALUES".length()).trim();
        if(!parts.startsWith("(") || !parts.endsWith(")")) {
            throw new IllegalArgumentException("Value list must be enclosed in parentheses.");
        }

        String valuesStr = parts.substring(1, parts.length() - 1).trim();
        if (!TokenValueList.isValidValueList(valuesStr)) {
            throw new IllegalArgumentException("Value list input format error or invalid value: " + valuesStr);
        }
        String[] values = valuesStr.split("\\s*,\\s*");
        return new InsertCommand(tableName.toLowerCase(), values);
    }

    public static class InsertCommand {
        private final String tableName;
        private final String[] values;

        public InsertCommand(String tableName, String[] values) {
            this.tableName = tableName;
            this.values = values;
        }

        public String getTableName() {
            return tableName;
        }

        public String[] getValues() {
            return values;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableName).append(" VALUES (");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(values[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}

