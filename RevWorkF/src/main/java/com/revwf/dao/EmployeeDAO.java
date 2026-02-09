package com.revwf.dao;

import com.revwf.config.DatabaseConfig;
import com.revwf.model.Department;
import com.revwf.model.Designation;
import com.revwf.model.Employee;
import com.revwf.model.Holiday;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private static final Logger logger = LogManager.getLogger(EmployeeDAO.class);
    private final DatabaseConfig dbConfig;

    public EmployeeDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Get all employees with department and designation details
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "ORDER BY e.emp_id";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = mapResultSetToEmployee(rs);
                employees.add(employee);
            }

            logger.info("Retrieved {} employees", employees.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all employees: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Get employee by ID
     */
    public Employee getEmployeeById(int empId) {
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE e.emp_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get employee by user ID
     */
    public Employee getEmployeeByUserId(int userId) {
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE e.user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee by user ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Create new employee
     */
    public boolean createEmployee(Employee employee) {
        String sql = "INSERT INTO employees (emp_id, user_id, first_name, last_name, phone, " +
                "address, date_of_birth, joining_date, dept_id, designation_id, " +
                "manager_id, salary, emergency_contact) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Get next employee ID
            int nextEmpId = getNextEmployeeId();

            stmt.setInt(1, nextEmpId);
            stmt.setInt(2, employee.getUserId());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getLastName());
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getAddress());
            stmt.setDate(7, employee.getDateOfBirth());
            stmt.setDate(8, employee.getJoiningDate());
            stmt.setInt(9, employee.getDeptId());
            stmt.setInt(10, employee.getDesignationId());

            if (employee.getManagerId() != null) {
                stmt.setInt(11, employee.getManagerId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }

            stmt.setDouble(12, employee.getSalary());
            stmt.setString(13, employee.getEmergencyContact());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                employee.setEmpId(nextEmpId);
                logger.info("Employee created successfully with ID: " + nextEmpId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating employee: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update employee information
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, phone = ?, " +
                "address = ?, date_of_birth = ?, dept_id = ?, designation_id = ?, " +
                "manager_id = ?, salary = ?, emergency_contact = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE emp_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getPhone());
            stmt.setString(4, employee.getAddress());
            stmt.setDate(5, employee.getDateOfBirth());
            stmt.setInt(6, employee.getDeptId());
            stmt.setInt(7, employee.getDesignationId());

            if (employee.getManagerId() != null) {
                stmt.setInt(8, employee.getManagerId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setDouble(9, employee.getSalary());
            stmt.setString(10, employee.getEmergencyContact());
            stmt.setInt(11, employee.getEmpId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Employee updated successfully: " + employee.getEmpId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating employee: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get employees by manager ID
     */
    public List<Employee> getEmployeesByManagerId(int managerId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE e.manager_id = ? " +
                "ORDER BY e.first_name, e.last_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, managerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }

            logger.info("Retrieved {} employees for manager ID: {}", employees.size(), managerId);
        } catch (SQLException e) {
            logger.error("Error getting employees by manager ID: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Get next employee ID
     */
    private int getNextEmployeeId() throws SQLException {
        String sql = "SELECT NVL(MAX(emp_id), 0) + 1 FROM employees";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 1;
    }

    /**
     * Search employees by name
     */
    public List<Employee> searchEmployeesByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE UPPER(e.first_name || ' ' || e.last_name) LIKE UPPER(?) " +
                "ORDER BY e.first_name, e.last_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }

            logger.info("Found {} employees matching name: {}", employees.size(), name);
        } catch (SQLException e) {
            logger.error("Error searching employees by name: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Search employees by employee ID
     */
    public List<Employee> searchEmployeesByEmployeeId(String employeeId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE UPPER(u.employee_id) LIKE UPPER(?) " +
                "ORDER BY e.first_name, e.last_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + employeeId + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }

            logger.info("Found {} employees matching employee ID: {}", employees.size(), employeeId);
        } catch (SQLException e) {
            logger.error("Error searching employees by employee ID: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Search employees by department
     */
    public List<Employee> searchEmployeesByDepartment(String department) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE UPPER(d.dept_name) LIKE UPPER(?) " +
                "ORDER BY e.first_name, e.last_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + department + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }

            logger.info("Found {} employees in department: {}", employees.size(), department);
        } catch (SQLException e) {
            logger.error("Error searching employees by department: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Search employees by designation
     */
    public List<Employee> searchEmployeesByDesignation(String designation) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE UPPER(des.designation_name) LIKE UPPER(?) " +
                "ORDER BY e.first_name, e.last_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + designation + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }

            logger.info("Found {} employees with designation: {}", employees.size(), designation);
        } catch (SQLException e) {
            logger.error("Error searching employees by designation: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Get potential managers (employees who can be managers)
     */
    public List<Employee> getPotentialManagers() {
        List<Employee> managers = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE des.designation_name IN ('Manager', 'Team Lead', 'Senior Software Engineer', 'Finance Manager', 'Marketing Manager', 'Admin') "
                +
                "AND u.is_active = 1 " +
                "ORDER BY e.first_name, e.last_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = mapResultSetToEmployee(rs);
                managers.add(employee);
            }

            logger.info("Retrieved {} potential managers", managers.size());
        } catch (SQLException e) {
            logger.error("Error retrieving potential managers: " + e.getMessage());
        }

        return managers;
    }

    /**
     * Update employee manager
     */
    public boolean updateEmployeeManager(int empId, Integer newManagerId) {
        String sql = "UPDATE employees SET manager_id = ?, updated_at = CURRENT_TIMESTAMP WHERE emp_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (newManagerId != null) {
                stmt.setInt(1, newManagerId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, empId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Manager updated for employee ID: {} to manager ID: {}", empId, newManagerId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating employee manager: " + e.getMessage());
        }

        return false;
    }

    /**
     * Map ResultSet to Employee object
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(rs.getInt("emp_id"));
        employee.setUserId(rs.getInt("user_id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setPhone(rs.getString("phone"));
        employee.setAddress(rs.getString("address"));
        employee.setDateOfBirth(rs.getDate("date_of_birth"));
        employee.setJoiningDate(rs.getDate("joining_date"));
        employee.setDeptId(rs.getInt("dept_id"));
        employee.setDesignationId(rs.getInt("designation_id"));

        int managerId = rs.getInt("manager_id");
        if (!rs.wasNull()) {
            employee.setManagerId(managerId);
        }

        employee.setSalary(rs.getDouble("salary"));
        employee.setEmergencyContact(rs.getString("emergency_contact"));
        employee.setCreatedAt(rs.getTimestamp("created_at"));
        employee.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Set additional fields from joins
        employee.setDepartmentName(rs.getString("dept_name"));
        employee.setDesignationName(rs.getString("designation_name"));
        employee.setManagerName(rs.getString("manager_name"));

        // Set active status from users table
        int isActiveInt = rs.getInt("is_active");
        employee.setActive(!rs.wasNull() && isActiveInt == 1);

        return employee;
    }

    /**
     * Get employee by employee ID string (e.g., "EMP001")
     */
    public Employee getEmployeeByEmployeeId(String employeeId) {
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE u.employee_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee by employee ID: " + e.getMessage());
        }

        return null;
    }
    
    /**
     * Get all departments
     */
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY dept_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Department dept = new Department();
                dept.setDeptId(rs.getInt("dept_id"));
                dept.setDeptName(rs.getString("dept_name"));
                dept.setDeptCode(rs.getString("dept_code"));
                dept.setCreatedAt(rs.getTimestamp("created_at"));
                departments.add(dept);
            }

            logger.info("Retrieved {} departments", departments.size());
        } catch (SQLException e) {
            logger.error("Error retrieving departments: " + e.getMessage());
        }

        return departments;
    }
    
    /**
     * Add new department
     */
    public boolean addDepartment(String deptName, String deptCode) {
        String sql = "INSERT INTO departments (dept_id, dept_name, dept_code) VALUES (?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Get next department ID
            int nextId = getNextDepartmentId();
            
            stmt.setInt(1, nextId);
            stmt.setString(2, deptName);
            stmt.setString(3, deptCode);

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Department added successfully: {} ({})", deptName, deptCode);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error adding department: " + e.getMessage());
        }

        return false;
    }
    
    /**
     * Get employee count by department
     */
    public int getEmployeeCountByDepartment(int deptId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE dept_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deptId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee count by department: " + e.getMessage());
        }

        return 0;
    }
    
    /**
     * Get total employee count
     */
    public int getTotalEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employees";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting total employee count: " + e.getMessage());
        }

        return 0;
    }
    
    /**
     * Get next department ID
     */
    private int getNextDepartmentId() {
        String sql = "SELECT NVL(MAX(dept_id), 0) + 1 FROM departments";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next department ID: " + e.getMessage());
        }

        return 1; // Default to 1 if error
    }
    
    /**
     * Get all designations
     */
    public List<Designation> getAllDesignations() {
        List<Designation> designations = new ArrayList<>();
        String sql = "SELECT * FROM designations ORDER BY designation_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Designation designation = new Designation();
                designation.setDesignationId(rs.getInt("designation_id"));
                designation.setDesignationName(rs.getString("designation_name"));
                designation.setDesignationCode(rs.getString("designation_code"));
                designation.setCreatedAt(rs.getTimestamp("created_at"));
                designations.add(designation);
            }

            logger.info("Retrieved {} designations", designations.size());
        } catch (SQLException e) {
            logger.error("Error retrieving designations: " + e.getMessage());
        }

        return designations;
    }
    
    /**
     * Add new designation
     */
    public boolean addDesignation(String designationName, String designationCode) {
        String sql = "INSERT INTO designations (designation_id, designation_name, designation_code) VALUES (?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Get next designation ID
            int nextId = getNextDesignationId();
            
            stmt.setInt(1, nextId);
            stmt.setString(2, designationName);
            stmt.setString(3, designationCode);

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Designation added successfully: {} ({})", designationName, designationCode);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error adding designation: " + e.getMessage());
        }

        return false;
    }
    
    /**
     * Get employee count by designation
     */
    public int getEmployeeCountByDesignation(int designationId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE designation_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, designationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee count by designation: " + e.getMessage());
        }

        return 0;
    }
    
    /**
     * Get employees by department
     */
    public List<Employee> getEmployeesByDepartment(int deptId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, des.designation_name, " +
                "m.first_name || ' ' || m.last_name as manager_name, u.is_active " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "LEFT JOIN designations des ON e.designation_id = des.designation_id " +
                "LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "LEFT JOIN users u ON e.user_id = u.user_id " +
                "WHERE e.dept_id = ? " +
                "ORDER BY e.emp_id";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deptId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }

            logger.info("Retrieved {} employees for department {}", employees.size(), deptId);
        } catch (SQLException e) {
            logger.error("Error retrieving employees by department: " + e.getMessage());
        }

        return employees;
    }
    
    /**
     * Get all holidays
     */
    public List<Holiday> getAllHolidays() {
        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM company_holidays ORDER BY holiday_date";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Holiday holiday = new Holiday();
                holiday.setHolidayId(rs.getInt("holiday_id"));
                holiday.setHolidayName(rs.getString("holiday_name"));
                holiday.setHolidayDate(rs.getDate("holiday_date"));
                // Note: description column doesn't exist in current schema
                holiday.setDescription(""); // Set empty description for now
                holiday.setOptional(rs.getInt("is_optional") == 1);
                holiday.setCreatedAt(rs.getTimestamp("created_at"));
                holidays.add(holiday);
            }

            logger.info("Retrieved {} holidays", holidays.size());
        } catch (SQLException e) {
            logger.error("Error retrieving holidays: " + e.getMessage());
        }

        return holidays;
    }
    
    /**
     * Add new holiday
     */
    public boolean addHoliday(String holidayName, java.sql.Date holidayDate, String description, boolean isOptional) {
        String sql = "INSERT INTO company_holidays (holiday_id, holiday_name, holiday_date, is_optional) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Get next holiday ID
            int nextId = getNextHolidayId();
            
            stmt.setInt(1, nextId);
            stmt.setString(2, holidayName);
            stmt.setDate(3, holidayDate);
            stmt.setInt(4, isOptional ? 1 : 0);

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Holiday added successfully: {} on {}", holidayName, holidayDate);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error adding holiday: " + e.getMessage());
        }

        return false;
    }
    
    /**
     * Get next designation ID
     */
    private int getNextDesignationId() {
        String sql = "SELECT NVL(MAX(designation_id), 0) + 1 FROM designations";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next designation ID: " + e.getMessage());
        }

        return 1; // Default to 1 if error
    }
    
    /**
     * Get next holiday ID
     */
    private int getNextHolidayId() {
        String sql = "SELECT NVL(MAX(holiday_id), 0) + 1 FROM company_holidays";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next holiday ID: " + e.getMessage());
        }

        return 1; // Default to 1 if error
    }
}