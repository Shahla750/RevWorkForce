package com.revwf.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Employee {
    private int empId;
    private int userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Date dateOfBirth;
    private Date joiningDate;
    private int deptId;
    private String departmentName;
    private int designationId;
    private String designationName;
    private Integer managerId;
    private String managerName;
    private double salary;
    private String emergencyContact;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Employee() {}
    
    public Employee(int userId, String firstName, String lastName, String phone, 
                   Date joiningDate, int deptId, int designationId, double salary) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.joiningDate = joiningDate;
        this.deptId = deptId;
        this.designationId = designationId;
        this.salary = salary;
    }
    
    // Getters and Setters
    public int getEmpId() {
        return empId;
    }
    
    public void setEmpId(int empId) {
        this.empId = empId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
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
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public Date getJoiningDate() {
        return joiningDate;
    }
    
    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }
    
    public int getDeptId() {
        return deptId;
    }
    
    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public int getDesignationId() {
        return designationId;
    }
    
    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }
    
    public String getDesignationName() {
        return designationName;
    }
    
    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }
    
    public Integer getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", designationName='" + designationName + '\'' +
                ", managerName='" + managerName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}