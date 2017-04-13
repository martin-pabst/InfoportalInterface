package pabstsoftware.infoportalinterface.requests;

import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;

public abstract class BaseRequest {

	protected InfoPortalInterface infoPortalInterface;
	
	abstract public String execute() throws Exception;

	protected HttpClientInterface getHttpClient(){
		return infoPortalInterface.getHttpClient();
	}
	
	protected String getBasePortalURL(){
		return infoPortalInterface.getBasePortalURL();
	}
	
	public BaseRequest(InfoPortalInterface infoPortalInterface) {
		super();
		this.infoPortalInterface = infoPortalInterface;
	}

	protected void expectResponseContains(String response, String expectedText) throws ResponseNotExpectedException {

		if (!response.contains(expectedText)) {
			throw new ResponseNotExpectedException(response, expectedText);
		}

	}

}
