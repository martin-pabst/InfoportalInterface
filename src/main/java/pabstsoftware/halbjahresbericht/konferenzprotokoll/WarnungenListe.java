package pabstsoftware.halbjahresbericht.konferenzprotokoll;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.config.Config;
import pabstsoftware.halbjahresbericht.ScheinerHalbjahresberichtMain;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.tools.word.RowChanger;
import pabstsoftware.tools.word.WordTool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by Martin on 10.04.2017.
 */
public class WarnungenListe {

    private ScheinerHalbjahresberichtMain halbjahresberichtMain;
    private IPKlasse klasse;
    private WordTool wt;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String nichtvorrueckerOutputFilename;
    private CellStyle wrappingCellStyle;
    private String outputDir;


    public WarnungenListe(ScheinerHalbjahresberichtMain halbjahresberichtMain, IPKlasse klasse) {
        this.halbjahresberichtMain = halbjahresberichtMain;
        this.klasse = klasse;
    }

    public void execute() throws IOException, URISyntaxException {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Liste mit Warnungen ...");


        Config config = halbjahresberichtMain.getConfig();

        outputDir = config.outputfolder + "/" + klasse.getName();


        /**
         * Word-Template fürs Konferenzprotokoll vorbereiten
         */

        prepareProtokollAusgabe(config);


        Collections.sort(klasse.getSchuelerList());

        schreibeSitzungsleiterDatumUhrzeit();

        schreibeLehrerDerKlasse();

        schreibeAbsenzen();

        schreibeEmpfehlungen();

        wt.write();

        schreibeAbsenzentabelle(outputDir + "/Absenzenliste_" + klasse.getName() + ".xlsx");

    }

    private void prepareProtokollAusgabe(Config config) throws IOException, URISyntaxException {
        String templateFilename = config.templates.folder + "/" + config.templates.halbjahresbericht.folder +
                "/" + config.templates.halbjahresbericht.warnungen;

        String name = config.templates.halbjahresbericht.warnungen;
        name = name.substring(0, name.length() - 5);

        String outputFilename = outputDir + "/" + name + "_" + klasse.getName() + ".docx";

        wt = new WordTool(templateFilename, outputFilename);
    }


    private void schreibeEmpfehlungen() {

        int fallNr = 1;

        for (IPSchueler schueler : klasse.getSchuelerList()) {

            ArrayList<IPFach> note5oder6 = new ArrayList<>();
            ArrayList<IPFach> knappe4er = new ArrayList<>();

            int anzahl5erInVorrueckungsfaechern = 0;
            int anzahl6erInVorrueckungsfaechern = 0;

            for (IPFach fach : schueler.getFaecher()) {

                if (fach.getsG() != null) {

                    if(fach.getFachEnum() == IPFachEnum.Mu){
                        if(fach.getsG().getValue() > 4.2){
                            System.out.println(schueler.getFamiliennameRufname() + " " +
                                    fach.getFachEnum().getAnzeigeform() + ": " + fach.getsG().getValue()
                             + klasse.getKlassenteam().getKlassenteamMap().get(fach.getFachEnum()).toString());
                        }
                    }

                    if (fach.getsG().getValue() >= 4.51 && fach.getsG().getValue() < 5.5) {
                        if (fach.getFachEnum().istVorrueckungsfach(klasse.getJahrgangsstufe())) {
                            anzahl5erInVorrueckungsfaechern++;
                        }
                        note5oder6.add(fach);
                    }

                    if (fach.getsG().getValue() >= 5.5) {
                        if (fach.getFachEnum().istVorrueckungsfach(klasse.getJahrgangsstufe())) {
                            anzahl6erInVorrueckungsfaechern++;
                        }
                        note5oder6.add(fach);
                    }

                    if(fach.getsG().getValue() < 4.51 && fach.getsG().getValue() >= 4.3){
                        knappe4er.add(fach);
                    }

                }

            }

            boolean sehrGefährdet = (anzahl6erInVorrueckungsfaechern > 0 || anzahl5erInVorrueckungsfaechern >= 2);
            boolean gefährdet = !sehrGefährdet && (anzahl5erInVorrueckungsfaechern >= 1 || knappe4er.size() >= 3);
            boolean beiWeiteremAbsinken = !sehrGefährdet && !gefährdet && (knappe4er.size() >= 2);

            schueler.setSehrGefährdet(sehrGefährdet);
            schueler.setGefährdet(gefährdet);
            schueler.setBeiWeiteremAbsinken(beiWeiteremAbsinken);

            if(sehrGefährdet || gefährdet || beiWeiteremAbsinken){

                RowChanger rc = wt.getRowChanger("$Y");


                rc.set("$Y", "" + fallNr);
                rc.set("$XN", schueler.getFamiliennameRufname());

                rc.set("$SG", sehrGefährdet ? "X" : "");
                rc.set("$G", gefährdet ? "X" : "");
                rc.set("$BW", beiWeiteremAbsinken ? "X" : "");

                Collections.sort(note5oder6);
                Collections.sort(knappe4er);

                schueler.addSchlechteNoten(note5oder6);
                schueler.addSchlechteNoten(knappe4er);


                String note5oder6Text = getNotentext(note5oder6);
                String knappe4erText = getNotentext(knappe4er);

                rc.set("$X56", note5oder6Text);
                rc.set("$X4", knappe4erText);

                rc.set("$AG1", schueler.darfWiederholen() ? "" : "X");
                rc.set("$AG2", schueler.getWiederholungen().size() < 2 ? "" : "X");

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
                if (tage + stundenweise / 2 > 5) {
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

        wt.replace("$SJ", halbjahresberichtMain.getConfig().schuljahr);

        Sitzungsleiter sl = halbjahresberichtMain.getSitzungsleiterListe().findByKlassenname(klasse.getName());

        if(sl == null){
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("Fehler: Habe keinen Sitzungsleiter für die Klasse " + klasse.getName() + " gefunden!");
        }


        wt.replace("$DA", halbjahresberichtMain.getConfig().templates.halbjahresbericht.datumnotenbildbericht);
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

        wt.replace("$SL(", "(Schulleitung)");
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
