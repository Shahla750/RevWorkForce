package com.revwf.service;

import com.revwf.dao.UserDAO;
import com.revwf.model.User;
import com.revwf.model.UserRole;
import com.revwf.util.PasswordUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private final UserDAO userDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Authenticate user with employee ID and password
     */
    public boolean login(String employeeId, String password) {
        try {
            if (employeeId == null || employeeId.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                logger.warn("Login attempt with empty credentials");
                return false;
            }
            
            // For demo purposes, use simple password verification
            User user = userDAO.authenticate(employeeId.trim(), password);
            
            if (user != null && user.isActive()) {
                this.currentUser = user;
                logger.info("User logged in successfully: " + employeeId);
                return true;
            }
            
            logger.warn("Login failed for user: " + employeeId);
            return false;
            
        } catch (Exception e) {
            logger.error("Error during login: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: " + currentUser.getEmployeeId());
            this.currentUser = null;
        }
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if current user has specific role
     */
    public boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
    
    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }
    
    /**
     * Check if current user is manager
     */
    public boolean isManager() {
        return hasRole(UserRole.MANAGER);
    }
    
    /**
     * Check if current user is employee
     */
    public boolean isEmployee() {
        return hasRole(UserRole.EMPLOYEE);
    }
    
    /**
     * Change password for current user
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            logger.warn("Attempt to change password without being logged in");
            return false;
        }
        
        // Verify current password
        if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
            logger.warn("Invalid current password provided for user: " + currentUser.getEmployeeId());
            return false;
        }
        
        // Validate new password
        if (!PasswordUtil.isValidPassword(newPassword)) {
            logger.warn("Invalid new password format for user: " + currentUser.getEmployeeId());
            return false;
        }
        
        // Hash new password and update
        String hashedNewPassword = PasswordUtil.simpleHash(newPassword);
        boolean success = userDAO.changePassword(currentUser.getUserId(), hashedNewPassword);
        
        if (success) {
            currentUser.setPasswordHash(hashedNewPassword);
            logger.info("Password changed successfully for user: " + currentUser.getEmployeeId());
        }
        
        return success;
    }
    
    /**
     * Register new user (Admin only)
     */
    public boolean registerUser(String employeeId, String email, String password, UserRole role) {
        if (!isAdmin()) {
            logger.warn("Non-admin user attempted to register new user");
            return false;
        }
        
        // Validate input
        if (employeeId == null || employeeId.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            logger.warn("Invalid input provided for user registration");
            return false;
        }
        
        // Check if employee ID already exists
        if (userDAO.employeeIdExists(employeeId)) {
            logger.warn("Employee ID already exists: " + employeeId);
            return false;
        }
        
        // Create new user
        User newUser = new User(employeeId, email, password, role);
        boolean success = userDAO.createUser(newUser);
        
        if (success) {
            logger.info("New user registered successfully: " + employeeId);
        }
        
        return success;
    }
    
    /**
     * Get user session info
     */
    public String getSessionInfo() {
        if (currentUser == null) {
            return "Not logged in";
        }
        
        return String.format("Logged in as: %s (%s)", 
                           currentUser.getEmployeeId(), 
                           currentUser.getRole().getDisplayName());
    }
}