package pabstsoftware.auswertung;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPSchueler;

public class SchriftlicheLeistungsnachweisAuswertung {

    private Workbook wb;
    private ScheinerAuswertung scheinerAuswertung;
    private InfoPortalInterface ip;

    private short[] colorIndices = new short[]{
            IndexedColors.LIGHT_BLUE.getIndex(), // 1,00 - 2,00
            IndexedColors.LIGHT_GREEN.getIndex(),      // 2,01 - 3,00
            IndexedColors.GREEN.getIndex(),     // 3,01 - 4,00
            IndexedColors.YELLOW.getIndex(),     // 3,01 - 4,50
            IndexedColors.ORANGE.getIndex(),        // 4,51 - 5,50
            IndexedColors.RED.getIndex()    // 5,51 - 6,00
    };


    public SchriftlicheLeistungsnachweisAuswertung(Workbook workbook, ScheinerAuswertung scheinerAuswertung, InfoPortalInterface ip) {
        this.wb = workbook;
        this.scheinerAuswertung = scheinerAuswertung;
        this.ip = ip;
    }

    public void execute() {

        Sheet sheet = wb.createSheet("Schriftliche Leistungserhebungen");
        Drawing drawing = sheet.createDrawingPatriarch();
        CreationHelper factory = wb.getCreationHelper();

        int rowNum = 0;
        int colNum = 0;

        Row row = sheet.createRow(rowNum++);

        Cell cell = row.createCell(0);
        cell.setCellStyle(scheinerAuswertung.cellstyles.get("bold"));
        cell.setCellValue("Schriftliche Leistungserhebungen");

        row = sheet.createRow(rowNum++);

        String[] headers = new String[]{"Jahrgangsstufe", "Klasse", "Fach", "Lehrkraft", "Art", "Nr.", "Datum",
                    "1", "2", "3", "4", "5", "6", "Durchschnitt"};

        for (String header : headers) {

            cell = row.createCell(colNum++);
            cell.setCellValue(header);
            cell.setCellStyle(scheinerAuswertung.cellstyles.get("bold"));

        }

        SchriftlicherLeistungsnachweisStore store = new SchriftlicherLeistungsnachweisStore();
        for (IPKlasse ipKlasse : ip.getKlassen()) {
            for (IPSchueler ipSchueler : ipKlasse.getSchuelerList()) {
                store.analysiere(ipSchueler, ip.getKoppelgruppen());
            }
        }

        for (SchriftlicherLeistungsnachweis lnw : store) {
            row = sheet.createRow(rowNum++);
            colNum = 0;

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getJahrgangsstufe());

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getKlasse().getName());

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getFach().getKurzform());

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getLehrkraft().getStundenplan_id());

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getArt().toString());

            cell = row.createCell(colNum++);
            cell.setCellValue(1);

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getDatum());
            cell.setCellStyle(scheinerAuswertung.cellstyles.get("date"));

            for(int i = 1; i <= 6; i++){
                cell = row.createCell(colNum++);
                cell.setCellValue(lnw.getAnzahl(i));
            }

            cell = row.createCell(colNum++);
            cell.setCellValue(lnw.getDurchschnitt());

        }

        formatSheet(sheet, 3, rowNum);

    }

    private void formatSheet(Sheet sheet, int rowFrom, int rowTo) {

        String[] bisNote = new String[]{
                "0.99", "1.50", "2.50", "3.50", "4.50", "5.50", "6.01"
        };

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        for(int i = 0; i < colorIndices.length; i++) {
            ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, bisNote[i], bisNote[i+1]);
            PatternFormatting fill1 = rule1.createPatternFormatting();

            if(colorIndices[i] != IndexedColors.LIGHT_BLUE.getIndex()){
                fill1.setFillBackgroundColor(colorIndices[i]);
            } else {
                fill1.setFillBackgroundColor(new XSSFColor(new java.awt.Color(180, 180, 255)));
            }

            if(colorIndices[i] == IndexedColors.RED.getIndex()){
                rule1.createFontFormatting().setFontStyle(false, true);
            }


            fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            String range = "N" + rowFrom + ":" + "N" + rowTo;

            CellRangeAddress[] regions = {CellRangeAddress.valueOf(range)};
            sheetCF.addConditionalFormatting(regions, rule1);
        }


        for(int i = 0; i < 14; i++){
            sheet.autoSizeColumn(i);
        }

        sheet.createFreezePane(0, 2);

    }


}
