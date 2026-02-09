package DAO;

import java.sql.*;

import Util.DBConnection;


public class LoginDAO {

    public static String login(int empId, String password) {

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT role FROM employees WHERE employee_id=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, empId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
