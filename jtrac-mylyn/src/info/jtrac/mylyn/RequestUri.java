package info.jtrac.mylyn;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

	private String method;
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public RequestUri(String method) {
		this.method = method;
	}
	
	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("/api?method=");
		sb.append(method);
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			sb.append('&').append(entry.getKey()).append('=').append(entry.getValue());
		}
		return sb.toString();
	}
	
}
