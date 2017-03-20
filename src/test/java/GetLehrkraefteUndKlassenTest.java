import static org.junit.Assert.*;

import org.junit.Test;

import infoportalinterface.InfoPortalInterface;

public class GetLehrkraefteUndKlassenTest {

	@Test
	public void test() throws Exception {

		InfoPortalInterface ip = TestInfoPortalInterfaceFactory.getInfoPortalInterface();
		String mainPage = ip.login();
		
		ip.fetchLehrkraefte(mainPage);
		
		System.out.println(ip.getLehrkraefte());

		ip.fetchKlassen(mainPage, ip.getLehrkraefte());
		
		ip.fetchNoten(mainPage);

		System.out.println(ip.getKlassen());

		ip.logout();

	}

}
