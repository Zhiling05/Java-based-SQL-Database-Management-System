package edu.uob;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MyTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    private String sendCommand(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(2000), () -> {
            return server.handleCommand(command);
        }, "Server took too long to respond (possible infinite loop)");
    }

    private String generateRandomDBName() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<8; i++){
            sb.append((char)('a' + (int)(Math.random() * 26)));
        }
        return sb.toString();
    }

    @Test
    public void testCreateUseAndDropDatabase() {
        String dbName = generateRandomDBName();

        // CREATE DATABASE
        String response = sendCommand("CREATE DATABASE " + dbName + ";");
        assertTrue(response.contains("[OK]"), "CREATE DATABASE should respond with [OK]");

        // USE
        response = sendCommand("USE " + dbName + ";");
        assertTrue(response.contains("[OK]"), "USE should respond with [OK]");

        // DROP DATABASE
        response = sendCommand("DROP DATABASE " + dbName + ";");
        assertTrue(response.contains("[OK]"), "DROP DATABASE should respond with [OK]");
    }

    @Test
    public void testCreateDropTableAndInsertSelect() {
        String dbName = generateRandomDBName();
        sendCommand("CREATE DATABASE " + dbName + ";");
        sendCommand("USE " + dbName + ";");

        // CREATE TABLE
        String response = sendCommand("CREATE TABLE students (name, age, passed);");
        assertTrue(response.contains("[OK]"), "CREATE TABLE should respond with [OK]");

        // INSERT
        response = sendCommand("INSERT INTO students VALUES ('Alice', 20, TRUE);");
        assertTrue(response.contains("[OK]"), "INSERT should respond with [OK]");
        response = sendCommand("INSERT INTO students VALUES ('Bob', 19, FALSE);");
        assertTrue(response.contains("[OK]"), "INSERT should respond with [OK]");

        // SELECT *
        response = sendCommand("SELECT * FROM students;");
        assertTrue(response.contains("[OK]"), "SELECT should respond with [OK]");
        assertTrue(response.contains("Alice"), "Should find 'Alice' in SELECT result");
        assertTrue(response.contains("Bob"),   "Should find 'Bob' in SELECT result");

        // DROP TABLE
        response = sendCommand("DROP TABLE students;");
        assertTrue(response.contains("[OK]"), "DROP TABLE should respond with [OK]");

        // Double-check that table is gone
        response = sendCommand("SELECT * FROM students;");
        assertTrue(response.contains("[ERROR]"), "Table no longer exists, so this should produce [ERROR]");
    }

    @Test
    public void testAlterTableAddAndDropColumn() {
        String dbName = generateRandomDBName();
        sendCommand("CREATE DATABASE " + dbName + ";");
        sendCommand("USE " + dbName + ";");
        sendCommand("CREATE TABLE marks (student, score);");

        // ALTER TABLE ADD
        String response = sendCommand("ALTER TABLE marks ADD grade;");
        assertTrue(response.contains("[OK]"), "ALTER TABLE ADD should respond with [OK]");

        // Check that inserting now requires 3 columns
        response = sendCommand("INSERT INTO marks VALUES ('Sally', 85, 'B');");
        assertTrue(response.contains("[OK]"), "INSERT with newly added column should respond [OK]");

        // SELECT to confirm new column
        response = sendCommand("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "SELECT should respond with [OK]");
        assertTrue(response.contains("Sally"), "Should find 'Sally' in SELECT result");
        assertTrue(response.contains("B"),     "Should find 'B' in SELECT result (new column)");

        // ALTER TABLE DROP
        response = sendCommand("ALTER TABLE marks DROP grade;");
        assertTrue(response.contains("[OK]"), "ALTER TABLE DROP should respond with [OK]");

        // Now we only have 2 columns again: (id, student, score)
        // Let's confirm that inserting 3 values fails with an [ERROR]
        response = sendCommand("INSERT INTO marks VALUES ('Jen', 88, 'A');");
        assertTrue(response.contains("[ERROR]"), "Should produce [ERROR] as 'grade' no longer exists");
    }

    @Test
    public void testUpdateAndDelete() {
        String dbName = generateRandomDBName();
        sendCommand("CREATE DATABASE " + dbName + ";");
        sendCommand("USE " + dbName + ";");
        sendCommand("CREATE TABLE employees (name, salary, active);");
        sendCommand("INSERT INTO employees VALUES ('John', 50000, TRUE);");
        sendCommand("INSERT INTO employees VALUES ('Marry', 55000, TRUE);");
        sendCommand("INSERT INTO employees VALUES ('Sam', 40000, FALSE);");

        // UPDATE: give Sam a raise, set active to TRUE
        String response = sendCommand("UPDATE employees SET salary=45000, active=TRUE WHERE name=='Sam';");
        assertTrue(response.contains("[OK]"), "UPDATE should respond [OK] if done correctly");

        // SELECT and see if Sam is updated
        response = sendCommand("SELECT * FROM employees WHERE name=='Sam';");
        assertTrue(response.contains("[OK]"), "SELECT should respond [OK]");
        assertTrue(response.contains("45000"), "Sam's salary should now be 45000");
        assertTrue(response.contains("TRUE"),  "Sam's 'active' should now be TRUE");

        // DELETE
        response = sendCommand("DELETE FROM employees WHERE name=='Marry';");
        assertTrue(response.contains("[OK]"), "DELETE should respond with [OK]");

        // Confirm Marry is gone
        response = sendCommand("SELECT * FROM employees;");
        assertFalse(response.contains("Marry"), "Marry should have been deleted");
    }

    @Test
    public void testJoin() {
        String dbName = generateRandomDBName();
        sendCommand("CREATE DATABASE " + dbName + ";");
        sendCommand("USE " + dbName + ";");

        // Create table "authors" with an ID we can match on
        sendCommand("CREATE TABLE authors (authorID, authorName, age);");
        sendCommand("INSERT INTO authors VALUES (1, 'Alice', 34);");
        sendCommand("INSERT INTO authors VALUES (2, 'Bob', 29);");
        sendCommand("INSERT INTO authors VALUES (3, 'Charlie', 41);");

        // Create table "books" that references 'authorID' instead of authorName
        sendCommand("CREATE TABLE books (title, authorID);");
        sendCommand("INSERT INTO books VALUES ('Java101', 2);");
        sendCommand("INSERT INTO books VALUES ('DB Fundamentals', 3);");
        sendCommand("INSERT INTO books VALUES ('Poetry', 1);");

        // Now do JOIN on authorID from both tables
        String response = sendCommand("JOIN authors AND books ON authorID AND authorID;");
        assertTrue(response.contains("[OK]"), "JOIN should respond with [OK]");

        // Check that we can see "Alice", "Bob", "Charlie", meaning authors.authorName is retained
        assertTrue(response.contains("Alice"),   "JOIN result should contain 'Alice'");
        assertTrue(response.contains("Bob"),     "JOIN result should contain 'Bob'");
        assertTrue(response.contains("Charlie"), "JOIN result should contain 'Charlie'");

        // Check that we can see "Java101", "DB Fundamentals", "Poetry" from books.title
        assertTrue(response.contains("Java101"),         "JOIN result should contain 'Java101'");
        assertTrue(response.contains("DB Fundamentals"), "JOIN result should contain 'DB Fundamentals'");
        assertTrue(response.contains("Poetry"),          "JOIN result should contain 'Poetry'");
    }

    @Test
    public void testErrorScenarios() {
        String dbName = generateRandomDBName();
        // Use non-existent DB
        String response = sendCommand("USE " + dbName + ";");
        assertTrue(response.contains("[ERROR]"), "Using non-existent DB should produce [ERROR]");

        sendCommand("CREATE DATABASE " + dbName + ";");
        sendCommand("USE " + dbName + ";");
        sendCommand("CREATE TABLE sample (x, y);");

        // Query a non-existent table
        response = sendCommand("SELECT * FROM unknownTable;");
        assertTrue(response.contains("[ERROR]"), "Selecting from unknown table should produce [ERROR]");

        // Insert with too many values
        response = sendCommand("INSERT INTO sample VALUES (10, 20, 30);");
        assertTrue(response.contains("[ERROR]"), "Too many values for columns => [ERROR]");

        // Drop a non-existent table
        response = sendCommand("DROP TABLE notHere;");
        assertTrue(response.contains("[ERROR]"), "Dropping a non-existent table => [ERROR]");
	}
}