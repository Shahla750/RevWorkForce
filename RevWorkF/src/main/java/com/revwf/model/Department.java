package com.revwf.model;

import java.sql.Timestamp;

public class Department {
    private int deptId;
    private String deptName;
    private String deptCode;
    private Timestamp createdAt;
    
    // Constructors
    public Department() {}
    
    public Department(String deptName, String deptCode) {
        this.deptName = deptName;
        this.deptCode = deptCode;
    }
    
    // Getters and Setters
    public int getDeptId() {
        return deptId;
    }
    
    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }
    
    public String getDeptName() {
        return deptName;
    }
    
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    
    public String getDeptCode() {
        return deptCode;
    }
    
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Department{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", deptCode='" + deptCode + '\'' +
                '}';
    }
}