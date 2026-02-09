package com.revwf.dao;

import com.revwf.config.DatabaseConfig;
import com.revwf.model.Notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private static final Logger logger = LogManager.getLogger(NotificationDAO.class);
    private final DatabaseConfig dbConfig;

    public NotificationDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Create a new notification
     */
    public boolean createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (notification_id, emp_id, title, message, type, is_read) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextNotificationId();

            stmt.setInt(1, nextId);
            stmt.setInt(2, notification.getEmpId());
            stmt.setString(3, notification.getTitle());
            stmt.setString(4, notification.getMessage());
            stmt.setString(5, notification.getType());
            stmt.setInt(6, notification.isRead() ? 1 : 0);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Notification created successfully for employee: {}", notification.getEmpId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating notification: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get notifications for employee
     */
    public List<Notification> getNotificationsByEmployeeId(int empId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE emp_id = ? ORDER BY created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = mapResultSetToNotification(rs);
                    notifications.add(notification);
                }
            }

            logger.info("Retrieved {} notifications for employee {}", notifications.size(), empId);
        } catch (SQLException e) {
            logger.error("Error retrieving notifications for employee {}: {}", empId, e.getMessage());
        }

        return notifications;
    }

    /**
     * Get unread notifications count
     */
    public int getUnreadNotificationCount(int empId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE emp_id = ? AND is_read = 0";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting unread notification count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Mark notification as read
     */
    public boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error marking notification as read: " + e.getMessage());
        }

        return false;
    }

    /**
     * Mark all notifications as read for employee
     */
    public boolean markAllNotificationsAsRead(int empId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE emp_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            int rowsAffected = stmt.executeUpdate();
            logger.info("Marked {} notifications as read for employee {}", rowsAffected, empId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error marking all notifications as read: " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete old notifications (older than 30 days)
     */
    public int deleteOldNotifications() {
        String sql = "DELETE FROM notifications WHERE created_at < SYSDATE - 30";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int deletedCount = stmt.executeUpdate();
            logger.info("Deleted {} old notifications", deletedCount);
            return deletedCount;
        } catch (SQLException e) {
            logger.error("Error deleting old notifications: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Map ResultSet to Notification object
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setEmpId(rs.getInt("emp_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setType(rs.getString("type"));
        notification.setRead(rs.getInt("is_read") == 1);
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }

    /**
     * Get next notification ID
     */
    private int getNextNotificationId() {
        String sql = "SELECT NVL(MAX(notification_id), 0) + 1 FROM notifications";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next notification ID: " + e.getMessage());
        }

        return 1; // Default to 1 if error
    }
}