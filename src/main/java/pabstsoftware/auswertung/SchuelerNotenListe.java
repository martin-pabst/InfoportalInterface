package pabstsoftware.auswertung;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.IPSchueler;

public class SchuelerNotenListe {

    private Workbook wb;
    private ScheinerAuswertung scheinerAuswertung;
    private InfoPortalInterface ip;

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

            row.createCell(colNum);

        }





        for (IPSchueler ipSchueler : ip.getSchueler()) {

        }



    }


}
