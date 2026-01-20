package com.restaurant.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/restaurant_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Sam219592"; // ton mot de passe MySQL

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
