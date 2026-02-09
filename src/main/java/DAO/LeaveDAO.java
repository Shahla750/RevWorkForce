package DAO;

import Model.Leave;
import Util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {
    private static final Logger logger = LogManager.getLogger(LeaveDAO.class);

    public void applyLeave(Leave leave) {
        String sql = "INSERT INTO Leaves (emp_id, leave_type_id, start_date, end_date, reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leave.getEmpId());
            stmt.setInt(2, leave.getLeaveTypeId());
            stmt.setDate(3, new java.sql.Date(leave.getStartDate().getTime()));
            stmt.setDate(4, new java.sql.Date(leave.getEndDate().getTime()));
            stmt.setString(5, leave.getReason());
            stmt.executeUpdate();
            logger.info("Leave applied for employee ID: " + leave.getEmpId());
        } catch (SQLException e) {
            logger.error("Failed to apply leave", e);
        }
    }

    public List<Leave> getLeavesByEmployee(int empId) {
        List<Leave> leaves = new ArrayList<>();
        String sql = "SELECT * FROM Leaves WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                leaves.add(mapToLeave(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch leaves", e);
        }
        return leaves;
    }

    public void approveLeave(int leaveId, String comment) {
        String sql = "UPDATE Leaves SET status = 'Approved', manager_comment = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comment);
            stmt.setInt(2, leaveId);
            stmt.executeUpdate();
            logger.info("Leave approved: " + leaveId);
        } catch (SQLException e) {
            logger.error("Failed to approve leave", e);
        }
    }

    private Leave mapToLeave(ResultSet rs) throws SQLException {
        return new Leave(
            rs.getInt("id"),
            rs.getInt("emp_id"),
            rs.getInt("leave_type_id"),
            rs.getDate("start_date"),
            rs.getDate("end_date"),
            rs.getString("reason"),
            rs.getString("status"),
            rs.getString("manager_comment")
        );
    }
}