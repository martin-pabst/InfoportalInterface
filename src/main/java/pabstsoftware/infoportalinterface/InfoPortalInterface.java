package pabstsoftware.infoportalinterface;

import pabstsoftware.config.Config;
import pabstsoftware.infoportalinterface.model.IPKlassenListe;
import pabstsoftware.infoportalinterface.model.IPKoppelgruppe;
import pabstsoftware.infoportalinterface.model.IPLehrkraftListe;
import pabstsoftware.infoportalinterface.model.IPSchuelerListe;
import pabstsoftware.infoportalinterface.requests.*;
import pabstsoftware.infoportalinterface.tools.httpclient.ApacheHttpClientWrapper;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;

import java.util.ArrayList;

public class InfoPortalInterface {

	private String username;

	private String password;

	private String basePortalURL;

	private HttpClientInterface httpClient;

	private String mainPage;

	/**
	 * Model
	 */
	private IPLehrkraftListe lehrkraefte = new IPLehrkraftListe();
	private IPSchuelerListe schueler = new IPSchuelerListe();
	private IPKlassenListe klassen = new IPKlassenListe();
	private Klassenfilter klassenfilter;
	private ArrayList<IPKoppelgruppe> koppelgruppen;

	/**
	 * 
	 * @param username
	 * @param password
	 * @param basePortalURL
	 */

	public InfoPortalInterface(String username, String password, String basePortalURL, Config config) {

		super();

		this.username = username;

		this.password = password;

		int i = basePortalURL.indexOf("schule_portal");
		if (i > 0) {
			basePortalURL = basePortalURL.substring(0, i);
		}

		this.basePortalURL = basePortalURL;

		httpClient = new ApacheHttpClientWrapper(config);

	}

	public String getBasePortalURL() {
		return basePortalURL;
	}

	public void login() throws Exception {

		LoginRequest loginRequest = new LoginRequest(this);
		mainPage = loginRequest.execute();

	}

	public void logout() throws Exception {

		LogoutRequest logoutRequest = new LogoutRequest(this, mainPage);
		logoutRequest.execute();

	}

	public void fetchLehrkraefte() throws Exception {
		GetLehrkraefteRequest glr = new GetLehrkraefteRequest(this, mainPage);
		glr.execute();

		lehrkraefte = glr.getLehrkraefte();
	}

	public void fetchKlassen(IPLehrkraftListe lehrkraefte) throws Exception {
		GetKlassenRequest gkr = new GetKlassenRequest(this, mainPage, lehrkraefte);
		gkr.execute();
		
		klassen = gkr.getKlassen();

		GetKlassenteamsRequest gktr = new GetKlassenteamsRequest(this,klassen, lehrkraefte, mainPage);
		gktr.execute();

		GetKlassenStundenplaeneRequest gksr = new GetKlassenStundenplaeneRequest(this, klassen, lehrkraefte, mainPage);
		gksr.execute();

	}

	public void fetchKoppelgruppen() throws Exception {
		GetKoppelgruppenRequest gkr = new GetKoppelgruppenRequest(this, mainPage, lehrkraefte, klassen);
		gkr.execute();
		this.koppelgruppen = gkr.getKoppelgruppen();

	}

	public void fetchNoten(boolean mitEinzelnoten) throws Exception {

		GetNotenRequest gnr = new GetNotenRequest(this, mainPage, lehrkraefte, klassen, mitEinzelnoten);
		gnr.execute();
		
	}

	public void fetchAbsenzen() throws Exception {

		GetSchuelerAbsenzenRequest gar = new GetSchuelerAbsenzenRequest(this, klassen, lehrkraefte, mainPage);
		gar.execute();

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


    public void setKlassenfilter(Klassenfilter klassenfilter) {

		this.klassenfilter = klassenfilter;

	}

	public Klassenfilter getKlassenfilter() {
		return klassenfilter;
	}

	public ArrayList<IPKoppelgruppe> getKoppelgruppen() {
		return koppelgruppen;
	}
}
