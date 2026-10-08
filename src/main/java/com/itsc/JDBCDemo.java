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
}