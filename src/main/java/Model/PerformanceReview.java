package Model;

public class PerformanceReview {
    private int id;
    private int empId;
    private int year;
    private String keyDeliverables;
    private String accomplishments;
    private String improvements;
    private int selfRating;
    private String managerFeedback;
    private Integer managerRating;
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
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getKeyDeliverables() {
		return keyDeliverables;
	}
	public void setKeyDeliverables(String keyDeliverables) {
		this.keyDeliverables = keyDeliverables;
	}
	public String getAccomplishments() {
		return accomplishments;
	}
	public void setAccomplishments(String accomplishments) {
		this.accomplishments = accomplishments;
	}
	public String getImprovements() {
		return improvements;
	}
	public void setImprovements(String improvements) {
		this.improvements = improvements;
	}
	public int getSelfRating() {
		return selfRating;
	}
	public void setSelfRating(int selfRating) {
		this.selfRating = selfRating;
	}
	public String getManagerFeedback() {
		return managerFeedback;
	}
	public void setManagerFeedback(String managerFeedback) {
		this.managerFeedback = managerFeedback;
	}
	public Integer getManagerRating() {
		return managerRating;
	}
	public void setManagerRating(Integer managerRating) {
		this.managerRating = managerRating;
	}
	public PerformanceReview(int id, int empId, int year, String keyDeliverables, String accomplishments,
			String improvements, int selfRating, String managerFeedback, Integer managerRating) {
		super();
		this.id = id;
		this.empId = empId;
		this.year = year;
		this.keyDeliverables = keyDeliverables;
		this.accomplishments = accomplishments;
		this.improvements = improvements;
		this.selfRating = selfRating;
		this.managerFeedback = managerFeedback;
		this.managerRating = managerRating;
	}

    // Getters and setters
    
}