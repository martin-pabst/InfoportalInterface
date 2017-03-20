package infoportalinterface;

import infoportalinterface.model.IPKlassenListe;
import infoportalinterface.model.IPLehrkraftListe;
import infoportalinterface.model.IPSchuelerListe;
import infoportalinterface.requests.GetKlassenRequest;
import infoportalinterface.requests.GetLehrkraefteRequest;
import infoportalinterface.requests.GetNotenRequest;
import infoportalinterface.requests.LoginRequest;
import infoportalinterface.requests.LogoutRequest;
import infoportalinterface.tools.httpclient.ApacheHttpClientWrapper;
import infoportalinterface.tools.httpclient.HttpClientInterface;

public class InfoPortalInterface {

	private String username;

	private String password;

	private String basePortalURL;

	private HttpClientInterface httpClient;

	/**
	 * Model
	 */
	private IPLehrkraftListe lehrkraefte = new IPLehrkraftListe();
	private IPSchuelerListe schueler = new IPSchuelerListe();
	private IPKlassenListe klassen = new IPKlassenListe();

	/**
	 * 
	 * @param username
	 * @param password
	 * @param basePortalURL
	 */

	public InfoPortalInterface(String username, String password, String basePortalURL) {

		super();

		this.username = username;

		this.password = password;

		int i = basePortalURL.indexOf("schule_portal");
		if (i > 0) {
			basePortalURL = basePortalURL.substring(0, i);
		}

		this.basePortalURL = basePortalURL;

		httpClient = new ApacheHttpClientWrapper();

	}

	public String getBasePortalURL() {
		return basePortalURL;
	}

	public String login() throws Exception {

		LoginRequest loginRequest = new LoginRequest(this);
		return loginRequest.execute();

	}

	public void logout() throws Exception {

		LogoutRequest logoutRequest = new LogoutRequest(this);
		logoutRequest.execute();

	}

	public void fetchLehrkraefte(String mainPage) throws Exception {
		GetLehrkraefteRequest glr = new GetLehrkraefteRequest(this, mainPage);
		glr.execute();

		lehrkraefte = glr.getLehrkraefte();
	}

	public void fetchKlassen(String mainPage, IPLehrkraftListe lehrkraefte) throws Exception {
		GetKlassenRequest gkr = new GetKlassenRequest(this, mainPage, lehrkraefte);
		gkr.execute();
		
		klassen = gkr.getKlassen();
	}
	
	public void fetchNoten(String mainPage) throws Exception {

		GetNotenRequest gnr = new GetNotenRequest(this, mainPage, lehrkraefte, klassen);
		gnr.execute();
		
	}
	
	
	
	public HttpClientInterface getHttpClient() {

		return httpClient;

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public IPLehrkraftListe getLehrkraefte() {
		return lehrkraefte;
	}

	public IPSchuelerListe getSchueler() {
		return schueler;
	}

	public IPKlassenListe getKlassen() {
		return klassen;
	}

	

}
