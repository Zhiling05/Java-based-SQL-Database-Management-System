package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class ParsedUpdateCommand {
    public static UpdateCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("UPDATE statement format error.");
        }
        command = command.trim();
        if (!command.toUpperCase().startsWith("UPDATE ")) {
            throw new IllegalArgumentException("UPDATE statement format error.");
        }

        String[] parts = command.split("(?i)\\s+SET\\s+", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("UPDATE statement format error.");
        }
        String[] updateTokens = parts[0].trim().split("\\s+");
        if (updateTokens.length != 2) {
            throw new IllegalArgumentException("UPDATE statement format error.");
        }
        String tableName = updateTokens[1].trim();
        if (!TokenTableName.isValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        TokenTableName tokenTableName = new TokenTableName(tableName);

        String[] setParts = parts[1].trim().split("(?i)\\s+WHERE\\s+", 2);
        if (setParts.length < 2) {
            throw new IllegalArgumentException("Missing WHERE clause.");
        }
        String nameValueListStr = setParts[0].trim();
        String[] pairs = nameValueListStr.split("\\s*,\\s*");
        List<TokenNameValuePair> nameValuePairs = new ArrayList<>();
        for (String pair : pairs) {
            TokenNameValuePair nameValuePair = new TokenNameValuePair(pair);
            String[] nameValue = pair.split("\\s*=\\s*");
            String attrName = nameValue[0];
            if (attrName.equalsIgnoreCase("id")) {
                throw new IllegalArgumentException("Cannot update 'id'.");
            }
            nameValuePairs.add(nameValuePair);
        }

        String conditionStr = setParts[1].trim();
        if (conditionStr.isEmpty()) {
            throw new IllegalArgumentException("WHERE clause cannot be empty.");
        }
        TokenCondition condition = ConditionParser.parseCondition(conditionStr);

        return new UpdateCommand(tokenTableName.getName(), nameValuePairs, condition);
    }

    public static class UpdateCommand {
        private final String tableName;
        private final List<TokenNameValuePair> nameValuePairs;
        private final TokenCondition condition;

        public UpdateCommand(String tableName, List<TokenNameValuePair> nameValuePairs, TokenCondition condition) {
            this.tableName = tableName.toLowerCase();
            this.nameValuePairs = nameValuePairs;
            this.condition = condition;
        }

        public String getTableName() {
            return tableName;
        }

        public List<TokenNameValuePair> getNameValuePairs() {
            return nameValuePairs;
        }

        public TokenCondition getCondition() {
            return condition;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(tableName)
                    .append(" SET ");
            List<String> pairStrs = new ArrayList<>();
            for (TokenNameValuePair pair : nameValuePairs) {
                pairStrs.add(pair.toString());
            }
            sb.append(String.join(", ", pairStrs));
            sb.append(" WHERE ").append(condition.toString());
            return sb.toString();
        }
    }
}
