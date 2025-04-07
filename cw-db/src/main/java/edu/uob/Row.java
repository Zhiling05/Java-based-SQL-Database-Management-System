package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row {
    private Map<String, String> rowData;

    public Row(List<Column> columns, List<String> values) {
        if(columns.size() != values.size()) {
            throw new IllegalArgumentException("The number of columns does not match the data.");
        }
        this.rowData = new HashMap<>();
        for(int i = 0; i < columns.size(); i++) {
            rowData.put(columns.get(i).getColName(), values.get(i));
        }
    }

    public String getValue(String columnName) {
        for(String key : rowData.keySet()) {
            if(key.equalsIgnoreCase(columnName)) {
                return rowData.get(key);
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' does not exist.");
    }


    public void removeValue(String columnName) {
        rowData.remove(columnName);
    }

    public List<String> getRowValues(List<String> columnNames) {

        List<String> rowValues = new ArrayList<>();
        for(String columnName : columnNames) {
            rowValues.add(rowData.get(columnName));
        }
        return rowValues;
    }

    public Map<String, String> getRowDataMap() {
        return rowData;
    }
}
