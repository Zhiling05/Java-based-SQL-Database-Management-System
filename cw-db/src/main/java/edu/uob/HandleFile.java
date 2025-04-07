package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HandleFile {
    public static List<String> readFromFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IOException("[ERROR]: Failed to read from file:  " + filePath, e);
        }

        return lines;
    }

    public static void writeToFile(String filePath, Table table) throws IOException {
        File file = new File(filePath);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(String.join("\t", table.getColumnNames()) + "\n");
            for(Row row : table.getRows()) {
                bw.write(String.join("\t", row.getRowValues(table.getColumnNames())) + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Failed to write to file: " + filePath, e);
        }
    }
}
