package com.revwf.dao;

import com.revwf.config.DatabaseConfig;
import com.revwf.model.LeaveApplication;
import com.revwf.model.LeaveBalance;
import com.revwf.model.LeaveType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {
    private static final Logger logger = LogManager.getLogger(LeaveDAO.class);
    private final DatabaseConfig dbConfig;
    
    public LeaveDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Get leave balances for an employee
     */
    public List<LeaveBalance> getLeaveBalancesByEmployeeId(int empId, int year) {
        List<LeaveBalance> balances = new ArrayList<>();
        String sql = "SELECT lb.*, lt.leave_type_name " +
                    "FROM leave_balances lb " +
                    "JOIN leave_types lt ON lb.leave_type_id = lt.leave_type_id " +
                    "WHERE lb.emp_id = ? AND lb.year = ? " +
                    "ORDER BY lt.leave_type_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, empId);
            stmt.setInt(2, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveBalance balance = mapResultSetToLeaveBalance(rs);
                    balances.add(balance);
                }
            }
            
            logger.info("Retrieved {} leave balances for employee ID: {}", balances.size(), empId);
        } catch (SQLException e) {
            logger.error("Error getting leave balances: " + e.getMessage());
        }
        
        return balances;
    }
    
    /**
     * Apply for leave
     */
    public boolean applyForLeave(LeaveApplication application) {
        String sql = "INSERT INTO leave_applications (application_id, emp_id, leave_type_id, " +
                    "start_date, end_date, total_days, reason, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING')";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int nextAppId = getNextApplicationId();
            
            stmt.setInt(1, nextAppId);
            stmt.setInt(2, application.getEmpId());
            stmt.setInt(3, application.getLeaveTypeId());
            stmt.setDate(4, application.getStartDate());
            stmt.setDate(5, application.getEndDate());
            stmt.setInt(6, application.getTotalDays());
            stmt.setString(7, application.getReason());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                application.setApplicationId(nextAppId);
                logger.info("Leave application created successfully with ID: " + nextAppId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error applying for leave: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get all leave applications (for admin analytics)
     */
    public List<LeaveApplication> getAllLeaveApplications() {
        List<LeaveApplication> applications = new ArrayList<>();
        String sql = "SELECT la.*, lt.leave_type_name, " +
                    "e.first_name || ' ' || e.last_name as employee_name, " +
                    "mgr.first_name || ' ' || mgr.last_name as approved_by_name " +
                    "FROM leave_applications la " +
                    "JOIN leave_types lt ON la.leave_type_id = lt.leave_type_id " +
                    "JOIN employees e ON la.emp_id = e.emp_id " +
                    "LEFT JOIN employees mgr ON la.approved_by = mgr.emp_id " +
                    "ORDER BY la.applied_date DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LeaveApplication app = mapResultSetToLeaveApplication(rs);
                applications.add(app);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting all leave applications: {}", e.getMessage());
            throw new RuntimeException("Database error while retrieving leave applications", e);
        }
        
        return applications;
    }

    /**
     * Get leave applications by employee ID
     */
    public List<LeaveApplication> getLeaveApplicationsByEmployeeId(int empId) {
        List<LeaveApplication> applications = new ArrayList<>();
        String sql = "SELECT la.*, lt.leave_type_name, " +
                    "e.first_name || ' ' || e.last_name as employee_name, " +
                    "m.first_name || ' ' || m.last_name as approved_by_name " +
                    "FROM leave_applications la " +
                    "JOIN leave_types lt ON la.leave_type_id = lt.leave_type_id " +
                    "JOIN employees e ON la.emp_id = e.emp_id " +
                    "LEFT JOIN employees m ON la.approved_by = m.emp_id " +
                    "WHERE la.emp_id = ? " +
                    "ORDER BY la.applied_date DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, empId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveApplication application = mapResultSetToLeaveApplication(rs);
                    applications.add(application);
                }
            }
            
            logger.info("Retrieved {} leave applications for employee ID: {}", applications.size(), empId);
        } catch (SQLException e) {
            logger.error("Error getting leave applications: " + e.getMessage());
        }
        
        return applications;
    }

    /**
     * Get pending leave applications for manager approval
     */
    public List<LeaveApplication> getPendingLeaveApplicationsForManager(int managerId) {
        List<LeaveApplication> applications = new ArrayList<>();
        String sql = "SELECT la.*, lt.leave_type_name, " +
                    "e.first_name || ' ' || e.last_name as employee_name " +
                    "FROM leave_applications la " +
                    "JOIN leave_types lt ON la.leave_type_id = lt.leave_type_id " +
                    "JOIN employees e ON la.emp_id = e.emp_id " +
                    "WHERE e.manager_id = ? AND la.status = 'PENDING' " +
                    "ORDER BY la.applied_date ASC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, managerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveApplication application = mapResultSetToLeaveApplication(rs);
                    applications.add(application);
                }
            }
            
            logger.info("Retrieved {} pending leave applications for manager ID: {}", applications.size(), managerId);
        } catch (SQLException e) {
            logger.error("Error getting pending leave applications: " + e.getMessage());
        }
        
        return applications;
    }
    
    /**
     * Approve or reject leave application
     */
    public boolean updateLeaveApplicationStatus(int applicationId, LeaveApplication.LeaveStatus status, 
                                              String managerComments, int approvedBy) {
        String sql = "UPDATE leave_applications SET status = ?, manager_comments = ?, " +
                    "approved_by = ?, approved_date = CURRENT_TIMESTAMP " +
                    "WHERE application_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setString(2, managerComments);
            stmt.setInt(3, approvedBy);
            stmt.setInt(4, applicationId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Leave application {} updated to status: {}", applicationId, status);
                
                // If approved, update leave balance
                if (status == LeaveApplication.LeaveStatus.APPROVED) {
                    updateLeaveBalanceAfterApproval(applicationId);
                }
                
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating leave application status: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Cancel pending leave application
     */
    public boolean cancelLeaveApplication(int applicationId, int empId) {
        String sql = "UPDATE leave_applications SET status = 'CANCELLED' " +
                    "WHERE application_id = ? AND emp_id = ? AND status = 'PENDING'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, applicationId);
            stmt.setInt(2, empId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Leave application cancelled: " + applicationId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error cancelling leave application: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update leave balance after approval
     */
    private void updateLeaveBalanceAfterApproval(int applicationId) {
        String sql = "UPDATE leave_balances SET used_days = used_days + " +
                    "(SELECT total_days FROM leave_applications WHERE application_id = ?), " +
                    "remaining_days = allocated_days - (used_days + " +
                    "(SELECT total_days FROM leave_applications WHERE application_id = ?)) " +
                    "WHERE emp_id = (SELECT emp_id FROM leave_applications WHERE application_id = ?) " +
                    "AND leave_type_id = (SELECT leave_type_id FROM leave_applications WHERE application_id = ?) " +
                    "AND year = EXTRACT(YEAR FROM SYSDATE)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, applicationId);
            stmt.setInt(2, applicationId);
            stmt.setInt(3, applicationId);
            stmt.setInt(4, applicationId);
            
            stmt.executeUpdate();
            logger.info("Leave balance updated for application: " + applicationId);
        } catch (SQLException e) {
            logger.error("Error updating leave balance: " + e.getMessage());
        }
    }
    
    /**
     * Get next application ID
     */
    private int getNextApplicationId() throws SQLException {
        String sql = "SELECT NVL(MAX(application_id), 0) + 1 FROM leave_applications";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 1;
    }
    
    /**
     * Map ResultSet to LeaveBalance object
     */
    private LeaveBalance mapResultSetToLeaveBalance(ResultSet rs) throws SQLException {
        LeaveBalance balance = new LeaveBalance();
        balance.setBalanceId(rs.getInt("balance_id"));
        balance.setEmpId(rs.getInt("emp_id"));
        balance.setLeaveTypeId(rs.getInt("leave_type_id"));
        balance.setLeaveTypeName(rs.getString("leave_type_name"));
        balance.setAllocatedDays(rs.getInt("allocated_days"));
        balance.setUsedDays(rs.getInt("used_days"));
        balance.setRemainingDays(rs.getInt("remaining_days"));
        balance.setYear(rs.getInt("year"));
        return balance;
    }
    
    /**
     * Map ResultSet to LeaveApplication object
     */
    private LeaveApplication mapResultSetToLeaveApplication(ResultSet rs) throws SQLException {
        LeaveApplication application = new LeaveApplication();
        application.setApplicationId(rs.getInt("application_id"));
        application.setEmpId(rs.getInt("emp_id"));
        application.setLeaveTypeId(rs.getInt("leave_type_id"));
        application.setLeaveTypeName(rs.getString("leave_type_name"));
        application.setStartDate(rs.getDate("start_date"));
        application.setEndDate(rs.getDate("end_date"));
        application.setTotalDays(rs.getInt("total_days"));
        application.setReason(rs.getString("reason"));
        application.setStatus(LeaveApplication.LeaveStatus.valueOf(rs.getString("status")));
        application.setManagerComments(rs.getString("manager_comments"));
        application.setAppliedDate(rs.getTimestamp("applied_date"));
        application.setApprovedDate(rs.getTimestamp("approved_date"));
        
        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            application.setApprovedBy(approvedBy);
        }
        
        application.setEmployeeName(rs.getString("employee_name"));
        application.setApprovedByName(rs.getString("approved_by_name"));
        
        return application;
    }
    
    // =============== LEAVE TYPE MANAGEMENT METHODS ===============
    
    /**
     * Get all leave types
     */
    public List<LeaveType> getAllLeaveTypes() {
        List<LeaveType> leaveTypes = new ArrayList<>();
        String sql = "SELECT * FROM leave_types ORDER BY leave_type_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LeaveType leaveType = new LeaveType();
                leaveType.setLeaveTypeId(rs.getInt("leave_type_id"));
                leaveType.setLeaveTypeName(rs.getString("leave_type_name"));
                leaveType.setLeaveTypeCode(rs.getString("leave_type_code"));
                leaveType.setMaxDaysPerYear(rs.getInt("max_days_per_year"));
                leaveType.setCreatedAt(rs.getTimestamp("created_at"));
                leaveTypes.add(leaveType);
            }
            
            logger.info("Retrieved {} leave types", leaveTypes.size());
        } catch (SQLException e) {
            logger.error("Error getting leave types: " + e.getMessage());
        }
        
        return leaveTypes;
    }
    
    /**
     * Add new leave type
     */
    public boolean addLeaveType(String leaveTypeName, String leaveTypeCode, int maxDaysPerYear) {
        String sql = "INSERT INTO leave_types (leave_type_id, leave_type_name, leave_type_code, max_days_per_year) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int nextId = getNextLeaveTypeId();
            stmt.setInt(1, nextId);
            stmt.setString(2, leaveTypeName);
            stmt.setString(3, leaveTypeCode.toUpperCase());
            stmt.setInt(4, maxDaysPerYear);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Leave type added successfully: {}", leaveTypeName);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error adding leave type: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update leave type
     */
    public boolean updateLeaveType(int leaveTypeId, String leaveTypeName, String leaveTypeCode, int maxDaysPerYear) {
        String sql = "UPDATE leave_types SET leave_type_name = ?, leave_type_code = ?, max_days_per_year = ? " +
                    "WHERE leave_type_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, leaveTypeName);
            stmt.setString(2, leaveTypeCode.toUpperCase());
            stmt.setInt(3, maxDaysPerYear);
            stmt.setInt(4, leaveTypeId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Leave type updated successfully: {}", leaveTypeName);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating leave type: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get next leave type ID
     */
    private int getNextLeaveTypeId() throws SQLException {
        String sql = "SELECT NVL(MAX(leave_type_id), 0) + 1 FROM leave_types";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 1;
    }
    
    // =============== LEAVE QUOTA MANAGEMENT METHODS ===============
    
    /**
     * Assign leave quotas to all employees
     */
    public boolean assignLeaveQuotasToAllEmployees(int year) {
        // Get all employees
        String getEmployeesSql = "SELECT emp_id FROM employees";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getEmployeesSql)) {
            
            int assignedCount = 0;
            
            try (ResultSet rs = getStmt.executeQuery()) {
                while (rs.next()) {
                    int empId = rs.getInt("emp_id");
                    
                    // Assign quotas to each employee individually
                    if (assignLeaveQuotasToEmployee(empId, year)) {
                        assignedCount++;
                    }
                }
            }
            
            logger.info("Assigned leave quotas to {} employees for year {}", assignedCount, year);
            return assignedCount > 0;
        } catch (SQLException e) {
            logger.error("Error assigning leave quotas to all employees: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Assign leave quotas to specific employee
     */
    public boolean assignLeaveQuotasToEmployee(int empId, int year) {
        // First check if employee already has quotas for this year
        String checkSql = "SELECT COUNT(*) FROM leave_balances WHERE emp_id = ? AND year = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, empId);
            checkStmt.setInt(2, year);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    logger.info("Employee {} already has leave quotas for year {}", empId, year);
                    return false; // Already has quotas
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking existing leave quotas: " + e.getMessage());
            return false;
        }
        
        // Get all leave types and insert balances one by one
        String getLeaveTypesSql = "SELECT leave_type_id, max_days_per_year FROM leave_types ORDER BY leave_type_id";
        String insertSql = "INSERT INTO leave_balances (balance_id, emp_id, leave_type_id, allocated_days, used_days, remaining_days, year) " +
                          "VALUES (?, ?, ?, ?, 0, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getLeaveTypesSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            int insertCount = 0;
            
            try (ResultSet rs = getStmt.executeQuery()) {
                while (rs.next()) {
                    int leaveTypeId = rs.getInt("leave_type_id");
                    int maxDays = rs.getInt("max_days_per_year");
                    
                    // Get next balance ID
                    int nextBalanceId = getNextBalanceId();
                    
                    insertStmt.setInt(1, nextBalanceId);
                    insertStmt.setInt(2, empId);
                    insertStmt.setInt(3, leaveTypeId);
                    insertStmt.setInt(4, maxDays);
                    insertStmt.setInt(5, maxDays); // remaining = allocated initially
                    insertStmt.setInt(6, year);
                    
                    insertStmt.executeUpdate();
                    insertCount++;
                }
            }
            
            logger.info("Assigned leave quotas to employee {} for year {}: {} leave types", empId, year, insertCount);
            return insertCount > 0;
        } catch (SQLException e) {
            logger.error("Error assigning leave quotas to employee: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get next balance ID
     */
    private int getNextBalanceId() throws SQLException {
        String sql = "SELECT NVL(MAX(balance_id), 0) + 1 FROM leave_balances";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 1;
    }
    
    /**
     * Assign leave quotas by department
     */
    public boolean assignLeaveQuotasByDepartment(int deptId, int year) {
        // Get all employees in the department
        String getEmployeesSql = "SELECT emp_id FROM employees WHERE dept_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getEmployeesSql)) {
            
            getStmt.setInt(1, deptId);
            int assignedCount = 0;
            
            try (ResultSet rs = getStmt.executeQuery()) {
                while (rs.next()) {
                    int empId = rs.getInt("emp_id");
                    
                    // Assign quotas to each employee individually
                    if (assignLeaveQuotasToEmployee(empId, year)) {
                        assignedCount++;
                    }
                }
            }
            
            logger.info("Assigned leave quotas to department {} for year {}: {} employees", deptId, year, assignedCount);
            return assignedCount > 0;
        } catch (SQLException e) {
            logger.error("Error assigning leave quotas by department: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update leave balance manually
     */
    public boolean updateLeaveBalance(int empId, int leaveTypeId, int year, int allocatedDays, int usedDays) {
        String sql = "UPDATE leave_balances SET allocated_days = ?, used_days = ?, remaining_days = ? - ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE emp_id = ? AND leave_type_id = ? AND year = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, allocatedDays);
            stmt.setInt(2, usedDays);
            stmt.setInt(3, allocatedDays);
            stmt.setInt(4, usedDays);
            stmt.setInt(5, empId);
            stmt.setInt(6, leaveTypeId);
            stmt.setInt(7, year);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Leave balance updated for employee {} leave type {} year {}", empId, leaveTypeId, year);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating leave balance: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Revoke approved leave application
     */
    public boolean revokeLeaveApplication(int applicationId, String revocationReason, int revokedBy) {
        String sql = "UPDATE leave_applications SET status = 'CANCELLED', manager_comments = ?, approved_by = ?, approved_date = CURRENT_TIMESTAMP " +
                    "WHERE application_id = ? AND status = 'APPROVED'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "REVOKED: " + revocationReason);
            stmt.setInt(2, revokedBy);
            stmt.setInt(3, applicationId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Restore leave balance
                restoreLeaveBalanceAfterRevocation(applicationId);
                logger.info("Leave application revoked: {}", applicationId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error revoking leave application: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Restore leave balance after revocation
     */
    private void restoreLeaveBalanceAfterRevocation(int applicationId) {
        String sql = "UPDATE leave_balances SET used_days = used_days - " +
                    "(SELECT total_days FROM leave_applications WHERE application_id = ?), " +
                    "remaining_days = allocated_days - (used_days - " +
                    "(SELECT total_days FROM leave_applications WHERE application_id = ?)) " +
                    "WHERE emp_id = (SELECT emp_id FROM leave_applications WHERE application_id = ?) " +
                    "AND leave_type_id = (SELECT leave_type_id FROM leave_applications WHERE application_id = ?) " +
                    "AND year = EXTRACT(YEAR FROM (SELECT start_date FROM leave_applications WHERE application_id = ?))";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, applicationId);
            stmt.setInt(2, applicationId);
            stmt.setInt(3, applicationId);
            stmt.setInt(4, applicationId);
            stmt.setInt(5, applicationId);
            
            stmt.executeUpdate();
            logger.info("Leave balance restored for revoked application: " + applicationId);
        } catch (SQLException e) {
            logger.error("Error restoring leave balance: " + e.getMessage());
        }
    }
}