package infoportalinterface.tools.httpclient;

import java.util.Map;

public interface HttpClientInterface {

	public HttpClientInterface setURL(String url);

	public HttpClientInterface addParameter(String key, String value);

	public HttpClientInterface clearParameters();

	public String get(String url) throws Exception;

	public String get() throws Exception;

	public String post(String url) throws Exception;

	public String post() throws Exception;

	public Map<String, String> getResponseHeaders();

	public int getStatusCode();
	
	public HttpClientInterface forkClient();
	
	public void close();

}
