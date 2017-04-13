package pabstsoftware.klassensitzung;

import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.klassensitzung.config.Config;
import pabstsoftware.klassensitzung.config.Credentials;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

public class InfoPortalInterfaceFactory {

    private static InfoPortalInterface ipi;

    public static InfoPortalInterface getInfoPortalInterface(Config config) {


        Serializer serializer = new Persister();
        File source = new File("data/Config.xml");

        if (ipi == null) {

            try {


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
