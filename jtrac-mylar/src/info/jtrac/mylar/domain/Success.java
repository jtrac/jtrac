package info.jtrac.mylar.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

public class Success {
	
	Map<String, String> map = new HashMap<String, String>();
	
	public Success(Document d) {
		Element root = d.getRootElement();
		if (!root.getName().equals("success")) {
			throw new RuntimeException("Unexpected XML from server");
		}
		for(Element e: (List<Element>) root.elements()) {
			map.put(e.getName(), e.getTextTrim());
		}
	}
	
	public String getValue(String key) {
		return map.get(key);
	}	

}
