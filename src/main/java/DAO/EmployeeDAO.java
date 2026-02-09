package DAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Model.Employee;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private static final Logger logger = LogManager.getLogger(EmployeeDAO.class);

    public Employee authenticate(String empId, String password) {
        String sql = "SELECT * FROM Employees WHERE emp_id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Employee emp = mapToEmployee(rs);
                if (org.mindrot.jbcrypt.BCrypt.checkpw(password, emp.getPasswordHash())) {
                    logger.info("Authentication successful for " + empId);
                    return emp;
                }
            }
        } catch (SQLException e) {
            logger.error("Authentication failed", e);
        }
        return null;
    }

    public void updateProfile(Employee emp) {
        String sql = "UPDATE Employees SET phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emp.getPhone());
            stmt.setString(2, emp.getAddress());
            stmt.setInt(3, emp.getId());
            stmt.executeUpdate();
            logger.info("Profile updated for employee ID: " + emp.getId());
        } catch (SQLException e) {
            logger.error("Failed to update profile", e);
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employees WHERE is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapToEmployee(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch employees", e);
        }
        return employees;
    }

    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM Employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToEmployee(rs);
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch employee", e);
        }
        return null;
    }

    public void addEmployee(Employee emp) {
        String sql = "INSERT INTO Employees (emp_id, name, email, phone, address, dob, joining_date, manager_id, dept_id, desig_id, salary, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emp.getEmpId());
            stmt.setString(2, emp.getName());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getPhone());
            stmt.setString(5, emp.getAddress());
            stmt.setDate(6, new java.sql.Date(emp.getDob().getTime()));
            stmt.setDate(7, new java.sql.Date(emp.getJoiningDate().getTime()));
            stmt.setObject(8, emp.getManagerId());
            stmt.setObject(9, emp.getDeptId());
            stmt.setObject(10, emp.getDesigId());
            stmt.setDouble(11, emp.getSalary());
            stmt.setString(12, emp.getPasswordHash());
            stmt.executeUpdate();
            logger.info("Employee added: " + emp.getEmpId());
        } catch (SQLException e) {
            logger.error("Failed to add employee", e);
        }
    }

    private Employee mapToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getInt("id"),
            rs.getString("emp_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getDate("dob"),
            rs.getDate("joining_date"),
            rs.getObject("manager_id", Integer.class),
            rs.getObject("dept_id", Integer.class),
            rs.getObject("desig_id", Integer.class),
            rs.getDouble("salary"),
            rs.getString("password_hash"),
            rs.getBoolean("is_active")
        );
    }
}