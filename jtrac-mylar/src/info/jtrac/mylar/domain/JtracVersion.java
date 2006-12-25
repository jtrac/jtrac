package info.jtrac.mylar.domain;

import info.jtrac.mylar.util.XmlUtils;

import org.dom4j.Document;

public class JtracVersion {
	
	private String number;
	private String timestamp;
	
	public JtracVersion(String xml) {
		Document d = XmlUtils.parse(xml);
		number = d.getRootElement().attributeValue("number");
		timestamp = d.getRootElement().attributeValue("timestamp");
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
