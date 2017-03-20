package infoportalinterface.requests;

import infoportalinterface.InfoPortalInterface;
import infoportalinterface.tools.httpclient.HttpClientInterface;

public class LogoutRequest extends BaseRequest {

	public LogoutRequest(InfoPortalInterface infoPortalInterface) {
		super(infoPortalInterface);
	}

	@Override
	public String execute() throws Exception {

		HttpClientInterface httpClient = getHttpClient();
		
		httpClient.addParameter("signoff", "1");
		String response = httpClient.get(getBasePortalURL() + "schule_portal/index.php");
		// https://portal.mzml.de/portal/csgying/schule_portal/index.php?signoff=1
		expectResponseContains(response, "form name='signon'");
		
		return response;
	}

}
