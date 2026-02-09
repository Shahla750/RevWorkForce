package com.revwf.view;

import com.revwf.model.Employee;
import com.revwf.model.Goal;
import com.revwf.model.Holiday;
import com.revwf.model.LeaveApplication;
import com.revwf.model.LeaveBalance;
import com.revwf.model.LeaveType;
import com.revwf.model.Notification;
import com.revwf.model.PerformanceReview;
import com.revwf.service.AuthService;
import com.revwf.service.EmployeeService;
import com.revwf.service.LeaveService;
import com.revwf.service.NotificationService;
import com.revwf.service.PerformanceService;
import com.revwf.util.InputValidator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.List;

public class EmployeeDashboard {
    private final Scanner scanner;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final NotificationService notificationService;
    private final PerformanceService performanceService;

    public EmployeeDashboard(AuthService authService) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.employeeService = new EmployeeService();
        this.leaveService = new LeaveService();
        this.notificationService = new NotificationService();
        this.performanceService = new PerformanceService();
    }

    public void show() {
        while (true) {
            displayMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 8)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-8.");
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
        System.out.println("EMPLOYEE DASHBOARD");
        System.out.println("User: " + authService.getCurrentUser().getEmployeeId());
        System.out.println("=".repeat(50));
        System.out.println("1. View Profile");
        System.out.println("2. Leave Management");
        System.out.println("3. Performance Management");
        System.out.println("4. View Company Calendar");
        System.out.println("5. Employee Directory");
        System.out.println("6. Notifications");
        System.out.println("7. Company Announcements");
        System.out.println("8. Back to Main Menu");
        System.out.println("=".repeat(50));
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                showLeaveManagement();
                break;
            case 3:
                showPerformanceManagement();
                break;
            case 4:
                viewCompanyCalendar();
                break;
            case 5:
                viewEmployeeDirectory();
                break;
            case 6:
                viewNotifications();
                break;
            case 7:
                viewAnnouncements();
                break;
            case 8:
                return true; // Back to main menu
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    private void viewProfile() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("**EMPLOYEE PROFILE**");
        System.out.println("=".repeat(40));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            System.out.println("👤 Personal Information :");
            System.out.println("=".repeat(25));
            System.out.println("Employee ID: " + authService.getCurrentUser().getEmployeeId());
            System.out.println("Name: " + currentEmployee.getFullName());
            System.out.println("Email: " + authService.getCurrentUser().getEmail());
            System.out.println("Phone: " + (currentEmployee.getPhone() != null ? currentEmployee.getPhone() : "N/A"));
            System.out.println("Role: " + authService.getCurrentUser().getRole().getDisplayName());

            System.out.println("\n🏢 Employment Details:");
            System.out.println("=".repeat(25));
            System.out.println("Department: "
                    + (currentEmployee.getDepartmentName() != null ? currentEmployee.getDepartmentName() : "N/A"));
            System.out.println("Designation: "
                    + (currentEmployee.getDesignationName() != null ? currentEmployee.getDesignationName() : "N/A"));
            System.out.println("Joining Date: "
                    + (currentEmployee.getJoiningDate() != null ? currentEmployee.getJoiningDate().toString() : "N/A"));
            System.out
                    .println("Manager: " + (currentEmployee.getManagerName() != null ? currentEmployee.getManagerName()
                            : "No manager assigned"));

            System.out.println("\n📊 Quick Stats:");
            System.out.println("=".repeat(15));

            // Get leave statistics
            List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(currentEmployee.getEmpId());
            int totalLeaveBalance = balances.stream().mapToInt(LeaveBalance::getRemainingDays).sum();

            List<LeaveApplication> applications = leaveService.getEmployeeLeaveApplications(currentEmployee.getEmpId());
            long pendingApplications = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.PENDING).count();

            System.out.println("Available Leave Days: " + totalLeaveBalance);
            System.out.println("Pending Applications: " + pendingApplications);
            System.out.println("Total Applications: " + applications.size());

        } catch (Exception e) {
            System.out.println("❌ Error retrieving profile: " + e.getMessage());
        }

        waitForEnter();
    }

    private void showLeaveManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("**LEAVE MANAGEMENT**");
            System.out.println("=".repeat(40));
            System.out.println("1. View Leave Balance");
            System.out.println("2. Apply for Leave");
            System.out.println("3. View My Leave Applications");
            System.out.println("4. Cancel Pending Application");
            System.out.println("5. View Company Holidays");
            System.out.println("6. View Manager Details");
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
                viewLeaveBalance();
                break;
            case 2:
                applyForLeave();
                break;
            case 3:
                viewMyLeaveApplications();
                break;
            case 4:
                cancelPendingApplication();
                break;
            case 5:
                viewCompanyHolidays();
                break;
            case 6:
                viewManagerDetails();
                break;
            case 7:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    // =============== LEAVE MANAGEMENT METHODS ===============

    private void viewLeaveBalance() {
        System.out.println("\n📊 MY LEAVE BALANCE");
        System.out.println("=".repeat(40));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(currentEmployee.getEmpId());

            if (balances.isEmpty()) {
                System.out.println("❌ No leave balances found.");
                System.out.println("💡 Please contact HR to assign leave quotas.");
                return;
            }

            System.out.printf("Employee: %s%n", currentEmployee.getFullName());
            System.out.printf("Department: %s%n",
                    currentEmployee.getDepartmentName() != null ? currentEmployee.getDepartmentName() : "N/A");
            System.out.printf("Year: %d%n%n", LocalDate.now().getYear());

            System.out.printf("%-15s | %-10s | %-8s | %-10s%n", "Leave Type", "Allocated", "Used", "Available");
            System.out.println("=".repeat(50));

            int totalAllocated = 0, totalUsed = 0, totalAvailable = 0;

            for (LeaveBalance balance : balances) {
                System.out.printf("%-15s | %-10d | %-8d | %-10d%n",
                        balance.getLeaveTypeName(),
                        balance.getAllocatedDays(),
                        balance.getUsedDays(),
                        balance.getRemainingDays());

                totalAllocated += balance.getAllocatedDays();
                totalUsed += balance.getUsedDays();
                totalAvailable += balance.getRemainingDays();
            }

            System.out.println("=".repeat(50));
            System.out.printf("%-15s | %-10d | %-8d | %-10d%n", "TOTAL", totalAllocated, totalUsed, totalAvailable);

            // Show leave usage statistics
            double usagePercent = totalAllocated > 0 ? (double) totalUsed / totalAllocated * 100 : 0;
            System.out.printf("%nLeave Usage: %.1f%% (%d/%d days)%n", usagePercent, totalUsed, totalAllocated);

        } catch (Exception e) {
            System.out.println("❌ Error retrieving leave balance: " + e.getMessage());
        }

        waitForEnter();
    }

    private void applyForLeave() {
        System.out.println("\n📝 APPLY FOR LEAVE");
        System.out.println("=".repeat(30));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            // Show current leave balances
            List<LeaveBalance> balances = leaveService.getEmployeeLeaveBalances(currentEmployee.getEmpId());
            if (balances.isEmpty()) {
                System.out.println("❌ No leave balances found. Please contact HR.");
                return;
            }

            System.out.println("📊 Your Current Leave Balance:");
            System.out.printf("%-15s | %-10s%n", "Leave Type", "Available");
            System.out.println("=".repeat(30));
            for (LeaveBalance balance : balances) {
                System.out.printf("%-15s | %-10d%n", balance.getLeaveTypeName(), balance.getRemainingDays());
            }
            System.out.println("=".repeat(30));

            // Select leave type
            System.out.println("\nSelect Leave Type:");
            List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();
            for (int i = 0; i < leaveTypes.size(); i++) {
                LeaveType lt = leaveTypes.get(i);
                LeaveBalance balance = balances.stream()
                        .filter(b -> b.getLeaveTypeId() == lt.getLeaveTypeId())
                        .findFirst().orElse(null);

                int available = balance != null ? balance.getRemainingDays() : 0;
                System.out.printf("%d. %s (%d days available)%n", i + 1, lt.getLeaveTypeName(), available);
            }

            System.out.print("Enter choice (1-" + leaveTypes.size() + "): ");
            String leaveTypeChoice = scanner.nextLine().trim();

            int leaveTypeIndex;
            try {
                leaveTypeIndex = Integer.parseInt(leaveTypeChoice) - 1;
                if (leaveTypeIndex < 0 || leaveTypeIndex >= leaveTypes.size()) {
                    System.out.println("❌ Invalid choice!");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input!");
                return;
            }

            LeaveType selectedLeaveType = leaveTypes.get(leaveTypeIndex);

            // Check available balance
            LeaveBalance selectedBalance = balances.stream()
                    .filter(b -> b.getLeaveTypeId() == selectedLeaveType.getLeaveTypeId())
                    .findFirst().orElse(null);

            if (selectedBalance == null || selectedBalance.getRemainingDays() <= 0) {
                System.out.println("❌ No available balance for " + selectedLeaveType.getLeaveTypeName());
                return;
            }

            // Get leave dates
            System.out.print("Start Date (YYYY-MM-DD): ");
            String startDateStr = scanner.nextLine().trim();
            if (!InputValidator.isValidDate(startDateStr)) {
                System.out.println("❌ Invalid date format!");
                return;
            }
            LocalDate startDate = LocalDate.parse(startDateStr);

            System.out.print("End Date (YYYY-MM-DD): ");
            String endDateStr = scanner.nextLine().trim();
            if (!InputValidator.isValidDate(endDateStr)) {
                System.out.println("❌ Invalid date format!");
                return;
            }
            LocalDate endDate = LocalDate.parse(endDateStr);

            // Validate dates
            if (startDate.isAfter(endDate)) {
                System.out.println("❌ Start date cannot be after end date!");
                return;
            }

            if (startDate.isBefore(LocalDate.now())) {
                System.out.println("❌ Cannot apply for past dates!");
                return;
            }

            // Calculate total days
            int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

            if (totalDays > selectedBalance.getRemainingDays()) {
                System.out.printf("❌ Insufficient balance! Requested: %d days, Available: %d days%n",
                        totalDays, selectedBalance.getRemainingDays());
                return;
            }

            // Get reason
            System.out.print("Reason for leave: ");
            String reason = scanner.nextLine().trim();
            if (reason.isEmpty()) {
                System.out.println("❌ Reason is required!");
                return;
            }

            // Show manager details
            if (currentEmployee.getManagerName() != null) {
                System.out.println("\n👤 Your Manager: " + currentEmployee.getManagerName());
                System.out.println("📧 Your application will be sent to your manager for approval.");
            }

            // Confirm application
            System.out.println("\n📋 Leave Application Summary:");
            System.out.println("Leave Type: " + selectedLeaveType.getLeaveTypeName());
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            System.out.println("Total Days: " + totalDays);
            System.out.println("Reason: " + reason);

            System.out.print("\n✅ Submit application? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                LeaveService.LeaveApplicationResult result = leaveService.applyForLeave(
                        currentEmployee.getEmpId(),
                        selectedLeaveType.getLeaveTypeId(),
                        startDate,
                        endDate,
                        reason);

                if (result.success) {
                    System.out.println("✅ " + result.message);
                    System.out.println("📧 Your manager will be notified for approval.");
                } else {
                    System.out.println("❌ " + result.message);
                }
            } else {
                System.out.println("❌ Application cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error applying for leave: " + e.getMessage());
        }

        waitForEnter();
    }

    private void viewMyLeaveApplications() {
        System.out.println("\n📋 MY LEAVE APPLICATIONS");
        System.out.println("=".repeat(60));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            List<LeaveApplication> applications = leaveService.getEmployeeLeaveApplications(currentEmployee.getEmpId());

            if (applications.isEmpty()) {
                System.out.println("📝 No leave applications found.");
                System.out.println("💡 You can apply for leave using 'Apply for Leave' option.");
                return;
            }

            System.out.printf("%-6s | %-12s | %-10s | %-10s | %-8s | %-10s%n",
                    "App ID", "Leave Type", "Start Date", "End Date", "Days", "Status");
            System.out.println("=".repeat(60));

            for (LeaveApplication app : applications) {
                String status = app.getStatus().toString();
                String statusIcon = getStatusIcon(app.getStatus());

                System.out.printf("%-6d | %-12s | %-10s | %-10s | %-8d | %s %s%n",
                        app.getApplicationId(),
                        app.getLeaveTypeName() != null ? app.getLeaveTypeName() : "N/A",
                        app.getStartDate(),
                        app.getEndDate(),
                        app.getTotalDays(),
                        statusIcon,
                        status);
            }

            System.out.println("=".repeat(60));

            // Show statistics
            long pendingCount = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.PENDING).count();
            long approvedCount = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.APPROVED).count();
            long rejectedCount = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.REJECTED).count();

            System.out.printf("Total: %d | Pending: %d | Approved: %d | Rejected: %d%n",
                    applications.size(), pendingCount, approvedCount, rejectedCount);

            // Show manager comments for recent applications
            System.out.println("\n💬 Recent Manager Comments:");
            applications.stream()
                    .filter(app -> app.getManagerComments() != null && !app.getManagerComments().isEmpty())
                    .limit(3)
                    .forEach(app -> {
                        System.out.printf("App %d (%s): %s%n",
                                app.getApplicationId(),
                                app.getStatus(),
                                app.getManagerComments());
                    });

        } catch (Exception e) {
            System.out.println("❌ Error retrieving leave applications: " + e.getMessage());
        }

        waitForEnter();
    }

    private void cancelPendingApplication() {
        System.out.println("\n🚫 CANCEL PENDING APPLICATION");
        System.out.println("=".repeat(40));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            List<LeaveApplication> applications = leaveService.getEmployeeLeaveApplications(currentEmployee.getEmpId());
            List<LeaveApplication> pendingApps = applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.PENDING)
                    .collect(java.util.stream.Collectors.toList());

            if (pendingApps.isEmpty()) {
                System.out.println("📝 No pending applications found to cancel.");
                return;
            }

            System.out.println("Pending Applications:");
            System.out.printf("%-6s | %-12s | %-10s | %-10s | %-8s%n",
                    "App ID", "Leave Type", "Start Date", "End Date", "Days");
            System.out.println("=".repeat(50));

            for (LeaveApplication app : pendingApps) {
                System.out.printf("%-6d | %-12s | %-10s | %-10s | %-8d%n",
                        app.getApplicationId(),
                        app.getLeaveTypeName() != null ? app.getLeaveTypeName() : "N/A",
                        app.getStartDate(),
                        app.getEndDate(),
                        app.getTotalDays());
            }

            System.out.print("\nEnter Application ID to cancel: ");
            String appIdStr = scanner.nextLine().trim();

            try {
                int appId = Integer.parseInt(appIdStr);

                LeaveApplication selectedApp = pendingApps.stream()
                        .filter(app -> app.getApplicationId() == appId)
                        .findFirst().orElse(null);

                if (selectedApp == null) {
                    System.out.println("❌ Invalid Application ID or application is not pending!");
                    return;
                }

                System.out.println("\n📋 Application to Cancel:");
                System.out.println("Leave Type: " + selectedApp.getLeaveTypeName());
                System.out.println("Period: " + selectedApp.getStartDate() + " to " + selectedApp.getEndDate());
                System.out.println("Days: " + selectedApp.getTotalDays());
                System.out.println("Reason: " + selectedApp.getReason());

                System.out.print("\n⚠️ Are you sure you want to cancel this application? (y/N): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if ("y".equals(confirm) || "yes".equals(confirm)) {
                    boolean success = leaveService.cancelLeaveApplication(appId, currentEmployee.getEmpId());

                    if (success) {
                        System.out.println("✅ Leave application cancelled successfully!");
                    } else {
                        System.out.println("❌ Failed to cancel leave application.");
                    }
                } else {
                    System.out.println("❌ Cancellation aborted.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid Application ID format!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error cancelling application: " + e.getMessage());
        }

        waitForEnter();
    }

    private void viewCompanyHolidays() {
        System.out.println("\n🎉 COMPANY HOLIDAYS");
        System.out.println("=".repeat(30));
        System.out.println("📅 Company Holiday Calendar for " + LocalDate.now().getYear());
        System.out.println("=".repeat(40));

        // Sample holidays - this should come from database
        System.out.println("📅 January 1    - New Year's Day");
        System.out.println("🇮🇳 January 26   - Republic Day");
        System.out.println("🕉️ March 8      - Holi");
        System.out.println("✝️ March 29     - Good Friday");
        System.out.println("🇮🇳 August 15    - Independence Day");
        System.out.println("🕉️ August 19    - Janmashtami");
        System.out.println("🇮🇳 October 2    - Gandhi Jayanti");
        System.out.println("🪔 October 31   - Diwali");
        System.out.println("🎄 December 25  - Christmas Day");

        System.out.println("=".repeat(40));
        System.out.println("💡 Note: Holiday calendar will be implemented with database integration.");

        waitForEnter();
    }

    private void viewManagerDetails() {
        System.out.println("\n👤 MANAGER DETAILS");
        System.out.println("=".repeat(30));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            if (currentEmployee.getManagerId() == null) {
                System.out.println("📝 No manager assigned.");
                System.out.println("💡 Please contact HR for manager assignment.");
                return;
            }

            Employee manager = employeeService.getEmployeeById(currentEmployee.getManagerId());
            if (manager == null) {
                System.out.println("❌ Manager information not found.");
                return;
            }

            System.out.println("📋 Your Reporting Manager:");
            System.out.println("=".repeat(35));
            System.out.println("Name: " + manager.getFullName());
            System.out.println(
                    "Department: " + (manager.getDepartmentName() != null ? manager.getDepartmentName() : "N/A"));
            System.out.println(
                    "Designation: " + (manager.getDesignationName() != null ? manager.getDesignationName() : "N/A"));
            System.out.println("Phone: " + (manager.getPhone() != null ? manager.getPhone() : "N/A"));
            System.out.println("=".repeat(35));

            System.out.println("\n📧 Leave Application Process:");
            System.out.println("- Your leave applications are sent to " + manager.getFullName());
            System.out.println("- You will receive notifications when applications are reviewed");
            System.out.println("- Contact your manager for any leave-related queries");

        } catch (Exception e) {
            System.out.println("❌ Error retrieving manager details: " + e.getMessage());
        }

        waitForEnter();
    }

    // Helper methods
    private Employee getCurrentEmployee() {
        try {
            return employeeService.getEmployeeByEmployeeId(authService.getCurrentUser().getEmployeeId());
        } catch (Exception e) {
            return null;
        }
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

    private void showPerformanceManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("**PERFORMANCE MANAGEMENT**");
            System.out.println("=".repeat(40));
            System.out.println("1. Create Performance Review");
            System.out.println("2. View Performance Reviews");
            System.out.println("3. Set Goals");
            System.out.println("4. Update Goal Progress");
            System.out.println("5. View Goals");
            System.out.println("6. Back");
            System.out.println("=".repeat(40));

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (!InputValidator.isValidMenuChoice(choice, 6)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-6.");
                continue;
            }

            int option = Integer.parseInt(choice);
            if (handlePerformanceMenuChoice(option)) {
                break;
            }
        }
    }

    private boolean handlePerformanceMenuChoice(int choice) {
        switch (choice) {
            case 1:
                createPerformanceReview();
                break;
            case 2:
                viewPerformanceReviews();
                break;
            case 3:
                setGoals();
                break;
            case 4:
                updateGoalProgress();
                break;
            case 5:
                viewGoals();
                break;
            case 6:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }

    // Performance Management Methods
    private void createPerformanceReview() {
        System.out.println("\n📝 CREATE PERFORMANCE REVIEW");
        System.out.println("=".repeat(45));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            int currentYear = LocalDate.now().getYear();

            System.out.println("📋 Annual Performance Review - " + currentYear);
            System.out.println("Employee: " + currentEmployee.getFullName());
            System.out.println("Department: "
                    + (currentEmployee.getDepartmentName() != null ? currentEmployee.getDepartmentName() : "N/A"));
            System.out.println("=".repeat(45));

            // Create new performance review
            PerformanceReview review = new PerformanceReview(currentEmployee.getEmpId(), currentYear);

            System.out.println("\n1️⃣ Key Deliverables Achieved:");
            System.out.println("(Describe your main accomplishments and deliverables this year)");
            System.out.print("Enter key deliverables: ");
            String keyDeliverables = scanner.nextLine().trim();
            if (keyDeliverables.isEmpty()) {
                System.out.println("❌ Key deliverables cannot be empty!");
                return;
            }
            review.setKeyDeliverables(keyDeliverables);

            System.out.println("\n2️⃣ Major Accomplishments:");
            System.out.println("(Highlight your significant achievements and contributions)");
            System.out.print("Enter major accomplishments: ");
            String accomplishments = scanner.nextLine().trim();
            if (accomplishments.isEmpty()) {
                System.out.println("❌ Major accomplishments cannot be empty!");
                return;
            }
            review.setMajorAccomplishments(accomplishments);

            System.out.println("\n3️⃣ Areas of Improvement:");
            System.out.println("(Identify areas where you can grow and improve)");
            System.out.print("Enter areas of improvement: ");
            String improvements = scanner.nextLine().trim();
            if (improvements.isEmpty()) {
                System.out.println("❌ Areas of improvement cannot be empty!");
                return;
            }
            review.setAreasOfImprovement(improvements);

            System.out.println("\n4️⃣ Self-Assessment Rating:");
            System.out.println("Rate your overall performance (1-5 scale):");
            System.out.println("1 = Poor, 2 = Below Average, 3 = Average, 4 = Good, 5 = Excellent");
            System.out.print("Enter rating (1-5): ");
            String ratingStr = scanner.nextLine().trim();

            try {
                int rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    System.out.println("❌ Rating must be between 1 and 5!");
                    return;
                }
                review.setSelfAssessmentRating(rating);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid rating format!");
                return;
            }

            // Show review summary
            System.out.println("\n📋 Performance Review Summary:");
            System.out.println("=".repeat(35));
            System.out.println("Key Deliverables: " + review.getKeyDeliverables());
            System.out.println("Major Accomplishments: " + review.getMajorAccomplishments());
            System.out.println("Areas of Improvement: " + review.getAreasOfImprovement());
            System.out.println("Self-Assessment Rating: " + review.getSelfAssessmentRating() + "/5");

            System.out.print("\n✅ Save performance review? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                PerformanceService.PerformanceReviewResult result = performanceService.savePerformanceReview(review);
                if (result.success) {
                    System.out.println("✅ " + result.message);

                    System.out.print("Submit for manager review now? (y/N): ");
                    String submitConfirm = scanner.nextLine().trim().toLowerCase();
                    if ("y".equals(submitConfirm) || "yes".equals(submitConfirm)) {
                        PerformanceService.PerformanceReviewResult submitResult = performanceService
                                .submitPerformanceReview(review.getReviewId());
                        System.out.println(
                                submitResult.success ? "✅ " + submitResult.message : "❌ " + submitResult.message);
                    }
                } else {
                    System.out.println("❌ " + result.message);
                }
            } else {
                System.out.println("❌ Performance review cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating performance review: " + e.getMessage());
        }

        waitForEnter();
    }

    private void viewPerformanceReviews() {
        System.out.println("\n📊 MY PERFORMANCE REVIEWS");
        System.out.println("=".repeat(40));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            List<PerformanceReview> reviews = performanceService
                    .getEmployeePerformanceReviews(currentEmployee.getEmpId());

            if (reviews.isEmpty()) {
                System.out.println("📝 No performance reviews found.");
                System.out.println("💡 Create your first performance review using 'Create Performance Review' option.");
                return;
            }

            System.out.printf("%-6s | %-6s | %-12s | %-15s | %-12s%n", "ID", "Year", "Self Rating", "Manager Rating",
                    "Status");
            System.out.println("=".repeat(60));

            for (PerformanceReview review : reviews) {
                String managerRating = review.getManagerRating() > 0 ? String.valueOf(review.getManagerRating())
                        : "Pending";
                String statusIcon = getReviewStatusIcon(review.getStatus());

                System.out.printf("%-6d | %-6d | %-12s | %-15s | %s %-10s%n",
                        review.getReviewId(),
                        review.getReviewYear(),
                        review.getSelfAssessmentRating() + "/5",
                        managerRating,
                        statusIcon,
                        review.getStatus());
            }

            System.out.println("=".repeat(60));
            System.out.println("Total Reviews: " + reviews.size());

            // Show manager feedback for recent reviews
            System.out.println("\n💬 Recent Manager Feedback:");
            reviews.stream()
                    .filter(review -> review.getManagerFeedback() != null && !review.getManagerFeedback().isEmpty())
                    .limit(2)
                    .forEach(review -> {
                        System.out.printf("Review %d (%d): %s%n",
                                review.getReviewId(),
                                review.getReviewYear(),
                                review.getManagerFeedback());
                    });

        } catch (Exception e) {
            System.out.println("❌ Error retrieving performance reviews: " + e.getMessage());
        }

        waitForEnter();
    }

    private void setGoals() {
        System.out.println("\n🎯 SET GOALS");
        System.out.println("=".repeat(30));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            System.out.println("📋 Create New Goal for " + LocalDate.now().getYear());
            System.out.println("Employee: " + currentEmployee.getFullName());
            System.out.println("=".repeat(35));

            System.out.print("Goal Description: ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                System.out.println("❌ Goal description cannot be empty!");
                return;
            }

            System.out.print("Deadline (YYYY-MM-DD): ");
            String deadlineStr = scanner.nextLine().trim();
            if (!InputValidator.isValidDate(deadlineStr)) {
                System.out.println("❌ Invalid date format!");
                return;
            }

            System.out.println("Priority Level:");
            System.out.println("1. High Priority 🔴");
            System.out.println("2. Medium Priority 🟡");
            System.out.println("3. Low Priority 🟢");
            System.out.print("Select priority (1-3): ");
            String priorityStr = scanner.nextLine().trim();

            Goal.Priority priority;
            switch (priorityStr) {
                case "1":
                    priority = Goal.Priority.HIGH;
                    break;
                case "2":
                    priority = Goal.Priority.MEDIUM;
                    break;
                case "3":
                    priority = Goal.Priority.LOW;
                    break;
                default:
                    System.out.println("❌ Invalid priority selection!");
                    return;
            }

            System.out.print("Success Metrics (How will you measure success?): ");
            String successMetrics = scanner.nextLine().trim();
            if (successMetrics.isEmpty()) {
                System.out.println("❌ Success metrics cannot be empty!");
                return;
            }

            // Create goal
            Goal goal = new Goal(currentEmployee.getEmpId(), description, java.sql.Date.valueOf(deadlineStr), priority);
            goal.setSuccessMetrics(successMetrics);

            // Show goal summary
            System.out.println("\n📋 Goal Summary:");
            System.out.println("=".repeat(25));
            System.out.println("Description: " + goal.getGoalDescription());
            System.out.println("Deadline: " + goal.getDeadline());
            System.out.println("Priority: " + goal.getPriorityIcon() + " " + goal.getPriority());
            System.out.println("Success Metrics: " + goal.getSuccessMetrics());

            System.out.print("\n✅ Create goal? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirm) || "yes".equals(confirm)) {
                PerformanceService.GoalResult result = performanceService.createGoal(goal);
                if (result.success) {
                    System.out.println("✅ " + result.message);
                } else {
                    System.out.println("❌ " + result.message);
                }
            } else {
                System.out.println("❌ Goal creation cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating goal: " + e.getMessage());
        }

        waitForEnter();
    }

    private void updateGoalProgress() {
        System.out.println("\n📈 UPDATE GOAL PROGRESS");
        System.out.println("=".repeat(35));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            List<Goal> goals = performanceService.getEmployeeGoals(currentEmployee.getEmpId());

            if (goals.isEmpty()) {
                System.out.println("📝 No goals found.");
                System.out.println("💡 Create your first goal using 'Set Goals' option.");
                return;
            }

            // Show current goals
            System.out.println("Your Goals:");
            System.out.printf("%-4s | %-30s | %-10s | %-8s%n", "ID", "Description", "Priority", "Progress");
            System.out.println("=".repeat(60));

            for (Goal goal : goals) {
                String shortDesc = goal.getGoalDescription().length() > 30
                        ? goal.getGoalDescription().substring(0, 27) + "..."
                        : goal.getGoalDescription();

                System.out.printf("%-4d | %-30s | %s %-7s | %-8s%n",
                        goal.getGoalId(),
                        shortDesc,
                        goal.getPriorityIcon(),
                        goal.getPriority(),
                        goal.getCompletionPercentage() + "%");
            }

            System.out.print("\nEnter Goal ID to update: ");
            String goalIdStr = scanner.nextLine().trim();

            try {
                int goalId = Integer.parseInt(goalIdStr);

                Goal selectedGoal = goals.stream()
                        .filter(goal -> goal.getGoalId() == goalId)
                        .findFirst().orElse(null);

                if (selectedGoal == null) {
                    System.out.println("❌ Invalid Goal ID!");
                    return;
                }

                System.out.println("\n📋 Goal: " + selectedGoal.getGoalDescription());
                System.out.println("Current Progress: " + selectedGoal.getCompletionPercentage() + "%");
                System.out.println("Status: " + selectedGoal.getStatusIcon() + " " + selectedGoal.getStatus());

                System.out.print("New completion percentage (0-100): ");
                String progressStr = scanner.nextLine().trim();

                try {
                    int progress = Integer.parseInt(progressStr);
                    if (progress < 0 || progress > 100) {
                        System.out.println("❌ Progress must be between 0 and 100!");
                        return;
                    }

                    System.out.print("Progress notes (optional): ");
                    String notes = scanner.nextLine().trim();

                    PerformanceService.GoalResult result = performanceService.updateGoalProgress(goalId, progress,
                            notes);
                    if (result.success) {
                        System.out.println("✅ " + result.message);
                    } else {
                        System.out.println("❌ " + result.message);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid progress format!");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid Goal ID format!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating goal progress: " + e.getMessage());
        }

        waitForEnter();
    }

    private void viewGoals() {
        System.out.println("\n🎯 MY GOALS");
        System.out.println("=".repeat(50));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            List<Goal> goals = performanceService.getEmployeeGoals(currentEmployee.getEmpId());

            if (goals.isEmpty()) {
                System.out.println("📝 No goals found.");
                System.out.println("💡 Create your first goal using 'Set Goals' option.");
                return;
            }

            System.out.printf("%-4s | %-25s | %-12s | %-10s | %-8s | %-8s%n",
                    "ID", "Description", "Deadline", "Priority", "Status", "Progress");
            System.out.println("=".repeat(75));

            for (Goal goal : goals) {
                String shortDesc = goal.getGoalDescription().length() > 25
                        ? goal.getGoalDescription().substring(0, 22) + "..."
                        : goal.getGoalDescription();

                System.out.printf("%-4d | %-25s | %-12s | %s %-7s | %s %-6s | %-8s%n",
                        goal.getGoalId(),
                        shortDesc,
                        goal.getDeadline().toString(),
                        goal.getPriorityIcon(),
                        goal.getPriority(),
                        goal.getStatusIcon(),
                        goal.getStatus(),
                        goal.getCompletionPercentage() + "%");
            }

            System.out.println("=".repeat(75));

            // Show goal statistics
            PerformanceService.GoalStatistics stats = performanceService.getGoalStatistics(currentEmployee.getEmpId());
            System.out.println("\n📊 Goal Statistics:");
            System.out.println("=".repeat(25));
            System.out.println("Total Goals: " + stats.totalGoals);
            System.out.println("Completed: " + stats.completedGoals);
            System.out.println("In Progress: " + stats.inProgressGoals);
            System.out.println("Not Started: " + stats.notStartedGoals);
            System.out.printf("Completion Rate: %.1f%%%n", stats.completionRate);

        } catch (Exception e) {
            System.out.println("❌ Error retrieving goals: " + e.getMessage());
        }

        waitForEnter();
    }

    private String getReviewStatusIcon(PerformanceReview.ReviewStatus status) {
        switch (status) {
            case DRAFT:
                return "📝";
            case SUBMITTED:
                return "📤";
            case REVIEWED:
                return "👀";
            case COMPLETED:
                return "✅";
            default:
                return "❓";
        }
    }

    // Other Methods
    private void viewCompanyCalendar() {
        System.out.println("\n📅 COMPANY CALENDAR");
        System.out.println("=".repeat(40));

        try {
            List<Holiday> holidays = employeeService.getAllHolidays();

            System.out.println("🗓️ Company Calendar 2026");
            System.out.println("=".repeat(35));

            if (holidays.isEmpty()) {
                System.out.println("📅 Upcoming Events:");
                System.out.println("=".repeat(20));
                System.out.println("🎉 January 1    - New Year's Day");
                System.out.println("🇮🇳 January 26   - Republic Day");
                System.out.println("🕉️ March 8      - Holi");
                System.out.println("✝️ March 29     - Good Friday");
                System.out.println("🇮🇳 August 15    - Independence Day");
                System.out.println("🕉️ August 19    - Janmashtami");
                System.out.println("🇮🇳 October 2    - Gandhi Jayanti");
                System.out.println("🪔 October 31   - Diwali");
                System.out.println("🎄 December 25  - Christmas Day");
            } else {
                System.out.println("📅 Company Holidays:");
                System.out.println("=".repeat(25));
                for (Holiday holiday : holidays) {
                    String type = holiday.isOptional() ? "Optional" : "Mandatory";
                    String icon = holiday.isOptional() ? "⭐" : "🎉";
                    System.out.printf("%s %s - %s (%s)%n",
                            icon,
                            holiday.getHolidayDate().toString(),
                            holiday.getHolidayName(),
                            type);
                }
            }

            System.out.println("=".repeat(35));
            System.out.println("\n📋 Important Reminders:");
            System.out.println("- Plan your leaves in advance");
            System.out.println("- Check with your manager for approval");
            System.out.println("- Optional holidays require prior approval");

        } catch (Exception e) {
            System.out.println("❌ Error loading company calendar: " + e.getMessage());
        }

        waitForEnter();
    }

    private void viewEmployeeDirectory() {
        System.out.println("\n👥 EMPLOYEE DIRECTORY");
        System.out.println("=".repeat(40));

        try {
            List<Employee> employees = employeeService.getAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No employees found.");
                return;
            }

            System.out.println("🔍 Search Options:");
            System.out.println("1. View All Employees");
            System.out.println("2. Search by Department");
            System.out.println("3. Search by Name");
            System.out.println("4. Back");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showAllEmployees(employees);
                    break;
                case "2":
                    searchByDepartment(employees);
                    break;
                case "3":
                    searchByName(employees);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading employee directory: " + e.getMessage());
        }

        waitForEnter();
    }

    private void showAllEmployees(List<Employee> employees) {
        System.out.println("\n📋 ALL EMPLOYEES");
        System.out.println("=".repeat(60));
        System.out.printf("%-12s | %-20s | %-15s | %-10s%n", "Employee ID", "Name", "Department", "Phone");
        System.out.println("=".repeat(60));

        for (Employee emp : employees) {
            System.out.printf("%-12s | %-20s | %-15s | %-10s%n",
                    "EMP" + String.format("%03d", emp.getEmpId()),
                    emp.getFullName(),
                    emp.getDepartmentName() != null ? emp.getDepartmentName() : "N/A",
                    emp.getPhone() != null ? emp.getPhone() : "N/A");
        }

        System.out.println("=".repeat(60));
        System.out.println("Total Employees: " + employees.size());
    }

    private void searchByDepartment(List<Employee> employees) {
        System.out.print("Enter department name: ");
        String deptName = scanner.nextLine().trim();

        List<Employee> filtered = employees.stream()
                .filter(emp -> emp.getDepartmentName() != null &&
                        emp.getDepartmentName().toLowerCase().contains(deptName.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No employees found in department: " + deptName);
        } else {
            System.out.println("\n👥 Employees in " + deptName + ":");
            showAllEmployees(filtered);
        }
    }

    private void searchByName(List<Employee> employees) {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();

        List<Employee> filtered = employees.stream()
                .filter(emp -> emp.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No employees found with name: " + name);
        } else {
            System.out.println("\n🔍 Search Results for '" + name + "':");
            showAllEmployees(filtered);
        }
    }

    private void viewNotifications() {
        System.out.println("\n🔔 NOTIFICATIONS");
        System.out.println("=".repeat(40));

        try {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                System.out.println("❌ Unable to retrieve employee information.");
                return;
            }

            // Get real notifications from database
            List<Notification> notifications = notificationService.getEmployeeNotifications(currentEmployee.getEmpId());
            int unreadCount = notificationService.getUnreadNotificationCount(currentEmployee.getEmpId());

            System.out.println("📬 Your Notifications (" + unreadCount + " unread):");
            System.out.println("=".repeat(40));

            if (notifications.isEmpty()) {
                System.out.println("📭 No notifications found.");

                // Option to create sample notifications for testing
                System.out.print("\n🧪 Generate sample notifications for testing? (y/N): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if ("y".equals(choice) || "yes".equals(choice)) {
                    if (notificationService.createSampleNotifications(currentEmployee.getEmpId())) {
                        System.out.println("✅ Sample notifications created! Check notifications again.");
                    } else {
                        System.out.println("❌ Failed to create sample notifications.");
                    }
                }

                // Show sample notifications for demonstration
                System.out.println("\n🔔 System Notifications:");
                System.out.println("- Welcome to RevWorkForce HRM System!");
                System.out.println("- Remember to update your profile information");
                System.out.println("- Check company calendar for upcoming holidays");
            } else {
                for (Notification notification : notifications) {
                    String status = notification.isRead() ? "📖" : "📩";
                    String typeIcon = getNotificationTypeIcon(notification.getType());

                    System.out.printf("%s %s %s%n", status, typeIcon, notification.getTitle());
                    System.out.printf("   %s%n", notification.getMessage());
                    System.out.printf("   📅 %s%n%n", notification.getCreatedAt().toString());
                }

                // Option to mark all as read
                System.out.print("Mark all notifications as read? (y/N): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if ("y".equals(choice) || "yes".equals(choice)) {
                    if (notificationService.markAllNotificationsAsRead(currentEmployee.getEmpId())) {
                        System.out.println("✅ All notifications marked as read!");
                    }
                }
            }

            System.out.println("=".repeat(40));
            System.out.println("💡 Tip: Check notifications regularly for important updates");

        } catch (Exception e) {
            System.out.println("❌ Error loading notifications: " + e.getMessage());
        }

        waitForEnter();
    }

    private String getNotificationTypeIcon(String type) {
        switch (type) {
            case "LEAVE_APPROVAL":
                return "✅";
            case "LEAVE_REJECTION":
                return "❌";
            case "PERFORMANCE_REVIEW":
                return "📊";
            case "BIRTHDAY_REMINDER":
                return "🎂";
            case "WORK_ANNIVERSARY":
                return "🎉";
            case "SYSTEM_ANNOUNCEMENT":
                return "📢";
            default:
                return "📋";
        }
    }

    private void viewAnnouncements() {
        System.out.println("\n📢 COMPANY ANNOUNCEMENTS");
        System.out.println("=".repeat(40));

        System.out.println("🎉 Latest Announcements:");
        System.out.println("=".repeat(25));

        System.out.println("📅 February 1, 2026");
        System.out.println("🎊 Welcome to RevWorkForce HRM System!");
        System.out.println("   We're excited to launch our new HR management platform.");
        System.out.println("   All employees can now manage leaves, view profiles, and more!");

        System.out.println("\n📅 January 15, 2026");
        System.out.println("🏖️ Summer Leave Policy Update");
        System.out.println("   Please plan your summer vacations in advance.");
        System.out.println("   Submit leave applications at least 2 weeks prior.");

        System.out.println("\n📅 January 1, 2026");
        System.out.println("🎆 Happy New Year 2026!");
        System.out.println("   Wishing all employees a prosperous and successful year ahead.");
        System.out.println("   Let's achieve great things together!");

        System.out.println("\n📅 December 20, 2025");
        System.out.println("🎄 Holiday Schedule");
        System.out.println("   Please check the company calendar for holiday dates.");
        System.out.println("   Office will be closed on all national holidays.");

        System.out.println("\n📅 December 1, 2025");
        System.out.println("📊 Performance Review Cycle");
        System.out.println("   Annual performance reviews will begin in Q4.");
        System.out.println("   Please prepare your self-assessment documents.");

        System.out.println("=".repeat(40));
        System.out.println("💡 Stay updated with company news and policies!");

        waitForEnter();
    }

    private void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}