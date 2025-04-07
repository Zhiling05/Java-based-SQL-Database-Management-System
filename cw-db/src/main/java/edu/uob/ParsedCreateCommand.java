package edu.uob;

public class ParsedCreateCommand {

    public static String parseCreateCommand(String command){
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty command");
        }

        String upperCommand = command.toUpperCase();
        if (upperCommand.startsWith("CREATE DATABASE ")) {
            return parseCreateDatabase(command);
        } else if (upperCommand.startsWith("CREATE TABLE")) {
            return parseCreateTable(command);
        } else {
            throw new IllegalArgumentException("CREATE statement format error.");
        }
    }

    private static String parseCreateDatabase(String command) {
        String dbName = command.substring("CREATE DATABASE ".length()).trim();
        TokenDatabaseName tokenDBName = new TokenDatabaseName(dbName);
        return tokenDBName.getName();
    }

    private static String parseCreateTable(String command) {
        String parts = command.substring("CREATE TABLE ".length()).trim();
        String tableName;
        TokenAttributeList attrList = null;

        if (parts.contains("(")) {
            int openParenIndex = parts.indexOf('(');
            int closeParenIndex = parts.lastIndexOf(')');
            if (openParenIndex == -1 || closeParenIndex == -1 || closeParenIndex <= openParenIndex) {
                throw new IllegalArgumentException("Missing or mismatched parentheses");
            }
            tableName = parts.substring(0, openParenIndex).trim();
            String attributesStr = parts.substring(openParenIndex + 1, closeParenIndex).trim();
            attrList = TokenAttributeList.parse(attributesStr);
        } else {
            tableName = parts.trim();
        }

        if (!TokenTableName.isValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        TokenTableName tokenTableName = new TokenTableName(tableName);
        return tokenTableName.getName();
    }
}
