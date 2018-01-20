package pabstsoftware.halbjahresbericht.briefevorschlagliste;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.config.Config;
import pabstsoftware.halbjahresbericht.ScheinerHalbjahresberichtMain;
import pabstsoftware.infoportalinterface.model.IPFach;
import pabstsoftware.infoportalinterface.model.IPFachEnum;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPSchueler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class BriefeVorschlagListe {


    private ScheinerHalbjahresberichtMain halbjahresberichtMain;
    private IPKlasse klasse;

    private XSSFWorkbook workbook;

    private String outputDir;

    public HashMap<String, CellStyle> cellstyles = new HashMap<>();

    private short[] colorIndices = new short[]{
            IndexedColors.LIGHT_BLUE.getIndex(), // 1,00 - 2,00
            IndexedColors.LIGHT_GREEN.getIndex(),      // 2,01 - 3,00
            IndexedColors.GREEN.getIndex(),     // 3,01 - 4,00
            IndexedColors.YELLOW.getIndex(),     // 3,01 - 4,50
            IndexedColors.ORANGE.getIndex(),        // 4,51 - 5,50
            IndexedColors.RED.getIndex()    // 5,51 - 6,00
    };


    public BriefeVorschlagListe(ScheinerHalbjahresberichtMain halbjahresberichtMain, IPKlasse klasse) {

        this.halbjahresberichtMain = halbjahresberichtMain;
        this.klasse = klasse;

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Liste mit Briefvorschlägen...");


        Config config = halbjahresberichtMain.getConfig();

        outputDir = config.outputfolder + "/" + klasse.getName();

        Collections.sort(klasse.getSchuelerList());
    }

    public void execute() throws IOException {

        prepareAuswertungWorkbook(halbjahresberichtMain.getConfig());

        werteAus();

        writeAuswertungWorkbook(halbjahresberichtMain.getConfig());

    }

    private void werteAus() {


        Sheet sheet = workbook.createSheet("Notenliste");

        int rowNum = 0;
        int colNum = 0;

        Row row = sheet.createRow(rowNum++);
        row.setHeightInPoints(30);

        String[] headers = new String[]{"Klasse", "Familienname", "Rufname"};

        for (String header : headers) {

            Cell cell = row.createCell(colNum++);

            cell.setCellValue(header);

        }

        HashMap<IPFachEnum, Integer> fachToColumnMap = new HashMap<>();

        for (IPFachEnum ipFachEnum : IPFachEnum.values()) {

            fachToColumnMap.put(ipFachEnum, colNum);
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(ipFachEnum.getKurzform());

        }

        row.createCell(colNum++).setCellValue("Ø");

        colNum++;

        row.createCell(colNum++).setCellValue("Brief (X)");
        row.createCell(colNum++).setCellValue("Gefährdung\n(bwa/g/sg)");
        row.createCell(colNum++).setCellValue("Darf wiederholen\n(j/n)");
        row.createCell(colNum++).setCellValue("Gespräch empfohlen\n(Sz/Kl/FLxy)");
        row.createCell(colNum++).setCellValue("konzentrierte\nArbeitsweise");
        row.createCell(colNum++).setCellValue("gesteigerter\nArbeitseinsatz");


        for (int i = 0; i < IPFachEnum.values().length + 11; i++) {
            row.getCell(i).setCellStyle(cellstyles.get("bold"));
        }

        for (IPSchueler schueler : klasse.getSchuelerList()) {

            row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(klasse.getName());
            row.createCell(1).setCellValue(schueler.getFamilienname());
            row.createCell(2).setCellValue(schueler.getRufname());

            int i = 3;
            for (IPFachEnum ipFachEnum : IPFachEnum.values()) {

                IPFach fach = schueler.getFach(ipFachEnum);

                if (fach != null && fach.getsG() != null && fach.getsG().getValue() > 0.01) {
                    Cell cell = row.createCell(i++);
                    cell.setCellValue(fach.getsG().getValue());
                    cell.setCellStyle(cellstyles.get("bold"));
                } else {
                    row.createCell(i++).setCellValue("---");
                }

            }


            Cell cell = row.createCell(i++);
            cell.setCellValue(schueler.getDurchschnittVorrueckungsfaecher2Dez());

            i++;

            String brief = (schueler.isBeiWeiteremAbsinken() || schueler.isGefährdet() || schueler.isSehrGefährdet()) ? "X" : "";
            row.createCell(i++).setCellValue(brief);

            String gefährdung = "";
            if(schueler.isBeiWeiteremAbsinken()){gefährdung = "bwa";}
            if(schueler.isGefährdet()){gefährdung = "g";}
            if(schueler.isSehrGefährdet()){gefährdung = "sg";}
            row.createCell(i++).setCellValue(gefährdung);

            String darfWiederholen = schueler.darfWiederholen() ? "j" : "n";
            row.createCell(i++).setCellValue(darfWiederholen);

            String fachlehrer = "";
            for (IPFach ipFach : schueler.getFaecher()) {
                if(ipFach.getsG() != null && ipFach.getsG().getValue() > 4.3){
                    fachlehrer += ipFach.getFachEnum().getKurzform() + ", ";
                }
            }

            if(fachlehrer.endsWith(", ")){
                fachlehrer = fachlehrer.substring(0, fachlehrer.length() - 2);
            }

            if(!fachlehrer.isEmpty()){
                fachlehrer = "FL(" + fachlehrer + ")";
            }

            if(gefährdung.isEmpty()){
                fachlehrer = "";
            }

            row.createCell(i++).setCellValue(fachlehrer);


        }


        String[] bisNote = new String[]{
                "0.99", "1.50", "2.50", "3.50", "4.50", "5.50", "6.01"
        };

        int schuelerAnzahl = klasse.getSchuelerList().size();

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        for (int i = 0; i < colorIndices.length; i++) {
            ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, bisNote[i], bisNote[i + 1]);
            PatternFormatting fill1 = rule1.createPatternFormatting();

            if (colorIndices[i] != IndexedColors.LIGHT_BLUE.getIndex()) {
                fill1.setFillBackgroundColor(colorIndices[i]);
            } else {
                fill1.setFillBackgroundColor(new XSSFColor(new java.awt.Color(180, 180, 255)));
            }

            if (colorIndices[i] == IndexedColors.RED.getIndex()) {
                rule1.createFontFormatting().setFontStyle(false, true);
            }


            fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            String range = "D1" + ":" + CellReference.convertNumToColString(IPFachEnum.values().length + 2)
                    + (schuelerAnzahl + 1);

            CellRangeAddress[] regions = {CellRangeAddress.valueOf(range)};
            sheetCF.addConditionalFormatting(regions, rule1);
        }


        for (int i = 0; i < IPFachEnum.values().length + 11; i++) {
            sheet.autoSizeColumn(i);
        }

        sheet.createFreezePane(3, 1);


    }



    private Workbook prepareAuswertungWorkbook(Config config) throws IOException {

        // convert it into a POI object
        workbook = new XSSFWorkbook();

        workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        CellStyle styleBold = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        styleBold.setFont(font);
        styleBold.setWrapText(true);

        cellstyles.put("bold", styleBold);

        return workbook;
    }

    private void writeAuswertungWorkbook(Config config) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String heute = sdf.format(Calendar.getInstance().getTime());

        String filename = outputDir + "/Briefvorschläge_" + klasse.getName() + ".xlsx";

        try {

            workbook.write(new FileOutputStream(new File(filename)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Workbook getWorkbook() {
        return workbook;
    }


}
