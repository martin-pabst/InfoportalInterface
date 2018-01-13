package pabstsoftware.auswertung;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.IPFach;
import pabstsoftware.infoportalinterface.model.IPFachEnum;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPSchueler;

import java.util.HashMap;

public class SchuelerNotenListe {

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


    public SchuelerNotenListe(Workbook workbook, ScheinerAuswertung scheinerAuswertung, InfoPortalInterface ip) {
        this.wb = workbook;
        this.scheinerAuswertung = scheinerAuswertung;
        this.ip = ip;
    }

    public void execute(){

        Sheet sheet = wb.createSheet("Notenliste");

        int rowNum = 0;
        int colNum = 0;

        Row row = sheet.createRow(rowNum++);

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

        row.createCell(colNum++).setCellValue("D SchA");

        for(int i = 0; i < IPFachEnum.values().length + 6; i++){
            row.getCell(i).setCellStyle(scheinerAuswertung.cellstyles.get("bold"));
        }

        for (IPKlasse klasse : ip.getKlassen()) {
            for (IPSchueler schueler : klasse.getSchuelerList()) {

                row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(klasse.getName());
                row.createCell(1).setCellValue(schueler.getFamilienname());
                row.createCell(2).setCellValue(schueler.getRufname());

                int i = 3;
                for (IPFachEnum ipFachEnum : IPFachEnum.values()) {

                    IPFach fach = schueler.getFach(ipFachEnum);

                    if(fach != null && fach.getsG() != null && fach.getsG().getValue() > 0.01){
                        Cell cell = row.createCell(i++);
                        cell.setCellValue(fach.getsG().getValue());
                        cell.setCellStyle(scheinerAuswertung.cellstyles.get("bold"));
                    } else {
                        row.createCell(i++).setCellValue("---");
                    }

                }


                Cell cell = row.createCell(i++);
                cell.setCellValue(schueler.getDurchschnittVorrueckungsfaecher2Dez());

                i++;

                IPFach fach = schueler.getFach(IPFachEnum.D);
                if(fach != null && fach.getDurchschnittGL() != null && fach.getDurchschnittGL().getValue() > 0.01){
                    cell = row.createCell(i++);
                    cell.setCellValue(fach.getDurchschnittGL().getValue());
                } else {
                    row.createCell(i++);
                }


            }
        }


        String[] bisNote = new String[]{
                "0.99", "1.50", "2.50", "3.50", "4.50", "5.50", "6.01"
        };

        int schuelerAnzahl = ip.getKlassen().stream().mapToInt( k -> k.getSchuelerList().size()).sum();

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

            String range = "D1" + ":" + CellReference.convertNumToColString(IPFachEnum.values().length + 2)
                    + (schuelerAnzahl + 1);

            CellRangeAddress[] regions = {CellRangeAddress.valueOf(range)};
            sheetCF.addConditionalFormatting(regions, rule1);
        }


        for(int i = 0; i < IPFachEnum.values().length + 6; i++){
            sheet.autoSizeColumn(i);
        }

        sheet.createFreezePane(3, 1);

    }


}
