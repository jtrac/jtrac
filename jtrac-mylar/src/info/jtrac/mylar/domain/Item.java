package info.jtrac.mylar.domain;

public class Item {
	
	private String refId;
	private String summary;
	private String detail;
	private String loggedBy;
	private String assignedTo;
	
	public Item(JtracDocument d) {
		refId = d.getText("/item/@refId");
		summary = d.getText("/item/summary");
		detail = d.getText("/item/detail");
		loggedBy = d.getText("/item/loggedBy");
		assignedTo = d.getText("/item/assignedTo");
	}
	
	//==========================================================================
	
	public String getRefId() {
		return refId;
	}
	
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getDetail() {
		return detail;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String getLoggedBy() {
		return loggedBy;
	}
	
	public void setLoggedBy(String loggedBy) {
		this.loggedBy = loggedBy;
	}
	
	public String getAssignedTo() {
		return assignedTo;
	}
	
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	
}
