import org.junit.Test;

import pabstsoftware.infoportalinterface.InfoPortalInterface;

public class GetLehrkraefteUndKlassenTest {

	@Test
	public void test() throws Exception {

		InfoPortalInterface ip = TestInfoPortalInterfaceFactory.getInfoPortalInterface();
		ip.login();
		
		ip.fetchLehrkraefte();
		
		System.out.println(ip.getLehrkraefte());

		ip.fetchKlassen(ip.getLehrkraefte());

		System.out.println(ip.getKlassen());

        ip.fetchAbsenzen();
        System.out.println("\nAbsenzen fertig geholt!\n");

        ip.fetchNoten();
        System.out.println("\nNoten fertig geholt!\n");

		ip.logout();

	}

}
