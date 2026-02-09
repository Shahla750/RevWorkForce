package com.revwf.model;

public class LeaveBalance {
    private int balanceId;
    private int empId;
    private int leaveTypeId;
    private String leaveTypeName;
    private int allocatedDays;
    private int usedDays;
    private int remainingDays;
    private int year;
    
    // Constructors
    public LeaveBalance() {}
    
    public LeaveBalance(int empId, int leaveTypeId, int allocatedDays, int year) {
        this.empId = empId;
        this.leaveTypeId = leaveTypeId;
        this.allocatedDays = allocatedDays;
        this.usedDays = 0;
        this.remainingDays = allocatedDays;
        this.year = year;
    }
    
    // Getters and Setters
    public int getBalanceId() {
        return balanceId;
    }
    
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }
    
    public int getEmpId() {
        return empId;
    }
    
    public void setEmpId(int empId) {
        this.empId = empId;
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
    
    public int getAllocatedDays() {
        return allocatedDays;
    }
    
    public void setAllocatedDays(int allocatedDays) {
        this.allocatedDays = allocatedDays;
    }
    
    public int getUsedDays() {
        return usedDays;
    }
    
    public void setUsedDays(int usedDays) {
        this.usedDays = usedDays;
    }
    
    public int getRemainingDays() {
        return remainingDays;
    }
    
    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public void updateBalance() {
        this.remainingDays = this.allocatedDays - this.usedDays;
    }
}