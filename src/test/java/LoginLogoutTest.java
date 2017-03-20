import static org.junit.Assert.*;

import org.junit.Test;

import infoportalinterface.InfoPortalInterface;

public class LoginLogoutTest {

	@Test
	public void test() throws Exception {

		InfoPortalInterface ip = TestInfoPortalInterfaceFactory.getInfoPortalInterface();
		ip.login();
		ip.logout();

	}

}
