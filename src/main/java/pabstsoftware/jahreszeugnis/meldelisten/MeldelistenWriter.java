package pabstsoftware.jahreszeugnis.meldelisten;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPLehrkraft;
import pabstsoftware.infoportalinterface.model.Sitzungsleiter;
import pabstsoftware.jahreszeugnis.ScheinerJahreszeugnisMain;
import pabstsoftware.config.Config;
import pabstsoftware.tools.file.FileTool;
import pabstsoftware.tools.word.WordTool;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Created by Martin on 10.04.2017.
 */
public class MeldelistenWriter {

    private ScheinerJahreszeugnisMain scheinerJahreszeugnisMain;
    private IPKlasse klasse;
    private WordTool wt;

    public MeldelistenWriter(ScheinerJahreszeugnisMain scheinerJahreszeugnisMain, IPKlasse klasse) {
        this.scheinerJahreszeugnisMain = scheinerJahreszeugnisMain;
        this.klasse = klasse;
    }


    public void execute() throws IOException, URISyntaxException {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Meldelisten...");

        Config config = scheinerJahreszeugnisMain.getConfig();

        String templateDirectory = config.templates.folder + "/" +
                config.templates.jahreszeugnis.folder + "/Ein_Exemplar_je_Klasse";

        List<String> templates = FileTool.fileList(templateDirectory);

        for (String template : templates) {

            String filenameWithoutDocx = template.substring(0, template.length() - 5);

            filenameWithoutDocx = filenameWithoutDocx.replace("\\", "/");

            int i = filenameWithoutDocx.lastIndexOf("/");
            filenameWithoutDocx = filenameWithoutDocx.substring(i + 1);

            String outputDir = config.outputfolder + "/" + klasse.getName();
            Files.createDirectories(Paths.get(outputDir));

            String outputFilename = outputDir + "/" + filenameWithoutDocx + "_" + klasse.getName() + ".docx";

            wt = new WordTool(template, outputFilename);

            schreibeBrief(filenameWithoutDocx);

            wt.write();

        }

        copyMaterialienBesonderePruefung();

    }

    private void copyMaterialienBesonderePruefung() throws IOException {

        Config config = scheinerJahreszeugnisMain.getConfig();

        String templateDirectory = config.templates.folder + "/" +
                config.templates.jahreszeugnis.folder + "/Material_besondere_Prüfung";

        List<String> templates = FileTool.fileList(templateDirectory);

        for (String template : templates) {

            String outputFilename = template.replace("\\", "/");

            int i = outputFilename.lastIndexOf("/");
            outputFilename = outputFilename.substring(i + 1);

            String outputDir = config.outputfolder + "/" + klasse.getName();
            Files.createDirectories(Paths.get(outputDir));

            outputFilename = outputDir + "/" + outputFilename;

            Files.copy(new File(template).toPath(), new File(outputFilename).toPath(), StandardCopyOption.REPLACE_EXISTING);

        }

    }


    private void schreibeBrief(String filenameWithoutDocx) {

/*
          $DLK: Datum der Lehrerkonferenz
          $DKK: Datum der Klassenkonferenz
          $DJZ: Datum des Jahreszeugnisses
          $SJ: 2016/17
          $KL: 4a
          $KK: Der Klassenleiter/Die Klassenleiterin
          $UK: Kaspar Wieselhuber, StR
          $US: Andrea Fischer, StD
*/

        Config config = scheinerJahreszeugnisMain.getConfig();

        wt.replace("$DLK", config.datumlehrerkonferenz);
        wt.replace("$SJ", config.schuljahr);
        wt.replace("$DJZ", config.templates.jahreszeugnis.datumzeugnis);

        Sitzungsleiter sl = scheinerJahreszeugnisMain.getSitzungsleiterListe().findByKlassenname(klasse.getName());
        if (sl != null) {
            wt.replace("$DKK", sl.getDatum());
        }

        wt.replace("$KL", klasse.getName());

        if (klasse.getKlassenleitung1() != null) {

            IPLehrkraft kl = klasse.getKlassenleitung1();

            if (kl.getDienstgrad() != null) {

                if (kl.getDienstgrad().contains("in") || kl.getDienstgrad().contains("LAv")) {
                    wt.replace("$KK", "Die Klassenleiterin");
                } else {
                    wt.replace("$KK", "Der Klassenleiter");
                }

                wt.replace("$UK", kl.getUnterzeichnername());

            } else {
                wt.replace("$KK", "Der Klassenleiter/die Klassenleiterin");
            }

        } else {
            System.out.println("Die Klasse " + klasse.getName() + " hat keinen Klassenleiter!");
            wt.replace("$KK", "Der Klassenleiter/die Klassenleiterin");
        }

        Sitzungsleiter sl1 = scheinerJahreszeugnisMain.getSitzungsleiterListe().findByKlassenname(klasse.getName());

        if(filenameWithoutDocx.contains("Bescheinigung")){
            wt.replace("$US", sl1.getSitzungsleiterUnterschriftAlsSchulleitung());
        } else {
            wt.replace("$US", sl1.getSitzungsleiterUnterschriftKlassensitzung());
        }


    }


}
