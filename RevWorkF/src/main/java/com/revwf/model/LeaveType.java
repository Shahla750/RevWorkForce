package com.revwf.model;

import java.sql.Timestamp;

public class LeaveType {
    private int leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private int maxDaysPerYear;
    private Timestamp createdAt;
    
    // Constructors
    public LeaveType() {}
    
    public LeaveType(String leaveTypeName, String leaveTypeCode, int maxDaysPerYear) {
        this.leaveTypeName = leaveTypeName;
        this.leaveTypeCode = leaveTypeCode;
        this.maxDaysPerYear = maxDaysPerYear;
    }
    
    // Getters and Setters
    public int getLeaveTypeId() {
        return leaveTypeId;
    }
    
    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }
    
    public String getLeaveTypeName() {
        return leaveTypeName;
    }
    
    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }
    
    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }
    
    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }
    
    public int getMaxDaysPerYear() {
        return maxDaysPerYear;
    }
    
    public void setMaxDaysPerYear(int maxDaysPerYear) {
        this.maxDaysPerYear = maxDaysPerYear;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "LeaveType{" +
                "leaveTypeId=" + leaveTypeId +
                ", leaveTypeName='" + leaveTypeName + '\'' +
                ", leaveTypeCode='" + leaveTypeCode + '\'' +
                ", maxDaysPerYear=" + maxDaysPerYear +
                '}';
    }
}