import java.util.Scanner;

import Model.Employee;
import Service.AuthService;

public class MainApp {

	 public static void main(String[] args) {

	        Scanner sc = new Scanner(System.in);
	        AuthService authService = new AuthService();

	        System.out.println("===== HR MANAGEMENT SYSTEM =====");
	        System.out.print("Email: ");
	        String email = sc.nextLine();
	        System.out.print("Password: ");
	        String password = sc.nextLine();

	        Employee emp = authService.login(email, password);

	        if (emp == null) {
	            System.out.println("❌ Invalid Login");
	            return;
	        }

	        System.out.println("✅ Welcome " + emp.getName());

	        while (true) {
	            System.out.println("\n1. Apply Leave");
	            System.out.println("2. View Profile");
	            System.out.println("3. Logout");
	            System.out.print("Choose: ");

	            int choice = sc.nextInt();

	            switch (choice) {
	                case 1:
	                    System.out.println("Leave module coming...");
	                    break;
	                case 2:
	                    System.out.println("Employee ID: " + emp.getEmployeeId());
	                    System.out.println("Email: " + emp.getEmail());
	                    break;
	                case 3:
	                    System.out.println("👋 Logged out");
	                    return;
	                default:
	                    System.out.println("Invalid choice");
	            }
	        }
	    }
}
