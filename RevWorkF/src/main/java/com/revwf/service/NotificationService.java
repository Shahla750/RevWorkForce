package com.revwf.service;

import com.revwf.dao.NotificationDAO;
import com.revwf.model.Notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);
    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    /**
     * Create leave approval notification
     */
    public boolean createLeaveApprovalNotification(int empId, String leaveType, String startDate, String endDate) {
        try {
            String title = "Leave Approved";
            String message = String.format("Your %s leave from %s to %s has been approved by your manager.", 
                leaveType, startDate, endDate);
            
            Notification notification = new Notification(empId, title, message, "LEAVE_APPROVAL");
            return notificationDAO.createNotification(notification);
        } catch (Exception e) {
            logger.error("Error creating leave approval notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create leave rejection notification
     */
    public boolean createLeaveRejectionNotification(int empId, String leaveType, String startDate, String endDate, String reason) {
        try {
            String title = "Leave Rejected";
            String message = String.format("Your %s leave from %s to %s has been rejected. Reason: %s", 
                leaveType, startDate, endDate, reason != null ? reason : "Not specified");
            
            Notification notification = new Notification(empId, title, message, "LEAVE_REJECTION");
            return notificationDAO.createNotification(notification);
        } catch (Exception e) {
            logger.error("Error creating leave rejection notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create performance review notification
     */
    public boolean createPerformanceReviewNotification(int empId, String reviewType) {
        try {
            String title = "Performance Review";
            String message = String.format("Your %s performance review is ready for your input. Please complete it by the deadline.", reviewType);
            
            Notification notification = new Notification(empId, title, message, "PERFORMANCE_REVIEW");
            return notificationDAO.createNotification(notification);
        } catch (Exception e) {
            logger.error("Error creating performance review notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create birthday reminder notification
     */
    public boolean createBirthdayReminderNotification(int empId, String colleagueName) {
        try {
            String title = "Birthday Reminder";
            String message = String.format("Today is %s's birthday! Don't forget to wish them well.", colleagueName);
            
            Notification notification = new Notification(empId, title, message, "BIRTHDAY_REMINDER");
            return notificationDAO.createNotification(notification);
        } catch (Exception e) {
            logger.error("Error creating birthday reminder notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create work anniversary notification
     */
    public boolean createWorkAnniversaryNotification(int empId, String colleagueName, int years) {
        try {
            String title = "Work Anniversary";
            String message = String.format("%s is celebrating %d years with the company today!", colleagueName, years);
            
            Notification notification = new Notification(empId, title, message, "WORK_ANNIVERSARY");
            return notificationDAO.createNotification(notification);
        } catch (Exception e) {
            logger.error("Error creating work anniversary notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create system announcement notification
     */
    public boolean createSystemAnnouncementNotification(int empId, String title, String message) {
        try {
            Notification notification = new Notification(empId, title, message, "SYSTEM_ANNOUNCEMENT");
            return notificationDAO.createNotification(notification);
        } catch (Exception e) {
            logger.error("Error creating system announcement notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all notifications for employee
     */
    public List<Notification> getEmployeeNotifications(int empId) {
        try {
            return notificationDAO.getNotificationsByEmployeeId(empId);
        } catch (Exception e) {
            logger.error("Error getting employee notifications: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve notifications", e);
        }
    }

    /**
     * Get unread notification count
     */
    public int getUnreadNotificationCount(int empId) {
        try {
            return notificationDAO.getUnreadNotificationCount(empId);
        } catch (Exception e) {
            logger.error("Error getting unread notification count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Mark notification as read
     */
    public boolean markNotificationAsRead(int notificationId) {
        try {
            return notificationDAO.markNotificationAsRead(notificationId);
        } catch (Exception e) {
            logger.error("Error marking notification as read: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mark all notifications as read
     */
    public boolean markAllNotificationsAsRead(int empId) {
        try {
            return notificationDAO.markAllNotificationsAsRead(empId);
        } catch (Exception e) {
            logger.error("Error marking all notifications as read: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clean up old notifications
     */
    public int cleanupOldNotifications() {
        try {
            return notificationDAO.deleteOldNotifications();
        } catch (Exception e) {
            logger.error("Error cleaning up old notifications: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Create sample notifications for testing
     */
    public boolean createSampleNotifications(int empId) {
        try {
            // Create different types of sample notifications
            createLeaveApprovalNotification(empId, "Casual Leave", "2026-02-10", "2026-02-12");
            createLeaveRejectionNotification(empId, "Sick Leave", "2026-02-15", "2026-02-17", "Insufficient notice period");
            createPerformanceReviewNotification(empId, "Annual");
            createBirthdayReminderNotification(empId, "John Doe");
            createSystemAnnouncementNotification(empId, "System Update", "The HRM system will be updated tonight at 10 PM");
            
            logger.info("Sample notifications created for employee: {}", empId);
            return true;
        } catch (Exception e) {
            logger.error("Error creating sample notifications: " + e.getMessage());
            return false;
        }
    }
}