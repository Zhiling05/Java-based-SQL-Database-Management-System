package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;
    private DB database;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    public String handleCommand(String command) {
        // TODO implement your server logic here
        if(command == null)  return "[ERROR]: Empty command";
        command = command.trim().replaceAll("\\s+", " ");
        if(!command.endsWith(";")) {
            return "[ERROR]: Semi colon missing at end of line.";
        }
        command = command.substring(0, command.length() - 1).trim();
        if(command.isEmpty())  return "[ERROR]: Invalid command";

        if(command.toUpperCase().startsWith("CREATE ")) {
            return handleCreate(command);
        } else if(command.toUpperCase().startsWith("USE")) {
            return handleUse(command);
        } else if(command.toUpperCase().startsWith("INSERT INTO")) {
            return handleInsert(command);
        } else if(command.toUpperCase().startsWith("DROP ")) {
           return handleDrop(command);
        } else if(command.toUpperCase().startsWith("ALTER TABLE")) {
            return handleAlter(command);
        } else if(command.toUpperCase().startsWith("SELECT")) {
            return handleSelect(command);
        } else if(command.toUpperCase().startsWith("DELETE ")) {
            return handleDelete(command);
        } else if(command.toUpperCase().startsWith("UPDATE ")) {
            return handleUpdate(command);
        } else if(command.toUpperCase().startsWith("JOIN ")) {
            return handleJoin(command);
        }
        return "[ERROR] Invalid command.";
    }

    private String handleCreate(String command) {
        try {
            String parsedName = ParsedCreateCommand.parseCreateCommand(command);

            if(command.toUpperCase().startsWith("CREATE DATABASE ")){
                File dbFolder = new File(storageFolderPath, parsedName);
                if(dbFolder.exists())  return "[ERROR]: Database " + parsedName + " already exists.";
                Files.createDirectories(dbFolder.toPath());
                return "[OK]";
            } else if(command.toUpperCase().startsWith("CREATE TABLE ")){
                if(database == null) return "[ERROR]: NO DATABASE.";
                if(!command.contains("(")) {
                    Table table = new Table(parsedName);
                    database.addTable(parsedName, table);
                    database.saveTable(storageFolderPath, parsedName);
                    return "[OK]";
                } else {
                    int openParen = command.indexOf('(');
                    int closeParen = command.indexOf(')');
                    if (openParen == -1 || closeParen == -1 || closeParen <= openParen) {
                        return "[ERROR]: Column syntax error.";
                    }
                    String tableName = parsedName;
                    String attributeList = command.substring(openParen + 1, closeParen).trim();

                    TokenAttributeList attriList = TokenAttributeList.parse(attributeList);

                    List<Column> columns = new ArrayList<>();
                    for (TokenAttributeName a : attriList.getAttributes()) {
                        columns.add(new Column(a.getName()));
                    }
                    Table table = new Table(tableName, columns);
                    database.addTable(tableName, table);
                    database.saveTable(storageFolderPath, tableName);
                    return "[OK]: Table " + parsedName + " created successfully.";
                }
            } else {
                return "[ERROR]: Invalid CREATE command.";
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[ERROR]: " + e.getMessage();
        } catch (IOException e) {
            return "[ERROR]: Failed to create database/table.";
        }
    }

    private String handleUse(String command) {
        try {
            String dbName = ParsedUseCommand.parse(command);
            File dbFolder = new File(storageFolderPath, dbName);
            if(!dbFolder.exists() || !dbFolder.isDirectory()) {
                return "[ERROR]: Database '" + dbName + "' does not exist.";
            }
            database = new DB(dbName);
            database.loadTables(storageFolderPath);
            return "[OK]";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    private String handleInsert(String command) {

        if(database == null) return "[ERROR]: NO DATABASE.";

        try {
            ParsedInsertCommand.InsertCommand insertCommand = ParsedInsertCommand.parse(command);
            String tableName = insertCommand.getTableName();
            Table table = database.getTable(tableName);
            if(table == null) return "[ERROR]: Table: '" + tableName + "' does not exist.";

            List<String> valueList = new ArrayList<>();
            for(String value : insertCommand.getValues()) {
                value = value.trim();
                if(value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }
                valueList.add(value);
            }

            table.insertRow(valueList);
            database.saveTable(storageFolderPath, tableName);
            return "[OK]";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    private String handleDrop(String command) {
        try {
            ParsedDropCommand.DropCommand dc = ParsedDropCommand.parse(command);
            switch (dc.getType()) {
                case DROP_DATABASE :
                    ParsedDropCommand.DropDatabaseCommand dropDb = (ParsedDropCommand.DropDatabaseCommand) dc;
                    String dbName = dropDb.getDatabaseName();
                    File dbFolder = new File(storageFolderPath, dbName);
                    if(!dbFolder.exists()){
                        return "[ERROR]: Database " + dbName + " does not exist";
                    }
                    DB.dropDatabase(storageFolderPath, dbName);
                    if(database != null && database.getDbName().equals(dbName)) {
                        database = null;
                    }
                    return "[OK]";
                case DROP_TABLE:
                    ParsedDropCommand.DropTableCommand dropTable = (ParsedDropCommand.DropTableCommand) dc;
                    String tableName = dropTable.getTableName();
                    if(database == null) {
                        return "[ERROR]: NO DATABASE.";
                    }
                    if(database.getTable(tableName) == null) {
                        return "[ERROR]: Table " + tableName + " does not exist";
                    }
                    database.dropTable(storageFolderPath, tableName);
                    return "[OK]";
                default:
                    return "[ERROR]: Unsupported DROP command.";
            }
        } catch (IllegalArgumentException | IllegalStateException e ){
            return "[ERROR]: " + e.getMessage();
        }

    }

    private String handleAlter(String command) {
        if(database == null) return "[ERROR]: NO DATABASE.";
        try {
            ParsedAlterCommand.AlterCommand alterCommand = ParsedAlterCommand.parse(command);

            String tableName = alterCommand.getTableName();
            String attributeName = alterCommand.getAttributeName();

            Table table = database.getTable(tableName);
            if(table == null) return "[ERROR]: Table: '" + tableName + "' does nor exist.";

            if(alterCommand.getType() == ParsedAlterCommand.AlterCommandType.ADD) {
                table.addColumn(attributeName);
            } else {
                table.dropColumn(attributeName);
            }
            database.saveTable(storageFolderPath, tableName);
            return "[OK]";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    private String handleSelect(String command) {
        if(database == null) return "[ERROR]: NO DATABASE.";

        try{
            ParsedSelectCommand.SelectCommand selectCommand = ParsedSelectCommand.parse(command);
            String tableName = selectCommand.getTableName();
            Table table = database.getTable(tableName);
            if(table == null)   return "[ERROR]: Table: '" + tableName + "' does not exist.";
            List<String> columns;
            TokenWildAttribList wildAttribute = selectCommand.getWildAttribList();
            if(wildAttribute.isWildcard()) {
                columns = table.getColumnNames();
            } else {
                columns = new ArrayList<>();
                for(TokenAttributeName a : wildAttribute.getAttributeList().getAttributes()){
                    columns.add(a.getName().toLowerCase());
                }
            }

            List<Row> resultRows = new ArrayList<>();
            for(Row row: table.getRows()) {
                if(selectCommand.getCondition() == null || evaluateCondition(row, selectCommand.getCondition())) {
                    resultRows.add(row);
                }
            }

            return formatSelectResult(columns, resultRows);
        } catch (IllegalArgumentException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    private boolean evaluateCondition(Row row, TokenCondition condition){
        if(condition instanceof TokenSimpleCondition) {
            TokenSimpleCondition sc = (TokenSimpleCondition) condition;
            String colName = sc.getAttributeName().toLowerCase();
            String value = sc.getValue();
            if(value.length() >= 2 && value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            String rowValue = row.getValue(colName);
            TokenComparator comp = sc.getComparator();
            switch (comp) {
                case EQ :
                    return rowValue.equalsIgnoreCase(value);
                case NE :
                    return !rowValue.equalsIgnoreCase(value);
                case GT :
                    try{
                        double left = Double.parseDouble(rowValue);
                        double right = Double.parseDouble(value);
                        return left > right;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                case LT :
                    try{
                        double left = Double.parseDouble(rowValue);
                        double right = Double.parseDouble(value);
                        return left < right;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                case GE :
                    try{
                        double left = Double.parseDouble(rowValue);
                        double right = Double.parseDouble(value);
                        return left >= right;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                case LE :
                    try{
                        double left = Double.parseDouble(rowValue);
                        double right = Double.parseDouble(value);
                        return left <= right;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                case LIKE :
                    return rowValue.contains(value);
                default :
                    throw new IllegalArgumentException("Invalid comparator: " + comp.getSymbol());
            }
        } else if (condition instanceof TokenCompoundCondition) {
            TokenCompoundCondition cc = (TokenCompoundCondition) condition;
            boolean leftResult = evaluateCondition(row, cc.getLeft());
            boolean rightResult = evaluateCondition(row, cc.getRight());
            if(cc.getOperator() == TokenBoolOperator.AND) {
                return leftResult && rightResult;
            } else if (cc.getOperator() == TokenBoolOperator.OR) {
                return leftResult || rightResult;
            } else {
                throw new IllegalArgumentException("Invalid booloperator.");
            }
        } else {
            throw new IllegalArgumentException("WHERE statement format error.");
        }
    }

    private String formatSelectResult(List<String> columns, List<Row> resultRows) {
        StringBuilder result = new StringBuilder();
        result.append("[OK]\n");

        result.append(String.join("\t", columns)).append("\n");
        //result.append("-".repeat(columns.size() * 10)).append("\n");

        if(!resultRows.isEmpty()) {
            for (Row row : resultRows) {
                List<String> rowValues = new ArrayList<>();
                for (String column : columns) {
                    rowValues.add(row.getValue(column));
                }
                result.append(String.join("\t", rowValues)).append("\n");
            }
        }

        return result.toString();
    }

    private String handleDelete(String command) {
        if(database == null) return "[ERROR]: NO DATABASE.";
        try {
            ParsedDeleteCommand.DeleteCommand deleteCommand = ParsedDeleteCommand.parse(command);
            String tableName = deleteCommand.getTableName();
            Table table = database.getTable(tableName);
            if(table == null)    return "[ERROR]: Table: '" + tableName + "' does not exist.";

            List<Row> deletedRows = new ArrayList<>();
            for(Row row : table.getRows()) {
                if(evaluateCondition(row, deleteCommand.getCondition())) {
                    deletedRows.add(row);
                }
            }

            table.getRows().removeAll(deletedRows);
            database.saveTable(storageFolderPath, tableName);
            return "[OK]";
        } catch (IllegalArgumentException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    private String handleUpdate(String command) {
        if(database == null) return "[ERROR]: NO DATABASE.";
        try {
            ParsedUpdateCommand.UpdateCommand updateCommand = ParsedUpdateCommand.parse(command);
            String tableName = updateCommand.getTableName();
            Table table = database.getTable(tableName);
            if(table == null)    return "[ERROR]: Table: '" + tableName + "' does not exist.";

            List<String> tableColumns = new ArrayList<>();
            for(String col : table.getColumnNames()) {
                tableColumns.add(col.toLowerCase());
            }

            for(TokenNameValuePair pair : updateCommand.getNameValuePairs()) {
                String colName = pair.getAttribute().toLowerCase();
                if(colName.equals("id"))   return "[ERROR]: Cannot update 'id'";
                if(!tableColumns.contains(colName))   return "[ERROR]: Column " + pair.getAttribute() + " does not exist.";
            }

            for(Row row : table.getRows()) {
                if(evaluateCondition(row, updateCommand.getCondition())) {
                    for(TokenNameValuePair pair : updateCommand.getNameValuePairs()) {
                        row.getRowDataMap().put(pair.getAttribute(), pair.getValue());
                    }
                }
            }

            database.saveTable(storageFolderPath, tableName);
            return "[OK]";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }


    private String handleJoin(String command) {
        if(database == null) return "[ERROR]: NO DATABASE.";
        try {
            ParsedJoinCommand.JoinCommand joinCommand = ParsedJoinCommand.parse(command);
            String tableName1 = joinCommand.getTableName1();
            String tableName2 = joinCommand.getTableName2();
            String attribute1 = joinCommand.getAttribute1();
            String attribute2 = joinCommand.getAttribute2();

            Table table1 = database.getTable(tableName1);
            Table table2 = database.getTable(tableName2);
            if(table1 == null)    return "[ERROR]: Table: '" + tableName1 + "' does not exist.";
            if(table2 == null)    return "[ERROR]: Table: '" + tableName1 + "' does not exist.";

            List<Column> joinedColumns = new ArrayList<>();
            for(Column col : table1.getColumns()) {
                if(!col.getColName().equalsIgnoreCase("id") && !col.getColName().equalsIgnoreCase(attribute1)) {
                    joinedColumns.add(new Column(tableName1 + "." + col.getColName()));
                }
            }
            for(Column col : table2.getColumns()) {
                if(!col.getColName().equalsIgnoreCase("id") && !col.getColName().equalsIgnoreCase(attribute2)) {
                    joinedColumns.add(new Column(tableName2 + "." + col.getColName()));
                }
            }

            Table joinedTable = new Table("join_result", joinedColumns);

            for(Row row1 : table1.getRows()) {
                String value1 = row1.getValue(attribute1.toLowerCase());
                for(Row row2 : table2.getRows()) {
                    String value2 = row2.getValue(attribute2.toLowerCase());
                    if(value1.equals(value2)) {
                        List<String> joinedRowValues = new ArrayList<>();
                        for(Column col : table1.getColumns()) {
                            if(!col.getColName().equalsIgnoreCase("id") && !col.getColName().equalsIgnoreCase(attribute1)) {
                                joinedRowValues.add(row1.getValue(col.getColName()));
                            }
                        }
                        for(Column col : table2.getColumns()) {
                            if(!col.getColName().equalsIgnoreCase("id") && !col.getColName().equalsIgnoreCase(attribute2)) {
                                joinedRowValues.add(row2.getValue(col.getColName()));
                            }
                        }
                        joinedTable.insertRow(joinedRowValues);
                    }
                }
            }
            return formatSelectResult(joinedTable.getColumnNames(), joinedTable.getRows());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[ERROR]: " + e.getMessage();
        }
    }



    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
