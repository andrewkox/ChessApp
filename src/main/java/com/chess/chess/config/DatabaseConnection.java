package com.chess.chess.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/chessbase";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("database is conected");
            return conn;
        } catch (SQLException e) {
            System.out.println("connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}