package pabstsoftware.infoportalinterface.requests;

import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;
import pabstsoftware.infoportalinterface.tools.string.Finder;

public class LogoutRequest extends BaseRequest {

	private String mainPage;

	public LogoutRequest(InfoPortalInterface infoPortalInterface, String mainPage) {
	    super(infoPortalInterface);
	    this.mainPage = mainPage;
	}

	@Override
	public String execute() throws Exception {

		Finder finder = new Finder(mainPage);
		String logoutURL = finder.findLinkURL("Abmelden</b>");

		HttpClientInterface httpClient = getHttpClient();

		String response = httpClient.get(logoutURL);
		expectResponseContains(response, "form name='signon'");
		
		return response;
	}

}
