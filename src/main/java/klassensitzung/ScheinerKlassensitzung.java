package klassensitzung;

import infoportalinterface.InfoPortalInterface;
import infoportalinterface.model.IPKlasse;
import infoportalinterface.model.SitzungsleiterList;
import klassensitzung.briefe.BriefeWriter;
import klassensitzung.config.Config;
import klassensitzung.klassenkonferenzprotokoll.Klassenkonferenzprotokoll;
import klassensitzung.meldelisten.MeldelistenWriter;
import klassensitzung.notendurchschnittliste.Notendurchschnittliste;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Martin on 10.04.2017.
 */
public class ScheinerKlassensitzung {

    private InfoPortalInterface ip;

    private SitzungsleiterList sitzungsleiterListe;

    private Config config;


    public static void main(String[] args) {
        try {

            ScheinerKlassensitzung sks = new ScheinerKlassensitzung();
            sks.fetchInfoportalData();
            sks.fetchSitzungsleiter();

            sks.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() {

        for(IPKlasse klasse: ip.getKlassen()){

            Klassenkonferenzprotokoll kp = new Klassenkonferenzprotokoll(this, klasse);
            Notendurchschnittliste ndl = new Notendurchschnittliste(this, klasse);
            BriefeWriter bw = new BriefeWriter(this,klasse);
            MeldelistenWriter mw = new MeldelistenWriter(this, klasse);

            try {
                kp.execute(); // muss an erster Stelle stehen, da es die Ausgabeordner löscht
                ndl.execute();
                bw.execute();
                mw.execute();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }


    }

    public ScheinerKlassensitzung() throws Exception {
        File source = new File("data/Config.xml");

        Serializer serializer = new Persister();
        config = serializer.read(Config.class, source);

    }

    public InfoPortalInterface getIp() {
        return ip;
    }

    public SitzungsleiterList getSitzungsleiterListe() {
        return sitzungsleiterListe;
    }

    public Config getConfig() {
        return config;
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
