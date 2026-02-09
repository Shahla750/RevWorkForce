package com.revwf.service;

import com.revwf.dao.EmployeeDAO;
import com.revwf.dao.LeaveDAO;
import com.revwf.model.Employee;
import com.revwf.model.LeaveApplication;
import com.revwf.model.LeaveBalance;
import com.revwf.model.LeaveType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LeaveService {
    private static final Logger logger = LogManager.getLogger(LeaveService.class);
    private final LeaveDAO leaveDAO;
    private final EmployeeDAO employeeDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    /**
     * Get leave balances for employee
     */
    public List<LeaveBalance> getEmployeeLeaveBalances(int empId) {
        try {
            int currentYear = LocalDate.now().getYear();
            return leaveDAO.getLeaveBalancesByEmployeeId(empId, currentYear);
        } catch (Exception e) {
            logger.error("Error getting leave balances for employee {}: {}", empId, e.getMessage());
            throw new RuntimeException("Failed to retrieve leave balances", e);
        }
    }

    /**
     * Apply for leave with validation
     */
    public LeaveApplicationResult applyForLeave(int empId, int leaveTypeId, LocalDate startDate,
            LocalDate endDate, String reason) {
        try {
            // Validate dates
            if (startDate.isAfter(endDate)) {
                return new LeaveApplicationResult(false, "Start date cannot be after end date");
            }

            if (startDate.isBefore(LocalDate.now())) {
                return new LeaveApplicationResult(false, "Cannot apply for past dates");
            }

            // Calculate total days
            int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

            // Check leave balance
            List<LeaveBalance> balances = getEmployeeLeaveBalances(empId);
            LeaveBalance applicableBalance = balances.stream()
                    .filter(balance -> balance.getLeaveTypeId() == leaveTypeId)
                    .findFirst()
                    .orElse(null);

            if (applicableBalance == null) {
                return new LeaveApplicationResult(false, "Leave type not found for employee");
            }

            if (applicableBalance.getRemainingDays() < totalDays) {
                return new LeaveApplicationResult(false,
                        String.format("Insufficient leave balance. Available: %d days, Requested: %d days",
                                applicableBalance.getRemainingDays(), totalDays));
            }

            // Create leave application
            LeaveApplication application = new LeaveApplication(empId, leaveTypeId,
                    Date.valueOf(startDate), Date.valueOf(endDate), totalDays, reason);

            boolean applied = leaveDAO.applyForLeave(application);

            if (applied) {
                logger.info("Leave application submitted successfully for employee: {}", empId);
                return new LeaveApplicationResult(true, "Leave application submitted successfully");
            } else {
                return new LeaveApplicationResult(false, "Failed to submit leave application");
            }

        } catch (Exception e) {
            logger.error("Error applying for leave: " + e.getMessage());
            return new LeaveApplicationResult(false, "System error occurred while applying for leave");
        }
    }

    /**
     * Get all leave applications (for admin analytics)
     */
    public List<LeaveApplication> getAllLeaveApplications() {
        try {
            return leaveDAO.getAllLeaveApplications();
        } catch (Exception e) {
            logger.error("Error getting all leave applications: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve leave applications", e);
        }
    }

    /**
     * Get leave applications for employee
     */
    public List<LeaveApplication> getEmployeeLeaveApplications(int empId) {
        try {
            return leaveDAO.getLeaveApplicationsByEmployeeId(empId);
        } catch (Exception e) {
            logger.error("Error getting leave applications for employee {}: {}", empId, e.getMessage());
            throw new RuntimeException("Failed to retrieve leave applications", e);
        }
    }

    /**
     * Get pending leave applications for manager
     */
    public List<LeaveApplication> getPendingLeaveApplicationsForManager(int managerId) {
        try {
            return leaveDAO.getPendingLeaveApplicationsForManager(managerId);
        } catch (Exception e) {
            logger.error("Error getting pending applications for manager {}: {}", managerId, e.getMessage());
            throw new RuntimeException("Failed to retrieve pending leave applications", e);
        }
    }

    /**
     * Approve leave application
     */
    public boolean approveLeaveApplication(int applicationId, String managerComments, int approvedBy) {
        try {
            boolean updated = leaveDAO.updateLeaveApplicationStatus(applicationId,
                    LeaveApplication.LeaveStatus.APPROVED, managerComments, approvedBy);

            if (updated) {
                logger.info("Leave application approved: {} by manager: {}", applicationId, approvedBy);

                // Future enhancement: Send notification to employee
                // This can be implemented when NotificationService is added
            }

            return updated;
        } catch (Exception e) {
            logger.error("Error approving leave application: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reject leave application
     */
    public boolean rejectLeaveApplication(int applicationId, String managerComments, int rejectedBy) {
        try {
            boolean updated = leaveDAO.updateLeaveApplicationStatus(applicationId,
                    LeaveApplication.LeaveStatus.REJECTED, managerComments, rejectedBy);

            if (updated) {
                logger.info("Leave application rejected: {} by manager: {}", applicationId, rejectedBy);

                // Future enhancement: Send notification to employee
                // This can be implemented when NotificationService is added
            }

            return updated;
        } catch (Exception e) {
            logger.error("Error rejecting leave application: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancel pending leave application
     */
    public boolean cancelLeaveApplication(int applicationId, int empId) {
        try {
            return leaveDAO.cancelLeaveApplication(applicationId, empId);
        } catch (Exception e) {
            logger.error("Error cancelling leave application: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get team leave calendar for manager
     */
    public List<LeaveApplication> getTeamLeaveCalendar(int managerId, LocalDate startDate, LocalDate endDate) {
        try {
            // For now, return pending applications (can be extended to show approved leaves
            // in date range)
            // Future enhancement: Filter by date range and include approved leaves
            return leaveDAO.getPendingLeaveApplicationsForManager(managerId);
        } catch (Exception e) {
            logger.error("Error getting team leave calendar: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve team leave calendar", e);
        }
    }

    /**
     * Validate leave application business rules
     */
    public LeaveValidationResult validateLeaveApplication(int empId, int leaveTypeId,
            LocalDate startDate, LocalDate endDate) {
        try {
            // Check if employee exists
            Employee employee = employeeDAO.getEmployeeById(empId);
            if (employee == null) {
                return new LeaveValidationResult(false, "Employee not found");
            }

            // Check date validity
            if (startDate.isAfter(endDate)) {
                return new LeaveValidationResult(false, "Start date cannot be after end date");
            }

            if (startDate.isBefore(LocalDate.now())) {
                return new LeaveValidationResult(false, "Cannot apply for past dates");
            }

            // Check if it's too far in future (e.g., more than 6 months)
            if (startDate.isAfter(LocalDate.now().plusMonths(6))) {
                return new LeaveValidationResult(false, "Cannot apply for leaves more than 6 months in advance");
            }

            // Check weekend policy (optional - can be configured)
            // For now, allowing weekend applications

            return new LeaveValidationResult(true, "Validation successful");

        } catch (Exception e) {
            logger.error("Error validating leave application: " + e.getMessage());
            return new LeaveValidationResult(false, "System error during validation");
        }
    }

    /**
     * Get leave statistics for reporting
     */
    public LeaveStats getLeaveStatistics(int empId) {
        try {
            List<LeaveBalance> balances = getEmployeeLeaveBalances(empId);
            List<LeaveApplication> applications = getEmployeeLeaveApplications(empId);

            LeaveStats stats = new LeaveStats();
            stats.totalAllocated = balances.stream().mapToInt(LeaveBalance::getAllocatedDays).sum();
            stats.totalUsed = balances.stream().mapToInt(LeaveBalance::getUsedDays).sum();
            stats.totalRemaining = balances.stream().mapToInt(LeaveBalance::getRemainingDays).sum();

            stats.pendingApplications = (int) applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.PENDING)
                    .count();

            stats.approvedApplications = (int) applications.stream()
                    .filter(app -> app.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                    .count();

            return stats;
        } catch (Exception e) {
            logger.error("Error getting leave statistics: " + e.getMessage());
            return new LeaveStats();
        }
    }

    /**
     * Result class for leave application
     */
    public static class LeaveApplicationResult {
        public final boolean success;
        public final String message;

        public LeaveApplicationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    /**
     * Result class for leave validation
     */
    public static class LeaveValidationResult {
        public final boolean valid;
        public final String message;

        public LeaveValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
    }

    /**
     * Leave statistics class
     */
    public static class LeaveStats {
        public int totalAllocated;
        public int totalUsed;
        public int totalRemaining;
        public int pendingApplications;
        public int approvedApplications;

        @Override
        public String toString() {
            return "LeaveStats{" +
                    "totalAllocated=" + totalAllocated +
                    ", totalUsed=" + totalUsed +
                    ", totalRemaining=" + totalRemaining +
                    ", pendingApplications=" + pendingApplications +
                    ", approvedApplications=" + approvedApplications +
                    '}';
        }
    }
    
    // =============== LEAVE TYPE MANAGEMENT METHODS ===============
    
    /**
     * Get all leave types
     */
    public List<LeaveType> getAllLeaveTypes() {
        try {
            return leaveDAO.getAllLeaveTypes();
        } catch (Exception e) {
            logger.error("Error getting all leave types: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve leave types", e);
        }
    }
    
    /**
     * Add new leave type
     */
    public boolean addLeaveType(String leaveTypeName, String leaveTypeCode, int maxDaysPerYear) {
        try {
            return leaveDAO.addLeaveType(leaveTypeName, leaveTypeCode, maxDaysPerYear);
        } catch (Exception e) {
            logger.error("Error adding leave type: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Update leave type
     */
    public boolean updateLeaveType(int leaveTypeId, String leaveTypeName, String leaveTypeCode, int maxDaysPerYear) {
        try {
            return leaveDAO.updateLeaveType(leaveTypeId, leaveTypeName, leaveTypeCode, maxDaysPerYear);
        } catch (Exception e) {
            logger.error("Error updating leave type: {}", e.getMessage());
            return false;
        }
    }
    
    // =============== LEAVE QUOTA MANAGEMENT METHODS ===============
    
    /**
     * Assign leave quotas to all employees
     */
    public boolean assignLeaveQuotasToAllEmployees() {
        try {
            int currentYear = LocalDate.now().getYear();
            return leaveDAO.assignLeaveQuotasToAllEmployees(currentYear);
        } catch (Exception e) {
            logger.error("Error assigning leave quotas to all employees: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Assign leave quotas to specific employee
     */
    public boolean assignLeaveQuotasToEmployee(int empId) {
        try {
            int currentYear = LocalDate.now().getYear();
            return leaveDAO.assignLeaveQuotasToEmployee(empId, currentYear);
        } catch (Exception e) {
            logger.error("Error assigning leave quotas to employee {}: {}", empId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Assign leave quotas by department
     */
    public boolean assignLeaveQuotasByDepartment(int deptId) {
        try {
            int currentYear = LocalDate.now().getYear();
            return leaveDAO.assignLeaveQuotasByDepartment(deptId, currentYear);
        } catch (Exception e) {
            logger.error("Error assigning leave quotas by department {}: {}", deptId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Update leave balance manually
     */
    public boolean updateLeaveBalance(int empId, int leaveTypeId, int allocatedDays, int usedDays) {
        try {
            int currentYear = LocalDate.now().getYear();
            return leaveDAO.updateLeaveBalance(empId, leaveTypeId, currentYear, allocatedDays, usedDays);
        } catch (Exception e) {
            logger.error("Error updating leave balance: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Revoke approved leave application
     */
    public boolean revokeLeaveApplication(int applicationId, String revocationReason, int revokedBy) {
        try {
            return leaveDAO.revokeLeaveApplication(applicationId, revocationReason, revokedBy);
        } catch (Exception e) {
            logger.error("Error revoking leave application: {}", e.getMessage());
            return false;
        }
    }
}