import org.junit.Test;

import pabstsoftware.infoportalinterface.InfoPortalInterface;

public class LoginLogoutTest {

	@Test
	public void test() throws Exception {

		InfoPortalInterface ip = TestInfoPortalInterfaceFactory.getInfoPortalInterface();

		System.out.println("InfoPortalInterface: " + ip.toString());

		ip.login();
		ip.logout();

	}

}
