package pabstsoftware.klassensitzung.klassenkonferenzprotokoll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.klassensitzung.ScheinerKlassensitzung;
import pabstsoftware.klassensitzung.config.Config;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pabstsoftware.tools.file.FileTool;
import pabstsoftware.tools.word.RowChanger;
import pabstsoftware.tools.word.WordTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by Martin on 10.04.2017.
 */
public class Klassenkonferenzprotokoll {

    private ScheinerKlassensitzung scheinerKlassensitzung;
    private IPKlasse klasse;
    private WordTool wt;

    private FileInputStream input_document;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String nichtvorrueckerOutputFilename;
    private CellStyle wrappingCellStyle;

    public Klassenkonferenzprotokoll(ScheinerKlassensitzung scheinerKlassensitzung, IPKlasse klasse) {
        this.scheinerKlassensitzung = scheinerKlassensitzung;
        this.klasse = klasse;
    }

    public void execute() throws IOException, URISyntaxException {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Klassenkonferenzprotokolle...");


        Config config = scheinerKlassensitzung.getConfig();

        /**
         * Word-Template fürs Konferenzprotokoll vorbereiten
         */

        prepareProtokollAusgabe(config);

        /**
         * Excel-Template für die Nichtvorrückerliste vorbereiten
         */
        String outputDir = prepareNichtvorrueckerliste(config);


        Collections.sort(klasse.getSchuelerList());

        schreibeSitzungsleiterDatumUhrzeit();

        schreibeLehrerDerKlasse();

        schreibeAbsenzen();

        schreibeNichtvorrueckerEmpfehlungen();

        schreibeNotenfestsetzung();

        wt.write();

        // Nichtvorrückerliste schreiben
        input_document.close();
        FileOutputStream output_file =new FileOutputStream(new File(nichtvorrueckerOutputFilename));
        //write changes
        workbook.write(output_file);
        //close the stream
        output_file.close();

        schreibeAbsenzentabelle(outputDir + "/Absenzenliste_" + klasse.getName() + ".xlsx");

    }

    private String prepareNichtvorrueckerliste(Config config) throws IOException {
        String filename = config.templates.jahreszeugnis.nichtvorrueckerliste;
        String templateFilename = config.templates.folder + "/" + config.templates.jahreszeugnis.folder + "/" + filename;

        String outputDir = config.outputfolder + "/" + klasse.getName();

        filename = filename.substring(0, filename.length() - 5);

        nichtvorrueckerOutputFilename = outputDir + "/" + filename + "_" + klasse.getName() + ".xlsx";

        //Read Excel document first
        input_document = new FileInputStream(new File(templateFilename));
        // convert it into a POI object
        workbook = new XSSFWorkbook(input_document);
        // Read excel sheet that needs to be updated
        sheet = workbook.getSheetAt(0);

        workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        return outputDir;
    }

    private void prepareProtokollAusgabe(Config config) throws IOException, URISyntaxException {
        String templateFilename = config.templates.folder + "/" + config.templates.jahreszeugnis.folder + "/" + config.templates.jahreszeugnis.klassenkonferenzprotokoll;

        String outputDir = config.outputfolder + "/" + klasse.getName();
        FileTool.deleteFolderRecursively(outputDir);
        Files.createDirectories(Paths.get(outputDir));

        String name = config.templates.jahreszeugnis.klassenkonferenzprotokoll;
        name = name.substring(0, name.length() - 5);

        String outputFilename = outputDir + "/" + name + "_" + klasse.getName() + ".docx";

        wt = new WordTool(templateFilename, outputFilename);
    }

    private void schreibeNotenfestsetzung() {

        boolean mindestensEineNotenfestsetzung = false;

        for(IPSchueler schueler: klasse.getSchuelerList()){

            for(IPFach fach: schueler.getFaecher()){

                if(fach.getjZ() != null){

                    int festgesetzteNote = (int)(fach.getjZ().getValue());
                    int berechneteNote = fach.getZeugnisnoteBerechnet();

                    if(festgesetzteNote != berechneteNote){

                        mindestensEineNotenfestsetzung = true;

                        RowChanger rc = wt.getRowChanger("$AN");
                        rc.set("$AN", schueler.getFamiliennameRufname());
                        rc.set("$AF", fach.getFachEnum().getAnzeigeform());
                        rc.set("$AE", fach.getsG().toString());
                        rc.set("$AO", "" + festgesetzteNote);
                        rc.set("$AB", "TODO");
                        rc.set("$AJN", "(.../...)");

                    }

                }

            }

        }

        if(!mindestensEineNotenfestsetzung){
            RowChanger rc = wt.getRowChanger("$AN");
            rc.set("$AN", "---");
            rc.set("$AF", "---");
            rc.set("$AE", "---");
            rc.set("$AO", "---");
            rc.set("$AB", "---");
            rc.set("$AJN", "---");

        }

    }

