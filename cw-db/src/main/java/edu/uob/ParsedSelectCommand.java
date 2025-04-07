package edu.uob;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedSelectCommand {
    public static SelectCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Select statement format error.");
        }
        command = command.trim();
        if (!command.toUpperCase().startsWith("SELECT ")) {
            throw new IllegalArgumentException("Select statement format error.");
        }

        String afterSelect = command.substring(7).trim();

        Pattern fromPattern = Pattern.compile("(?i)\\s+FROM\\s+");
        String[] selectParts = fromPattern.split(afterSelect, 2);
        if (selectParts.length < 2) {
            throw new IllegalArgumentException("Select statement format error.");
        }
        String wildAttribStr = selectParts[0].trim();
        TokenWildAttribList wildAttribList = TokenWildAttribList.parse(wildAttribStr);

        String fromPart = selectParts[1].trim();
        String tableName;
        TokenCondition condition = null;
        Pattern wherePattern = Pattern.compile("(?i)\\s+WHERE\\s+");
        Matcher whereMatcher = wherePattern.matcher(fromPart);
        if (whereMatcher.find()) {
            int whereIndex = whereMatcher.start();
            tableName = fromPart.substring(0, whereIndex).trim();
            String conditionStr = fromPart.substring(whereMatcher.end()).trim();
            condition = ConditionParser.parseCondition(conditionStr);
        } else {
            tableName = fromPart;
        }
        if (!TokenTableName.isValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        TokenTableName tokenTableName = new TokenTableName(tableName);

        return new SelectCommand(tokenTableName.getName(), wildAttribList, condition);
    }

    public static class SelectCommand {
        private final String tableName;
        private final TokenWildAttribList wildAttribList;
        private final TokenCondition condition;

        public SelectCommand(String tableName, TokenWildAttribList wildAttribList, TokenCondition condition) {
            this.tableName = tableName.toLowerCase();
            this.wildAttribList = wildAttribList;
            this.condition = condition;
        }

        public String getTableName() {
            return tableName;
        }

        public TokenWildAttribList getWildAttribList() {
            return wildAttribList;
        }

        public TokenCondition getCondition() {
            return condition;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ").append(wildAttribList.toString())
                    .append(" FROM ").append(tableName);
            if (condition != null) {
                sb.append(" WHERE ").append(condition.toString());
            }
            return sb.toString();
        }
    }
}

