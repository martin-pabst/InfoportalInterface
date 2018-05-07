package pabstsoftware.auswertung;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.IPFachEnum;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPLehrkraft;

import java.util.List;
import java.util.stream.Collectors;

public class Fachproblemliste {

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


    public Fachproblemliste(Workbook workbook, ScheinerAuswertung scheinerAuswertung, InfoPortalInterface ip) {
        this.wb = workbook;
        this.scheinerAuswertung = scheinerAuswertung;
        this.ip = ip;
    }

    public void execute() {

        Sheet sheet = wb.createSheet("Fachquoten");
        Drawing drawing = sheet.createDrawingPatriarch();
        CreationHelper factory = wb.getCreationHelper();

        int rowNum = 0;
        int colNum = 0;

        Row row = sheet.createRow(rowNum++);

        Cell cell = row.createCell(0);
        cell.setCellStyle(scheinerAuswertung.cellstyles.get("bold"));
        cell.setCellValue("Anteil der Schüler mit schlechten Durchschnittsnoten");

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Schwellenwert:");

        cell = row.createCell(3);
        cell.setCellValue(4.0);

        row = sheet.createRow(rowNum++);

        String[] headers = new String[]{"Klasse"};

        for (String header : headers) {

            cell = row.createCell(colNum++);

            cell.setCellValue(header);

        }

        for (IPFachEnum ipFachEnum : IPFachEnum.values()) {

            cell = row.createCell(colNum++);
            cell.setCellValue(ipFachEnum.getKurzform());

        }

        for (int i = 0; i < IPFachEnum.values().length + headers.length; i++) {
            row.getCell(i).setCellStyle(scheinerAuswertung.cellstyles.get("bold"));
        }


        /**
         * Übersicht über die Klassen
         */
        int rowErsteKlasse = rowNum + 1;
        int n = 2;

        for (IPKlasse klasse : ip.getKlassen()) {

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(klasse.getName());
            cell.setCellStyle(scheinerAuswertung.cellstyles.get("bold"));

            int anzahl = klasse.getSchuelerList().size();

            for (int ind = 0; ind < IPFachEnum.values().length; ind++) {

                String comment = "";

                List<IPLehrkraft> lk = klasse.getKlassenteam().getKlassenteamMap().get(IPFachEnum.values()[ind]);
                if (lk != null && lk.size() > 0) {
                    comment = lk.stream().map(l -> l.getStundenplan_id()).collect(Collectors.joining(", "));
                }

                createPercentageCell(drawing, factory, row, headers, n, anzahl, ind, comment);


            }

            n += klasse.getSchuelerList().size();

        }

        /**
         * Übersicht über die Jahrgangsstuufen
         */
        rowNum++; // eine Zeile frei

        int rowErsteJgst = rowNum + 1;
        n = 2; // Zeile des ersten Schülers

        int[] jahrgangsstufeZeileVon = new int[6]; // 5 - 10
        int[] jahrgangsstufeZeileBis = new int[6];

        int aktuelleJahrgangsstufe = 5;
        int i = 0;
        jahrgangsstufeZeileVon[0] = 2;

        for (IPKlasse ipKlasse : ip.getKlassen()) {
            if(ipKlasse.getJahrgangsstufe() > aktuelleJahrgangsstufe){
                jahrgangsstufeZeileVon[i+1] = n;
                jahrgangsstufeZeileBis[i] = n - 1;
                i++;
                aktuelleJahrgangsstufe = ipKlasse.getJahrgangsstufe();
            }

            n += ipKlasse.getSchuelerList().size();

        }

        jahrgangsstufeZeileBis[i] = n - 1;

        for (i = 0; i < 6; i++) {

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("" + (i + 5));
            cell.setCellStyle(scheinerAuswertung.cellstyles.get("bold"));

            for (int ind = 0; ind < IPFachEnum.values().length; ind++) {

                createPercentageCell(drawing, factory, row, headers, jahrgangsstufeZeileVon[i],
                        jahrgangsstufeZeileBis[i] - jahrgangsstufeZeileVon[i] + 1, ind, "");


            }

        }




        String[] bisProzent = new String[]{
                "0.00", "0.10", "0.15", "0.20", "0.25", "0.30", "1.00"
        };


        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        for ( i = 0; i < colorIndices.length; i++) {
            ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, bisProzent[i], bisProzent[i + 1]);
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

            String range1 = 'B' + "" + rowErsteKlasse + ":" + CellReference.convertNumToColString(IPFachEnum.values().length)
                    + (rowErsteKlasse + ip.getKlassen().size() - 1);

            String range2 = 'B' + "" + (rowErsteKlasse + ip.getKlassen().size() + 1) + ":" + CellReference.convertNumToColString(IPFachEnum.values().length)
                    + (rowErsteKlasse + ip.getKlassen().size() + 6);

            CellRangeAddress[] regions = {CellRangeAddress.valueOf(range1), CellRangeAddress.valueOf(range2)};
            sheetCF.addConditionalFormatting(regions, rule1);
        }


        sheet.setColumnWidth(0, 256 * 8);
        for (i = 1; i < IPFachEnum.values().length + headers.length; i++) {
            //sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, 256 * 6);
        }

        sheet.createFreezePane(1, 3);


    }

    private void createPercentageCell(Drawing drawing, CreationHelper factory, Row row, String[] headers, int n, int anzahl, int fachIndex, String comment) {
        Cell cell;
        String spalte = CellReference.convertNumToColString(fachIndex + 3);
        String begin = spalte + n;
        String end = spalte + (n + anzahl - 1);
        String range = begin + ":" + end;

        if(anzahl > 1) {
            cell = row.createCell(fachIndex + headers.length);

            CellStyle style = scheinerAuswertung.cellstyles.get("percent");
            cell.setCellStyle(style);

            String formel = "IF(COUNT(Notenliste!" + range + ")>0,COUNTIF(Notenliste!" + range + ",\">\"&Fachquoten!$D$2)/COUNT(Notenliste!" + range + "),\"\")";
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formel);

            if (!comment.isEmpty()) {
                ClientAnchor anchor = factory.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 1);
                anchor.setRow1(row.getRowNum());
                anchor.setRow2(row.getRowNum() + 3);

                Comment com = drawing.createCellComment(anchor);
                RichTextString str = factory.createRichTextString(comment);
                com.setVisible(Boolean.FALSE);
                com.setString(str);

                cell.setCellComment(com);
            }
        }
    }


}
