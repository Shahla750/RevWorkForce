package Model;

import java.util.Date;

public class Goal {
    private int id;
    private int empId;
    private String description;
    private Date deadline;
    private String weightage;
    private String metrics;
    private int progress;
    private String status;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public String getWeightage() {
		return weightage;
	}
	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}
	public String getMetrics() {
		return metrics;
	}
	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Goal(int id, int empId, String description, Date deadline, String weightage, String metrics, int progress,
			String status) {
		super();
		this.id = id;
		this.empId = empId;
		this.description = description;
		this.deadline = deadline;
		this.weightage = weightage;
		this.metrics = metrics;
		this.progress = progress;
		this.status = status;
	}

    // Getters and setters
	
    
}