package MainApp;


import Model.Employee;
import Service.AuthService;
import Service.EmployeeService;
import Ui.EmployeeMenu;
import ui.ManagerMenu;
import com.revworkforce.util.LoggerUtil;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final EmployeeService employeeService = new EmployeeService();

    public static void main(String[] args) {
        LoggerUtil.info("Rev Workforce Application Started");
        printHeader("Welcome to Rev Workforce HRM System");

        // Step 1: Force Admin login first (fixed credentials)
        if (adminLogin()) {
            printSuccess("Admin login successful! Welcome, Admin.");
            LoggerUtil.info("Admin logged in with fixed credentials");
            adminMenu();  // Inline Admin menu
        }

        // Step 2: After Admin logout, proceed to general login for Employees/Managers
        printHeader("Now Proceed to Employee/Manager Login");
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("1. Login as Employee/Manager");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                generalLogin();
            } else if (choice == 2) {
                printSuccess("Application Exited. Goodbye!");
                LoggerUtil.info("Application Exited");
                break;
            } else {
                printError("Invalid choice. Try again.");
            }
        }
    }

    private static boolean adminLogin() {
        while (true) {
            printHeader("Admin Login Required First (Fixed Credentials)");
            System.out.print("Enter Admin Employee ID: ");
            String empId = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Fixed Admin login: empId "admin", password "admin123"
            if ("admin".equals(empId) && "admin123".equals(password)) {
                return true;
            } else {
                printError("Invalid Admin credentials. Use empId 'admin' and password 'admin123'. Try again.");
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n" + "\033[1;36m" + "=== ADMIN MENU ===" + "\033[0m");
            System.out.println("1. View Total Employees and Managers");
            System.out.println("2. View All Employee Details");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewCounts();
                    break;
                case 2:
                    viewAllDetails();
                    break;
                case 3:
                    printSuccess("Admin logged out.");
                    LoggerUtil.info("Admin logged out");
                    return;  // Exit Admin menu, proceed to general login
                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

    private static void viewCounts() {
        List<Employee> allEmployees = employeeService.getAllEmployees();
        long managerCount = allEmployees.stream().filter(e -> e.getEmpId().startsWith("M")).count();
        long employeeCount = allEmployees.size() - managerCount - (allEmployees.stream().anyMatch(e -> e.getEmpId().startsWith("A")) ? 1 : 0);  // Exclude Admins if any
        System.out.println("\n" + "-".repeat(30));
        System.out.println("Total Managers: " + managerCount);
        System.out.println("Total Employees: " + employeeCount);
        System.out.println("Total Users: " + allEmployees.size());
    }

    private static void viewAllDetails() {
        List<Employee> employees = employeeService.getAllEmployees();
        System.out.println("\n" + "-".repeat(50));
        System.out.println("Employee Details:");
        for (Employee emp : employees) {
            System.out.println("ID: " + emp.getEmpId() + ", Name: " + emp.getName() + ", Email: " + emp.getEmail() + ", Department ID: " + emp.getDeptId() + ", Designation ID: " + emp.getDesigId());
        }
    }

    private static void generalLogin() {
        printHeader("Login to Your Account");
        System.out.print("Enter Employee ID: ");
        String empId = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Employee employee = authService.authenticate(empId, password);
        if (employee != null) {
            printSuccess("Login successful! Welcome, " + employee.getName());
            LoggerUtil.info("User logged in: " + empId);
            // Role determination: Manager if 'M', else Employee
            if (empId.startsWith("M")) {
                new ManagerMenu(employee).showMenu();
            } else {
                new EmployeeMenu(employee).showMenu();
            }
        } else {
            printError("Invalid credentials. Please try again.");
        }
    }

    private static void printHeader(String text) {
        System.out.println("\n" + "\033[1;34m" + "=== " + text.toUpperCase() + " ===\033[0m");
    }

    private static void printSuccess(String text) {
        System.out.println("\033[1;32m" + text + "\033[0m");
    }

    private static void printError(String text) {
        System.out.println("\033[1;31m" + text + "\033[0m");
    }
}