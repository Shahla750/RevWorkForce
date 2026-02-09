package com.revwf.service;

import com.revwf.dao.PerformanceDAO;
import com.revwf.model.Goal;
import com.revwf.model.PerformanceReview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PerformanceService {
    private static final Logger logger = LogManager.getLogger(PerformanceService.class);
    private final PerformanceDAO performanceDAO;
    private final NotificationService notificationService;

    public PerformanceService() {
        this.performanceDAO = new PerformanceDAO();
        this.notificationService = new NotificationService();
    }

    // =============== PERFORMANCE REVIEW METHODS ===============

    /**
     * Create or update performance review
     */
    public PerformanceReviewResult savePerformanceReview(PerformanceReview review) {
        try {
            // Validate review data
            if (!validatePerformanceReview(review)) {
                return new PerformanceReviewResult(false, "Invalid performance review data");
            }

            boolean success = performanceDAO.savePerformanceReview(review);
            
            if (success) {
                String message = review.getReviewId() > 0 ? 
                    "Performance review updated successfully" : 
                    "Performance review created successfully";
                
                logger.info("Performance review saved for employee: {}", review.getEmpId());
                return new PerformanceReviewResult(true, message);
            } else {
                return new PerformanceReviewResult(false, "Failed to save performance review");
            }
        } catch (Exception e) {
            logger.error("Error saving performance review: " + e.getMessage());
            return new PerformanceReviewResult(false, "System error occurred while saving review");
        }
    }

    /**
     * Submit performance review for manager review
     */
    public PerformanceReviewResult submitPerformanceReview(int reviewId) {
        try {
            PerformanceReview review = performanceDAO.getPerformanceReviewById(reviewId);
            if (review == null) {
                return new PerformanceReviewResult(false, "Performance review not found");
            }

            review.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);
            review.setSubmissionDate(Date.valueOf(LocalDate.now()));

            boolean success = performanceDAO.savePerformanceReview(review);
            
            if (success) {
                // Create notification for manager
                notificationService.createPerformanceReviewNotification(
                    review.getEmpId(), "Annual Performance Review");
                
                logger.info("Performance review submitted: {}", reviewId);
                return new PerformanceReviewResult(true, "Performance review submitted for manager review");
            } else {
                return new PerformanceReviewResult(false, "Failed to submit performance review");
            }
        } catch (Exception e) {
            logger.error("Error submitting performance review: " + e.getMessage());
            return new PerformanceReviewResult(false, "System error occurred while submitting review");
        }
    }

    /**
     * Get performance reviews for employee
     */
    public List<PerformanceReview> getEmployeePerformanceReviews(int empId) {
        try {
            return performanceDAO.getPerformanceReviewsByEmployee(empId);
        } catch (Exception e) {
            logger.error("Error getting employee performance reviews: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve performance reviews", e);
        }
    }

    /**
     * Get performance review by ID
     */
    public PerformanceReview getPerformanceReviewById(int reviewId) {
        try {
            return performanceDAO.getPerformanceReviewById(reviewId);
        } catch (Exception e) {
            logger.error("Error getting performance review: " + e.getMessage());
            return null;
        }
    }

    // =============== GOAL METHODS ===============

    /**
     * Create new goal
     */
    public GoalResult createGoal(Goal goal) {
        try {
            // Validate goal data
            if (!validateGoal(goal)) {
                return new GoalResult(false, "Invalid goal data");
            }

            boolean success = performanceDAO.createGoal(goal);
            
            if (success) {
                logger.info("Goal created for employee: {}", goal.getEmpId());
                return new GoalResult(true, "Goal created successfully");
            } else {
                return new GoalResult(false, "Failed to create goal");
            }
        } catch (Exception e) {
            logger.error("Error creating goal: " + e.getMessage());
            return new GoalResult(false, "System error occurred while creating goal");
        }
    }

    /**
     * Update goal progress
     */
    public GoalResult updateGoalProgress(int goalId, int completionPercentage, String progressNotes) {
        try {
            // Validate completion percentage
            if (completionPercentage < 0 || completionPercentage > 100) {
                return new GoalResult(false, "Completion percentage must be between 0 and 100");
            }

            // Determine status based on completion
            Goal.GoalStatus status;
            if (completionPercentage == 0) {
                status = Goal.GoalStatus.NOT_STARTED;
            } else if (completionPercentage == 100) {
                status = Goal.GoalStatus.COMPLETED;
            } else {
                status = Goal.GoalStatus.IN_PROGRESS;
            }

            boolean success = performanceDAO.updateGoalProgress(goalId, completionPercentage, progressNotes, status);
            
            if (success) {
                logger.info("Goal progress updated: {} - {}%", goalId, completionPercentage);
                return new GoalResult(true, "Goal progress updated successfully");
            } else {
                return new GoalResult(false, "Failed to update goal progress");
            }
        } catch (Exception e) {
            logger.error("Error updating goal progress: " + e.getMessage());
            return new GoalResult(false, "System error occurred while updating goal");
        }
    }

    /**
     * Get goals for employee
     */
    public List<Goal> getEmployeeGoals(int empId) {
        try {
            return performanceDAO.getGoalsByEmployee(empId);
        } catch (Exception e) {
            logger.error("Error getting employee goals: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve goals", e);
        }
    }

    /**
     * Get goal statistics for employee
     */
    public GoalStatistics getGoalStatistics(int empId) {
        try {
            List<Goal> goals = getEmployeeGoals(empId);
            
            GoalStatistics stats = new GoalStatistics();
            stats.totalGoals = goals.size();
            
            for (Goal goal : goals) {
                switch (goal.getStatus()) {
                    case NOT_STARTED:
                        stats.notStartedGoals++;
                        break;
                    case IN_PROGRESS:
                        stats.inProgressGoals++;
                        break;
                    case COMPLETED:
                        stats.completedGoals++;
                        break;
                    case CANCELLED:
                        stats.cancelledGoals++;
                        break;
                }
                
                switch (goal.getPriority()) {
                    case HIGH:
                        stats.highPriorityGoals++;
                        break;
                    case MEDIUM:
                        stats.mediumPriorityGoals++;
                        break;
                    case LOW:
                        stats.lowPriorityGoals++;
                        break;
                }
            }
            
            // Calculate completion rate
            if (stats.totalGoals > 0) {
                stats.completionRate = (double) stats.completedGoals / stats.totalGoals * 100;
            }
            
            return stats;
        } catch (Exception e) {
            logger.error("Error getting goal statistics: " + e.getMessage());
            return new GoalStatistics();
        }
    }

    // =============== VALIDATION METHODS ===============

    private boolean validatePerformanceReview(PerformanceReview review) {
        if (review.getEmpId() <= 0) {
            return false;
        }
        
        if (review.getReviewYear() < 2020 || review.getReviewYear() > LocalDate.now().getYear() + 1) {
            return false;
        }
        
        if (review.getSelfAssessmentRating() < 1 || review.getSelfAssessmentRating() > 5) {
            return false;
        }
        
        return true;
    }

    private boolean validateGoal(Goal goal) {
        if (goal.getEmpId() <= 0) {
            return false;
        }
        
        if (goal.getGoalDescription() == null || goal.getGoalDescription().trim().isEmpty()) {
            return false;
        }
        
        if (goal.getDeadline() == null || goal.getDeadline().before(Date.valueOf(LocalDate.now()))) {
            return false;
        }
        
        return true;
    }

    // =============== RESULT CLASSES ===============

    public static class PerformanceReviewResult {
        public final boolean success;
        public final String message;

        public PerformanceReviewResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static class GoalResult {
        public final boolean success;
        public final String message;

        public GoalResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static class GoalStatistics {
        public int totalGoals;
        public int completedGoals;
        public int inProgressGoals;
        public int notStartedGoals;
        public int cancelledGoals;
        public int highPriorityGoals;
        public int mediumPriorityGoals;
        public int lowPriorityGoals;
        public double completionRate;

        @Override
        public String toString() {
            return "GoalStatistics{" +
                    "totalGoals=" + totalGoals +
                    ", completedGoals=" + completedGoals +
                    ", completionRate=" + completionRate +
                    '}';
        }
    }
}