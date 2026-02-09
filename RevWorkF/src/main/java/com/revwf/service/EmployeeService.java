package com.revwf.service;

import com.revwf.dao.EmployeeDAO;
import com.revwf.dao.UserDAO;
import com.revwf.model.Department;
import com.revwf.model.Designation;
import com.revwf.model.Employee;
import com.revwf.model.Holiday;
import com.revwf.model.User;
import com.revwf.model.UserRole;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EmployeeService {
    private static final Logger logger = LogManager.getLogger(EmployeeService.class);
    private final EmployeeDAO employeeDAO;
    private final UserDAO userDAO;
    
    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Get all employees
     */
    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (Exception e) {
            logger.error("Error in getAllEmployees: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve employees", e);
        }
    }
    
    /**
     * Get employee by ID
     */
    public Employee getEmployeeById(int empId) {
        try {
            return employeeDAO.getEmployeeById(empId);
        } catch (Exception e) {
            logger.error("Error in getEmployeeById: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve employee", e);
        }
    }
    
    /**
     * Get employee by user ID
     */
    public Employee getEmployeeByUserId(int userId) {
        try {
            return employeeDAO.getEmployeeByUserId(userId);
        } catch (Exception e) {
            logger.error("Error in getEmployeeByUserId: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve employee", e);
        }
    }
    
    /**
     * Create new employee with user account
     */
    public boolean createEmployeeWithUser(String employeeId, String email, String password, 
                                        UserRole role, Employee employee) {
        try {
            // First create user account
            User user = new User(employeeId, email, password, role);
            boolean userCreated = userDAO.createUser(user);
            
            if (!userCreated) {
                logger.error("Failed to create user account for employee: " + employeeId);
                return false;
            }
            
            // Get the created user to get user_id
            User createdUser = userDAO.getUserByEmployeeId(employeeId);
            if (createdUser == null) {
                logger.error("Failed to retrieve created user: " + employeeId);
                return false;
            }
            
            // Set user_id in employee object
            employee.setUserId(createdUser.getUserId());
            
            // Create employee record
            boolean employeeCreated = employeeDAO.createEmployee(employee);
            
            if (employeeCreated) {
                logger.info("Employee created successfully: " + employeeId);
                return true;
            } else {
                // If employee creation fails, we should ideally rollback user creation
                // For now, just log the error
                logger.error("Employee creation failed after user creation: " + employeeId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error in createEmployeeWithUser: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update employee information
     */
    public boolean updateEmployee(Employee employee) {
        try {
            return employeeDAO.updateEmployee(employee);
        } catch (Exception e) {
            logger.error("Error in updateEmployee: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get team members for a manager
     */
    public List<Employee> getTeamMembers(int managerId) {
        try {
            return employeeDAO.getEmployeesByManagerId(managerId);
        } catch (Exception e) {
            logger.error("Error in getTeamMembers: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve team members", e);
        }
    }
    
    /**
     * Assign manager to employee
     */
    public boolean assignManager(int empId, int managerId) {
        try {
            Employee employee = employeeDAO.getEmployeeById(empId);
            if (employee == null) {
                logger.error("Employee not found: " + empId);
                return false;
            }
            
            // Verify manager exists
            Employee manager = employeeDAO.getEmployeeById(managerId);
            if (manager == null) {
                logger.error("Manager not found: " + managerId);
                return false;
            }
            
            employee.setManagerId(managerId);
            boolean updated = employeeDAO.updateEmployee(employee);
            
            if (updated) {
                logger.info("Manager assigned successfully. Employee: {}, Manager: {}", empId, managerId);
            }
            
            return updated;
        } catch (Exception e) {
            logger.error("Error in assignManager: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Search employees by name
     */
    public List<Employee> searchEmployeesByName(String searchTerm) {
        try {
            return employeeDAO.searchEmployeesByName(searchTerm);
        } catch (Exception e) {
            logger.error("Error in searchEmployeesByName: " + e.getMessage());
            throw new RuntimeException("Failed to search employees by name", e);
        }
    }

    /**
     * Search employees by employee ID
     */
    public List<Employee> searchEmployeesByEmployeeId(String employeeId) {
        try {
            return employeeDAO.searchEmployeesByEmployeeId(employeeId);
        } catch (Exception e) {
            logger.error("Error in searchEmployeesByEmployeeId: " + e.getMessage());
            throw new RuntimeException("Failed to search employees by employee ID", e);
        }
    }

    /**
     * Search employees by department
     */
    public List<Employee> searchEmployeesByDepartment(String department) {
        try {
            return employeeDAO.searchEmployeesByDepartment(department);
        } catch (Exception e) {
            logger.error("Error in searchEmployeesByDepartment: " + e.getMessage());
            throw new RuntimeException("Failed to search employees by department", e);
        }
    }

    /**
     * Search employees by designation
     */
    public List<Employee> searchEmployeesByDesignation(String designation) {
        try {
            return employeeDAO.searchEmployeesByDesignation(designation);
        } catch (Exception e) {
            logger.error("Error in searchEmployeesByDesignation: " + e.getMessage());
            throw new RuntimeException("Failed to search employees by designation", e);
        }
    }

    /**
     * Get potential managers
     */
    public List<Employee> getPotentialManagers() {
        try {
            return employeeDAO.getPotentialManagers();
        } catch (Exception e) {
            logger.error("Error in getPotentialManagers: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve potential managers", e);
        }
    }

    /**
     * Update employee manager
     */
    public boolean updateEmployeeManager(int empId, Integer newManagerId) {
        try {
            // Validate employee exists
            Employee employee = employeeDAO.getEmployeeById(empId);
            if (employee == null) {
                logger.error("Employee not found: " + empId);
                return false;
            }

            // Validate manager exists if provided
            if (newManagerId != null) {
                Employee manager = employeeDAO.getEmployeeById(newManagerId);
                if (manager == null) {
                    logger.error("Manager not found: " + newManagerId);
                    return false;
                }

                // Prevent self-assignment as manager
                if (empId == newManagerId) {
                    logger.error("Employee cannot be their own manager: " + empId);
                    return false;
                }

                // Check for circular hierarchy (basic check)
                if (wouldCreateCircularHierarchy(empId, newManagerId)) {
                    logger.error("Manager assignment would create circular hierarchy");
                    return false;
                }
            }

            boolean success = employeeDAO.updateEmployeeManager(empId, newManagerId);
            if (success) {
                logger.info("Manager updated for employee ID: {} to manager ID: {}", empId, newManagerId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Error in updateEmployeeManager: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if assigning a manager would create a circular hierarchy
     */
    private boolean wouldCreateCircularHierarchy(int empId, int newManagerId) {
        try {
            Employee currentManager = employeeDAO.getEmployeeById(newManagerId);
            while (currentManager != null && currentManager.getManagerId() != null) {
                if (currentManager.getManagerId() == empId) {
                    return true; // Circular hierarchy detected
                }
                currentManager = employeeDAO.getEmployeeById(currentManager.getManagerId());
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking circular hierarchy: " + e.getMessage());
            return true; // Err on the side of caution
        }
    }
    
    /**
     * Validate employee data
     */
    public boolean validateEmployeeData(Employee employee) {
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            logger.warn("Employee first name is required");
            return false;
        }
        
        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            logger.warn("Employee last name is required");
            return false;
        }
        
        if (employee.getJoiningDate() == null) {
            logger.warn("Employee joining date is required");
            return false;
        }
        
        if (employee.getDeptId() <= 0) {
            logger.warn("Valid department is required");
            return false;
        }
        
        if (employee.getDesignationId() <= 0) {
            logger.warn("Valid designation is required");
            return false;
        }
        
        if (employee.getSalary() < 0) {
            logger.warn("Salary cannot be negative");
            return false;
        }
        
        return true;
    }
    
    /**
     * Get employee statistics
     */
    public EmployeeStats getEmployeeStatistics() {
        try {
            List<Employee> allEmployees = employeeDAO.getAllEmployees();
            
            EmployeeStats stats = new EmployeeStats();
            stats.totalEmployees = allEmployees.size();
            stats.activeEmployees = allEmployees.size(); // Assuming all are active for now
            
            // Count by department (simplified)
            stats.itEmployees = (int) allEmployees.stream()
                    .filter(emp -> "Information Technology".equals(emp.getDepartmentName()))
                    .count();
            
            stats.hrEmployees = (int) allEmployees.stream()
                    .filter(emp -> "Human Resources".equals(emp.getDepartmentName()))
                    .count();
            
            return stats;
        } catch (Exception e) {
            logger.error("Error in getEmployeeStatistics: " + e.getMessage());
            return new EmployeeStats();
        }
    }
    
    /**
     * Deactivate employee account
     */
    public boolean deactivateEmployee(int empId) {
        try {
            Employee employee = employeeDAO.getEmployeeById(empId);
            if (employee == null) {
                logger.error("Employee not found: " + empId);
                return false;
            }
            
            boolean success = userDAO.deactivateUser(employee.getUserId());
            if (success) {
                logger.info("Employee deactivated successfully: " + empId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Error in deactivateEmployee: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reactivate employee account
     */
    public boolean reactivateEmployee(int empId) {
        try {
            Employee employee = employeeDAO.getEmployeeById(empId);
            if (employee == null) {
                logger.error("Employee not found: " + empId);
                return false;
            }
            
            boolean success = userDAO.reactivateUser(employee.getUserId());
            if (success) {
                logger.info("Employee reactivated successfully: " + empId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Error in reactivateEmployee: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inner class for employee statistics
     */
    public static class EmployeeStats {
        public int totalEmployees;
        public int activeEmployees;
        public int itEmployees;
        public int hrEmployees;
        
        @Override
        public String toString() {
            return "EmployeeStats{" +
                    "totalEmployees=" + totalEmployees +
                    ", activeEmployees=" + activeEmployees +
                    ", itEmployees=" + itEmployees +
                    ", hrEmployees=" + hrEmployees +
                    '}';
        }
    }
    
    /**
     * Get employee by employee ID string (e.g., "EMP001")
     */
    public Employee getEmployeeByEmployeeId(String employeeId) {
        try {
            return employeeDAO.getEmployeeByEmployeeId(employeeId);
        } catch (Exception e) {
            logger.error("Error in getEmployeeByEmployeeId: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve employee by employee ID", e);
        }
    }
    
    /**
     * Get all departments
     */
    public List<Department> getAllDepartments() {
        try {
            return employeeDAO.getAllDepartments();
        } catch (Exception e) {
            logger.error("Error in getAllDepartments: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve departments", e);
        }
    }
    
    /**
     * Add new department
     */
    public boolean addDepartment(String deptName, String deptCode) {
        try {
            return employeeDAO.addDepartment(deptName, deptCode);
        } catch (Exception e) {
            logger.error("Error in addDepartment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get employee count by department
     */
    public int getEmployeeCountByDepartment(int deptId) {
        try {
            return employeeDAO.getEmployeeCountByDepartment(deptId);
        } catch (Exception e) {
            logger.error("Error in getEmployeeCountByDepartment: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get total employee count
     */
    public int getTotalEmployeeCount() {
        try {
            return employeeDAO.getTotalEmployeeCount();
        } catch (Exception e) {
            logger.error("Error in getTotalEmployeeCount: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get all designations
     */
    public List<Designation> getAllDesignations() {
        try {
            return employeeDAO.getAllDesignations();
        } catch (Exception e) {
            logger.error("Error in getAllDesignations: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve designations", e);
        }
    }
    
    /**
     * Add new designation
     */
    public boolean addDesignation(String designationName, String designationCode) {
        try {
            return employeeDAO.addDesignation(designationName, designationCode);
        } catch (Exception e) {
            logger.error("Error in addDesignation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get employee count by designation
     */
    public int getEmployeeCountByDesignation(int designationId) {
        try {
            return employeeDAO.getEmployeeCountByDesignation(designationId);
        } catch (Exception e) {
            logger.error("Error in getEmployeeCountByDesignation: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get employees by department
     */
    public List<Employee> getEmployeesByDepartment(int deptId) {
        try {
            return employeeDAO.getEmployeesByDepartment(deptId);
        } catch (Exception e) {
            logger.error("Error in getEmployeesByDepartment: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve employees by department", e);
        }
    }
    
    /**
     * Get all holidays
     */
    public List<Holiday> getAllHolidays() {
        try {
            return employeeDAO.getAllHolidays();
        } catch (Exception e) {
            logger.error("Error in getAllHolidays: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve holidays", e);
        }
    }
    
    /**
     * Add new holiday
     */
    public boolean addHoliday(String holidayName, java.sql.Date holidayDate, String description, boolean isOptional) {
        try {
            return employeeDAO.addHoliday(holidayName, holidayDate, description, isOptional);
        } catch (Exception e) {
            logger.error("Error in addHoliday: " + e.getMessage());
            return false;
        }
    }
}