    private void schreibeNichtvorrueckerEmpfehlungen() {

        int fallNr = 1;

        for (IPSchueler schueler : klasse.getSchuelerList()) {

            ArrayList<IPFach> note5oder6 = new ArrayList<>();
            ArrayList<IPFach> knappe4er = new ArrayList<>();

            int anzahl5erInVorrueckungsfaechern = 0;
            int anzahl6erInVorrueckungsfaechern = 0;

            for (IPFach fach : schueler.getFaecher()) {

                if (fach.getJahreszeugnisNote() != null) {

                    if(fach.getFachEnum() == IPFachEnum.Mu){
                        if(fach.getsG().getValue() > 4.2){
                            System.out.println(schueler.getFamiliennameRufname() +
                                    fach.getFachEnum().getAnzeigeform() + ": " + fach.getsG().getValue());
                        }
                    }

                    if (fach.getJahreszeugnisNote() == 5) {
                        if (fach.getFachEnum().istVorrueckungsfach(klasse.getJahrgangsstufe())) {
                            anzahl5erInVorrueckungsfaechern++;
                        }
                        note5oder6.add(fach);
                    }

                    if (fach.getJahreszeugnisNote() == 6) {
                        if (fach.getFachEnum().istVorrueckungsfach(klasse.getJahrgangsstufe())) {
                            anzahl6erInVorrueckungsfaechern++;
                        }
                        note5oder6.add(fach);
                    }

                    if(fach.getJahreszeugnisNote() < 5 && fach.getsG().getValue() > 4.2){
                        knappe4er.add(fach);
                    }

                }

            }

            boolean nichtvorruecker = (anzahl6erInVorrueckungsfaechern > 0 || anzahl5erInVorrueckungsfaechern >= 2);

            if(nichtvorruecker){

                RowChanger rc = wt.getRowChanger("$Y");

                rc.set("$Y", "" + fallNr);
                rc.set("$XN", schueler.getFamiliennameRufname());

                Collections.sort(note5oder6);
                Collections.sort(knappe4er);

                String note5oder6Text = getNotentext(note5oder6);
                String knappe4erText = getNotentext(knappe4er);

                rc.set("$X56", note5oder6Text);
                rc.set("$X4", knappe4erText);

                String besonderePruefungNachpruefung = "---";
                if(klasse.getJahrgangsstufe() >= 6){
                    besonderePruefungNachpruefung = "NP(Ja/Nein)";
                    if(klasse.getJahrgangsstufe() == 10){
                        besonderePruefungNachpruefung = "BP(Ja/Nein)";
                    }
                }

                rc.set("$PM", besonderePruefungNachpruefung);

                rc.set("$TD", "TODO");
                rc.set("$JN", "(J/N)");
                rc.set("$ST", "(.../...)");

                /**
                 * Eintrag in die Nichtvorrückerliste schreiben
                 */

                Row row = sheet.getRow(fallNr + 1);

                if(row == null){
                    row = sheet.createRow(fallNr + 1);
                }

                setCellValue(row, 0, klasse.getName());
                setCellValue(row, 1, schueler.getFamiliennameRufname());

                note5oder6Text = getNichtvorrueckerNotentext(note5oder6);
                setCellValue(row, 2, note5oder6Text);

                row.setHeightInPoints((note5oder6.size()*sheet.getDefaultRowHeightInPoints()));

                fallNr++;

            }

        }

        if(fallNr == 1){

            RowChanger rc = wt.getRowChanger("$Y");

            rc.set("$Y", "---");
            rc.set("$XN", "---");
            rc.set("$X56", "---");
            rc.set("$X4", "---");

            rc.set("$PM", "---");
            rc.set("$TD", "---");
            rc.set("$JN", "---");
            rc.set("$KL", "---");

        }



    }

