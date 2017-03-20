import infoportalinterface.InfoPortalInterface;

public class TestInfoPortalInterfaceFactory {

	private static InfoPortalInterface ipi;

	public static InfoPortalInterface getInfoPortalInterface() {

		if (ipi == null) {
			ipi = new InfoPortalInterface("", "",
					"");
		}

		return ipi;

	}
}
