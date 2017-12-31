package pabstsoftware.jahreszeugnis.briefe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.anschriftliste.ASchueler;
import pabstsoftware.anschriftliste.Anschriftliste;
import pabstsoftware.anschriftliste.Briefdaten;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.jahreszeugnis.ScheinerKlassensitzung;
import pabstsoftware.jahreszeugnis.config.Config;
import pabstsoftware.tools.word.RowChanger;
import pabstsoftware.tools.word.WordTool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 10.04.2017.
 */
public class BriefeWriter {

    private ScheinerKlassensitzung scheinerKlassensitzung;
    private IPKlasse klasse;
    private WordTool wt;
    private Anschriftliste anschriftliste;

    public BriefeWriter(ScheinerKlassensitzung scheinerKlassensitzung, IPKlasse klasse) {
        this.scheinerKlassensitzung = scheinerKlassensitzung;
        this.klasse = klasse;
    }


    public void execute() throws IOException, URISyntaxException {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Briefe...");

        Config config = scheinerKlassensitzung.getConfig();
        anschriftliste = new Anschriftliste(config);

        for (String briefFilename : config.templates.jahreszeugnis.briefe.briefe) {

            if (briefZutreffendFuerKlasse(klasse, briefFilename)) {

                for (IPSchueler schueler : klasse.getSchuelerList()) {

                    if (briefZutreffendFuerSchueler(schueler, briefFilename)) {

                        ASchueler anschriftSchueler = anschriftliste.findSchueler(schueler);
                        if (anschriftSchueler == null) {
                            System.out.println("Für den Schüler " + schueler.toString() + " konnte keine Anschrift ermittelt werden.");
                            continue;
                        }

                        for (Briefdaten briefdaten : anschriftSchueler.getBriefdaten()) {

                            String templateFilename = config.templates.folder + "/" + config.templates.jahreszeugnis.folder + "/" + briefFilename;

                            String filenameWithoutDocx = briefFilename.substring(0, briefFilename.length() - 5);

                            String outputDir = config.outputfolder + "/" + klasse.getName() + "/Briefe_" + filenameWithoutDocx;
                            Files.createDirectories(Paths.get(outputDir));

                            String outputFilename = outputDir + "/" + filenameWithoutDocx + "_" + klasse.getName() + "_" + schueler.getFamiliennameRufname()
                                    .replace(" ", "_").replace(",", "") + "(" + briefdaten.mutterVaterBeide + ")" + ".docx";

                            wt = new WordTool(templateFilename, outputFilename);

                            schreibeBrief(schueler, briefdaten);

                            if (wt.hasHint("$FA")) {
                                schreibe5er6er(schueler);
                            }

                            wt.write();

                        }

                    }

                }

            }

        }


    }

    private boolean briefZutreffendFuerSchueler(IPSchueler schueler, String briefFilename) {

/*
        if(briefFilename.contains("olljährig") && !schueler.isVolljaehrig()){
            return false;
        }

        if(briefFilename.contains("_nv") && schueler.isVolljaehrig()){
            return false;
        }
*/

        return true;

    }

    private boolean briefZutreffendFuerKlasse(IPKlasse klasse, String briefFilename) {

        if (briefFilename.contains("Nachprüfung") && klasse.getJahrgangsstufe() == 10) {
            return false;
        }

        return true;
    }

    private void schreibe5er6er(IPSchueler schueler) {

        int jahrgangsstufe = schueler.getKlasse().getJahrgangsstufe();

        boolean mindestensEins = false;

        for (IPFach fach : schueler.getFaecher()) {
            if (fach.getFachEnum().istVorrueckungsfach(jahrgangsstufe)) {
                if (fach.getJahreszeugnisNote() != null && fach.getJahreszeugnisNote() >= 5) {
                    RowChanger rc = wt.getRowChanger("$FA");
                    rc.set("$FA", fach.getFachEnum().getAnzeigeform());
                    rc.set("$NO", "" + fach.getJahreszeugnisNote());
                    mindestensEins = true;
                }
            }
        }

        if (!mindestensEins) {
            RowChanger rc = wt.getRowChanger("$FA");
            rc.set("$FA", "TODO");
            rc.set("$NO", "TODO");
        }

    }

