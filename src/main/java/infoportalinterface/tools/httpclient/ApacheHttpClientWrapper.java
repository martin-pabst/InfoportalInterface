package infoportalinterface.tools.httpclient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

public class ApacheHttpClientWrapper implements HttpClientInterface {

	private String url;

	private ArrayList<Parameter> parameters = new ArrayList<>();

	private RequestConfig requestConfig;
	private CookieStore cookieStore;
	private HttpClientContext context;
	private CloseableHttpClient httpClient;

	boolean requestJustExecuted = false;

	private HashMap<String, String> lastHeaders = new HashMap<>();
	private int lastStatusCode = 0;

	public ApacheHttpClientWrapper(CookieStore cookieStore) {

		requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();
		context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore)
				.setRedirectStrategy(new LaxRedirectStrategy()).build();

		System.setProperty("jsse.enableSNIExtension", "false");

	}

	public ApacheHttpClientWrapper() {

		// PoolingHttpClientConnectionManager cm;
		//
		// cm = new PoolingHttpClientConnectionManager();
		// cm.setMaxTotal(10);
		// cm.setDefaultMaxPerRoute(10);

		requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();
		cookieStore = new BasicCookieStore();
		context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore)
				.setRedirectStrategy(new LaxRedirectStrategy()).build();

		System.setProperty("jsse.enableSNIExtension", "false");

	}

	@Override
	public HttpClientInterface setURL(String url) {

		// Relative URL?
		if (!url.startsWith("http") && this.url != null) {
			int i = this.url.lastIndexOf('/');
			if (i > 0) {
				url = this.url.substring(0, i + 1) + url;
			}
		}

		this.url = url;

		return this;
	}

	@Override
	public HttpClientInterface addParameter(String key, String value) {

		if (requestJustExecuted) {
			clearParameters();
			requestJustExecuted = false;
		}

		parameters.add(new Parameter(key, value));

		return this;
	}

	@Override
	public HttpClientInterface clearParameters() {

		parameters.clear();
		return this;

	}

	@Override
	public String get(String url) throws Exception {

		setURL(url);

		HttpGet httpGet = null;

		URIBuilder b = new URIBuilder(this.url);

		parameters.forEach((p) -> {
			b.addParameter(p.getKey(), p.getValue());
		});

		httpGet = new HttpGet(b.build());

		HttpClientContext context = HttpClientContext.create();

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		// Get new url, if redirected
		URI finalUrl = httpGet.getURI();
		List<URI> locations = context.getRedirectLocations();
		if (locations != null) {
			finalUrl = locations.get(locations.size() - 1);
		}

		this.url = finalUrl.toString();

		requestJustExecuted = true;

		storeHeaders(response);

		clearParameters();

		return convertStreamToString(response.getEntity().getContent());

	}

	@Override
	public String get() throws Exception {

		return get(url);

	}

	@Override
	public String post(String url) throws Exception {

		setURL(url);

		HttpPost httpPost = new HttpPost(this.url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		parameters.forEach((p) -> {
			nvps.add(new BasicNameValuePair(p.getKey(), p.getValue()));
		});

		httpPost.setEntity(new UrlEncodedFormEntity(nvps));

		HttpClientContext context = HttpClientContext.create();

		CloseableHttpResponse response = httpClient.execute(httpPost, context);

		// Get new url, if redirected
		URI finalUrl = httpPost.getURI();
		List<URI> locations = context.getRedirectLocations();
		if (locations != null) {
			finalUrl = locations.get(locations.size() - 1);
		}

		this.url = finalUrl.toString();

		requestJustExecuted = true;

		storeHeaders(response);

		clearParameters();

		return convertStreamToString(response.getEntity().getContent());

	}

	private void storeHeaders(CloseableHttpResponse response) {

		lastHeaders.clear();

		for (org.apache.http.Header header : response.getAllHeaders()) {

			lastHeaders.put(header.getName(), header.getValue());

		}

		lastStatusCode = response.getStatusLine().getStatusCode();

	}

	@Override
	public String post() throws Exception {

		return post(url);

	}

	private String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			return s.useDelimiter("\\A").hasNext() ? s.next() : "";
		}
	}

	@Override
	public Map<String, String> getResponseHeaders() {

		return lastHeaders;

	}

	@Override
	public int getStatusCode() {

		return lastStatusCode;

	}

	@Override
	public HttpClientInterface forkClient() {

		CookieStore cs = new BasicCookieStore();

		for (Cookie c : context.getCookieStore().getCookies()) {
			cs.addCookie(c);
		}

		ApacheHttpClientWrapper httpClient = new ApacheHttpClientWrapper(cs);
		// httpClient.context.setCookieStore(context.getCookieStore());

		return httpClient;
	}

	@Override
	public void close() {

		try {
			httpClient.close();
		} catch (IOException e) {

		}

	}

}
