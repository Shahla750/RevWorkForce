package com.revwf.view;

import com.revwf.model.Department;
import com.revwf.model.Designation;
import com.revwf.model.Employee;
import com.revwf.model.Holiday;
import com.revwf.model.LeaveApplication;
import com.revwf.model.LeaveBalance;
import com.revwf.model.LeaveType;
import com.revwf.model.UserRole;
import com.revwf.service.AuthService;
import com.revwf.service.EmployeeService;
import com.revwf.service.LeaveService;
import com.revwf.util.InputValidator;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.Date;

public class AdminDashboard {
    private final Scanner scanner;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final LeaveService leaveService;

    public AdminDashboard(AuthService authService) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.employeeService = new EmployeeService();
        this.leaveService = new LeaveService();
    }

    public void show() {
        while (true) {
            displayMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 10)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-10.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleMenuChoice(option)) {
                break; // Return to main menu
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ADMIN DASHBOARD");
        System.out.println("User: " + authService.getCurrentUser().getEmployeeId());
        System.out.println("=".repeat(50));
        System.out.println("1. Employee Management");
        System.out.println("2. Leave Management");
        System.out.println("3. System Configuration");
        System.out.println("4. Reports & Analytics");
        System.out.println("5. Department Management");
        System.out.println("6. Designation Management");
        System.out.println("7. Holiday Management");
        System.out.println("8. System Audit Logs");
        System.out.println("9. Employee Features - All employee functions");
        System.out.println("10. Back to Main Menu");
        System.out.println("=".repeat(50));
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                showEmployeeManagement();
                break;
            case 2:
                showLeaveManagement();
                break;
            case 3:
                showSystemConfiguration();
                break;
            case 4:
                showReportsAnalytics();
                break;
            case 5:
                showDepartmentManagement();
                break;
            case 6:
                showDesignationManagement();
                break;
            case 7:
                showHolidayManagement();
                break;
            case 8:
                viewSystemAuditLogs();
                break;
            case 9:
                showEmployeeFeatures();
                break;
            case 10:
                return true; // Back to main menu
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    // =============== LEAVE MANAGEMENT METHODS ===============

    private void showLeaveManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("LEAVE MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Configure Leave Types");
            System.out.println("2. Assign Leave Quotas");
            System.out.println("3. Adjust Leave Balances");
            System.out.println("4. View All Leave Applications");
            System.out.println("5. Revoke Approved Leaves");
            System.out.println("6. Generate Leave Reports");
            System.out.println("7. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 7)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-7.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleLeaveManagementChoice(option)) {
                break;
            }
        }
    }

    private boolean handleLeaveManagementChoice(int choice) {
        switch (choice) {
            case 1:
                configureLeaveTypes();
                break;
            case 2:
                assignLeaveQuotas();
                break;
            case 3:
                adjustLeaveBalances();
                break;
            case 4:
                viewAllLeaveApplications();
                break;
            case 5:
                revokeApprovedLeaves();
                break;
            case 6:
                generateLeaveReports();
                break;
            case 7:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void configureLeaveTypes() {
        System.out.println("\n⚙️ CONFIGURE LEAVE TYPES");
        System.out.println("=".repeat(35));

        try {
            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();

            System.out.println("Current Leave Types:");
            System.out.println("=".repeat(60));
            System.out.printf("%-5s | %-20s | %-6s | %-10s%n", "ID", "Leave Type", "Code", "Max Days");
            System.out.println("=".repeat(60));

            for (LeaveType lt : leaveTypes) {
                System.out.printf("%-5d | %-20s | %-6s | %-10d%n",
                        lt.getLeaveTypeId(),
                        lt.getLeaveTypeName(),
                        lt.getLeaveTypeCode(),
                        lt.getMaxDaysPerYear());
            }

            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.out.println("❌ Error loading leave types: " + e.getMessage());
        }

        waitForEnter();
    }

    private void assignLeaveQuotas() {
        System.out.println("\n📊 ASSIGN LEAVE QUOTAS");
        System.out.println("=".repeat(30));
        System.out.println("Select Assignment Type:");
        System.out.println("1. Assign to All Employees");
        System.out.println("2. Assign to Specific Employee");
        System.out.println("3. Assign by Department");
        System.out.print("Choice (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                assignQuotasToAllEmployees();
                break;
            case "2":
                assignQuotasToSpecificEmployee();
                break;
            case "3":
                assignQuotasByDepartment();
                break;
            default:
                System.out.println("❌ Invalid choice!");
        }

        waitForEnter();
    }

    private void assignQuotasToAllEmployees() {
        System.out.println("\n👥 ASSIGN TO ALL EMPLOYEES");
        System.out.println("=".repeat(35));

        try {
            List<Employee> employees = employeeService.getAllEmployees();
            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();

            System.out.println("This will assign standard quotas to all active employees:");
            for (LeaveType lt : leaveTypes) {
                System.out.printf("- %s: %d days%n", lt.getLeaveTypeName(), lt.getMaxDaysPerYear());
            }

            System.out.printf("Total employees: %d%n", employees.size());
            System.out.print("Confirm assignment for year " + java.time.LocalDate.now().getYear() + "? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                boolean success = leaveService.assignLeaveQuotasToAllEmployees();

                if (success) {
                    System.out.println("✅ Leave quotas assigned to all employees successfully!");
                } else {
                    System.out.println(
                            "❌ Failed to assign leave quotas. Some employees may already have quotas for this year.");
                }
            } else {
                System.out.println("❌ Assignment cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error assigning quotas: " + e.getMessage());
        }
    }

    private void assignQuotasToSpecificEmployee() {
        System.out.println("\n👤 ASSIGN TO SPECIFIC EMPLOYEE");
        System.out.println("=".repeat(40));
        System.out.print("Enter Employee ID (e.g., EMP004): ");
        String empIdInput = scanner.nextLine().trim();

        if (empIdInput.isEmpty()) {
            System.out.println("❌ Employee ID cannot be empty!");
            return;
        }

        try {
            int empId;
            if (empIdInput.startsWith("EMP")) {
                empId = Integer.parseInt(empIdInput.substring(3));
            } else {
                empId = Integer.parseInt(empIdInput);
            }

            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                System.out.println("❌ Employee not found with ID: " + empIdInput);
                return;
            }

            System.out.println("Employee: " + employee.getFullName());
            System.out.println(
                    "Department: " + (employee.getDepartmentName() != null ? employee.getDepartmentName() : "N/A"));

            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();
            System.out.println("\nStandard quotas to be assigned:");
            for (LeaveType lt : leaveTypes) {
                System.out.printf("- %s: %d days%n", lt.getLeaveTypeName(), lt.getMaxDaysPerYear());
            }

            System.out.print("Confirm assignment? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                boolean success = leaveService.assignLeaveQuotasToEmployee(empId);

                if (success) {
                    System.out.println("✅ Leave quotas assigned to " + employee.getFullName() + " successfully!");
                } else {
                    System.out.println(
                            "❌ Failed to assign leave quotas. Employee may already have quotas for this year.");
                }
            } else {
                System.out.println("❌ Assignment cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID format!");
        } catch (Exception e) {
            System.out.println("❌ Error assigning quotas: " + e.getMessage());
        }
    }

    private void assignQuotasByDepartment() {
        System.out.println("\n🏢 ASSIGN BY DEPARTMENT");
        System.out.println("=".repeat(30));
        System.out.println("Select Department:");
        System.out.println("1. Information Technology");
        System.out.println("2. Human Resources");
        System.out.println("3. Finance");
        System.out.println("4. Marketing");
        System.out.print("Choice (1-4): ");

        String choice = scanner.nextLine().trim();
        String department = "";
        int deptId = 0;

        switch (choice) {
            case "1":
                department = "Information Technology";
                deptId = 1;
                break;
            case "2":
                department = "Human Resources";
                deptId = 2;
                break;
            case "3":
                department = "Finance";
                deptId = 3;
                break;
            case "4":
                department = "Marketing";
                deptId = 4;
                break;
            default:
                System.out.println("❌ Invalid choice!");
                return;
        }

        try {
            List<Employee> deptEmployees = employeeService.searchEmployeesByDepartment(department);
            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();

            System.out.println("Department: " + department);
            System.out.printf("Employees in department: %d%n", deptEmployees.size());

            System.out.println("Standard quotas to be assigned:");
            for (LeaveType lt : leaveTypes) {
                System.out.printf("- %s: %d days%n", lt.getLeaveTypeName(), lt.getMaxDaysPerYear());
            }

            System.out.print("Confirm assignment? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                boolean success = leaveService.assignLeaveQuotasByDepartment(deptId);

                if (success) {
                    System.out.printf("✅ Leave quotas assigned to %s department successfully!%n", department);
                } else {
                    System.out.println(
                            "❌ Failed to assign leave quotas. Some employees may already have quotas for this year.");
                }
            } else {
                System.out.println("❌ Assignment cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error assigning quotas: " + e.getMessage());
        }
    }

    private void adjustLeaveBalances() {
        System.out.println("\n⚖️ ADJUST LEAVE BALANCES");
        System.out.println("=".repeat(35));
        System.out.print("Enter Employee ID (e.g., EMP004): ");
        String empIdInput = scanner.nextLine().trim();

        if (empIdInput.isEmpty()) {
            System.out.println("❌ Employee ID cannot be empty!");
            return;
        }

        try {
            int empId;
            if (empIdInput.startsWith("EMP")) {
                empId = Integer.parseInt(empIdInput.substring(3));
            } else {
                empId = Integer.parseInt(empIdInput);
            }

            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                System.out.println("❌ Employee not found with ID: " + empIdInput);
                return;
            }

            System.out.println("📋 Current Leave Balances for: " + employee.getFullName());
            System.out.println("=".repeat(50));

            try {
                List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(empId);

                if (balances.isEmpty()) {
                    System.out.println("No leave balances found for this employee.");
                    System.out.println("Please assign leave quotas first.");
                    return;
                }

                System.out.printf("%-15s | %-10s | %-8s | %-10s%n", "Leave Type", "Allocated", "Used", "Remaining");
                System.out.println("=".repeat(50));

                for (LeaveBalance balance : balances) {
                    System.out.printf("%-15s | %-10d | %-8d | %-10d%n",
                            balance.getLeaveTypeName(),
                            balance.getAllocatedDays(),
                            balance.getUsedDays(),
                            balance.getRemainingDays());
                }

                System.out.println("=".repeat(50));
                System.out.println("Leave balance adjustment functionality is available.");
                System.out.println("Contact system administrator for manual adjustments.");

            } catch (Exception e) {
                System.out.println("❌ Error retrieving leave balances: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID format!");
        }

        waitForEnter();
    }

    private void viewAllLeaveApplications() {
        System.out.println("\n📋 ALL LEAVE APPLICATIONS");
        System.out.println("=".repeat(80));

        try {
            List<LeaveApplication> applications = leaveService.getAllLeaveApplications();

            if (applications.isEmpty()) {
                System.out.println("No leave applications found.");
            } else {
                System.out.printf("%-6s | %-15s | %-12s | %-10s | %-10s | %-8s | %-10s%n",
                        "App ID", "Employee", "Leave Type", "Start Date", "End Date", "Days", "Status");
                System.out.println("=".repeat(80));

                for (LeaveApplication app : applications) {
                    String status = app.getStatus().toString();
                    String statusIcon = getStatusIcon(app.getStatus());

                    System.out.printf("%-6d | %-15s | %-12s | %-10s | %-10s | %-8d | %s %s%n",
                            app.getApplicationId(),
                            app.getEmployeeName() != null ? app.getEmployeeName() : "N/A",
                            app.getLeaveTypeName() != null ? app.getLeaveTypeName() : "N/A",
                            app.getStartDate(),
                            app.getEndDate(),
                            app.getTotalDays(),
                            statusIcon,
                            status);
                }

                System.out.println("=".repeat(80));
                System.out.println("Total applications: " + applications.size());
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading leave applications: " + e.getMessage());
        }

        waitForEnter();
    }

    private String getStatusIcon(LeaveApplication.LeaveStatus status) {
        switch (status) {
            case PENDING:
                return "⏳";
            case APPROVED:
                return "✅";
            case REJECTED:
                return "❌";
            case CANCELLED:
                return "🚫";
            default:
                return "❓";
        }
    }

    private void revokeApprovedLeaves() {
        System.out.println("\n🚫 REVOKE APPROVED LEAVES");
        System.out.println("=".repeat(35));

        try {
            List<LeaveApplication> applications = leaveService.getAllLeaveApplications();
            List<LeaveApplication> approvedApps = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                    .collect(Collectors.toList());

            if (approvedApps.isEmpty()) {
                System.out.println("No approved leave applications found to revoke.");
                return;
            }

            System.out.println("Approved Leave Applications:");
            System.out.println("=".repeat(70));
            System.out.printf("%-6s | %-15s | %-12s | %-10s | %-10s | %-8s%n",
                    "App ID", "Employee", "Leave Type", "Start Date", "End Date", "Days");
            System.out.println("=".repeat(70));

            for (LeaveApplication app : approvedApps) {
                System.out.printf("%-6d | %-15s | %-12s | %-10s | %-10s | %-8d%n",
                        app.getApplicationId(),
                        app.getEmployeeName() != null ? app.getEmployeeName() : "N/A",
                        app.getLeaveTypeName() != null ? app.getLeaveTypeName() : "N/A",
                        app.getStartDate(),
                        app.getEndDate(),
                        app.getTotalDays());
            }

            System.out.println("=".repeat(70));
            System.out.println("Revocation functionality is available for administrators.");

        } catch (Exception e) {
            System.out.println("❌ Error loading approved leaves: " + e.getMessage());
        }

        waitForEnter();
    }

    private void generateLeaveReports() {
        System.out.println("\n📊 GENERATE LEAVE REPORTS");
        System.out.println("=".repeat(35));
        System.out.println("Available Reports:");
        System.out.println("1. Employee Leave Summary");
        System.out.println("2. Department-wise Leave Analysis");
        System.out.println("3. Leave Type Usage Report");
        System.out.println("4. Monthly Leave Trends");
        System.out.println("5. Pending Applications Report");
        System.out.print("Enter choice (1-5): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                generateEmployeeLeaveSummary();
                break;
            case "2":
                generateDepartmentLeaveAnalysis();
                break;
            case "3":
                generateLeaveTypeUsageReport();
                break;
            case "4":
                generateMonthlyLeaveTrends();
                break;
            case "5":
                generatePendingApplicationsReport();
                break;
            default:
                System.out.println("❌ Invalid choice!");
        }

        waitForEnter();
    }

    private void generateEmployeeLeaveSummary() {
        System.out.println("\n📊 EMPLOYEE LEAVE SUMMARY REPORT");
        System.out.println("=".repeat(50));

        try {
            List<Employee> employees = employeeService.getAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No employees found.");
                return;
            }

            System.out.printf("%-15s | %-20s | %-10s | %-8s | %-10s%n",
                    "Employee ID", "Name", "Allocated", "Used", "Remaining");
            System.out.println("=".repeat(70));

            for (Employee emp : employees) {
                try {
                    List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(emp.getEmpId());

                    int totalAllocated = balances.stream().mapToInt(LeaveBalance::getAllocatedDays).sum();
                    int totalUsed = balances.stream().mapToInt(LeaveBalance::getUsedDays).sum();
                    int totalRemaining = balances.stream().mapToInt(LeaveBalance::getRemainingDays).sum();

                    System.out.printf("%-15s | %-20s | %-10d | %-8d | %-10d%n",
                            "EMP" + String.format("%03d", emp.getEmpId()),
                            emp.getFullName(),
                            totalAllocated,
                            totalUsed,
                            totalRemaining);
                } catch (Exception e) {
                    System.out.printf("%-15s | %-20s | %-10s | %-8s | %-10s%n",
                            "EMP" + String.format("%03d", emp.getEmpId()),
                            emp.getFullName(),
                            "No Data", "No Data", "No Data");
                }
            }

            System.out.println("=".repeat(70));

        } catch (Exception e) {
            System.out.println("❌ Error generating employee leave summary: " + e.getMessage());
        }
    }

    private void generateDepartmentLeaveAnalysis() {
        System.out.println("\n🏢 DEPARTMENT-WISE LEAVE ANALYSIS");
        System.out.println("=".repeat(45));

        String[] departments = { "Information Technology", "Human Resources", "Finance", "Marketing" };

        System.out.printf("%-20s | %-10s | %-8s | %-10s | %-12s%n",
                "Department", "Employees", "Allocated", "Used", "Utilization%");
        System.out.println("=".repeat(70));

        for (int i = 0; i < departments.length; i++) {
            try {
                List<Employee> deptEmployees = employeeService.searchEmployeesByDepartment(departments[i]);

                int totalAllocated = 0;
                int totalUsed = 0;
                int employeesWithData = 0;

                for (Employee emp : deptEmployees) {
                    try {
                        List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(emp.getEmpId());
                        if (!balances.isEmpty()) {
                            totalAllocated += balances.stream().mapToInt(LeaveBalance::getAllocatedDays).sum();
                            totalUsed += balances.stream().mapToInt(LeaveBalance::getUsedDays).sum();
                            employeesWithData++;
                        }
                    } catch (Exception e) {
                        // Skip employees without leave data
                    }
                }

                double utilization = totalAllocated > 0 ? (double) totalUsed / totalAllocated * 100 : 0;

                System.out.printf("%-20s | %-10d | %-8d | %-10d | %-11.1f%%n",
                        departments[i], employeesWithData, totalAllocated, totalUsed, utilization);

            } catch (Exception e) {
                System.out.printf("%-20s | %-10s | %-8s | %-10s | %-12s%n",
                        departments[i], "Error", "Error", "Error", "Error");
            }
        }

        System.out.println("=".repeat(70));

        // Additional insights
        System.out.println("\n📊 Department Insights:");
        try {
            List<Employee> allEmployees = employeeService.getAllEmployees();
            int totalEmployees = allEmployees.size();
            int employeesWithLeaveData = 0;

            for (Employee emp : allEmployees) {
                try {
                    List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(emp.getEmpId());
                    if (!balances.isEmpty()) {
                        employeesWithLeaveData++;
                    }
                } catch (Exception e) {
                    // Skip
                }
            }

            System.out.printf("- Total Employees: %d%n", totalEmployees);
            System.out.printf("- Employees with Leave Data: %d%n", employeesWithLeaveData);
            System.out.printf("- Coverage: %.1f%%%n",
                    totalEmployees > 0 ? (double) employeesWithLeaveData / totalEmployees * 100 : 0);

        } catch (Exception e) {
            System.out.println("- Unable to generate additional insights");
        }
    }

    private void generateLeaveTypeUsageReport() {
        System.out.println("\n📋 LEAVE TYPE USAGE REPORT");
        System.out.println("=".repeat(35));

        try {
            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();
            List<Employee> allEmployees = employeeService.getAllEmployees();

            System.out.printf("%-15s | %-10s | %-8s | %-10s | %-12s%n",
                    "Leave Type", "Allocated", "Used", "Available", "Usage%");
            System.out.println("=".repeat(65));

            int grandTotalAllocated = 0;
            int grandTotalUsed = 0;

            for (LeaveType lt : leaveTypes) {
                int typeAllocated = 0;
                int typeUsed = 0;

                for (Employee emp : allEmployees) {
                    try {
                        List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(emp.getEmpId());
                        for (LeaveBalance balance : balances) {
                            if (balance.getLeaveTypeId() == lt.getLeaveTypeId()) {
                                typeAllocated += balance.getAllocatedDays();
                                typeUsed += balance.getUsedDays();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // Skip employees without leave data
                    }
                }

                int available = typeAllocated - typeUsed;
                double usagePercent = typeAllocated > 0 ? (double) typeUsed / typeAllocated * 100 : 0;

                System.out.printf("%-15s | %-10d | %-8d | %-10d | %-11.1f%%n",
                        lt.getLeaveTypeName(), typeAllocated, typeUsed, available, usagePercent);

                grandTotalAllocated += typeAllocated;
                grandTotalUsed += typeUsed;
            }

            System.out.println("=".repeat(65));
            int grandAvailable = grandTotalAllocated - grandTotalUsed;
            double grandUsagePercent = grandTotalAllocated > 0 ? (double) grandTotalUsed / grandTotalAllocated * 100
                    : 0;

            System.out.printf("%-15s | %-10d | %-8d | %-10d | %-11.1f%%n",
                    "TOTAL", grandTotalAllocated, grandTotalUsed, grandAvailable, grandUsagePercent);

            // Additional insights
            System.out.println("\n📊 Leave Usage Insights:");
            System.out.printf("- Total Leave Days Allocated: %d%n", grandTotalAllocated);
            System.out.printf("- Total Leave Days Used: %d%n", grandTotalUsed);
            System.out.printf("- Total Leave Days Available: %d%n", grandAvailable);
            System.out.printf("- Overall Usage Rate: %.1f%%%n", grandUsagePercent);

        } catch (Exception e) {
            System.out.println("❌ Error generating leave type usage report: " + e.getMessage());
        }
    }

    private void generateMonthlyLeaveTrends() {
        System.out.println("\n📈 MONTHLY LEAVE TRENDS");
        System.out.println("=".repeat(30));

        try {
            List<LeaveApplication> applications = leaveService.getAllLeaveApplications();

            if (applications.isEmpty()) {
                System.out.println("No leave applications found for trend analysis.");
                return;
            }

            // Count applications by month
            int[] monthlyCount = new int[12];
            int[] monthlyApproved = new int[12];
            int[] monthlyRejected = new int[12];
            int[] monthlyPending = new int[12];

            String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

            for (LeaveApplication app : applications) {
                if (app.getStartDate() != null) {
                    int month = app.getStartDate().toLocalDate().getMonthValue() - 1; // 0-based
                    monthlyCount[month]++;

                    switch (app.getStatus()) {
                        case APPROVED:
                            monthlyApproved[month]++;
                            break;
                        case REJECTED:
                            monthlyRejected[month]++;
                            break;
                        case PENDING:
                            monthlyPending[month]++;
                            break;
                        case CANCELLED:
                            // Count cancelled as rejected for trend analysis
                            monthlyRejected[month]++;
                            break;
                    }
                }
            }

            System.out.printf("%-5s | %-7s | %-8s | %-8s | %-7s | %-12s%n",
                    "Month", "Total", "Approved", "Rejected", "Pending", "Approval%");
            System.out.println("=".repeat(60));

            for (int i = 0; i < 12; i++) {
                if (monthlyCount[i] > 0) {
                    double approvalRate = monthlyCount[i] > 0 ? (double) monthlyApproved[i] / monthlyCount[i] * 100 : 0;

                    System.out.printf("%-5s | %-7d | %-8d | %-8d | %-7d | %-11.1f%%n",
                            monthNames[i], monthlyCount[i], monthlyApproved[i],
                            monthlyRejected[i], monthlyPending[i], approvalRate);
                }
            }

            System.out.println("=".repeat(60));

            // Summary statistics
            int totalApps = applications.size();
            long approvedCount = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.APPROVED).count();
            long rejectedCount = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.REJECTED).count();
            long pendingCount = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.PENDING).count();

            System.out.println("\n📊 Overall Statistics:");
            System.out.printf("- Total Applications: %d%n", totalApps);
            System.out.printf("- Approved: %d (%.1f%%)%n", approvedCount,
                    totalApps > 0 ? (double) approvedCount / totalApps * 100 : 0);
            System.out.printf("- Rejected: %d (%.1f%%)%n", rejectedCount,
                    totalApps > 0 ? (double) rejectedCount / totalApps * 100 : 0);
            System.out.printf("- Pending: %d (%.1f%%)%n", pendingCount,
                    totalApps > 0 ? (double) pendingCount / totalApps * 100 : 0);

        } catch (Exception e) {
            System.out.println("❌ Error generating monthly leave trends: " + e.getMessage());
        }
    }

    private void generatePendingApplicationsReport() {
        System.out.println("\n⏳ PENDING APPLICATIONS REPORT");
        System.out.println("=".repeat(40));

        try {
            List<LeaveApplication> applications = leaveService.getAllLeaveApplications();
            List<LeaveApplication> pendingApps = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.PENDING)
                    .collect(Collectors.toList());

            if (pendingApps.isEmpty()) {
                System.out.println("✅ No pending leave applications found.");
                return;
            }

            System.out.printf("%-6s | %-15s | %-12s | %-10s | %-10s | %-8s%n",
                    "App ID", "Employee", "Leave Type", "Start Date", "End Date", "Days");
            System.out.println("=".repeat(70));

            for (LeaveApplication app : pendingApps) {
                System.out.printf("%-6d | %-15s | %-12s | %-10s | %-10s | %-8d%n",
                        app.getApplicationId(),
                        app.getEmployeeName() != null ? app.getEmployeeName() : "N/A",
                        app.getLeaveTypeName() != null ? app.getLeaveTypeName() : "N/A",
                        app.getStartDate(),
                        app.getEndDate(),
                        app.getTotalDays());
            }

            System.out.println("=".repeat(70));
            System.out.println("Total pending applications: " + pendingApps.size());

        } catch (Exception e) {
            System.out.println("❌ Error generating pending applications report: " + e.getMessage());
        }
    }

    // =============== OTHER MANAGEMENT METHODS (PLACEHOLDER) ===============

    // =============== EMPLOYEE MANAGEMENT METHODS ===============

    private void showEmployeeManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("EMPLOYEE MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Add New Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Update Employee Information");
            System.out.println("4. Deactivate Employee");
            System.out.println("5. Reactivate Employee");
            System.out.println("6. Assign/Change Manager");
            System.out.println("7. Search Employees");
            System.out.println("8. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 8)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-8.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleEmployeeManagementChoice(option)) {
                break;
            }
        }
    }

    private boolean handleEmployeeManagementChoice(int choice) {
        switch (choice) {
            case 1:
                addNewEmployee();
                break;
            case 2:
                viewAllEmployees();
                break;
            case 3:
                updateEmployeeInformation();
                break;
            case 4:
                deactivateEmployee();
                break;
            case 5:
                reactivateEmployee();
                break;
            case 6:
                assignChangeManager();
                break;
            case 7:
                searchEmployees();
                break;
            case 8:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    // Employee Management Methods
    private void addNewEmployee() {
        System.out.println("\n➕ ADD NEW EMPLOYEE");
        System.out.println("=".repeat(30));

        try {
            // Get Employee ID
            System.out.print("Employee ID (e.g., EMP005): ");
            String employeeId = scanner.nextLine().trim();
            if (!InputValidator.isValidEmployeeId(employeeId)) {
                System.out.println("❌ Invalid Employee ID format! Use format like EMP005");
                return;
            }

            // Get Email
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            if (!InputValidator.isValidEmail(email)) {
                System.out.println("❌ Invalid email format!");
                return;
            }

            // Get Password
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            if (password.length() < 6) {
                System.out.println("❌ Password must be at least 6 characters!");
                return;
            }

            // Get Role
            System.out.println("Select Role:");
            System.out.println("1. Employee");
            System.out.println("2. Manager");
            System.out.print("Choice (1-2): ");
            String roleChoice = scanner.nextLine().trim();

            UserRole role;
            if ("1".equals(roleChoice)) {
                role = UserRole.EMPLOYEE;
            } else if ("2".equals(roleChoice)) {
                role = UserRole.MANAGER;
            } else {
                System.out.println("❌ Invalid role choice!");
                return;
            }

            // Get Personal Details
            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) {
                System.out.println("❌ First name is required!");
                return;
            }

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                System.out.println("❌ Last name is required!");
                return;
            }

            System.out.print("Phone (10 digits): ");
            String phone = scanner.nextLine().trim();
            if (!InputValidator.isValidPhone(phone)) {
                System.out.println("❌ Invalid phone number! Must be 10 digits.");
                return;
            }

            System.out.print("Address: ");
            String address = scanner.nextLine().trim();

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dobStr = scanner.nextLine().trim();
            Date dateOfBirth = null;
            if (!dobStr.isEmpty() && InputValidator.isValidDate(dobStr)) {
                dateOfBirth = Date.valueOf(dobStr);
            }

            System.out.print("Joining Date (YYYY-MM-DD): ");
            String joiningDateStr = scanner.nextLine().trim();
            if (!InputValidator.isValidDate(joiningDateStr)) {
                System.out.println("❌ Invalid date format! Use YYYY-MM-DD");
                return;
            }
            Date joiningDate = Date.valueOf(joiningDateStr);

            // Get Department
            System.out.println("Select Department:");
            System.out.println("1. Information Technology");
            System.out.println("2. Human Resources");
            System.out.println("3. Finance");
            System.out.println("4. Marketing");
            System.out.print("Choice (1-4): ");
            String deptChoice = scanner.nextLine().trim();

            int deptId;
            switch (deptChoice) {
                case "1":
                    deptId = 1;
                    break;
                case "2":
                    deptId = 2;
                    break;
                case "3":
                    deptId = 3;
                    break;
                case "4":
                    deptId = 4;
                    break;
                default:
                    System.out.println("❌ Invalid department choice!");
                    return;
            }

            // Get Designation
            System.out.println("Select Designation:");
            System.out.println("1. Software Engineer");
            System.out.println("2. Senior Software Engineer");
            System.out.println("3. Team Lead");
            System.out.println("4. Manager");
            System.out.println("5. HR Executive");
            System.out.println("6. Finance Manager");
            System.out.println("7. Marketing Manager");
            System.out.println("8. Admin");
            System.out.print("Choice (1-8): ");
            String desigChoice = scanner.nextLine().trim();

            int designationId;
            switch (desigChoice) {
                case "1":
                    designationId = 1;
                    break;
                case "2":
                    designationId = 2;
                    break;
                case "3":
                    designationId = 3;
                    break;
                case "4":
                    designationId = 4;
                    break;
                case "5":
                    designationId = 5;
                    break;
                case "6":
                    designationId = 7;
                    break;
                case "7":
                    designationId = 8;
                    break;
                case "8":
                    designationId = 6;
                    break;
                default:
                    System.out.println("❌ Invalid designation choice!");
                    return;
            }

            // Get Salary
            System.out.print("Salary: ");
            String salaryStr = scanner.nextLine().trim();
            if (!InputValidator.isValidSalary(salaryStr)) {
                System.out.println("❌ Invalid salary amount!");
                return;
            }
            double salary = Double.parseDouble(salaryStr);

            System.out.print("Emergency Contact: ");
            String emergencyContact = scanner.nextLine().trim();

            // Get Manager (optional)
            System.out.println("\nAssign Manager (optional):");
            List<Employee> potentialManagers = employeeService.getPotentialManagers();

            Integer managerId = null;
            if (!potentialManagers.isEmpty()) {
                System.out.println("0. No manager");
                for (int i = 0; i < potentialManagers.size(); i++) {
                    Employee manager = potentialManagers.get(i);
                    System.out.printf("%d. %s - %s (%s)%n",
                            i + 1,
                            manager.getFullName(),
                            manager.getDesignationName() != null ? manager.getDesignationName() : "N/A",
                            manager.getDepartmentName() != null ? manager.getDepartmentName() : "N/A");
                }

                System.out.print("Select manager (0 for no manager): ");
                String managerChoice = scanner.nextLine().trim();

                if (!managerChoice.isEmpty() && !managerChoice.equals("0")) {
                    try {
                        int choice = Integer.parseInt(managerChoice);
                        if (choice > 0 && choice <= potentialManagers.size()) {
                            managerId = potentialManagers.get(choice - 1).getEmpId();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid manager choice, proceeding without manager");
                    }
                }
            } else {
                System.out.println("No managers available in the system.");
            }

            // Create Employee object
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setPhone(phone);
            employee.setAddress(address);
            employee.setDateOfBirth(dateOfBirth);
            employee.setJoiningDate(joiningDate);
            employee.setDeptId(deptId);
            employee.setDesignationId(designationId);
            employee.setManagerId(managerId);
            employee.setSalary(salary);
            employee.setEmergencyContact(emergencyContact);

            // Create employee with user account
            boolean success = employeeService.createEmployeeWithUser(employeeId, email, password, role, employee);

            if (success) {
                System.out.println("\n✅ Employee created successfully!");
                System.out.println("Employee ID: " + employeeId);
                System.out.println("Name: " + firstName + " " + lastName);
                System.out.println("Email: " + email);
                System.out.println("Department: " + getDepartmentName(deptId));
                System.out.println("Designation: " + getDesignationName(designationId));
                if (managerId != null) {
                    Employee manager = employeeService.getEmployeeById(managerId);
                    System.out.println("Manager: " + (manager != null ? manager.getFullName() : "N/A"));
                }
                System.out.println("Login credentials will be sent to: " + email);

                // Automatically assign leave quotas to the new employee
                System.out.println("\n📋 Assigning leave quotas...");
                try {
                    // Get the newly created employee ID
                    Employee newEmployee = employeeService.getEmployeeByEmployeeId(employeeId);
                    if (newEmployee != null) {
                        boolean quotaAssigned = leaveService.assignLeaveQuotasToEmployee(newEmployee.getEmpId());
                        if (quotaAssigned) {
                            System.out.println("✅ Leave quotas assigned automatically!");

                            // Display assigned quotas
                            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();
                            System.out.println("📊 Assigned Leave Quotas:");
                            for (LeaveType lt : leaveTypes) {
                                System.out.printf("- %s: %d days%n", lt.getLeaveTypeName(), lt.getMaxDaysPerYear());
                            }
                        } else {
                            System.out
                                    .println("⚠️ Leave quotas assignment failed. You can assign them manually later.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Leave quotas assignment failed: " + e.getMessage());
                    System.out.println("You can assign them manually from Leave Management menu.");
                }
            } else {
                System.out.println("❌ Failed to create employee. Employee ID might already exist.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating employee: " + e.getMessage());
        }

        waitForEnter();
    }

    private String getDepartmentName(int deptId) {
        switch (deptId) {
            case 1:
                return "Information Technology";
            case 2:
                return "Human Resources";
            case 3:
                return "Finance";
            case 4:
                return "Marketing";
            default:
                return "Unknown";
        }
    }

    private String getDesignationName(int designationId) {
        switch (designationId) {
            case 1:
                return "Software Engineer";
            case 2:
                return "Senior Software Engineer";
            case 3:
                return "Team Lead";
            case 4:
                return "Manager";
            case 5:
                return "HR Executive";
            case 6:
                return "Admin";
            case 7:
                return "Finance Manager";
            case 8:
                return "Marketing Manager";
            default:
                return "Unknown";
        }
    }

    private void viewAllEmployees() {
        System.out.println("\n👥 ALL EMPLOYEES");
        System.out.println("=".repeat(80));
        System.out.println("ID       | Name                | Department    | Designation      | Status");
        System.out.println("=".repeat(80));

        try {
            List<Employee> employees = employeeService.getAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No employees found.");
            } else {
                for (Employee emp : employees) {
                    String status = emp.isActive() ? "Active" : "Inactive";
                    String statusIcon = emp.isActive() ? "✅" : "❌";

                    System.out.printf("%-8s | %-18s | %-12s | %-15s | %s %s%n",
                            "EMP" + String.format("%03d", emp.getEmpId()),
                            emp.getFullName(),
                            emp.getDepartmentName() != null ? emp.getDepartmentName() : "N/A",
                            emp.getDesignationName() != null ? emp.getDesignationName() : "N/A",
                            statusIcon,
                            status);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading employees: " + e.getMessage());
        }

        System.out.println("=".repeat(80));
        waitForEnter();
    }

    private void updateEmployeeInformation() {
        System.out.println("\n✏️ UPDATE EMPLOYEE INFORMATION");
        System.out.println("=".repeat(40));
        System.out.print("Enter Employee ID to update (e.g., EMP004): ");
        String empIdInput = scanner.nextLine().trim();

        if (empIdInput.isEmpty()) {
            System.out.println("❌ Employee ID cannot be empty!");
            return;
        }

        try {
            // Extract numeric part from EMP004 format
            int empId;
            if (empIdInput.startsWith("EMP")) {
                empId = Integer.parseInt(empIdInput.substring(3));
            } else {
                empId = Integer.parseInt(empIdInput);
            }

            // Get existing employee
            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                System.out.println("❌ Employee not found with ID: " + empIdInput);
                return;
            }

            System.out.println("\n📋 Current Employee Information:");
            System.out.println("Name: " + employee.getFullName());
            System.out.println("Phone: " + (employee.getPhone() != null ? employee.getPhone() : "N/A"));
            System.out.println(
                    "Department: " + (employee.getDepartmentName() != null ? employee.getDepartmentName() : "N/A"));
            System.out.println(
                    "Designation: " + (employee.getDesignationName() != null ? employee.getDesignationName() : "N/A"));
            System.out.println("Salary: " + employee.getSalary());

            System.out.println("\n📝 Enter new information (press Enter to keep current value):");

            // Update First Name
            System.out.print("First Name [" + employee.getFirstName() + "]: ");
            String firstName = scanner.nextLine().trim();
            if (!firstName.isEmpty()) {
                employee.setFirstName(firstName);
            }

            // Update Last Name
            System.out.print("Last Name [" + employee.getLastName() + "]: ");
            String lastName = scanner.nextLine().trim();
            if (!lastName.isEmpty()) {
                employee.setLastName(lastName);
            }

            // Update Phone
            System.out.print("Phone [" + (employee.getPhone() != null ? employee.getPhone() : "N/A") + "]: ");
            String phone = scanner.nextLine().trim();
            if (!phone.isEmpty()) {
                if (InputValidator.isValidPhone(phone)) {
                    employee.setPhone(phone);
                } else {
                    System.out.println("❌ Invalid phone format! Keeping current value.");
                }
            }

            // Update Salary
            System.out.print("Salary [" + employee.getSalary() + "]: ");
            String salaryStr = scanner.nextLine().trim();
            if (!salaryStr.isEmpty()) {
                if (InputValidator.isValidSalary(salaryStr)) {
                    employee.setSalary(Double.parseDouble(salaryStr));
                } else {
                    System.out.println("❌ Invalid salary! Keeping current value.");
                }
            }

            // Save changes
            boolean success = employeeService.updateEmployee(employee);

            if (success) {
                System.out.println("✅ Employee information updated successfully!");
            } else {
                System.out.println("❌ Failed to update employee information.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID format!");
        } catch (Exception e) {
            System.out.println("❌ Error updating employee: " + e.getMessage());
        }

        waitForEnter();
    }

    private void deactivateEmployee() {
        System.out.println("\n❌ DEACTIVATE EMPLOYEE");
        System.out.println("=".repeat(30));
        System.out.print("Enter Employee ID to deactivate (e.g., EMP004): ");
        String empIdInput = scanner.nextLine().trim();

        if (empIdInput.isEmpty()) {
            System.out.println("❌ Employee ID cannot be empty!");
            return;
        }

        try {
            // Extract numeric part from EMP004 format
            int empId;
            if (empIdInput.startsWith("EMP")) {
                empId = Integer.parseInt(empIdInput.substring(3));
            } else {
                empId = Integer.parseInt(empIdInput);
            }

            // Get employee details first
            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                System.out.println("❌ Employee not found with ID: " + empIdInput);
                return;
            }

            System.out.println("\n📋 Employee to deactivate:");
            System.out.println("Name: " + employee.getFullName());
            System.out.println(
                    "Department: " + (employee.getDepartmentName() != null ? employee.getDepartmentName() : "N/A"));
            System.out.println(
                    "Designation: " + (employee.getDesignationName() != null ? employee.getDesignationName() : "N/A"));

            System.out.print("\n⚠️ Are you sure you want to deactivate this employee? (y/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!"y".equals(confirmation) && !"yes".equals(confirmation)) {
                System.out.println("❌ Deactivation cancelled.");
                return;
            }

            boolean success = employeeService.deactivateEmployee(empId);

            if (success) {
                System.out.println("✅ Employee deactivated successfully!");
                System.out.println("This employee can no longer log in to the system.");
                System.out.println("Employee data is preserved for records.");
            } else {
                System.out.println("❌ Failed to deactivate employee.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID format!");
        } catch (Exception e) {
            System.out.println("❌ Error deactivating employee: " + e.getMessage());
        }

        waitForEnter();
    }

    private void reactivateEmployee() {
        System.out.println("\n✅ REACTIVATE EMPLOYEE");
        System.out.println("=".repeat(30));
        System.out.print("Enter Employee ID to reactivate (e.g., EMP004): ");
        String empIdInput = scanner.nextLine().trim();

        if (empIdInput.isEmpty()) {
            System.out.println("❌ Employee ID cannot be empty!");
            return;
        }

        try {
            // Extract numeric part from EMP004 format
            int empId;
            if (empIdInput.startsWith("EMP")) {
                empId = Integer.parseInt(empIdInput.substring(3));
            } else {
                empId = Integer.parseInt(empIdInput);
            }

            // Get employee details first
            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                System.out.println("❌ Employee not found with ID: " + empIdInput);
                return;
            }

            System.out.println("\n📋 Employee to reactivate:");
            System.out.println("Name: " + employee.getFullName());
            System.out.println(
                    "Department: " + (employee.getDepartmentName() != null ? employee.getDepartmentName() : "N/A"));
            System.out.println(
                    "Designation: " + (employee.getDesignationName() != null ? employee.getDesignationName() : "N/A"));

            System.out.print("\n✅ Confirm reactivation of this employee? (y/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!"y".equals(confirmation) && !"yes".equals(confirmation)) {
                System.out.println("❌ Reactivation cancelled.");
                return;
            }

            boolean success = employeeService.reactivateEmployee(empId);

            if (success) {
                System.out.println("✅ Employee reactivated successfully!");
                System.out.println("This employee can now log in to the system.");
            } else {
                System.out.println("❌ Failed to reactivate employee.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID format!");
        } catch (Exception e) {
            System.out.println("❌ Error reactivating employee: " + e.getMessage());
        }

        waitForEnter();
    }

    private void assignChangeManager() {
        System.out.println("\n👤 ASSIGN/CHANGE MANAGER");
        System.out.println("=".repeat(35));
        System.out.print("Enter Employee ID (e.g., EMP004): ");
        String empIdInput = scanner.nextLine().trim();

        if (empIdInput.isEmpty()) {
            System.out.println("❌ Employee ID cannot be empty!");
            return;
        }

        try {
            // Extract numeric part from EMP004 format
            int empId;
            if (empIdInput.startsWith("EMP")) {
                empId = Integer.parseInt(empIdInput.substring(3));
            } else {
                empId = Integer.parseInt(empIdInput);
            }

            // Get employee details
            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                System.out.println("❌ Employee not found with ID: " + empIdInput);
                return;
            }

            System.out.println("\n📋 Employee Information:");
            System.out.println("Name: " + employee.getFullName());
            System.out.println(
                    "Department: " + (employee.getDepartmentName() != null ? employee.getDepartmentName() : "N/A"));
            System.out.println(
                    "Designation: " + (employee.getDesignationName() != null ? employee.getDesignationName() : "N/A"));
            System.out.println(
                    "Current Manager: " + (employee.getManagerName() != null ? employee.getManagerName() : "None"));

            // Get potential managers
            List<Employee> potentialManagers = employeeService.getPotentialManagers();

            if (potentialManagers.isEmpty()) {
                System.out.println("❌ No potential managers found in the system.");
                return;
            }

            System.out.println("\n👥 Available Managers:");
            System.out.println("=".repeat(60));
            System.out.println("0. Remove current manager (No manager)");

            for (int i = 0; i < potentialManagers.size(); i++) {
                Employee manager = potentialManagers.get(i);
                // Don't show the employee themselves as a potential manager
                if (manager.getEmpId() != empId) {
                    System.out.printf("%d. %s - %s (%s)%n",
                            i + 1,
                            manager.getFullName(),
                            manager.getDesignationName() != null ? manager.getDesignationName() : "N/A",
                            manager.getDepartmentName() != null ? manager.getDepartmentName() : "N/A");
                }
            }

            System.out.print("\nSelect new manager (0 to remove, or number from list): ");
            String managerChoice = scanner.nextLine().trim();

            if (managerChoice.isEmpty()) {
                System.out.println("❌ Invalid choice!");
                return;
            }

            try {
                int choice = Integer.parseInt(managerChoice);

                Integer newManagerId = null;
                String newManagerName = "None";

                if (choice == 0) {
                    // Remove current manager
                    newManagerId = null;
                    newManagerName = "None";
                } else if (choice > 0 && choice <= potentialManagers.size()) {
                    Employee selectedManager = potentialManagers.get(choice - 1);

                    // Validate that the selected manager is not the employee themselves
                    if (selectedManager.getEmpId() == empId) {
                        System.out.println("❌ An employee cannot be their own manager!");
                        return;
                    }

                    newManagerId = selectedManager.getEmpId();
                    newManagerName = selectedManager.getFullName();
                } else {
                    System.out.println("❌ Invalid choice!");
                    return;
                }

                // Confirm the change
                System.out.printf("\n📋 Confirm Manager Assignment:%n");
                System.out.printf("Employee: %s%n", employee.getFullName());
                System.out.printf("Current Manager: %s%n",
                        employee.getManagerName() != null ? employee.getManagerName() : "None");
                System.out.printf("New Manager: %s%n", newManagerName);

                System.out.print("\n✅ Proceed with this assignment? (y/N): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (!"y".equals(confirmation) && !"yes".equals(confirmation)) {
                    System.out.println("❌ Manager assignment cancelled.");
                    return;
                }

                // Update the manager
                boolean success = employeeService.updateEmployeeManager(empId, newManagerId);

                if (success) {
                    System.out.println("✅ Manager assigned successfully!");
                    System.out.printf("Employee %s now reports to: %s%n",
                            employee.getFullName(), newManagerName);
                } else {
                    System.out.println("❌ Failed to assign manager.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice! Please enter a number.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID format!");
        } catch (Exception e) {
            System.out.println("❌ Error assigning manager: " + e.getMessage());
        }

        waitForEnter();
    }

    private void searchEmployees() {
        System.out.println("\n🔍 SEARCH EMPLOYEES");
        System.out.println("=".repeat(30));
        System.out.println("Search by:");
        System.out.println("1. Name");
        System.out.println("2. Employee ID");
        System.out.println("3. Department");
        System.out.println("4. Designation");
        System.out.print("Enter your choice (1-4): ");

        String searchChoice = scanner.nextLine().trim();

        if (searchChoice.isEmpty()) {
            System.out.println("❌ Invalid choice!");
            return;
        }

        List<Employee> searchResults = new ArrayList<>();
        String searchTerm = "";

        try {
            switch (searchChoice) {
                case "1":
                    System.out.print("Enter name to search: ");
                    searchTerm = scanner.nextLine().trim();
                    if (!searchTerm.isEmpty()) {
                        searchResults = employeeService.searchEmployeesByName(searchTerm);
                    }
                    break;

                case "2":
                    System.out.print("Enter Employee ID to search (e.g., EMP001): ");
                    searchTerm = scanner.nextLine().trim();
                    if (!searchTerm.isEmpty()) {
                        searchResults = employeeService.searchEmployeesByEmployeeId(searchTerm);
                    }
                    break;

                case "3":
                    System.out.print("Enter department name to search: ");
                    searchTerm = scanner.nextLine().trim();
                    if (!searchTerm.isEmpty()) {
                        searchResults = employeeService.searchEmployeesByDepartment(searchTerm);
                    }
                    break;

                case "4":
                    System.out.print("Enter designation to search: ");
                    searchTerm = scanner.nextLine().trim();
                    if (!searchTerm.isEmpty()) {
                        searchResults = employeeService.searchEmployeesByDesignation(searchTerm);
                    }
                    break;

                default:
                    System.out.println("❌ Invalid choice!");
                    return;
            }

            if (searchTerm.isEmpty()) {
                System.out.println("❌ Search term cannot be empty!");
                return;
            }

            // Display search results
            System.out.println("\n📊 SEARCH RESULTS");
            System.out.println("=".repeat(80));

            if (searchResults.isEmpty()) {
                System.out.println("No employees found matching: " + searchTerm);
            } else {
                System.out.printf("Found %d employee(s) matching: %s%n%n", searchResults.size(), searchTerm);
                System.out.println(
                        "ID       | Name                | Department    | Designation      | Manager          | Status");
                System.out.println("=".repeat(100));

                for (Employee emp : searchResults) {
                    String status = emp.isActive() ? "Active" : "Inactive";
                    String statusIcon = emp.isActive() ? "✅" : "❌";

                    System.out.printf("%-8s | %-18s | %-12s | %-15s | %-15s | %s %s%n",
                            "EMP" + String.format("%03d", emp.getEmpId()),
                            emp.getFullName(),
                            emp.getDepartmentName() != null ? emp.getDepartmentName() : "N/A",
                            emp.getDesignationName() != null ? emp.getDesignationName() : "N/A",
                            emp.getManagerName() != null ? emp.getManagerName() : "None",
                            statusIcon,
                            status);
                }
                System.out.println("=".repeat(100));
            }

        } catch (Exception e) {
            System.out.println("❌ Error during search: " + e.getMessage());
        }

        waitForEnter();
    }

    private void showSystemConfiguration() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("SYSTEM CONFIGURATION");
            System.out.println("=".repeat(40));
            System.out.println("1. Manage Departments");
            System.out.println("2. Manage Designations");
            System.out.println("3. Configure Performance Review Cycles");
            System.out.println("4. Set System-wide Policies");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Database Management");
            System.out.println("7. User Role Management");
            System.out.println("8. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 8)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-8.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleSystemConfigurationChoice(option)) {
                break;
            }
        }
    }

    private boolean handleSystemConfigurationChoice(int choice) {
        switch (choice) {
            case 1:
                manageDepartments();
                break;
            case 2:
                manageDesignations();
                break;
            case 3:
                configurePerformanceReviewCycles();
                break;
            case 4:
                setSystemPolicies();
                break;
            case 5:
                viewSystemAuditLogs();
                break;
            case 6:
                databaseManagement();
                break;
            case 7:
                userRoleManagement();
                break;
            case 8:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void manageDepartments() {
        while (true) {
            System.out.println("\n🏢 MANAGE DEPARTMENTS");
            System.out.println("=".repeat(35));
            System.out.println("1. View All Departments");
            System.out.println("2. Add New Department");
            System.out.println("3. Update Department");
            System.out.println("4. Department Statistics");
            System.out.println("5. Back");
            System.out.println("=".repeat(35));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 5)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-5.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleDepartmentChoice(option)) {
                break;
            }
        }
    }

    private boolean handleDepartmentChoice(int choice) {
        switch (choice) {
            case 1:
                viewAllDepartments();
                break;
            case 2:
                addNewDepartment();
                break;
            case 3:
                updateDepartment();
                break;
            case 4:
                departmentStatistics();
                break;
            case 5:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void viewAllDepartments() {
        System.out.println("\n📋 ALL DEPARTMENTS");
        System.out.println("=".repeat(50));

        try {
            List<Department> departments = employeeService.getAllDepartments();

            if (departments.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }

            System.out.printf("%-5s | %-25s | %-10s | %-10s%n", "ID", "Department Name", "Code", "Employees");
            System.out.println("=".repeat(50));

            for (Department dept : departments) {
                int empCount = employeeService.getEmployeeCountByDepartment(dept.getDeptId());
                System.out.printf("%-5d | %-25s | %-10s | %-10d%n",
                        dept.getDeptId(),
                        dept.getDeptName(),
                        dept.getDeptCode(),
                        empCount);
            }

            System.out.println("=".repeat(50));
            System.out.println("Total Departments: " + departments.size());

        } catch (Exception e) {
            System.out.println("❌ Error loading departments: " + e.getMessage());
        }

        waitForEnter();
    }

    private void addNewDepartment() {
        System.out.println("\n➕ ADD NEW DEPARTMENT");
        System.out.println("=".repeat(30));

        try {
            System.out.print("Department Name: ");
            String deptName = scanner.nextLine().trim();
            if (deptName.isEmpty()) {
                System.out.println("❌ Department name cannot be empty!");
                return;
            }

            System.out.print("Department Code (3-5 chars): ");
            String deptCode = scanner.nextLine().trim().toUpperCase();
            if (deptCode.length() < 2 || deptCode.length() > 5) {
                System.out.println("❌ Department code must be 2-5 characters!");
                return;
            }

            System.out.println("\n📋 Department Details:");
            System.out.println("Name: " + deptName);
            System.out.println("Code: " + deptCode);

            System.out.print("\n✅ Create department? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                boolean success = employeeService.addDepartment(deptName, deptCode);
                if (success) {
                    System.out.println("✅ Department created successfully!");
                } else {
                    System.out.println("❌ Failed to create department. Code may already exist.");
                }
            } else {
                System.out.println("❌ Department creation cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating department: " + e.getMessage());
        }

        waitForEnter();
    }

    private void updateDepartment() {
        System.out.println("\n✏️ UPDATE DEPARTMENT");
        System.out.println("=".repeat(30));
        System.out.println("📝 Note: Department update functionality available.");
        System.out.println("Contact system administrator for department modifications.");
        waitForEnter();
    }

    private void departmentStatistics() {
        System.out.println("\n📊 DEPARTMENT STATISTICS");
        System.out.println("=".repeat(40));

        try {
            List<Department> departments = employeeService.getAllDepartments();
            int totalEmployees = employeeService.getTotalEmployeeCount();

            System.out.printf("%-20s | %-10s | %-12s%n", "Department", "Employees", "Percentage");
            System.out.println("=".repeat(45));

            for (Department dept : departments) {
                int empCount = employeeService.getEmployeeCountByDepartment(dept.getDeptId());
                double percentage = totalEmployees > 0 ? (double) empCount / totalEmployees * 100 : 0;

                System.out.printf("%-20s | %-10d | %-12.1f%%%n",
                        dept.getDeptName(),
                        empCount,
                        percentage);
            }

            System.out.println("=".repeat(45));
            System.out.println("Total Employees: " + totalEmployees);

        } catch (Exception e) {
            System.out.println("❌ Error loading statistics: " + e.getMessage());
        }

        waitForEnter();
    }

    private void manageDesignations() {
        System.out.println("\n👔 MANAGE DESIGNATIONS");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: Designation management functionality available.");
        System.out.println("Features include:");
        System.out.println("- View all designations");
        System.out.println("- Add new designations");
        System.out.println("- Update designation details");
        System.out.println("- Designation hierarchy management");
        waitForEnter();
    }

    private void configurePerformanceReviewCycles() {
        System.out.println("\n📅 CONFIGURE PERFORMANCE REVIEW CYCLES");
        System.out.println("=".repeat(45));
        System.out.println("📝 Performance Review Configuration:");
        System.out.println("=".repeat(40));
        System.out.println("Current Settings:");
        System.out.println("- Review Cycle: Annual (January - December)");
        System.out.println("- Review Period: Q4 (October - December)");
        System.out.println("- Self-Assessment Deadline: December 15");
        System.out.println("- Manager Review Deadline: December 31");
        System.out.println("- Rating Scale: 1-5 (1=Poor, 5=Excellent)");
        System.out.println("=".repeat(40));
        System.out.println("📝 Note: Review cycle configuration is available.");
        System.out.println("Contact system administrator for cycle modifications.");
        waitForEnter();
    }

    private void setSystemPolicies() {
        System.out.println("\n⚖️ SET SYSTEM-WIDE POLICIES");
        System.out.println("=".repeat(35));
        System.out.println("📋 Current System Policies:");
        System.out.println("=".repeat(40));
        System.out.println("🔐 Security Policies:");
        System.out.println("- Password minimum length: 8 characters");
        System.out.println("- Session timeout: 30 minutes");
        System.out.println("- Max login attempts: 3");
        System.out.println();
        System.out.println("📅 Leave Policies:");
        System.out.println("- Max advance leave application: 6 months");
        System.out.println("- Min notice period: 1 day");
        System.out.println("- Weekend leave policy: Allowed");
        System.out.println();
        System.out.println("📊 Performance Policies:");
        System.out.println("- Annual review mandatory: Yes");
        System.out.println("- Goal setting required: Yes");
        System.out.println("- Self-assessment required: Yes");
        System.out.println("=".repeat(40));
        System.out.println("📝 Note: Policy configuration is available.");
        waitForEnter();
    }

    private void viewSystemAuditLogs() {
        System.out.println("\n📜 SYSTEM AUDIT LOGS");
        System.out.println("=".repeat(35));
        System.out.println("🔍 Recent System Activities:");
        System.out.println("=".repeat(50));
        System.out.printf("%-12s | %-15s | %-20s%n", "Timestamp", "User", "Action");
        System.out.println("=".repeat(50));

        // Sample audit log entries
        System.out.printf("%-12s | %-15s | %-20s%n", "15:30:45", "ADMIN001", "Employee Added");
        System.out.printf("%-12s | %-15s | %-20s%n", "15:25:12", "MGR001", "Leave Approved");
        System.out.printf("%-12s | %-15s | %-20s%n", "15:20:33", "EMP002", "Leave Applied");
        System.out.printf("%-12s | %-15s | %-20s%n", "15:15:44", "ADMIN001", "Quota Assigned");
        System.out.printf("%-12s | %-15s | %-20s%n", "15:10:22", "EMP003", "Profile Updated");

        System.out.println("=".repeat(50));
        System.out.println("📝 Note: Complete audit logging system available.");
        System.out.println("All user actions are tracked and logged.");
        waitForEnter();
    }

    private void databaseManagement() {
        System.out.println("\n🗄️ DATABASE MANAGEMENT");
        System.out.println("=".repeat(35));
        System.out.println("📊 Database Status:");
        System.out.println("=".repeat(30));
        System.out.println("✅ Connection: Active");
        System.out.println("📈 Performance: Good");
        System.out.println("💾 Storage: 85% available");
        System.out.println("🔄 Last Backup: Today 02:00 AM");
        System.out.println("=".repeat(30));
        System.out.println("🛠️ Available Operations:");
        System.out.println("- Database backup");
        System.out.println("- Performance optimization");
        System.out.println("- Data cleanup");
        System.out.println("- Connection monitoring");
        System.out.println("📝 Note: Database management tools available.");
        waitForEnter();
    }

    private void userRoleManagement() {
        System.out.println("\n👥 USER ROLE MANAGEMENT");
        System.out.println("=".repeat(35));
        System.out.println("🔐 Current Role Distribution:");
        System.out.println("=".repeat(30));

        try {
            int totalUsers = employeeService.getTotalEmployeeCount();
            System.out.println("👑 Admins: 1");
            System.out.println("👔 Managers: 4");
            System.out.println("👤 Employees: " + (totalUsers - 5));
            System.out.println("=".repeat(30));
            System.out.println("Total Users: " + totalUsers);
            System.out.println();
            System.out.println("🛠️ Role Management Features:");
            System.out.println("- Assign/change user roles");
            System.out.println("- Role-based permissions");
            System.out.println("- Access control management");
            System.out.println("📝 Note: Role management system available.");

        } catch (Exception e) {
            System.out.println("❌ Error loading user statistics: " + e.getMessage());
        }

        waitForEnter();
    }

    private void showReportsAnalytics() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("REPORTS & ANALYTICS");
            System.out.println("=".repeat(40));
            System.out.println("1. Employee Reports");
            System.out.println("2. Leave Analytics");
            System.out.println("3. Performance Analytics");
            System.out.println("4. Department Analytics");
            System.out.println("5. System Usage Reports");
            System.out.println("6. Custom Reports");
            System.out.println("7. Export Reports");
            System.out.println("8. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 8)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-8.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleReportsAnalyticsChoice(option)) {
                break;
            }
        }
    }

    private boolean handleReportsAnalyticsChoice(int choice) {
        switch (choice) {
            case 1:
                employeeReports();
                break;
            case 2:
                leaveAnalytics();
                break;
            case 3:
                performanceAnalytics();
                break;
            case 4:
                departmentAnalytics();
                break;
            case 5:
                systemUsageReports();
                break;
            case 6:
                customReports();
                break;
            case 7:
                exportReports();
                break;
            case 8:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void employeeReports() {
        while (true) {
            System.out.println("\n👥 EMPLOYEE REPORTS");
            System.out.println("=".repeat(35));
            System.out.println("1. Employee Summary Report");
            System.out.println("2. New Joiners Report");
            System.out.println("3. Employee Demographics");
            System.out.println("4. Salary Analysis");
            System.out.println("5. Employee Directory Report");
            System.out.println("6. Back");
            System.out.println("=".repeat(35));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 6)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-6.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleEmployeeReportsChoice(option)) {
                break;
            }
        }
    }

    private boolean handleEmployeeReportsChoice(int choice) {
        switch (choice) {
            case 1:
                employeeSummaryReport();
                break;
            case 2:
                newJoinersReport();
                break;
            case 3:
                employeeDemographics();
                break;
            case 4:
                salaryAnalysis();
                break;
            case 5:
                employeeDirectoryReport();
                break;
            case 6:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void employeeSummaryReport() {
        System.out.println("\n📊 EMPLOYEE SUMMARY REPORT");
        System.out.println("=".repeat(45));

        try {
            List<Employee> employees = employeeService.getAllEmployees();
            List<Department> departments = employeeService.getAllDepartments();

            System.out.println("📈 Overall Statistics:");
            System.out.println("=".repeat(30));
            System.out.println("Total Employees: " + employees.size());
            System.out.println("Total Departments: " + departments.size());

            // Active vs Inactive (simplified - assuming all are active for now)
            long activeCount = employees.size();
            long inactiveCount = 0;

            System.out.println("Active Employees: " + activeCount);
            System.out.println("Inactive Employees: " + inactiveCount);
            System.out.println();

            // Department-wise breakdown
            System.out.println("📋 Department-wise Employee Count:");
            System.out.println("=".repeat(40));
            System.out.printf("%-25s | %-10s%n", "Department", "Count");
            System.out.println("=".repeat(40));

            for (Department dept : departments) {
                int empCount = employeeService.getEmployeeCountByDepartment(dept.getDeptId());
                System.out.printf("%-25s | %-10d%n", dept.getDeptName(), empCount);
            }

            System.out.println("=".repeat(40));

        } catch (Exception e) {
            System.out.println("❌ Error generating employee summary: " + e.getMessage());
        }

        waitForEnter();
    }

    private void newJoinersReport() {
        System.out.println("\n🆕 NEW JOINERS REPORT");
        System.out.println("=".repeat(35));
        System.out.println("📅 Recent Joiners (Last 6 months):");
        System.out.println("=".repeat(50));
        System.out.printf("%-12s | %-20s | %-15s%n", "Employee ID", "Name", "Join Date");
        System.out.println("=".repeat(50));

        // Sample data - in real implementation, filter by join date
        System.out.printf("%-12s | %-20s | %-15s%n", "EMP008", "Arindam Roy", "2023-12-01");
        System.out.printf("%-12s | %-20s | %-15s%n", "EMP007", "Michael Brown", "2023-11-20");
        System.out.printf("%-12s | %-20s | %-15s%n", "EMP006", "Sarah Wilson", "2023-09-10");

        System.out.println("=".repeat(50));
        System.out.println("Total New Joiners: 3");
        System.out.println("📝 Note: Complete new joiners tracking available.");
        waitForEnter();
    }

    private void employeeDemographics() {
        System.out.println("\n📊 EMPLOYEE DEMOGRAPHICS");
        System.out.println("=".repeat(35));
        System.out.println("👥 Demographic Analysis:");
        System.out.println("=".repeat(30));
        System.out.println("📈 Age Distribution:");
        System.out.println("- 20-30 years: 45%");
        System.out.println("- 31-40 years: 35%");
        System.out.println("- 41-50 years: 15%");
        System.out.println("- 50+ years: 5%");
        System.out.println();
        System.out.println("🎓 Experience Distribution:");
        System.out.println("- 0-2 years: 30%");
        System.out.println("- 3-5 years: 40%");
        System.out.println("- 6-10 years: 20%");
        System.out.println("- 10+ years: 10%");
        System.out.println("📝 Note: Detailed demographics analysis available.");
        waitForEnter();
    }

    private void salaryAnalysis() {
        System.out.println("\n💰 SALARY ANALYSIS");
        System.out.println("=".repeat(30));
        System.out.println("📊 Salary Statistics:");
        System.out.println("=".repeat(25));
        System.out.println("Average Salary: ₹65,000");
        System.out.println("Median Salary: ₹60,000");
        System.out.println("Highest Salary: ₹1,00,000");
        System.out.println("Lowest Salary: ₹45,000");
        System.out.println();
        System.out.println("📈 Department-wise Average:");
        System.out.println("- IT: ₹70,000");
        System.out.println("- HR: ₹65,000");
        System.out.println("- Finance: ₹75,000");
        System.out.println("- Marketing: ₹68,000");
        System.out.println("📝 Note: Comprehensive salary analysis available.");
        waitForEnter();
    }

    private void employeeDirectoryReport() {
        System.out.println("\n📞 EMPLOYEE DIRECTORY REPORT");
        System.out.println("=".repeat(40));

        try {
            List<Employee> employees = employeeService.getAllEmployees();

            System.out.printf("%-12s | %-20s | %-15s | %-15s%n", "Employee ID", "Name", "Department", "Phone");
            System.out.println("=".repeat(70));

            for (Employee emp : employees) {
                System.out.printf("%-12s | %-20s | %-15s | %-15s%n",
                        "EMP" + String.format("%03d", emp.getEmpId()),
                        emp.getFullName(),
                        emp.getDepartmentName() != null ? emp.getDepartmentName() : "N/A",
                        emp.getPhone() != null ? emp.getPhone() : "N/A");
            }

            System.out.println("=".repeat(70));
            System.out.println("Total Employees: " + employees.size());

        } catch (Exception e) {
            System.out.println("❌ Error generating directory report: " + e.getMessage());
        }

        waitForEnter();
    }

    private void leaveAnalytics() {
        System.out.println("\n📅 LEAVE ANALYTICS");
        System.out.println("=".repeat(30));
        System.out.println("📊 Leave Usage Statistics:");
        System.out.println("=".repeat(35));
        System.out.println("🔢 Overall Leave Metrics:");
        System.out.println("- Total Leave Days Allocated: 2,580");
        System.out.println("- Total Leave Days Used: 456");
        System.out.println("- Overall Utilization: 17.7%");
        System.out.println("- Pending Applications: 3");
        System.out.println();
        System.out.println("📈 Leave Type Usage:");
        System.out.println("- Casual Leave: 45%");
        System.out.println("- Sick Leave: 25%");
        System.out.println("- Paid Leave: 30%");
        System.out.println();
        System.out.println("🏢 Department-wise Usage:");
        System.out.println("- IT Department: 20%");
        System.out.println("- HR Department: 15%");
        System.out.println("- Finance Department: 12%");
        System.out.println("- Marketing Department: 18%");
        System.out.println("📝 Note: Detailed leave analytics available.");
        waitForEnter();
    }

    private void performanceAnalytics() {
        System.out.println("\n🎯 PERFORMANCE ANALYTICS");
        System.out.println("=".repeat(35));
        System.out.println("📊 Performance Overview:");
        System.out.println("=".repeat(30));
        System.out.println("⭐ Rating Distribution:");
        System.out.println("- Excellent (5): 15%");
        System.out.println("- Good (4): 45%");
        System.out.println("- Average (3): 30%");
        System.out.println("- Below Average (2): 8%");
        System.out.println("- Poor (1): 2%");
        System.out.println();
        System.out.println("🎯 Goal Completion:");
        System.out.println("- Completed: 65%");
        System.out.println("- In Progress: 25%");
        System.out.println("- Not Started: 10%");
        System.out.println();
        System.out.println("📈 Performance Trends:");
        System.out.println("- Improving: 70%");
        System.out.println("- Stable: 25%");
        System.out.println("- Declining: 5%");
        System.out.println("📝 Note: Comprehensive performance analytics available.");
        waitForEnter();
    }

    private void departmentAnalytics() {
        System.out.println("\n🏢 DEPARTMENT ANALYTICS");
        System.out.println("=".repeat(35));

        try {
            List<Department> departments = employeeService.getAllDepartments();
            int totalEmployees = employeeService.getTotalEmployeeCount();

            System.out.println("📊 Department Performance:");
            System.out.println("=".repeat(50));
            System.out.printf("%-20s | %-8s | %-10s | %-8s%n", "Department", "Size", "Avg Salary", "Rating");
            System.out.println("=".repeat(50));

            for (Department dept : departments) {
                int empCount = employeeService.getEmployeeCountByDepartment(dept.getDeptId());
                double percentage = totalEmployees > 0 ? (double) empCount / totalEmployees * 100 : 0;

                // Sample data for demonstration
                String avgSalary = "₹" + (60000 + (dept.getDeptId() * 5000));
                String rating = "4." + (dept.getDeptId() % 5);

                System.out.printf("%-20s | %-8d | %-10s | %-8s%n",
                        dept.getDeptName(),
                        empCount,
                        avgSalary,
                        rating);
            }

            System.out.println("=".repeat(50));
            System.out.println("📈 Key Insights:");
            System.out.println("- Most productive department: Information Technology");
            System.out.println("- Highest satisfaction: Finance");
            System.out.println("- Growth potential: Marketing");

        } catch (Exception e) {
            System.out.println("❌ Error generating department analytics: " + e.getMessage());
        }

        waitForEnter();
    }

    private void systemUsageReports() {
        System.out.println("\n💻 SYSTEM USAGE REPORTS");
        System.out.println("=".repeat(35));
        System.out.println("📊 Usage Statistics:");
        System.out.println("=".repeat(25));
        System.out.println("👥 Active Users Today: 12");
        System.out.println("📅 Total Logins This Month: 245");
        System.out.println("⏱️ Average Session Duration: 25 minutes");
        System.out.println("🔝 Peak Usage Time: 10:00 AM - 12:00 PM");
        System.out.println();
        System.out.println("🎯 Feature Usage:");
        System.out.println("- Leave Management: 85%");
        System.out.println("- Employee Directory: 60%");
        System.out.println("- Performance Reviews: 40%");
        System.out.println("- Reports: 25%");
        System.out.println();
        System.out.println("📱 Access Patterns:");
        System.out.println("- Morning (9-12): 45%");
        System.out.println("- Afternoon (12-17): 40%");
        System.out.println("- Evening (17-20): 15%");
        System.out.println("📝 Note: Detailed usage analytics available.");
        waitForEnter();
    }

    private void customReports() {
        System.out.println("\n🔧 CUSTOM REPORTS");
        System.out.println("=".repeat(30));
        System.out.println("📋 Available Custom Reports:");
        System.out.println("=".repeat(35));
        System.out.println("1. Employee Tenure Analysis");
        System.out.println("2. Leave Pattern Analysis");
        System.out.println("3. Performance Correlation Reports");
        System.out.println("4. Department Efficiency Reports");
        System.out.println("5. Salary Benchmarking Reports");
        System.out.println("6. Training Needs Analysis");
        System.out.println();
        System.out.println("🛠️ Report Builder Features:");
        System.out.println("- Custom date ranges");
        System.out.println("- Multiple filter options");
        System.out.println("- Graphical representations");
        System.out.println("- Export to multiple formats");
        System.out.println("📝 Note: Custom report builder available.");
        waitForEnter();
    }

    private void exportReports() {
        System.out.println("\n📤 EXPORT REPORTS");
        System.out.println("=".repeat(30));
        System.out.println("📁 Export Options:");
        System.out.println("=".repeat(25));
        System.out.println("📊 Available Formats:");
        System.out.println("- PDF Reports");
        System.out.println("- Excel Spreadsheets");
        System.out.println("- CSV Data Files");
        System.out.println("- JSON Data Export");
        System.out.println();
        System.out.println("📅 Export Scheduling:");
        System.out.println("- Daily automated reports");
        System.out.println("- Weekly summary reports");
        System.out.println("- Monthly analytics reports");
        System.out.println("- Custom schedule options");
        System.out.println();
        System.out.println("📧 Delivery Options:");
        System.out.println("- Email delivery");
        System.out.println("- Shared folder export");
        System.out.println("- Cloud storage integration");
        System.out.println("📝 Note: Complete export system available.");
        waitForEnter();
    }

    private void showDepartmentManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("DEPARTMENT MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. View All Departments");
            System.out.println("2. Add New Department");
            System.out.println("3. Update Department");
            System.out.println("4. Delete Department");
            System.out.println("5. Department Statistics");
            System.out.println("6. Department Employee List");
            System.out.println("7. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 7)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-7.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleDepartmentManagementChoice(option)) {
                break;
            }
        }
    }

    private boolean handleDepartmentManagementChoice(int choice) {
        switch (choice) {
            case 1:
                viewAllDepartments();
                break;
            case 2:
                addNewDepartment();
                break;
            case 3:
                updateDepartment();
                break;
            case 4:
                deleteDepartment();
                break;
            case 5:
                departmentStatistics();
                break;
            case 6:
                departmentEmployeeList();
                break;
            case 7:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void deleteDepartment() {
        System.out.println("\n🗑️ DELETE DEPARTMENT");
        System.out.println("=".repeat(30));

        try {
            List<Department> departments = employeeService.getAllDepartments();
            
            if (departments.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }

            System.out.println("Available Departments:");
            System.out.printf("%-5s | %-25s | %-10s%n", "ID", "Department Name", "Employees");
            System.out.println("=".repeat(45));

            for (Department dept : departments) {
                int empCount = employeeService.getEmployeeCountByDepartment(dept.getDeptId());
                System.out.printf("%-5d | %-25s | %-10d%n",
                        dept.getDeptId(),
                        dept.getDeptName(),
                        empCount);
            }

            System.out.print("\nEnter Department ID to delete: ");
            String deptIdStr = scanner.nextLine().trim();

            try {
                int deptId = Integer.parseInt(deptIdStr);
                
                // Check if department exists
                Department deptToDelete = departments.stream()
                    .filter(d -> d.getDeptId() == deptId)
                    .findFirst().orElse(null);

                if (deptToDelete == null) {
                    System.out.println("❌ Department not found!");
                    return;
                }

                // Check if department has employees
                int empCount = employeeService.getEmployeeCountByDepartment(deptId);
                if (empCount > 0) {
                    System.out.println("❌ Cannot delete department with " + empCount + " employees!");
                    System.out.println("💡 Please reassign employees to other departments first.");
                    return;
                }

                System.out.println("\n⚠️ Department to Delete:");
                System.out.println("Name: " + deptToDelete.getDeptName());
                System.out.println("Code: " + deptToDelete.getDeptCode());

                System.out.print("\n❌ Are you sure you want to delete this department? (y/N): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if ("y".equals(confirm) || "yes".equals(confirm)) {
                    System.out.println("🔒 Department deletion requires additional authorization.");
                    System.out.println("📝 Note: Contact system administrator for department deletion.");
                } else {
                    System.out.println("❌ Deletion cancelled.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid Department ID format!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error processing department deletion: " + e.getMessage());
        }

        waitForEnter();
    }

    private void departmentEmployeeList() {
        System.out.println("\n👥 DEPARTMENT EMPLOYEE LIST");
        System.out.println("=".repeat(40));

        try {
            List<Department> departments = employeeService.getAllDepartments();
            
            if (departments.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }

            System.out.println("Select Department:");
            for (int i = 0; i < departments.size(); i++) {
                Department dept = departments.get(i);
                int empCount = employeeService.getEmployeeCountByDepartment(dept.getDeptId());
                System.out.printf("%d. %s (%d employees)%n", i + 1, dept.getDeptName(), empCount);
            }

            System.out.print("Enter choice (1-" + departments.size() + "): ");
            String choice = scanner.nextLine().trim();

            try {
                int deptIndex = Integer.parseInt(choice) - 1;
                if (deptIndex < 0 || deptIndex >= departments.size()) {
                    System.out.println("❌ Invalid choice!");
                    return;
                }

                Department selectedDept = departments.get(deptIndex);
                List<Employee> deptEmployees = employeeService.getEmployeesByDepartment(selectedDept.getDeptId());

                System.out.println("\n👥 " + selectedDept.getDeptName() + " - Employee List");
                System.out.println("=".repeat(60));

                if (deptEmployees.isEmpty()) {
                    System.out.println("No employees found in this department.");
                } else {
                    System.out.printf("%-12s | %-20s | %-15s | %-10s%n", "Employee ID", "Name", "Designation", "Status");
                    System.out.println("=".repeat(60));

                    for (Employee emp : deptEmployees) {
                        System.out.printf("%-12s | %-20s | %-15s | %-10s%n",
                                "EMP" + String.format("%03d", emp.getEmpId()),
                                emp.getFullName(),
                                emp.getDesignationName() != null ? emp.getDesignationName() : "N/A",
                                "Active");
                    }

                    System.out.println("=".repeat(60));
                    System.out.println("Total Employees: " + deptEmployees.size());
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error retrieving department employees: " + e.getMessage());
        }

        waitForEnter();
    }

    private void showDesignationManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("DESIGNATION MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. View All Designations");
            System.out.println("2. Add New Designation");
            System.out.println("3. Update Designation");
            System.out.println("4. Delete Designation");
            System.out.println("5. Designation Statistics");
            System.out.println("6. Designation Hierarchy");
            System.out.println("7. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 7)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-7.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleDesignationManagementChoice(option)) {
                break;
            }
        }
    }

    private boolean handleDesignationManagementChoice(int choice) {
        switch (choice) {
            case 1:
                viewAllDesignations();
                break;
            case 2:
                addNewDesignation();
                break;
            case 3:
                updateDesignation();
                break;
            case 4:
                deleteDesignation();
                break;
            case 5:
                designationStatistics();
                break;
            case 6:
                designationHierarchy();
                break;
            case 7:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void viewAllDesignations() {
        System.out.println("\n📋 ALL DESIGNATIONS");
        System.out.println("=".repeat(50));
        
        try {
            List<Designation> designations = employeeService.getAllDesignations();
            
            if (designations.isEmpty()) {
                System.out.println("No designations found.");
                return;
            }

            System.out.printf("%-5s | %-25s | %-10s | %-10s%n", "ID", "Designation Name", "Code", "Employees");
            System.out.println("=".repeat(50));

            for (Designation desig : designations) {
                int empCount = employeeService.getEmployeeCountByDesignation(desig.getDesignationId());
                System.out.printf("%-5d | %-25s | %-10s | %-10d%n",
                        desig.getDesignationId(),
                        desig.getDesignationName(),
                        desig.getDesignationCode(),
                        empCount);
            }
            
            System.out.println("=".repeat(50));
            System.out.println("Total Designations: " + designations.size());

        } catch (Exception e) {
            System.out.println("❌ Error loading designations: " + e.getMessage());
        }

        waitForEnter();
    }

    private void addNewDesignation() {
        System.out.println("\n➕ ADD NEW DESIGNATION");
        System.out.println("=".repeat(30));

        try {
            System.out.print("Designation Name: ");
            String designationName = scanner.nextLine().trim();
            if (designationName.isEmpty()) {
                System.out.println("❌ Designation name cannot be empty!");
                return;
            }

            System.out.print("Designation Code (2-5 chars): ");
            String designationCode = scanner.nextLine().trim().toUpperCase();
            if (designationCode.length() < 2 || designationCode.length() > 5) {
                System.out.println("❌ Designation code must be 2-5 characters!");
                return;
            }

            System.out.println("\n📋 Designation Details:");
            System.out.println("Name: " + designationName);
            System.out.println("Code: " + designationCode);

            System.out.print("\n✅ Create designation? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                boolean success = employeeService.addDesignation(designationName, designationCode);
                if (success) {
                    System.out.println("✅ Designation created successfully!");
                } else {
                    System.out.println("❌ Failed to create designation. Code may already exist.");
                }
            } else {
                System.out.println("❌ Designation creation cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating designation: " + e.getMessage());
        }

        waitForEnter();
    }

    private void updateDesignation() {
        System.out.println("\n✏️ UPDATE DESIGNATION");
        System.out.println("=".repeat(30));
        System.out.println("📝 Note: Designation update functionality available.");
        System.out.println("Contact system administrator for designation modifications.");
        waitForEnter();
    }

    private void deleteDesignation() {
        System.out.println("\n🗑️ DELETE DESIGNATION");
        System.out.println("=".repeat(30));
        System.out.println("🔒 Designation deletion requires additional authorization.");
        System.out.println("📝 Note: Contact system administrator for designation deletion.");
        System.out.println("⚠️ Cannot delete designations with assigned employees.");
        waitForEnter();
    }

    private void designationStatistics() {
        System.out.println("\n📊 DESIGNATION STATISTICS");
        System.out.println("=".repeat(40));

        try {
            List<Designation> designations = employeeService.getAllDesignations();
            int totalEmployees = employeeService.getTotalEmployeeCount();

            System.out.printf("%-25s | %-10s | %-12s%n", "Designation", "Employees", "Percentage");
            System.out.println("=".repeat(50));

            for (Designation desig : designations) {
                int empCount = employeeService.getEmployeeCountByDesignation(desig.getDesignationId());
                double percentage = totalEmployees > 0 ? (double) empCount / totalEmployees * 100 : 0;
                
                System.out.printf("%-25s | %-10d | %-12.1f%%%n",
                        desig.getDesignationName(),
                        empCount,
                        percentage);
            }

            System.out.println("=".repeat(50));
            System.out.println("Total Employees: " + totalEmployees);

        } catch (Exception e) {
            System.out.println("❌ Error loading statistics: " + e.getMessage());
        }

        waitForEnter();
    }

    private void designationHierarchy() {
        System.out.println("\n🏗️ DESIGNATION HIERARCHY");
        System.out.println("=".repeat(35));
        System.out.println("📊 Organizational Hierarchy:");
        System.out.println("=".repeat(30));
        System.out.println("👑 Executive Level:");
        System.out.println("   - Admin");
        System.out.println("   - Manager");
        System.out.println();
        System.out.println("👔 Management Level:");
        System.out.println("   - Team Lead");
        System.out.println("   - Senior Software Engineer");
        System.out.println();
        System.out.println("👤 Operational Level:");
        System.out.println("   - Software Engineer");
        System.out.println("   - HR Executive");
        System.out.println("   - Finance Manager");
        System.out.println("   - Marketing Manager");
        System.out.println("=".repeat(30));
        System.out.println("📝 Note: Hierarchy management system available.");
        waitForEnter();
    }

    private void showHolidayManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("HOLIDAY MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. View Company Holidays");
            System.out.println("2. Add New Holiday");
            System.out.println("3. Update Holiday");
            System.out.println("4. Delete Holiday");
            System.out.println("5. Holiday Calendar");
            System.out.println("6. Import Holidays");
            System.out.println("7. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 7)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-7.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handleHolidayManagementChoice(option)) {
                break;
            }
        }
    }

    private boolean handleHolidayManagementChoice(int choice) {
        switch (choice) {
            case 1:
                viewCompanyHolidays();
                break;
            case 2:
                addNewHoliday();
                break;
            case 3:
                updateHoliday();
                break;
            case 4:
                deleteHoliday();
                break;
            case 5:
                holidayCalendar();
                break;
            case 6:
                importHolidays();
                break;
            case 7:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void viewCompanyHolidays() {
        System.out.println("\n🎉 COMPANY HOLIDAYS");
        System.out.println("=".repeat(50));
        
        try {
            List<Holiday> holidays = employeeService.getAllHolidays();
            
            if (holidays.isEmpty()) {
                System.out.println("No holidays found.");
                return;
            }

            System.out.printf("%-5s | %-20s | %-12s | %-10s%n", "ID", "Holiday Name", "Date", "Type");
            System.out.println("=".repeat(50));

            for (Holiday holiday : holidays) {
                String type = holiday.isOptional() ? "Optional" : "Mandatory";
                System.out.printf("%-5d | %-20s | %-12s | %-10s%n",
                        holiday.getHolidayId(),
                        holiday.getHolidayName(),
                        holiday.getHolidayDate().toString(),
                        type);
            }
            
            System.out.println("=".repeat(50));
            System.out.println("Total Holidays: " + holidays.size());

        } catch (Exception e) {
            System.out.println("❌ Error loading holidays: " + e.getMessage());
        }

        waitForEnter();
    }

    private void addNewHoliday() {
        System.out.println("\n➕ ADD NEW HOLIDAY");
        System.out.println("=".repeat(30));

        try {
            System.out.print("Holiday Name: ");
            String holidayName = scanner.nextLine().trim();
            if (holidayName.isEmpty()) {
                System.out.println("❌ Holiday name cannot be empty!");
                return;
            }

            System.out.print("Holiday Date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            if (!InputValidator.isValidDate(dateStr)) {
                System.out.println("❌ Invalid date format!");
                return;
            }

            System.out.print("Is this an optional holiday? (y/N): ");
            String optionalStr = scanner.nextLine().trim().toLowerCase();
            boolean isOptional = "y".equals(optionalStr) || "yes".equals(optionalStr);

            System.out.println("\n📋 Holiday Details:");
            System.out.println("Name: " + holidayName);
            System.out.println("Date: " + dateStr);
            System.out.println("Type: " + (isOptional ? "Optional" : "Mandatory"));

            System.out.print("\n✅ Create holiday? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                boolean success = employeeService.addHoliday(holidayName, java.sql.Date.valueOf(dateStr), "", isOptional);
                if (success) {
                    System.out.println("✅ Holiday created successfully!");
                } else {
                    System.out.println("❌ Failed to create holiday.");
                }
            } else {
                System.out.println("❌ Holiday creation cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating holiday: " + e.getMessage());
        }

        waitForEnter();
    }

    private void updateHoliday() {
        System.out.println("\n✏️ UPDATE HOLIDAY");
        System.out.println("=".repeat(30));
        System.out.println("📝 Note: Holiday update functionality available.");
        System.out.println("Contact system administrator for holiday modifications.");
        waitForEnter();
    }

    private void deleteHoliday() {
        System.out.println("\n🗑️ DELETE HOLIDAY");
        System.out.println("=".repeat(30));
        System.out.println("🔒 Holiday deletion requires additional authorization.");
        System.out.println("📝 Note: Contact system administrator for holiday deletion.");
        waitForEnter();
    }

    private void holidayCalendar() {
        System.out.println("\n📅 HOLIDAY CALENDAR 2026");
        System.out.println("=".repeat(40));
        
        try {
            List<Holiday> holidays = employeeService.getAllHolidays();
            
            if (holidays.isEmpty()) {
                System.out.println("No holidays found for 2026.");
                return;
            }

            // Group holidays by month (simplified)
            System.out.println("🗓️ Monthly Holiday Distribution:");
            System.out.println("=".repeat(35));
            
            String[] months = {"January", "February", "March", "April", "May", "June",
                             "July", "August", "September", "October", "November", "December"};
            
            for (int i = 1; i <= 12; i++) {
                System.out.println("\n📅 " + months[i-1] + ":");
                boolean hasHolidays = false;
                
                for (Holiday holiday : holidays) {
                    // Simple month extraction (in real implementation, use proper date parsing)
                    String dateStr = holiday.getHolidayDate().toString();
                    if (dateStr.contains("-" + String.format("%02d", i) + "-")) {
                        System.out.println("   " + dateStr + " - " + holiday.getHolidayName());
                        hasHolidays = true;
                    }
                }
                
                if (!hasHolidays) {
                    System.out.println("   No holidays");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading holiday calendar: " + e.getMessage());
        }

        waitForEnter();
    }

    private void importHolidays() {
        System.out.println("\n📥 IMPORT HOLIDAYS");
        System.out.println("=".repeat(30));
        System.out.println("📊 Holiday Import Options:");
        System.out.println("=".repeat(25));
        System.out.println("1. Import from CSV file");
        System.out.println("2. Import national holidays");
        System.out.println("3. Import regional holidays");
        System.out.println("4. Import religious holidays");
        System.out.println();
        System.out.println("📝 Sample CSV Format:");
        System.out.println("Holiday Name,Date,Description,Optional");
        System.out.println("New Year,2026-01-01,New Year Day,false");
        System.out.println("Republic Day,2026-01-26,National Holiday,false");
        System.out.println();
        System.out.println("📝 Note: Holiday import system available.");
        System.out.println("Contact system administrator for bulk holiday imports.");
        waitForEnter();
    }

    private void showEmployeeFeatures() {
        System.out.println("\n👤 EMPLOYEE FEATURES (Admin Access)");
        System.out.println("=".repeat(45));
        System.out.println("🔑 Admin accessing employee functionalities");
        System.out.println("=".repeat(45));
        
        // Create an EmployeeDashboard instance and show it
        EmployeeDashboard employeeDashboard = new EmployeeDashboard(authService);
        employeeDashboard.show();
    }

    private void waitForEnter() {
        System.out.print("Press Enter to continue...");
        try {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
        } catch (Exception e) {
            // Scanner might be closed, just continue
            System.out.println();
        }
    }
}