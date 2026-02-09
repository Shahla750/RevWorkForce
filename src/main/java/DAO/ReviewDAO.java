package DAO;

import java.sql.*;

import Util.DBConnection;


public class ReviewDAO {

    public static void submitReview(
        int empId, int year, String achievements, int rating) {

        String sql =
            "INSERT INTO employee_reviews " +
            "(review_id, employee_id, review_year, achievements, self_rating) " +
            "VALUES (review_seq.NEXTVAL, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ps.setInt(2, year);
            ps.setString(3, achievements);
            ps.setInt(4, rating);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
