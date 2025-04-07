package edu.uob;

public class ParsedJoinCommand {
    public static JoinCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("JOIN statement format error.");
        }

        command = command.trim().toUpperCase();
        if (!command.startsWith("JOIN ")) {
            throw new IllegalArgumentException("JOIN statement format error.");
        }
        String parts = command.substring(5).trim();
        int partIndex1 = parts.indexOf(" AND ");
        if(partIndex1 == -1)   throw new IllegalArgumentException("JOIN statement format error.");
        String tableName1 = parts.substring(0, partIndex1).trim();
        if(tableName1.isEmpty())   throw new IllegalArgumentException("Missing the name of the table.");

        parts = parts.substring(partIndex1 + 5).trim();
        int onIndex = parts.toUpperCase().indexOf(" ON ");
        if(onIndex == -1)   throw new IllegalArgumentException("Missing 'ON' clause.");
        String tableName2 = parts.substring(0, onIndex).trim();
        if(tableName2.isEmpty())   throw new IllegalArgumentException("Missing the name of the table.");

        parts = parts.substring(onIndex + 4).trim();
        int partIndex2 = parts.indexOf(" AND ");
        if(partIndex2 == -1)   throw new IllegalArgumentException("JOIN statement format error.");
        String attribute1 = parts.substring(0, partIndex2).trim();
        String attribute2 = parts.substring(partIndex2 + 5).trim();
        if(attribute1.isEmpty() || attribute2.isEmpty()) {
            throw new IllegalArgumentException("Missing attribute name.");
        }

        if(!TokenTableName.isValid(tableName1))   throw new IllegalArgumentException("Invalid table name: " + tableName1);
        if(!TokenTableName.isValid(tableName2))   throw new IllegalArgumentException("Invalid table name: " + tableName2);
        if(!TokenAttributeName.isValid(attribute1))   throw new IllegalArgumentException("Invalid attribute name: " + attribute1);
        if(!TokenAttributeName.isValid(attribute2))   throw new IllegalArgumentException("Invalid attribute name: " + attribute2);

        return new JoinCommand(
                tableName1.toLowerCase(),
                tableName2.toLowerCase(),
                attribute1.toLowerCase(),
                attribute2.toLowerCase()
        );
    }

    public static class JoinCommand {
        private final String tableName1;
        private final String tableName2;
        private final String attribute1;
        private final String attribute2;

        public JoinCommand(String tableName1, String tableName2, String attribute1, String attribute2) {
            this.tableName1 = tableName1;
            this.tableName2 = tableName2;
            this.attribute1 = attribute1;
            this.attribute2 = attribute2;
        }

        public String getTableName1() {
            return tableName1;
        }

        public String getTableName2() {
            return tableName2;
        }

        public String getAttribute1() {
            return attribute1;
        }

        public String getAttribute2() {
            return attribute2;
        }

        @Override
        public String toString() {
            return "JOIN " + tableName1 + " AND " + tableName2 + " ON " + attribute1 + " AND " + attribute2;
        }
    }
}
