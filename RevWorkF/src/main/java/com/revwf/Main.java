package com.revwf;

import com.revwf.config.DatabaseConfig;
import com.revwf.view.LoginView;

public class Main {

	public static void main(String[] args) {
        try {
            displayApplicationHeader();
            initializeDatabase();
            startApplication();
        } catch (Exception e) {
            System.err.println("❌ Application failed to start: " + e.getMessage());
            System.err.println("Please check your database configuration and try again.");
            System.exit(1);
        }
    }

    private static void displayApplicationHeader() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("        REVWORKFORCE MANAGEMENT SYSTEM     ");
       
        System.out.println("=".repeat(60));
        System.out.println("Initializing application...");
    }

    private static void initializeDatabase() {
        System.out.println("Checking database connection...");
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            dbConfig.testConnection();
            System.out.println("✅ Database connection successful");
        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void startApplication() {
        System.out.println("✅ Application initialized successfully!");
        try {
            LoginView loginView = new LoginView();
            if (loginView.showLoginScreen()) {
                loginView.showMainMenu();
            } else {
                System.out.println("Login failed ❌. Exiting application.");
            }
        } catch (Exception e) {
            System.err.println("❌ An error occurred: " + e.getMessage());
        } finally {
            System.out.println("\nThank you for using RevWorkForce  System!");
        }
    }
}

