import static org.junit.Assert.*;

import org.junit.Test;

import infoportalinterface.InfoPortalInterface;

public class GetLehrkraefteUndKlassenTest {

	@Test
	public void test() throws Exception {

		InfoPortalInterface ip = TestInfoPortalInterfaceFactory.getInfoPortalInterface();
		ip.login();
		
		ip.fetchLehrkraefte();
		
		System.out.println(ip.getLehrkraefte());

		ip.fetchKlassen(ip.getLehrkraefte());
		
		ip.fetchNoten();

		System.out.println(ip.getKlassen());

		ip.logout();

	}

}
