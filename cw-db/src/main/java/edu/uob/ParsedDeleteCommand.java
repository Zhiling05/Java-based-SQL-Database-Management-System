package edu.uob;

public class ParsedDeleteCommand {
    public static DeleteCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("DELETE statement format error.");
        }
        command = command.trim();
        String upper = command.toUpperCase();
        if (!upper.startsWith("DELETE FROM ")) {
            throw new IllegalArgumentException("DELETE statement format error.");
        }

        String afterPart = command.substring("DELETE FROM ".length()).trim();
        int whereIndex = afterPart.toUpperCase().indexOf(" WHERE ");
        if (whereIndex == -1) {
            throw new IllegalArgumentException("Missing WHERE clause.");
        }

        String tableName = afterPart.substring(0, whereIndex).trim();
        if (!TokenTableName.isValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        TokenTableName tokenTableName = new TokenTableName(tableName);
        String conditionStr = afterPart.substring(whereIndex + " WHERE ".length()).trim();
        if (conditionStr.isEmpty()) {
            throw new IllegalArgumentException("Missing WHERE clause.");
        }
        TokenCondition condition = ConditionParser.parseCondition(conditionStr);
        return new DeleteCommand(tokenTableName.getName(), condition);
    }

    public static class DeleteCommand {
        private final String tableName;
        private final TokenCondition condition;

        public DeleteCommand(String tableName, TokenCondition condition) {
            this.tableName = tableName;
            this.condition = condition;
        }

        public String getTableName() {
            return tableName;
        }

        public TokenCondition getCondition() {
            return condition;
        }

        @Override
        public String toString() {
            return "DELETE FROM " + tableName + " WHERE " + condition.toString();
        }
    }
}
