package com.revwf.dao;

import com.revwf.config.DatabaseConfig;
import com.revwf.model.Goal;
import com.revwf.model.PerformanceReview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {
    private static final Logger logger = LogManager.getLogger(PerformanceDAO.class);
    private final DatabaseConfig dbConfig;

    public PerformanceDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    // =============== PERFORMANCE REVIEW METHODS ===============

    /**
     * Create or update performance review
     */
    public boolean savePerformanceReview(PerformanceReview review) {
        if (review.getReviewId() > 0) {
            return updatePerformanceReview(review);
        } else {
            return createPerformanceReview(review);
        }
    }

    /**
     * Create new performance review
     */
    private boolean createPerformanceReview(PerformanceReview review) {
        String sql = "INSERT INTO performance_reviews (review_id, emp_id, review_year, key_deliverables, " +
                "major_accomplishments, areas_of_improvement, self_assessment_rating, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextReviewId();
            
            stmt.setInt(1, nextId);
            stmt.setInt(2, review.getEmpId());
            stmt.setInt(3, review.getReviewYear());
            stmt.setString(4, review.getKeyDeliverables());
            stmt.setString(5, review.getMajorAccomplishments());
            stmt.setString(6, review.getAreasOfImprovement());
            stmt.setInt(7, review.getSelfAssessmentRating());
            stmt.setString(8, review.getStatus().toString());

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                review.setReviewId(nextId);
                logger.info("Performance review created successfully for employee: {}", review.getEmpId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating performance review: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update existing performance review
     */
    private boolean updatePerformanceReview(PerformanceReview review) {
        String sql = "UPDATE performance_reviews SET key_deliverables = ?, major_accomplishments = ?, " +
                "areas_of_improvement = ?, self_assessment_rating = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE review_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, review.getKeyDeliverables());
            stmt.setString(2, review.getMajorAccomplishments());
            stmt.setString(3, review.getAreasOfImprovement());
            stmt.setInt(4, review.getSelfAssessmentRating());
            stmt.setString(5, review.getStatus().toString());
            stmt.setInt(6, review.getReviewId());

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Performance review updated successfully: {}", review.getReviewId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating performance review: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get performance reviews by employee
     */
    public List<PerformanceReview> getPerformanceReviewsByEmployee(int empId) {
        List<PerformanceReview> reviews = new ArrayList<>();
        String sql = "SELECT pr.*, e.first_name || ' ' || e.last_name as employee_name, " +
                "m.first_name || ' ' || m.last_name as reviewed_by_name " +
                "FROM performance_reviews pr " +
                "LEFT JOIN employees e ON pr.emp_id = e.emp_id " +
                "LEFT JOIN employees m ON pr.reviewed_by = m.emp_id " +
                "WHERE pr.emp_id = ? ORDER BY pr.review_year DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PerformanceReview review = mapResultSetToPerformanceReview(rs);
                    reviews.add(review);
                }
            }

            logger.info("Retrieved {} performance reviews for employee {}", reviews.size(), empId);
        } catch (SQLException e) {
            logger.error("Error retrieving performance reviews: " + e.getMessage());
        }

        return reviews;
    }

    /**
     * Get performance review by ID
     */
    public PerformanceReview getPerformanceReviewById(int reviewId) {
        String sql = "SELECT pr.*, e.first_name || ' ' || e.last_name as employee_name, " +
                "m.first_name || ' ' || m.last_name as reviewed_by_name " +
                "FROM performance_reviews pr " +
                "LEFT JOIN employees e ON pr.emp_id = e.emp_id " +
                "LEFT JOIN employees m ON pr.reviewed_by = m.emp_id " +
                "WHERE pr.review_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reviewId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPerformanceReview(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving performance review: " + e.getMessage());
        }

        return null;
    }

    // =============== GOAL METHODS ===============

    /**
     * Create new goal
     */
    public boolean createGoal(Goal goal) {
        String sql = "INSERT INTO goals (goal_id, emp_id, goal_description, deadline, priority, " +
                "success_metrics, status, completion_percentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextGoalId();
            
            stmt.setInt(1, nextId);
            stmt.setInt(2, goal.getEmpId());
            stmt.setString(3, goal.getGoalDescription());
            stmt.setDate(4, goal.getDeadline());
            stmt.setString(5, goal.getPriority().toString());
            stmt.setString(6, goal.getSuccessMetrics());
            stmt.setString(7, goal.getStatus().toString());
            stmt.setInt(8, goal.getCompletionPercentage());

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                goal.setGoalId(nextId);
                logger.info("Goal created successfully for employee: {}", goal.getEmpId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating goal: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update goal progress
     */
    public boolean updateGoalProgress(int goalId, int completionPercentage, String progressNotes, Goal.GoalStatus status) {
        String sql = "UPDATE goals SET completion_percentage = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE goal_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, completionPercentage);
            stmt.setString(2, status.toString());
            stmt.setInt(3, goalId);

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Goal progress updated: {} - {}%", goalId, completionPercentage);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating goal progress: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get goals by employee
     */
    public List<Goal> getGoalsByEmployee(int empId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT g.*, e.first_name || ' ' || e.last_name as employee_name " +
                "FROM goals g " +
                "LEFT JOIN employees e ON g.emp_id = e.emp_id " +
                "WHERE g.emp_id = ? ORDER BY g.deadline ASC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Goal goal = mapResultSetToGoal(rs);
                    goals.add(goal);
                }
            }

            logger.info("Retrieved {} goals for employee {}", goals.size(), empId);
        } catch (SQLException e) {
            logger.error("Error retrieving goals: " + e.getMessage());
        }

        return goals;
    }

    // =============== HELPER METHODS ===============

    private PerformanceReview mapResultSetToPerformanceReview(ResultSet rs) throws SQLException {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(rs.getInt("review_id"));
        review.setEmpId(rs.getInt("emp_id"));
        review.setEmployeeName(rs.getString("employee_name"));
        review.setReviewYear(rs.getInt("review_year"));
        review.setKeyDeliverables(rs.getString("key_deliverables"));
        review.setMajorAccomplishments(rs.getString("major_accomplishments"));
        review.setAreasOfImprovement(rs.getString("areas_of_improvement"));
        review.setSelfAssessmentRating(rs.getInt("self_assessment_rating"));
        review.setManagerRating(rs.getInt("manager_rating"));
        review.setManagerFeedback(rs.getString("manager_feedback"));
        review.setStatus(PerformanceReview.ReviewStatus.valueOf(rs.getString("status")));
        review.setSubmissionDate(rs.getDate("submission_date"));
        review.setReviewDate(rs.getDate("review_date"));
        review.setReviewedBy(rs.getObject("reviewed_by", Integer.class));
        review.setReviewedByName(rs.getString("reviewed_by_name"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        review.setUpdatedAt(rs.getTimestamp("updated_at"));
        return review;
    }

    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setGoalId(rs.getInt("goal_id"));
        goal.setEmpId(rs.getInt("emp_id"));
        goal.setEmployeeName(rs.getString("employee_name"));
        goal.setGoalDescription(rs.getString("goal_description"));
        goal.setDeadline(rs.getDate("deadline"));
        goal.setPriority(Goal.Priority.valueOf(rs.getString("priority")));
        goal.setSuccessMetrics(rs.getString("success_metrics"));
        goal.setStatus(Goal.GoalStatus.valueOf(rs.getString("status")));
        goal.setCompletionPercentage(rs.getInt("completion_percentage"));
        goal.setCreatedAt(rs.getTimestamp("created_at"));
        goal.setUpdatedAt(rs.getTimestamp("updated_at"));
        return goal;
    }

    private int getNextReviewId() {
        String sql = "SELECT NVL(MAX(review_id), 0) + 1 FROM performance_reviews";
        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next review ID: " + e.getMessage());
        }
        return 1;
    }

    private int getNextGoalId() {
        String sql = "SELECT NVL(MAX(goal_id), 0) + 1 FROM goals";
        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next goal ID: " + e.getMessage());
        }
        return 1;
    }
}