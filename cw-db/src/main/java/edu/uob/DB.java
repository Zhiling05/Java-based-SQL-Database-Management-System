package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DB {
    private String dbName;
    private Map<String, Table> tables;


    public DB(String dbName) {
        this.dbName = dbName.toLowerCase();
        this.tables = new HashMap<>();
    }

    public void addTable(String tableName, Table table) {
        if(tables.containsKey(tableName.toLowerCase())) {
            throw new IllegalArgumentException("The table has already existed.");
        }
        tables.put(tableName.toLowerCase(), table);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName.toLowerCase());
    }

    public String getDbName() {
        return dbName;
    }

    public void loadTables(String storageFolderPath) {
        tables.clear();
        String dbPath = storageFolderPath + File.separator + dbName;
        File dbFolder = new File(dbPath);

        File[] includedTableFiles = dbFolder.listFiles((dir, name) -> name.endsWith(".tab"));
        if(includedTableFiles != null) {
            for(File tableFile : includedTableFiles) {
                try {
                    Table table = new Table(tableFile.getName().replace(".tab", ""));
                    table.loadFromFile(tableFile.getAbsolutePath());
                    addTable(table.getTableName(), table);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to load table from file: " + tableFile.getName() );
                }

            }
        }
    }

    public void saveTable(String storageFolderPath, String tableName) {
        Table table = tables.get(tableName.toLowerCase());
        if(table != null) {
            String tablePath = storageFolderPath + File.separator + dbName + File.separator + tableName + ".tab";
            try {
                HandleFile.writeToFile(tablePath, table);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to save table " + tableName);
            }

        }
    }

    public static void dropDatabase(String storageFolderPath, String dbName) {
        File dbFolder = new File(storageFolderPath, dbName.toLowerCase());
        if(!dbFolder.exists())  {
            throw new IllegalArgumentException("Database: '" + dbName + "' does not exist.'");
        }

        if(!deleteFolder(dbFolder)) {
            throw new IllegalStateException("Failed to drop database.");
        }
    }

    private static boolean deleteFolder(File folder) {
        //if(!folder.exists()) return false;
        File[] files = folder.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        return folder.delete();
    }

    public void dropTable(String storageFolderPath, String tableName) {
        tableName = tableName.toLowerCase();

        if(!tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table: '" + tableName + "' does not exist.");
        }

        String tablePath = storageFolderPath + File.separator + dbName + File.separator + tableName + ".tab";
        File tableFile = new File(tablePath);

        if(tableFile.exists() && !tableFile.delete()) {
            throw new IllegalStateException("Table: '" + tableName + "' cannot be dropped.");
        }

        tables.remove(tableName);
    }
}
