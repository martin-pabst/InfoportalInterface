import infoportalinterface.InfoPortalInterface;

public class TestInfoPortalInterfaceFactory {

	private static InfoPortalInterface ipi;

	public static InfoPortalInterface getInfoPortalInterface() {

		if (ipi == null) {
			ipi = new InfoPortalInterface("martin.pabst", "Schrobenhausen1mp",
					"https://portal.mzml.de/portal/csgying/schule_portal/index_dir/index.php");
		}

		return ipi;

	}
}
