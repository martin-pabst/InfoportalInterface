package pabstsoftware.klassensitzung.notendurchschnittliste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPSchueler;
import pabstsoftware.klassensitzung.ScheinerKlassensitzung;
import pabstsoftware.klassensitzung.config.Config;
import pabstsoftware.tools.word.RowChanger;
import pabstsoftware.tools.word.WordTool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Martin on 10.04.2017.
 */
public class Notendurchschnittliste {

    private ScheinerKlassensitzung scheinerKlassensitzung;
    private IPKlasse klasse;
    private WordTool wt;

    public Notendurchschnittliste(ScheinerKlassensitzung scheinerKlassensitzung, IPKlasse klasse) {
        this.scheinerKlassensitzung = scheinerKlassensitzung;
        this.klasse = klasse;
    }

    public void execute() throws IOException, URISyntaxException {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Listen mit Notendurchschnitten...");


        Config config = scheinerKlassensitzung.getConfig();

        String templateFilename = config.templates.folder + "/" + config.templates.jahreszeugnis.folder + "/" + config.templates.jahreszeugnis.notendurchschnittliste;

        String outputDir = config.outputfolder + "/" + klasse.getName();
        Files.createDirectories(Paths.get(outputDir));

        String name = config.templates.jahreszeugnis.notendurchschnittliste;
        name = name.substring(0, name.length() - 5);

        String outputFilename = outputDir + "/" + name + "_" + klasse.getName() + ".docx";

        wt = new WordTool(templateFilename, outputFilename);

        schreibeSchuljahrKlasse();

        schreibeNotenlisteAlphabetisch();

        schreibeNotenlisteRangfolge();

        wt.write();

    }

    private void schreibeNotenlisteRangfolge() {

        Collections.sort(klasse.getSchuelerList(), new Comparator<IPSchueler>(){

            @Override
            public int compare(IPSchueler s1, IPSchueler s2) {
                return s1.getDurchschnittVorrueckungsfaecher().compareTo(s2.getDurchschnittVorrueckungsfaecher());
            }

        });

        int nr = 1;

        for(IPSchueler schueler: klasse.getSchuelerList()){

            RowChanger rc = wt.getRowChanger("$N2");
            rc.set("$R2", "" + nr++);
            rc.set("$N2", schueler.getFamiliennameRufname());
            rc.set("$D2", schueler.getDurchschnittVorrueckungsfaecher2Dez());

        }

    }

    private void schreibeNotenlisteAlphabetisch() {

        Collections.sort(klasse.getSchuelerList());

        int nr = 1;

        for(IPSchueler schueler: klasse.getSchuelerList()){

            RowChanger rc = wt.getRowChanger("$N1");
            rc.set("$R1", "" + nr++);
            rc.set("$N1", schueler.getFamiliennameRufname());
            rc.set("$D1", schueler.getDurchschnittVorrueckungsfaecher2Dez());

        }

    }


    private void schreibeSchuljahrKlasse() {

        wt.replace("$SJ", scheinerKlassensitzung.getConfig().schuljahr);

        wt.replace("$KL", klasse.getName());

    }



}