    private void schreibeBrief(IPSchueler schueler, Briefdaten briefdaten) {

/*
          $DLK: Datum der Lehrerkonferenz
          $DLS: Datum der Lehrersprechstunde
          $DKK: Datum der Klassenkonferenz
          $FN: Frist zur Anmeldung der Nachprüfung bzw. Besonderen Prüfung (z.b. 04.08.2017)
          $ZNP: Zeitraum der Nachprüfung
          $DJZ: Datum des Jahreszeugnisses
          $SJ: 2016/17
          $A1, ..., $A4: Anschrift
          $AR1, $AR2
          Sehr geehrte Frau ...,
          sehr geehrter Herr ...,
          $IN: Ihre Tochter/Ihr Sohn
          $IA: Ihre Tochter/Ihren Sohn
          $ID: Ihrer Tochter/Ihrem Sohn
          $IG: Ihrer Tochter/Ihres Sohnes
          $SIE: sie/ihn
          $DSA: die Schülerin/den Schüler
          $DSN: Die Schülerin/Der Schüler
          $VJSG: des volljährigen Schülers/der volljährigen Schülerin
          $DER: der/des
          $DEM: der/dem
          $SCN: Schülerin/Schüler
          $SCG: Schülerin/Schülers
          $SEG: Sie/Er
          $SEK: sie/er
          $MST: Meine Tochter/Mein Sohn
          $MSTA: meine Tochter/meinen Sohn
          $VN: Benno Beispiel
          $KL: 4a
          $KK: Der Klassenleiter/Die Klassenleiterin
          $UK: Kaspar Wieselhuber, StR
          $US: Andrea Fischer, StD
*/

        Config config = scheinerKlassensitzung.getConfig();

        wt.replace("$DLK", config.datumlehrerkonferenz);
        wt.replace("$DLS", config.templates.jahreszeugnis.datumklassenlehrersprechstunde);
        wt.replace("$FN", config.templates.jahreszeugnis.fristanmeldungnachpruefung);
        wt.replace("$SJ", config.schuljahr);
        wt.replace("$ZNP", config.templates.jahreszeugnis.zeitraumnachpruefung);
        wt.replace("$DJZ", config.templates.jahreszeugnis.datumzeugnis);

        Sitzungsleiter sl = scheinerKlassensitzung.getSitzungsleiterListe().findByKlassenname(klasse.getName());
        if(sl != null){
            wt.replace("$DKK", sl.getDatum());
        }

        ArrayList<String> anschrift = briefdaten.anschriftzeilen;

        for (int i = 1; i <= 4; i++) {

            String anschriftzeile = "";

            if (anschrift.size() >= i) {
                anschriftzeile = anschrift.get(i - 1);
            }

            wt.replace("$A" + i, anschriftzeile);
        }

        List<String> anrede = briefdaten.anredezeilen;

        for (int i = 1; i <= 2; i++) {

            String anschriftzeile = "";

            if (anrede.size() >= i) {
                anschriftzeile = anrede.get(i - 1);
            }

            wt.replace("$AR" + i, anschriftzeile);
        }

        boolean m = schueler.isMaennlich();

        wt.replace("$IN", m ? "Ihr Sohn" : "Ihre Tochter");
        wt.replace("$IA", m ? "Ihren Sohn" : "Ihre Tochter");
        wt.replace("$ID", m ? "Ihrem Sohn" : "Ihrer Tochter");
        wt.replace("$IG", m ? "Ihres Sohnes" : "Ihrer Tochter");
        wt.replace("$SIE", m ? "ihn" : "sie");
        wt.replace("$DSA", m ? "den Schüler" : "die Schülerin");
        wt.replace("$DSN", m ? "Der Schüler" : "Die Schülerin");
        wt.replace("$DEM", m ? "dem" : "der");
        wt.replace("$SCN", m ? "Schüler" : "Schülerin");
        wt.replace("$MUTS", m ? "mein/unser Sohn" : "meine/unsere Tochter");
        wt.replace("$SEG", m ? "Er" : "Sie");
        wt.replace("$SEK", m ? "er" : "sie");
        wt.replace("$MSTA", m ? "meinen Sohn" : "meine Tochter");
        wt.replace("$MST", m ? "Mein Sohn" : "Meine Tochter");
        wt.replace("$DER", m ? "des" : "der");
        wt.replace("$SCG", m ? "Schülers" : "Schülerin");
        wt.replace("$SCG", m ? "des volljährigen Schülers" : "der volljährigen Schülerin");


        wt.replace("$VN", schueler.getRufnameFamilienname());

        wt.replace("$KL", klasse.getName());

        wt.replace("$JG+2", "" + (klasse.getJahrgangsstufe() + 2));

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

        wt.replace("$US", sl1.getSitzungsleiterUnterschriftAlsSchulleitung());
        /*
                 * $KL: 4a
                * $KK: Der Klassenleiter/Die Klassenleiterin
         * $UK: Kaspar Wieselhuber, StR
         * $US: Andrea Fischer, StD
         */


    }


}
