package Service;

import DAO.EmployeeDAO;
import Model.Employee;

public class AuthService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public Employee authenticate(String empId, String password) {
        return employeeDAO.authenticate(empId, password);
    }

    public void updatePassword(int empId, String newPassword) {
        // Implement password update logic (hash and update in DAO)
    }
}