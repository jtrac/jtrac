package info.jtrac.mylyn.domain;

public class JtracVersion {
	
	private String number;
	private String timestamp;
	
	public JtracVersion(JtracDocument d) {		
		number = d.getText("/version/@number");
		timestamp = d.getText("/version/@timestamp");
		if (number == null) {
			throw new RuntimeException("Unexpected XML response from server");
		}
	}
	
	//==========================================================================
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
