package pabstsoftware.infoportalinterface.requests;

public class ResponseNotExpectedException extends Exception {

	private static final long serialVersionUID = 1L;

	private String response;

	private String expectedString;

	public ResponseNotExpectedException(String response, String expectedText) {
		super("Expected text '" + expectedText + "' not found in subsequent response.\n\n" + response);
		this.response = response;
		this.expectedString = expectedText;
	}

}
