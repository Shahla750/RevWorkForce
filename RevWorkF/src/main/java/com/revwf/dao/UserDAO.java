package com.revwf.dao;

import com.revwf.config.DatabaseConfig;
import com.revwf.model.User;
import com.revwf.model.UserRole;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);
    private final DatabaseConfig dbConfig;

    public UserDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Authenticate user by employee ID and password
     */
    public User authenticate(String employeeId, String password) {
        String sql = "SELECT * FROM users WHERE employee_id = ? AND password_hash = ? AND is_active = 1";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.setString(2, password); // In production, hash the password

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.info("User authenticated successfully: " + employeeId);
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Error authenticating user: " + e.getMessage());
        }

        logger.warn("Authentication failed for user: " + employeeId);
        return null;
    }

    /**
     * Create a new user
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (user_id, employee_id, email, password_hash, role, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Get next user ID
            int nextUserId = getNextUserId();

            stmt.setInt(1, nextUserId);
            stmt.setString(2, user.getEmployeeId());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole().name());
            stmt.setInt(6, user.isActive() ? 1 : 0);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                user.setUserId(nextUserId);
                logger.info("User created successfully: " + user.getEmployeeId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, role = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getRole().name());
            stmt.setInt(3, user.isActive() ? 1 : 0);
            stmt.setInt(4, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User updated successfully: " + user.getEmployeeId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Change user password
     */
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword); // In production, hash the password
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Password changed successfully for user ID: " + userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error changing password: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get user by employee ID
     */
    public User getUserByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM users WHERE employee_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user by employee ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY employee_id";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Deactivate user account
     */
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = 0, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User deactivated successfully: " + userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deactivating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if employee ID exists
     */
    public boolean employeeIdExists(String employeeId) {
        String sql = "SELECT COUNT(*) FROM users WHERE employee_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking employee ID existence: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get next user ID
     */
    private int getNextUserId() throws SQLException {
        String sql = "SELECT NVL(MAX(user_id), 0) + 1 FROM users";

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
     * Reactivate user account
     */
    public boolean reactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = 1, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User reactivated successfully: " + userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error reactivating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmployeeId(rs.getString("employee_id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getInt("is_active") == 1);
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}