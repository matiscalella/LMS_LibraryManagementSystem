package main;

import config.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✔ Successful connection to the database!");
            } else {
                System.out.println("✖ Connection object is null or closed.");
            }
        } catch (Exception e) {
            System.out.println("✖ Error while connecting to the database:");
            e.printStackTrace();
        }
    }
}
