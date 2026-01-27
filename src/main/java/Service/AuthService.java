package Service;

import DAO.EmployeeDAO;
import Model.Employee;

public class AuthService {

	private EmployeeDAO dao = new EmployeeDAO();

    public Employee login(String email, String password) {
        return dao.login(email, password);
    }
}
