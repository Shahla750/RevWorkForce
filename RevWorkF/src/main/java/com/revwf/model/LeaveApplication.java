package com.revwf.model;

import java.sql.Date;
import java.sql.Timestamp;

public class LeaveApplication {
    private int applicationId;
    private int empId;
    private String employeeName;
    private int leaveTypeId;
    private String leaveTypeName;
    private Date startDate;
    private Date endDate;
    private int totalDays;
    private String reason;
    private LeaveStatus status;
    private String managerComments;
    private Timestamp appliedDate;
    private Timestamp approvedDate;
    private Integer approvedBy;
    private String approvedByName;
    
    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
    
    // Constructors
    public LeaveApplication() {}
    
    public LeaveApplication(int empId, int leaveTypeId, Date startDate, Date endDate, 
                           int totalDays, String reason) {
        this.empId = empId;
        this.leaveTypeId = leaveTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
    }
    
    // Getters and Setters
    public int getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    
    public int getEmpId() {
        return empId;
    }
    
    public void setEmpId(int empId) {
        this.empId = empId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
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
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public int getTotalDays() {
        return totalDays;
    }
    
    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LeaveStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
    
    public String getManagerComments() {
        return managerComments;
    }
    
    public void setManagerComments(String managerComments) {
        this.managerComments = managerComments;
    }
    
    public Timestamp getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(Timestamp appliedDate) {
        this.appliedDate = appliedDate;
    }
    
    public Timestamp getApprovedDate() {
        return approvedDate;
    }
    
    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }
    
    public Integer getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public String getApprovedByName() {
        return approvedByName;
    }
    
    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }
}