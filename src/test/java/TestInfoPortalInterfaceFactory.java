import config.TestConfig;
import config.TestCredentials;
import infoportalinterface.InfoPortalInterface;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

public class TestInfoPortalInterfaceFactory {

    private static InfoPortalInterface ipi;

    public static InfoPortalInterface getInfoPortalInterface() {


        Serializer serializer = new Persister();
        File source = new File("data/TestConfig.xml");

        if (ipi == null) {

            try {

                TestConfig config = serializer.read(TestConfig.class, source);

                TestCredentials credentials = config.getCredentials();

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
