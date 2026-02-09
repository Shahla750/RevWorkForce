package Model;

import java.util.Date;

public class Employee {
    private int id;
    private String empId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date dob;
    private Date joiningDate;
    private Integer managerId;
    private Integer deptId;
    private Integer desigId;
    private double salary;
    private String passwordHash;
    private boolean isActive;
 // Getters and setters (omitted for brevity; generate via IDE)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}
	public Integer getManagerId() {
		return managerId;
	}
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Integer getDesigId() {
		return desigId;
	}
	public void setDesigId(Integer desigId) {
		this.desigId = desigId;
	}
	public double getSalary() {
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Employee(int id, String empId, String name, String email, String phone, String address, Date dob,
			Date joiningDate, Integer managerId, Integer deptId, Integer desigId, double salary, String passwordHash,
			boolean isActive) {
		super();
		this.id = id;
		this.empId = empId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.dob = dob;
		this.joiningDate = joiningDate;
		this.managerId = managerId;
		this.deptId = deptId;
		this.desigId = desigId;
		this.salary = salary;
		this.passwordHash = passwordHash;
		this.isActive = isActive;
	}

    
    
}