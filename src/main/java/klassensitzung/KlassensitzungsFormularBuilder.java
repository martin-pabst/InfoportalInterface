package klassensitzung;

import infoportalinterface.InfoPortalInterface;
import infoportalinterface.model.SitzungsleiterList;
import klassensitzung.config.Config;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;

/**
 * Created by Martin on 10.04.2017.
 */
public class KlassensitzungsFormularBuilder {

    private InfoPortalInterface ip;

    private SitzungsleiterList sitzungsleiterListe;

    private Config config;


    public KlassensitzungsFormularBuilder() throws Exception {
        File source = new File("data/Config.xml");

        Serializer serializer = new Persister();
        config = serializer.read(Config.class, source);

    }

    public void fetchSitzungsleiter() throws IOException {
        sitzungsleiterListe = new SitzungsleiterList();
    }

    public void fetchInfoportalData() throws Exception {

        ip = InfoPortalInterfaceFactory.getInfoPortalInterface(config);

        ip.login();

        ip.fetchLehrkraefte();

        ip.fetchKlassen(ip.getLehrkraefte());

        ip.fetchAbsenzen();

        ip.fetchNoten();

        ip.logout();

    }
}