    private void setCellValue(Row row, int cellnum, String value) {

        Cell cell = row.getCell(cellnum);
        cell.setCellValue(value);
        cell.setCellStyle(wrappingCellStyle);

    }

    private String getNichtvorrueckerNotentext(ArrayList<IPFach> note5oder6) {
        return note5oder6.stream().map(fach -> fach.getFachEnum().getKurzform() +
                 " " + fach.getsG() + " -> " + fach.getJahreszeugnisNote())
                .collect(Collectors.joining("\n"));
    }

    private String getNotentext(ArrayList<IPFach> faecher) {

        return faecher.stream().map(fach -> fach.getFachEnum().getKurzform() + "(" + fach.getsG() + ")")
                .collect(Collectors.joining(", "));

    }

    private void schreibeAbsenzentabelle(String filename) {
        Workbook wb = new XSSFWorkbook();
        //Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("Absenzen");

        int rownum = 0;

        CellStyle boldStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        boldStyle.setFont(font);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("dd.mm.yyyy"));

        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(rownum++);

        createCell(row, 0, boldStyle, "Familienname, Rufname");
        createCell(row, 1, boldStyle, "Von");
        createCell(row, 2, boldStyle, "Bis");
        createCell(row, 3, boldStyle, "Tage");
        createCell(row, 4, boldStyle, "Stunden");
        createCell(row, 5, boldStyle, "Art");

        for (IPSchueler schueler : klasse.getSchuelerList()) {
            for (IPAbsenz absenz : schueler.getAbsenzen()) {
                // Create a row and put some cells in it. Rows are 0 based.
                row = sheet.createRow(rownum++);

                row.createCell(0).setCellValue(schueler.getFamiliennameRufname());
                if (absenz.getVon() != null) {
                    Cell dc1 = row.createCell(1);
                    dc1.setCellValue(absenz.getVon());
                    dc1.setCellStyle(dateStyle);
                }
                if (absenz.getBis() != null) {
                    Cell dc1 = row.createCell(2);
                    dc1.setCellValue(absenz.getBis());
                    dc1.setCellStyle(dateStyle);
                }
                row.createCell(3).setCellValue(absenz.getTage());
                row.createCell(4).setCellValue(absenz.getStunden());
                row.createCell(5).setCellValue(absenz.getArtText());

            }
        }

