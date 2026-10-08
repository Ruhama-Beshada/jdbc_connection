package com.itsc;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCDemo {

    static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    static final String DB_URL = "jdbc:mysql://localhost:3306/StudentsDB";
    static String username;
    static String password;

    public static void main(String[] args) {
        try {
            loadCredentials();

            // Task 1: create database and table
            createDatabaseAndTable();

            try (Connection conn = DriverManager.getConnection(DB_URL, username, password)) {
                System.out.println("Established Connection to StudentsDB");

                insertSampleData(conn);                 // Task 2
                retrieveData(conn);                     // Task 3
                updateStudentName(conn, 1, "Jonathan"); // Task 4
                deleteStudent(conn, 2);                 // Task 5
                calculateAverageGrade(conn);            // Task 6
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadCredentials() throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        }
        username = props.getProperty("db.user");
        password = props.getProperty("db.password");
    }

    // Task 1
    private static void createDatabaseAndTable() throws SQLException {
        try (Connection conn = DriverManager.getConnection(SERVER_URL, username, password);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS StudentsDB");
            st.executeUpdate("USE StudentsDB");
            st.executeUpdate("DROP TABLE IF EXISTS students");
            st.executeUpdate("CREATE TABLE students ("
                    + "id INT PRIMARY KEY, "
                    + "firstname VARCHAR(255), "
                    + "lastname VARCHAR(255), "
                    + "grade INT)");
            System.out.println("Task 1: Database and table created.");
        }
    }

    // Task 2
    private static void insertSampleData(Connection conn) throws SQLException {
        String sql = "INSERT INTO students (id, firstname, lastname, grade) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Single row example
            ps.setInt(1, 1);
            ps.setString(2, "John");
            ps.setString(3, "Doe");
            ps.setInt(4, 90);
            ps.executeUpdate();

            // Ten more rows
            String[][] data = {
                    {"Aster", "Nega", "85"},
                    {"Jemal", "Edris", "78"},
                    {"Haile", "Anaol", "92"},
                    {"Teddy", "Habtu", "66"},
                    {"Teklay", "Michael", "88"},
                    {"Johny", "Deep", "74"},
                    {"Memar", "Alebachew", "95"},
                    {"Sara", "Tesfaye", "81"},
                    {"Dawit", "Bekele", "69"},
                    {"Meron", "Kebede", "77"}
            };
            int id = 2;
            for (String[] row : data) {
                ps.setInt(1, id++);
                ps.setString(2, row[0]);
                ps.setString(3, row[1]);
                ps.setInt(4, Integer.parseInt(row[2]));
                ps.executeUpdate();
            }
            System.out.println("Task 2: Data inserted successfully.");
        }
    }

    // Task 3
    private static void retrieveData(Connection conn) throws SQLException {
        System.out.println("Task 3: First five rows:");
        printFirstFive(conn);
    }

    // Task 4
    private static void updateStudentName(Connection conn, int id, String newFirstName) throws SQLException {
        String sql = "UPDATE students SET firstname = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newFirstName);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            System.out.println("Task 4: Rows updated: " + rows);
        }
        System.out.println("First five rows after update:");
        printFirstFive(conn);
    }

    // Task 5
    private static void deleteStudent(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("Task 5: Rows deleted: " + rows);
        }
        System.out.println("First five rows after delete:");
        printFirstFive(conn);
    }

    // Task 6
    private static void calculateAverageGrade(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT AVG(grade) AS average_grade FROM students")) {
            while (rs.next()) {
                System.out.println("Task 6: Average Grade: " + rs.getDouble("average_grade"));
            }
        }
    }

    private static void printFirstFive(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM students LIMIT 5")) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id")
                        + ", Name: " + rs.getString("firstname") + " " + rs.getString("lastname")
                        + ", Grade: " + rs.getInt("grade"));
            }
        }
    }
}