package com.revwf.model;

import java.sql.Timestamp;

public class Designation {
    private int designationId;
    private String designationName;
    private String designationCode;
    private Timestamp createdAt;
    
    // Constructors
    public Designation() {}
    
    public Designation(String designationName, String designationCode) {
        this.designationName = designationName;
        this.designationCode = designationCode;
    }
    
    // Getters and Setters
    public int getDesignationId() {
        return designationId;
    }
    
    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }
    
    public String getDesignationName() {
        return designationName;
    }
    
    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }
    
    public String getDesignationCode() {
        return designationCode;
    }
    
    public void setDesignationCode(String designationCode) {
        this.designationCode = designationCode;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Designation{" +
                "designationId=" + designationId +
                ", designationName='" + designationName + '\'' +
                ", designationCode='" + designationCode + '\'' +
                '}';
    }
}