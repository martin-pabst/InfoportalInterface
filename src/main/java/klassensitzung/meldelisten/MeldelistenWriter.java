package klassensitzung.meldelisten;

import infoportalinterface.model.IPKlasse;
import infoportalinterface.model.IPLehrkraft;
import infoportalinterface.model.Sitzungsleiter;
import klassensitzung.ScheinerKlassensitzung;
import klassensitzung.config.Config;
import tools.file.FileTool;
import tools.word.WordTool;

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

    private ScheinerKlassensitzung scheinerKlassensitzung;
    private IPKlasse klasse;
    private WordTool wt;

    public MeldelistenWriter(ScheinerKlassensitzung scheinerKlassensitzung, IPKlasse klasse) {
        this.scheinerKlassensitzung = scheinerKlassensitzung;
        this.klasse = klasse;
    }


    public void execute() throws IOException, URISyntaxException {

        Config config = scheinerKlassensitzung.getConfig();

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

            schreibeBrief();

            wt.write();

        }

        copyMaterialienBesonderePruefung();

    }

    private void copyMaterialienBesonderePruefung() throws IOException {

        Config config = scheinerKlassensitzung.getConfig();

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


    private void schreibeBrief() {

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

        Config config = scheinerKlassensitzung.getConfig();

        wt.replace("$DLK", config.datumlehrerkonferenz);
        wt.replace("$SJ", config.schuljahr);
        wt.replace("$DJZ", config.datumzeugnis);

        Sitzungsleiter sl = scheinerKlassensitzung.getSitzungsleiterListe().findByKlassenname(klasse.getName());
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

        Sitzungsleiter sl1 = scheinerKlassensitzung.getSitzungsleiterListe().findByKlassenname(klasse.getName());

        wt.replace("$US", sl1.getSitzungsleiter());

    }


}
