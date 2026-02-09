package com.revwf.view;

import com.revwf.service.AuthService;
import com.revwf.util.InputValidator;

import java.util.Scanner;

public class LoginView {
    private final Scanner scanner;
    private final AuthService authService;

    public LoginView() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthService();
    }

    /**
     * Display login screen and handle authentication
     */
    public boolean showLoginScreen() {
        displayWelcomeMessage();

        int attempts = 0;
        final int maxAttempts = 3;

        while (attempts < maxAttempts) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("LOGIN TO REVWORKFORCE  SYSTEM");
            System.out.println("=".repeat(50));

            System.out.print("Employee ID: ");
            String employeeId = scanner.nextLine().trim();

            if (employeeId.isEmpty()) {
                System.out.println("❌ Employee ID cannot be empty!");
                continue;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                System.out.println("❌ Password cannot be empty!");
                continue;
            }

            System.out.println("\nAuthenticating...");

            if (authService.login(employeeId, password)) {
                System.out.println("✅ Login successful!");
                System.out.println("Welcome, " + authService.getCurrentUser().getEmployeeId() + "!");
                return true;
            } else {
                attempts++;
                int remainingAttempts = maxAttempts - attempts;

                if (remainingAttempts > 0) {
                    System.out.println("❌ Invalid ! " + remainingAttempts + " attempts remaining.");
                } else {
                    System.out.println("❌ Maximum login attempts exceeded. Access denied.");
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Display main menu after successful login
     */
    public void showMainMenu() {
        while (authService.isLoggedIn()) {
            displayMainMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 5)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-5.");
                continue;
            }

            int option = Integer.parseInt(choice);
            handleMainMenuChoice(option);
        }
    }

    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    WELCOME TO REVWORKFORCE HRM SYSTEM");
        System.out.println("    Human Resource Management Application");
        System.out.println("=".repeat(60));
        System.out.println("Default Login Credentials:");
        System.out.println("Employee ID: ADMIN001");
        System.out.println("Password: admin123");
        System.out.println("=".repeat(60));
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU - " + authService.getSessionInfo());
        System.out.println("=".repeat(50));
        System.out.println("1. Dashboard");
        System.out.println("2. Profile");
        System.out.println("3. Change Password");
        System.out.println("4. Session Info");
        System.out.println("5. Logout");
        System.out.println("=".repeat(50));
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                showDashboard();
                break;
            case 2:
            	showProfile();
                break;
            case 3:
            	showChangePasswordScreen();
                break;
            case 4:
            	showSessionInfo();
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("❌ Invalid choice!");
        }
    }

    private void showDashboard() {
        if (authService.isAdmin()) {
            new AdminDashboard(authService).show();
        } else if (authService.isManager()) {
            new ManagerDashboard(authService).show();
        } else {
            new EmployeeDashboard(authService).show();
        }
    }

    private void showChangePasswordScreen() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("CHANGE PASSWORD");
        System.out.println("=".repeat(40));

        System.out.print("Current Password: ");
        String currentPassword = scanner.nextLine().trim();

        System.out.print("New Password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Passwords do not match!");
            return;
        }

        if (newPassword.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters long!");
            return;
        }

        if (authService.changePassword(currentPassword, newPassword)) {
            System.out.println("✅ Password changed successfully!");
        } else {
            System.out.println("❌ Failed to change password. Please check your current password.");
        }

        waitForEnter();
    }

    private void showProfile() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("USER PROFILE");
        System.out.println("=".repeat(40));
        System.out.println("Employee ID: " + authService.getCurrentUser().getEmployeeId());
        System.out.println("Email: " + authService.getCurrentUser().getEmail());
        System.out.println("Role: " + authService.getCurrentUser().getRole().getDisplayName());
        System.out.println("Status: " + (authService.getCurrentUser().isActive() ? "Active" : "Inactive"));
        System.out.println("=".repeat(40));

        waitForEnter();
    }
    private void showSessionInfo() {
        System.out.println(authService.getSessionInfo());
    }

    private void logout() {
        System.out.println("\nLogging out...");
        authService.logout();
        System.out.println("✅ Logged out successfully!");
       
    }

    private void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public AuthService getAuthService() {
        return authService;
    }
}