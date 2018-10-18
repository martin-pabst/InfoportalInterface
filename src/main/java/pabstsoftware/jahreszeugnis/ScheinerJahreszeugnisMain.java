package pabstsoftware.jahreszeugnis;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.config.Config;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.InfoPortalInterfaceFactory;
import pabstsoftware.infoportalinterface.Klassenfilter;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.SitzungsleiterList;
import pabstsoftware.jahreszeugnis.bestenliste.Bestenliste;
import pabstsoftware.jahreszeugnis.briefe.BriefeWriter;
import pabstsoftware.jahreszeugnis.klassenkonferenzprotokoll.Klassenkonferenzprotokoll;
import pabstsoftware.jahreszeugnis.meldelisten.MeldelistenWriter;
import pabstsoftware.jahreszeugnis.notendurchschnittliste.Notendurchschnittliste;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Martin on 10.04.2017.
 */
public class ScheinerJahreszeugnisMain implements Klassenfilter {

    private InfoPortalInterface ip;

    private SitzungsleiterList sitzungsleiterListe;

    private Config config;

    private long time;

    private boolean alleKlassen = true ;

    /**
     * Für Testzwecke kann hier die Menge der Klassen eingeschränkt werden, um die Laufzeit niedrig zu halten
     *
     * @param name
     * @return
     */
    public boolean holeKlasse(String name) {

        if (alleKlassen) {
            return true;
        } else {
            if (name.toLowerCase().contains("8a")) {
                return true;
            } else {
                return false;
            }
        }

    }

    public static void main(String[] args) {
        try {

            ScheinerJahreszeugnisMain sks = new ScheinerJahreszeugnisMain();

            sks.fetchInfoportalData();

            sks.fetchSitzungsleiter();

            sks.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() {

        for (IPKlasse klasse : ip.getKlassen()) {

            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info("Schreibe die Ausgabedateien für Klasse " + klasse.getName());

            Klassenkonferenzprotokoll kp = new Klassenkonferenzprotokoll(this, klasse);
            Notendurchschnittliste ndl = new Notendurchschnittliste(this, klasse);
            BriefeWriter bw = new BriefeWriter(this, klasse);
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

        Bestenliste bestenliste = new Bestenliste(this);
        try {

            bestenliste.execute();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Logger logger = LoggerFactory.getLogger(this.getClass());
        time = System.currentTimeMillis() - time;
        time /= 1000;

        long min = time / 60;
        long s = time - min * 60;

        logger.info("Arbeiten abgeschlossen in " + min + " min " + s + " s.");


    }

    public ScheinerJahreszeugnisMain() throws Exception {

        time = System.currentTimeMillis();

        String pathname = "data/Config.xml";
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Lese Konfiguration aus der Datei " + pathname);

        File source = new File(pathname);


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

        ip.setKlassenfilter(this);

        ip.login();

        ip.fetchLehrkraefte();

        ip.fetchKlassen(ip.getLehrkraefte());

        ip.fetchAbsenzen();

        ip.fetchNoten(false);

        ip.logout();

    }
}
