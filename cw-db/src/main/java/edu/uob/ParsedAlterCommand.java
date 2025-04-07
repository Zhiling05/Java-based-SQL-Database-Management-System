package edu.uob;

public class ParsedAlterCommand {
    public static AlterCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Alter statement format error.");
        }
        command = command.trim();
        String[] tokens = command.split("\\s+");

        if (tokens.length < 5) {
            throw new IllegalArgumentException("Alter statement format error.");
        }
        String tableName = tokens[2];
        String alterType = tokens[3].toUpperCase();
        String attributeName;
        if(tokens.length == 5) {
            attributeName = tokens[4];
        } else {
            StringBuilder sb = new StringBuilder();
            for(int i = 4; i < tokens.length; i++) {
                if(i > 4) {
                    sb.append(" ");
                }
                sb.append(tokens[i]);
            }
            attributeName = sb.toString();
        }
        if(!TokenTableName.isValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        TokenTableName tokenTableName = new TokenTableName(tableName);
        if (!TokenAttributeName.isValid(attributeName)) {
            throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }

        TokenAttributeName tokenAttrName = new TokenAttributeName(attributeName);
        if (!alterType.equals("ADD") && !alterType.equals("DROP")) {
            throw new IllegalArgumentException("Alteration type can only be 'ADD' or 'DROP'.");
        }

        if (alterType.equals("ADD")) {
            return new AlterAddCommand(tokenTableName.getName(), tokenAttrName.getName());
        } else {
            return new AlterDropCommand(tokenTableName.getName(), tokenAttrName.getName());
        }
    }

    public static abstract class AlterCommand {
        public abstract AlterCommandType getType();
        public abstract String getTableName();
        public abstract String getAttributeName();
    }

    public enum AlterCommandType {
        ADD,
        DROP
    }

    public static class AlterAddCommand extends AlterCommand {
        private final String tableName;
        private final String attributeName;

        public AlterAddCommand(String tableName, String attributeName) {
            this.tableName = tableName.toLowerCase();
            this.attributeName = attributeName;
        }

        @Override
        public AlterCommandType getType() {
            return AlterCommandType.ADD;
        }

        @Override
        public String getTableName() {
            return tableName;
        }

        @Override
        public String getAttributeName() {
            return attributeName;
        }

        @Override
        public String toString() {
            return "ALTER TABLE " + tableName + " ADD " + attributeName;
        }
    }

    public static class AlterDropCommand extends AlterCommand {
        private final String tableName;
        private final String attributeName;

        public AlterDropCommand(String tableName, String attributeName) {
            this.tableName = tableName.toLowerCase();
            this.attributeName = attributeName;
        }

        @Override
        public AlterCommandType getType() {
            return AlterCommandType.DROP;
        }

        @Override
        public String getTableName() {
            return tableName;
        }

        @Override
        public String getAttributeName() {
            return attributeName;
        }

        @Override
        public String toString() {
            return "ALTER TABLE " + tableName + " DROP " + attributeName;
        }
    }
}
