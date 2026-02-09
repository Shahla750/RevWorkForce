package com.revwf.view;

import com.revwf.service.AuthService;
import com.revwf.util.InputValidator;

import java.util.Scanner;

public class ManagerDashboard {
    private final Scanner scanner;
    private final AuthService authService;
    
    public ManagerDashboard(AuthService authService) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
    }
    
    public void show() {
        while (true) {
            displayMenu();
            
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();
            
            if (!InputValidator.isValidMenuChoice(choice, 9)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-9.");
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
        System.out.println("MANAGER DASHBOARD");
        System.out.println("User: " + authService.getCurrentUser().getEmployeeId());
        System.out.println("=".repeat(50));
        System.out.println("1. Employee Features (All employee functions)");
        System.out.println("2. Team Leave Management");
        System.out.println("3. Team Performance Management");
        System.out.println("4. View Team Structure");
        System.out.println("5. Team Attendance Summary");
        System.out.println("6. Approve/Reject Leave Requests");
        System.out.println("7. Review Performance Documents");
        System.out.println("8. Team Reports");
        System.out.println("9. Back to Main Menu");
        System.out.println("=".repeat(50));
    }
    
    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                showEmployeeFeatures();
                break;
            case 2:
                showTeamLeaveManagement();
                break;
            case 3:
                showTeamPerformanceManagement();
                break;
            case 4:
                viewTeamStructure();
                break;
            case 5:
                viewTeamAttendance();
                break;
            case 6:
                approveRejectLeaveRequests();
                break;
            case 7:
                reviewPerformanceDocuments();
                break;
            case 8:
                generateTeamReports();
                break;
            case 9:
                return true; // Back to main menu
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }
    
    private void showEmployeeFeatures() {
        System.out.println("\n📝 Note: Managers have access to all employee features");
        new EmployeeDashboard(authService).show();
    }
    
    private void showTeamLeaveManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("TEAM LEAVE MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. View Team Leave Calendar");
            System.out.println("2. View Team Leave Balances");
            System.out.println("3. Pending Leave Requests");
            System.out.println("4. Leave History");
            System.out.println("5. Back");
            System.out.println("=".repeat(40));
            
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();
            
            if (!InputValidator.isValidMenuChoice(choice, 5)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-5.");
                continue;
            }
            
            int option = Integer.parseInt(choice);
            if (handleTeamLeaveMenuChoice(option)) {
                break;
            }
        }
    }
    
    private boolean handleTeamLeaveMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewTeamLeaveCalendar();
                break;
            case 2:
                viewTeamLeaveBalances();
                break;
            case 3:
                viewPendingLeaveRequests();
                break;
            case 4:
                viewLeaveHistory();
                break;
            case 5:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }
    
    private void showTeamPerformanceManagement() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("TEAM PERFORMANCE MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Review Team Performance");
            System.out.println("2. Provide Performance Feedback");
            System.out.println("3. Rate Employee Performance");
            System.out.println("4. Track Team Goals");
            System.out.println("5. Generate Performance Reports");
            System.out.println("6. Back");
            System.out.println("=".repeat(40));
            
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();
            
            if (!InputValidator.isValidMenuChoice(choice, 6)) {
                System.out.println("❌ Invalid choice! Please enter a number between 1-6.");
                continue;
            }
            
            int option = Integer.parseInt(choice);
            if (handleTeamPerformanceMenuChoice(option)) {
                break;
            }
        }
    }
    
    private boolean handleTeamPerformanceMenuChoice(int choice) {
        switch (choice) {
            case 1:
                reviewTeamPerformance();
                break;
            case 2:
                providePerformanceFeedback();
                break;
            case 3:
                rateEmployeePerformance();
                break;
            case 4:
                trackTeamGoals();
                break;
            case 5:
                generatePerformanceReports();
                break;
            case 6:
                return true; // Back
            default:
                System.out.println("❌ Invalid choice!");
        }
        return false;
    }
    
    // Team Leave Management Methods
    private void viewTeamLeaveCalendar() {
        System.out.println("\n📅 TEAM LEAVE CALENDAR");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: Team leave calendar will be implemented");
        System.out.println("Features to include:");
        System.out.println("- Monthly view of team leaves");
        System.out.println("- Color-coded by leave type");
        System.out.println("- Filter by employee");
        waitForEnter();
    }
    
    private void viewTeamLeaveBalances() {
        System.out.println("\n📊 TEAM LEAVE BALANCES");
        System.out.println("=".repeat(35));
        System.out.println("Employee Name    | CL | SL | PL | PR");
        System.out.println("=".repeat(35));
        System.out.println("John Doe         | 8  | 10 | 15 | 5");
        System.out.println("Jane Smith       | 10 | 8  | 18 | 3");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: This will be loaded from database");
        waitForEnter();
    }
    
    private void viewPendingLeaveRequests() {
        System.out.println("\n⏳ PENDING LEAVE REQUESTS");
        System.out.println("=".repeat(40));
        System.out.println("No pending leave requests.");
        System.out.println("📝 Note: This will show all pending requests for approval");
        waitForEnter();
    }
    
    private void viewLeaveHistory() {
        System.out.println("\n📋 TEAM LEAVE HISTORY");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: Leave history will be implemented");
        System.out.println("Features to include:");
        System.out.println("- Filter by date range");
        System.out.println("- Filter by employee");
        System.out.println("- Export to CSV");
        waitForEnter();
    }
    
    // Team Performance Management Methods
    private void reviewTeamPerformance() {
        System.out.println("\n📊 TEAM PERFORMANCE REVIEW");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: Team performance review will be implemented");
        System.out.println("Features to include:");
        System.out.println("- Individual performance summaries");
        System.out.println("- Team performance metrics");
        System.out.println("- Comparison charts");
        waitForEnter();
    }
    
    private void providePerformanceFeedback() {
        System.out.println("\n💬 PROVIDE PERFORMANCE FEEDBACK");
        System.out.println("=".repeat(40));
        System.out.println("📝 Note: Performance feedback form will be implemented");
        System.out.println("Features to include:");
        System.out.println("- Select employee");
        System.out.println("- Detailed feedback form");
        System.out.println("- Rating system");
        waitForEnter();
    }
    
    private void rateEmployeePerformance() {
        System.out.println("\n⭐ RATE EMPLOYEE PERFORMANCE");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: Performance rating system will be implemented");
        System.out.println("Features to include:");
        System.out.println("- 1-5 star rating system");
        System.out.println("- Category-wise ratings");
        System.out.println("- Comments section");
        waitForEnter();
    }
    
    private void trackTeamGoals() {
        System.out.println("\n🎯 TRACK TEAM GOALS");
        System.out.println("=".repeat(30));
        System.out.println("📝 Note: Team goal tracking will be implemented");
        System.out.println("Features to include:");
        System.out.println("- Individual goal progress");
        System.out.println("- Team goal overview");
        System.out.println("- Goal completion statistics");
        waitForEnter();
    }
    
    private void generatePerformanceReports() {
        System.out.println("\n📈 GENERATE PERFORMANCE REPORTS");
        System.out.println("=".repeat(40));
        System.out.println("📝 Note: Performance report generation will be implemented");
        System.out.println("Available reports:");
        System.out.println("- Team performance summary");
        System.out.println("- Individual performance reports");
        System.out.println("- Goal achievement reports");
        waitForEnter();
    }
    
    // Other Manager Methods
    private void viewTeamStructure() {
        System.out.println("\n🏢 TEAM STRUCTURE");
        System.out.println("=".repeat(30));
        System.out.println("Manager: " + authService.getCurrentUser().getEmployeeId());
        System.out.println("├── John Doe (Software Engineer)");
        System.out.println("├── Jane Smith (Senior Software Engineer)");
        System.out.println("└── Mike Johnson (Software Engineer)");
        System.out.println("=".repeat(30));
        System.out.println("📝 Note: This will be loaded from database");
        waitForEnter();
    }
    
    private void viewTeamAttendance() {
        System.out.println("\n📊 TEAM ATTENDANCE SUMMARY");
        System.out.println("=".repeat(40));
        System.out.println("Employee Name    | Present | Absent | Leave");
        System.out.println("=".repeat(40));
        System.out.println("John Doe         | 20      | 2      | 3");
        System.out.println("Jane Smith       | 22      | 1      | 2");
        System.out.println("Mike Johnson     | 21      | 3      | 1");
        System.out.println("=".repeat(40));
        System.out.println("📝 Note: This will be calculated from database");
        waitForEnter();
    }
    
    private void approveRejectLeaveRequests() {
        System.out.println("\n✅❌ APPROVE/REJECT LEAVE REQUESTS");
        System.out.println("=".repeat(45));
        System.out.println("No pending leave requests to review.");
        System.out.println("📝 Note: This will show detailed leave requests");
        System.out.println("Features to include:");
        System.out.println("- View leave details");
        System.out.println("- Add manager comments");
        System.out.println("- Approve/Reject with reasons");
        waitForEnter();
    }
    
    private void reviewPerformanceDocuments() {
        System.out.println("\n📄 REVIEW PERFORMANCE DOCUMENTS");
        System.out.println("=".repeat(45));
        System.out.println("No performance documents to review.");
        System.out.println("📝 Note: This will show submitted performance reviews");
        System.out.println("Features to include:");
        System.out.println("- Review employee submissions");
        System.out.println("- Provide detailed feedback");
        System.out.println("- Rate performance");
        waitForEnter();
    }
    
    private void generateTeamReports() {
        System.out.println("\n📊 GENERATE TEAM REPORTS");
        System.out.println("=".repeat(35));
        System.out.println("Available Reports:");
        System.out.println("1. Team Leave Summary");
        System.out.println("2. Team Performance Summary");
        System.out.println("3. Team Attendance Report");
        System.out.println("4. Goal Achievement Report");
        System.out.println("=".repeat(35));
        System.out.println("📝 Note: Report generation will be implemented");
        waitForEnter();
    }
    
    private void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}