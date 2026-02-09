package DAO;

import Model.Goal;
import Util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    private static final Logger logger = LogManager.getLogger(GoalDAO.class);

    public void addGoal(Goal goal) {
        String sql = "INSERT INTO Goals (emp_id, description, deadline, weightage, metrics) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goal.getEmpId());
            stmt.setString(2, goal.getDescription());
            stmt.setDate(3, new java.sql.Date(goal.getDeadline().getTime()));
            stmt.setString(4, goal.getWeightage());
            stmt.setString(5, goal.getMetrics());
            stmt.executeUpdate();
            logger.info("Goal added for employee ID: " + goal.getEmpId());
        } catch (SQLException e) {
            logger.error("Failed to add goal", e);
        }
    }

    public List<Goal> getGoalsByEmployee(int empId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM Goals WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                goals.add(mapToGoal(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch goals", e);
        }
        return goals;
    }

    private Goal mapToGoal(ResultSet rs) throws SQLException {
        return new Goal(
            rs.getInt("id"),
            rs.getInt("emp_id"),
            rs.getString("description"),
            rs.getDate("deadline"),
            rs.getString("weightage"),
            rs.getString("metrics"),
            rs.getInt("progress"),
            rs.getString("status")
        );
    }
}