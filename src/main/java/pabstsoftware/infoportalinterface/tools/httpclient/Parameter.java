package pabstsoftware.infoportalinterface.tools.httpclient;

public class Parameter {

	private String key;

	private String value;

	public Parameter(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
