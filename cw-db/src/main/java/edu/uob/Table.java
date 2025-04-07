package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private String tableName;
    private List<Column> columns;
    private List<Row> rows;
    private int id;
    private int nextId;

    public Table(String tableName) {
        this.tableName = tableName.toLowerCase();
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        //this.id = 1;
        this.nextId = 1;
        this.columns.add(new Column("id"));
    }

    public Table(String tableName, List<Column> columns) {
        this.tableName = tableName.toLowerCase();
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        //this.id = 1;
        this.nextId = 1;

        for(Column col : columns) {
            if(col.getColName().equalsIgnoreCase("id")) {
                throw new IllegalArgumentException("Cannot pass in the 'id' column.");
            }
        }
        this.columns.add(0, new Column("id"));
        this.columns.addAll(columns);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnNames() {
        List<String> names = new ArrayList<>();
        for(Column col : columns) {
            names.add(col.getColName());
        }
        return names;
    }

    public void addColumn(String columnName) {
        columnName = columnName.trim();
        for(Column col : columns) {
            if(col.getColName().equalsIgnoreCase(columnName)) {
                throw new IllegalArgumentException("Column '" + columnName + "' has already existed.");
            }
        }
        columns.add(new Column(columnName));

        for(Row row : rows) {
            row.getRowDataMap().put(columnName, "NULL");
        }
    }

    public void dropColumn(String columnName) {
        if(columnName.equalsIgnoreCase("id")) {
            throw new IllegalArgumentException("Cannot delete 'id' column.");
        }

        boolean found = false;
        for(int i = 0; i< columns.size(); i++) {
            if(columns.get(i).getColName().equalsIgnoreCase(columnName)) {
                found = true;
                columns.remove(i);
                break;
            }
        }
        if(!found) {
            throw new IllegalArgumentException("The column does not exist.");
        }

        for(Row row : rows) {
            row.removeValue(columnName);
        }
    }


    public void insertRow(List<String> values) {
        if(values.size() != columns.size() - 1){
            throw new IllegalArgumentException("The number of values in the VALUE LIST does not match the table.");
        }

        List<String> rowValues = new ArrayList<>();
        rowValues.add(String.valueOf(nextId++));
        rowValues.addAll(values);

        rows.add(new Row(columns, rowValues));
    }


    public void loadFromFile(String filePath) throws IOException {
        List<String> lines = HandleFile.readFromFile(filePath);
        if(lines.isEmpty()) {
            throw new IOException("Table file " + filePath + " is unreadable or empty");
        }
        columns.clear();
        columns = new ArrayList<>();

        String[] colNames = lines.get(0).split("\t");
        for(String colName : colNames) {
            columns.add(new Column(colName));
        }
        rows.clear();

        for(int i = 1; i < lines.size(); i++) {
            List<String> rowValues = List.of(lines.get(i).split("\t"));
            rows.add(new Row(columns, rowValues));
        }
        calculateMaxId();
    }

    public void calculateMaxId() {
        int maxId = 0;
        for(Row row : rows) {
            int rowId = Integer.parseInt(row.getValue("id"));
            maxId = Math.max(maxId, rowId);
        }
        nextId = maxId + 1;
    }
}
