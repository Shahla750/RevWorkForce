package DAO;

import Model.PerformanceReview;
import Util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {
    private static final Logger logger = LogManager.getLogger(PerformanceDAO.class);

    public void submitReview(PerformanceReview review) {
        String sql = "INSERT INTO PerformanceReviews (emp_id, year, key_deliverables, accomplishments, improvements, self_rating) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getEmpId());
            stmt.setInt(2, review.getYear());
            stmt.setString(3, review.getKeyDeliverables());
            stmt.setString(4, review.getAccomplishments());
            stmt.setString(5, review.getImprovements());
            stmt.setInt(6, review.getSelfRating());
            stmt.executeUpdate();
            logger.info("Performance review submitted for employee ID: " + review.getEmpId());
        } catch (SQLException e) {
            logger.error("Failed to submit review", e);
        }
    }

    public List<PerformanceReview> getReviewsByEmployee(int empId) {
        List<PerformanceReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM PerformanceReviews WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reviews.add(mapToReview(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch reviews", e);
        }
        return reviews;
    }

    private PerformanceReview mapToReview(ResultSet rs) throws SQLException {
        return new PerformanceReview(
            rs.getInt("id"),
            rs.getInt("emp_id"),
            rs.getInt("year"),
            rs.getString("key_deliverables"),
            rs.getString("accomplishments"),
            rs.getString("improvements"),
            rs.getInt("self_rating"),
            rs.getString("manager_feedback"),
            rs.getObject("manager_rating", Integer.class)
        );
    }
}