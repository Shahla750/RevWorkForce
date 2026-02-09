package com.revwf.model;

import java.sql.Timestamp;

public class Notification {
    private int notificationId;
    private int empId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private Timestamp createdAt;
    
    // Constructors
    public Notification() {}
    
    public Notification(int empId, String title, String message, String type) {
        this.empId = empId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }
    
    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
    
    public int getEmpId() {
        return empId;
    }
    
    public void setEmpId(int empId) {
        this.empId = empId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", empId=" + empId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}