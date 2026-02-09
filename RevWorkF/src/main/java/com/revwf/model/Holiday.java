package com.revwf.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Holiday {
    private int holidayId;
    private String holidayName;
    private Date holidayDate;
    private String description;
    private boolean isOptional;
    private Timestamp createdAt;
    
    // Constructors
    public Holiday() {}
    
    public Holiday(String holidayName, Date holidayDate) {
        this.holidayName = holidayName;
        this.holidayDate = holidayDate;
        this.isOptional = false;
    }
    
    public Holiday(String holidayName, Date holidayDate, String description, boolean isOptional) {
        this.holidayName = holidayName;
        this.holidayDate = holidayDate;
        this.description = description;
        this.isOptional = isOptional;
    }
    
    // Getters and Setters
    public int getHolidayId() {
        return holidayId;
    }
    
    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }
    
    public String getHolidayName() {
        return holidayName;
    }
    
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }
    
    public Date getHolidayDate() {
        return holidayDate;
    }
    
    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isOptional() {
        return isOptional;
    }
    
    public void setOptional(boolean optional) {
        isOptional = optional;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Holiday{" +
                "holidayId=" + holidayId +
                ", holidayName='" + holidayName + '\'' +
                ", holidayDate=" + holidayDate +
                ", isOptional=" + isOptional +
                '}';
    }
}