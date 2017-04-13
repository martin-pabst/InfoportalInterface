package pabstsoftware.infoportalinterface.requests;

import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;

public class LoginRequest extends BaseRequest {

	public LoginRequest(InfoPortalInterface infoPortalInterface) {
		super(infoPortalInterface);
	}

	@Override
	public String execute() throws Exception {

		HttpClientInterface httpClient = getHttpClient();
		
		String response = httpClient.get(getBasePortalURL() + "schule_portal/index_dir/index.php");
		expectResponseContains(response, "Informationsportal");
		
		httpClient.addParameter("user", infoPortalInterface.getUsername());
		httpClient.addParameter("password", infoPortalInterface.getPassword());
		httpClient.addParameter("signoff", "0");
		
		response = httpClient.post(getBasePortalURL() + "schule_portal/project/auth/login.php");
		
		if(response.contains("Autorisierung fehlgeschlagen!")){
			throw new Exception("Passwort und/oder Benutzername falsch. Autorisierung fehlgeschlagen.");
		}
		
		
		
		expectResponseContains(response, "Sie sind angemeldet");
		
		return response;
	}

}
