package Service;

import java.sql.*;
import java.util.Scanner;

import Util.DBConnection;


public class EmployeeService {

    public static void employeeMenu(int empId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- EMPLOYEE DASHBOARD ---");
            System.out.println("1. Apply Leave");
            System.out.println("2. View My Leaves");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 : applyLeave(empId);
                case 2 : viewLeaves(empId);
                case 3 : {
                    System.out.println("Employee logged out.");
                    return;
                }
            }
        }
    }

    private static void applyLeave(int empId) {
        try (Connection con = DBConnection.getConnection()) {

            Scanner sc = new Scanner(System.in);
            System.out.print("Reason: ");
            String reason = sc.nextLine();

            String sql = "INSERT INTO leaves(employee_id, reason) VALUES (?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, empId);
            ps.setString(2, reason);

            ps.executeUpdate();
            System.out.println("✅ Leave applied");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewLeaves(int empId) {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps =
                con.prepareStatement("SELECT * FROM leaves WHERE employee_id=?");
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getInt("leave_id") + " | " +
                    rs.getString("reason") + " | " +
                    rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
