package com.revwf.model;

import java.sql.Date;
import java.sql.Timestamp;

public class PerformanceReview {
    private int reviewId;
    private int empId;
    private String employeeName;
    private int reviewYear;
    private String keyDeliverables;
    private String majorAccomplishments;
    private String areasOfImprovement;
    private int selfAssessmentRating;
    private int managerRating;
    private String managerFeedback;
    private ReviewStatus status;
    private Date submissionDate;
    private Date reviewDate;
    private Integer reviewedBy;
    private String reviewedByName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public enum ReviewStatus {
        DRAFT, SUBMITTED, REVIEWED, COMPLETED
    }
    
    // Constructors
    public PerformanceReview() {}
    
    public PerformanceReview(int empId, int reviewYear) {
        this.empId = empId;
        this.reviewYear = reviewYear;
        this.status = ReviewStatus.DRAFT;
    }
    
    // Getters and Setters
    public int getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
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
    
    public int getReviewYear() {
        return reviewYear;
    }
    
    public void setReviewYear(int reviewYear) {
        this.reviewYear = reviewYear;
    }
    
    public String getKeyDeliverables() {
        return keyDeliverables;
    }
    
    public void setKeyDeliverables(String keyDeliverables) {
        this.keyDeliverables = keyDeliverables;
    }
    
    public String getMajorAccomplishments() {
        return majorAccomplishments;
    }
    
    public void setMajorAccomplishments(String majorAccomplishments) {
        this.majorAccomplishments = majorAccomplishments;
    }
    
    public String getAreasOfImprovement() {
        return areasOfImprovement;
    }
    
    public void setAreasOfImprovement(String areasOfImprovement) {
        this.areasOfImprovement = areasOfImprovement;
    }
    
    public int getSelfAssessmentRating() {
        return selfAssessmentRating;
    }
    
    public void setSelfAssessmentRating(int selfAssessmentRating) {
        this.selfAssessmentRating = selfAssessmentRating;
    }
    
    public int getManagerRating() {
        return managerRating;
    }
    
    public void setManagerRating(int managerRating) {
        this.managerRating = managerRating;
    }
    
    public String getManagerFeedback() {
        return managerFeedback;
    }
    
    public void setManagerFeedback(String managerFeedback) {
        this.managerFeedback = managerFeedback;
    }
    
    public ReviewStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReviewStatus status) {
        this.status = status;
    }
    
    public Date getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public Date getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public Integer getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public String getReviewedByName() {
        return reviewedByName;
    }
    
    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
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
    
    @Override
    public String toString() {
        return "PerformanceReview{" +
                "reviewId=" + reviewId +
                ", empId=" + empId +
                ", reviewYear=" + reviewYear +
                ", status=" + status +
                ", selfAssessmentRating=" + selfAssessmentRating +
                ", managerRating=" + managerRating +
                '}';
    }
}