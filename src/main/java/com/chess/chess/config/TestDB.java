package com.chess.chess.config;

import java.sql.Connection;

public class TestDB {

    public static void main(String[] args) {

        Connection conn = DatabaseConnection.connect();

        if (conn != null) {
            System.out.println("🔥 TEST OK - działa połączenie!");
        } else {
            System.out.println("❌ TEST FAIL - brak połączenia!");
        }
    }
}



