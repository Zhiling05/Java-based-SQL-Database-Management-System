package edu.uob;

public class Column {
    private String colName;

    public Column(String colName) {
        this.colName = colName;
    }

    public String getColName() {
        return colName;
    }

    @Override
    public String toString() {
        return colName;
    }
}
