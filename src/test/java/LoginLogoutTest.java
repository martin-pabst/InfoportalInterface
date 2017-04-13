import org.junit.Test;

import pabstsoftware.infoportalinterface.InfoPortalInterface;

public class LoginLogoutTest {

	@Test
	public void test() throws Exception {

		InfoPortalInterface ip = TestInfoPortalInterfaceFactory.getInfoPortalInterface();
		ip.login();
		ip.logout();

	}

}
