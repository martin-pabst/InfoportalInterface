package pabstsoftware.klassensitzung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.SitzungsleiterList;
import pabstsoftware.klassensitzung.briefe.BriefeWriter;
import pabstsoftware.klassensitzung.config.Config;
import pabstsoftware.klassensitzung.klassenkonferenzprotokoll.Klassenkonferenzprotokoll;
import pabstsoftware.klassensitzung.meldelisten.MeldelistenWriter;
import pabstsoftware.klassensitzung.notendurchschnittliste.Notendurchschnittliste;
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

    private long time;

    /**
     * Für Testzwecke kann hier die Menge der Klassen eingeschränkt werden, um die Laufzeit niedrig zu halten
     *
     * @param name
     * @return
     */
    public static boolean holeKlasse(String name){



/*
        if(name.toLowerCase().startsWith("07a")){
            return true;
        } else {
            return false;
        }
*/


        return true;
    }

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

            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info("Schreibe die Ausgabedateien für Klasse " + klasse.getName());

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

        Logger logger = LoggerFactory.getLogger(this.getClass());
        time = System.currentTimeMillis() - time;
        time /= 1000;

        long min = time/60;
        long s = time - min*60;

        logger.info("Arbeiten abgeschlossen in " + min + " min " + s + " s.");


    }

    public ScheinerKlassensitzung() throws Exception {

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

        ip.login();

        ip.fetchLehrkraefte();

        ip.fetchKlassen(ip.getLehrkraefte());

        ip.fetchAbsenzen();

        ip.fetchNoten();

        ip.logout();

    }
}
