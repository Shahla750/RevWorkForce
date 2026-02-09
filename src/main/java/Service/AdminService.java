package Service;

import java.sql.*;
import java.util.Scanner;

import Util.DBConnection;


public class AdminService {

    public static void adminMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employees");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 : addEmployee();
                case 2 : viewEmployees();
                case 3 : {
                    System.out.println("Admin logged out.");
                    return;
                }
            }
        }
    }

    private static void addEmployee() {
        try (Connection con = DBConnection.getConnection()) {

            Scanner sc = new Scanner(System.in);

            System.out.print("Employee ID: ");
            int id = sc.nextInt();
            System.out.print("Name: ");
            String name = sc.next();
            System.out.print("Password: ");
            String pass = sc.next();
            System.out.print("Role (EMPLOYEE/MANAGER): ");
            String role = sc.next();

            String sql = "INSERT INTO employees VALUES (?,?,?, ?, NULL)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, pass);
            ps.setString(4, role);

            ps.executeUpdate();
            System.out.println("✅ Employee added");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewEmployees() {
        try (Connection con = DBConnection.getConnection()) {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM employees");

            while (rs.next()) {
                System.out.println(
                    rs.getInt(1) + " | " +
                    rs.getString(2) + " | " +
                    rs.getString(4)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
