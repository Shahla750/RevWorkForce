package com.revwf.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Goal {
    private int goalId;
    private int empId;
    private String employeeName;
    private String goalDescription;
    private Date deadline;
    private Priority priority;
    private String successMetrics;
    private GoalStatus status;
    private int completionPercentage;
    private String progressNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
    
    public enum GoalStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    // Constructors
    public Goal() {}
    
    public Goal(int empId, String goalDescription, Date deadline, Priority priority) {
        this.empId = empId;
        this.goalDescription = goalDescription;
        this.deadline = deadline;
        this.priority = priority;
        this.status = GoalStatus.NOT_STARTED;
        this.completionPercentage = 0;
    }
    
    // Getters and Setters
    public int getGoalId() {
        return goalId;
    }
    
    public void setGoalId(int goalId) {
        this.goalId = goalId;
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
    
    public String getGoalDescription() {
        return goalDescription;
    }
    
    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }
    
    public Date getDeadline() {
        return deadline;
    }
    
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public String getSuccessMetrics() {
        return successMetrics;
    }
    
    public void setSuccessMetrics(String successMetrics) {
        this.successMetrics = successMetrics;
    }
    
    public GoalStatus getStatus() {
        return status;
    }
    
    public void setStatus(GoalStatus status) {
        this.status = status;
    }
    
    public int getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public String getProgressNotes() {
        return progressNotes;
    }
    
    public void setProgressNotes(String progressNotes) {
        this.progressNotes = progressNotes;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getPriorityIcon() {
        switch (priority) {
            case HIGH:
                return "🔴";
            case MEDIUM:
                return "🟡";
            case LOW:
                return "🟢";
            default:
                return "⚪";
        }
    }
    
    public String getStatusIcon() {
        switch (status) {
            case NOT_STARTED:
                return "⏸️";
            case IN_PROGRESS:
                return "▶️";
            case COMPLETED:
                return "✅";
            case CANCELLED:
                return "❌";
            default:
                return "❓";
        }
    }
    
    @Override
    public String toString() {
        return "Goal{" +
                "goalId=" + goalId +
                ", empId=" + empId +
                ", goalDescription='" + goalDescription + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", completionPercentage=" + completionPercentage +
                '}';
    }
}