import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.config.Config;
import pabstsoftware.config.Credentials;

import java.io.File;

public class TestInfoPortalInterfaceFactory {

    private static InfoPortalInterface ipi;

    public static InfoPortalInterface getInfoPortalInterface() {


        Serializer serializer = new Persister();
        File source = new File("data/Config.xml");

        if (ipi == null) {

            try {

                Config config = serializer.read(Config.class, source);

                Credentials credentials = config.getCredentials();

                ipi = new InfoPortalInterface(credentials.username, credentials.password,
                      credentials.baseurl);

                return ipi;

            } catch (Exception e) {

                return null;

            }

        } else {
            return ipi;
        }

    }
}
