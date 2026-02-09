package Model;

import java.util.Date;

public class Leave {
    private int id;
    private int empId;
    private int leaveTypeId;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;
    private String managerComment;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEmpId() {
		return empId;
	}
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	public int getLeaveTypeId() {
		return leaveTypeId;
	}
	public void setLeaveTypeId(int leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getManagerComment() {
		return managerComment;
	}
	public void setManagerComment(String managerComment) {
		this.managerComment = managerComment;
	}
	public Leave(int id, int empId, int leaveTypeId, Date startDate, Date endDate, String reason, String status,
			String managerComment) {
		super();
		this.id = id;
		this.empId = empId;
		this.leaveTypeId = leaveTypeId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.reason = reason;
		this.status = status;
		this.managerComment = managerComment;
	}

    // Getters and setters
    
}