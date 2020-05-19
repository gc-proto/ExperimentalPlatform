package ca.gc.tbs.domain;

import org.springframework.data.annotation.Id;

public class Problem {
	@Id
	private String id;
	private String url;
	private String problem;
	private String problemDetails;
	private String department;
	private String language;
	private String resolutionDate;
	private String resolution;
	private String topic;
	private String problemDate;

	public Problem(String id, String url, String problemDate, String problem, String problemDetails, String department,
			String language, String resolutionDate, String resolution, String topic) {
		super();
		this.id = id;
		this.url = url;
		this.problem = problem;
		this.problemDetails = problemDetails;
		this.problemDate = problemDate;
		this.resolutionDate = resolutionDate;
		this.resolution = resolution;
		this.topic = topic;
		this.department = department;
		this.language = language;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getProblemDetails() {
		return problemDetails;
	}

	public void setProblemDetails(String problemDetails) {
		this.problemDetails = problemDetails;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getResolutionDate() {
		return resolutionDate;
	}

	public void setResolutionDate(String resolutionDate) {
		this.resolutionDate = resolutionDate;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getProblemDate() {
		return problemDate;
	}

	public void setProblemDate(String problemDate) {
		this.problemDate = problemDate;
	}
}
