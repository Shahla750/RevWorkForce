package com.revwf.model;

public enum UserRole {
    EMPLOYEE("Employee"),
    MANAGER("Manager"),
    ADMIN("Admin");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}