        for (int i = 0; i <= 5; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filename);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createCell(Row row, int column, CellStyle style, String value) {
        Cell c = row.createCell(column);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private void schreibeAbsenzen() {

        ArrayList<IPSchueler> laengereErkrankungen = new ArrayList<>(); // Schüler mit mindestens einem Fehlintervall > 10 Tage
        ArrayList<IPSchueler> haeufigeVersaeumnisse = new ArrayList<>(); // Schüler mit mehr als 10 Tagen insgesamt


        for (IPSchueler schueler : klasse.getSchuelerList()) {

            int tage = 0;
            int stundenweise = 0;

            boolean laengereErkrankung = false;

            for (IPAbsenz absenz : schueler.getAbsenzen()) {

                tage += absenz.getTage();

                if (absenz.getStunden() > 0) {
                    stundenweise++;
                }

                if (absenz.getTage() > 10) {
                    laengereErkrankung = true;
                }

            }

            schueler.setAbsenzStundenGesamt(stundenweise);
            schueler.setAbsenzTageGesamt(tage);

            if (laengereErkrankung) {
                laengereErkrankungen.add(schueler);
            } else {
                if (tage + stundenweise / 2 > 10) {
                    haeufigeVersaeumnisse.add(schueler);
                }
            }

        }

        Collections.sort(laengereErkrankungen);
        Collections.sort(haeufigeVersaeumnisse);

        for (IPSchueler schueler : laengereErkrankungen) {
            RowChanger rc = wt.getRowChanger("$EN");
            rc.set("$EN", schueler.getFamiliennameRufname());
            rc.set("$ET", "" + schueler.getAbsenzTageGesamt() + " Tage und " + schueler.getAbsenzStundenGesamt() + "-mal stundenweise");
        }

        if (laengereErkrankungen.size() == 0) {
            RowChanger rc = wt.getRowChanger("$EN");
            rc.set("$EN", "---");
            rc.set("$ET", "---");
        }

        for (IPSchueler schueler : haeufigeVersaeumnisse) {
            RowChanger rc = wt.getRowChanger("$HN");
            rc.set("$HN", schueler.getFamiliennameRufname());
            rc.set("$HT", "" + schueler.getAbsenzTageGesamt() + " Tage und " + schueler.getAbsenzStundenGesamt() + "-mal stundenweise");
        }

        if (haeufigeVersaeumnisse.size() == 0) {
            RowChanger rc = wt.getRowChanger("$HN");
            rc.set("$HN", "---");
            rc.set("$HT", "---");
        }

    }


    private void schreibeSitzungsleiterDatumUhrzeit() {

        wt.replace("$SJ", scheinerKlassensitzung.getConfig().schuljahr);

        Sitzungsleiter sl = scheinerKlassensitzung.getSitzungsleiterListe().findByKlassenname(klasse.getName());

        wt.replace("$DA", sl.getDatum());
        wt.replace("$VO", sl.getVon());
        wt.replace("$BI", sl.getBis());
        wt.replace("$KL", klasse.getName());


        String klassenleiter = "";

        if(klasse.getKlassenleitung1() != null){

            klassenleiter += klasse.getKlassenleitung1().getNameMitDienstgrad();
        } else {
            System.out.println("Die Klasse " + klasse.getName() + " hat keinen Klassenleiter!");
        }

        if (klasse.getKlassenleitung2() != null) {
            klassenleiter += "; " + klasse.getKlassenleitung2().getNameMitDienstgrad();
        }

        wt.replace("$KT", klassenleiter);

        String klassenleiterUnterzeichner = "";
        String klassenleiterIn = "(Klassenleiter/in)";

        if(klasse.getKlassenleitung1() != null){
            klassenleiterUnterzeichner = klasse.getKlassenleitung1().getUnterzeichnername();
            klassenleiterIn = "(" + klasse.getKlassenleitung1().getKlassenleiterIn() + ")";
        }


        wt.replace("$K1(", klassenleiterIn);
        wt.replace("$K1", klassenleiterUnterzeichner);



        wt.replace("$SZ", "" + klasse.getSchuelerList().size());

        wt.replace("$SL(", "(" + sl.getSitzungsleiterIn() + ")");
        wt.replace("$SL", sl.getSitzungsleiterUnterschriftKlassensitzung());


    }

    private void schreibeLehrerDerKlasse() {

        IPKlassenteam klassenteam = klasse.getKlassenteam();

        ArrayList<LehrkraftImFach> lehrkraftImFachListe = new ArrayList<>();
        HashMap<IPLehrkraft, LehrkraftImFach> lehrkraftMap = new HashMap<>();

        klassenteam.getKlassenteamMap().forEach((ipFach, lehrkraefte) -> {

            for (IPLehrkraft lehrkraft : lehrkraefte) {

                boolean stimmberechtigt = !(lehrkraefte.size() > 1 && lehrkraft.getStundenplan_id().length() == 4);

                LehrkraftImFach lkImFach = lehrkraftMap.get(lehrkraft);

                if (lkImFach == null) {

                    lkImFach = new LehrkraftImFach(lehrkraft, stimmberechtigt);
                    lehrkraftImFachListe.add(lkImFach);
                    lehrkraftMap.put(lehrkraft, lkImFach);

                }

                lkImFach.addFach(ipFach);

            }

        });

        lehrkraftImFachListe.forEach(lkf -> lkf.bereinigeSport());

        Collections.sort(lehrkraftImFachListe);

        for (int i = 0; i < lehrkraftImFachListe.size(); i += 2) {

            RowChanger rc = wt.getRowChanger("$L1");
            LehrkraftImFach lkLinkeSeite = lehrkraftImFachListe.get(i);
            rc.set("$L1", "" + (i + 1) + ". " + lkLinkeSeite.getLkListeName());

            if (i + 1 < lehrkraftImFachListe.size()) {

                LehrkraftImFach lkRechteSeite = lehrkraftImFachListe.get(i + 1);
                rc.set("$L2", "" + (i + 2) + ". " + lkRechteSeite.getLkListeName());

            } else {
                rc.set("$L2", "");
            }

        }


    }


}
