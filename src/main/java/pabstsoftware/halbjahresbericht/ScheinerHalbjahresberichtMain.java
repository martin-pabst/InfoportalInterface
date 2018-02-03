package pabstsoftware.halbjahresbericht;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.config.Config;
import pabstsoftware.halbjahresbericht.briefe.BriefeWriterHalbjahr;
import pabstsoftware.halbjahresbericht.briefevorschlagliste.Notenübersicht;
import pabstsoftware.halbjahresbericht.konferenzprotokoll.Halbjahreskonferenzprotokoll;
import pabstsoftware.halbjahresbericht.konferenzprotokoll.WarnungenListe;
import pabstsoftware.halbjahresbericht.notendurchschnittliste.NotendurchschnittlisteHalbjahr;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.InfoPortalInterfaceFactory;
import pabstsoftware.infoportalinterface.Klassenfilter;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.SitzungsleiterList;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Martin on 10.04.2017.
 */
public class ScheinerHalbjahresberichtMain implements Klassenfilter{

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
    public boolean holeKlasse(String name){


        boolean alle = true;

        if(alle){
            return true;
        }


        if(name.toLowerCase().startsWith("09a")){
            return true;
        } else {
            return false;
        }

    }

    public static void main(String[] args) {
        try {

            ScheinerHalbjahresberichtMain halbjahresberichtMain = new ScheinerHalbjahresberichtMain();

            halbjahresberichtMain.fetchInfoportalData();

            halbjahresberichtMain.fetchSitzungsleiter();

            halbjahresberichtMain.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() {

        for(IPKlasse klasse: ip.getKlassen()){

            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info("Schreibe die Ausgabedateien für Klasse " + klasse.getName());

            Halbjahreskonferenzprotokoll kp = new Halbjahreskonferenzprotokoll(this, klasse);
            WarnungenListe wl = new WarnungenListe(this, klasse);
            NotendurchschnittlisteHalbjahr ndl = new NotendurchschnittlisteHalbjahr(this, klasse);
            BriefeWriterHalbjahr bw = new BriefeWriterHalbjahr(this,klasse);
            Notenübersicht bvl = new Notenübersicht(this, klasse);

            try {
                kp.execute(); // muss an erster Stelle stehen, da es die Ausgabeordner löscht
                wl.execute();
                //ndl.execute();
                bw.execute();
                bvl.execute();
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

    public ScheinerHalbjahresberichtMain() throws Exception {

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

        ip.fetchNoten();

        ip.logout();

    }
}
