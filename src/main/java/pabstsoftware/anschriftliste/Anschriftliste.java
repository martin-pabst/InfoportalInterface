package pabstsoftware.anschriftliste;

import pabstsoftware.infoportalinterface.model.IPSchueler;
import pabstsoftware.config.Config;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Martin on 11.04.2017.
 */
public class Anschriftliste extends ArrayList<ASchueler> {

    private DataFormatter dataFormatter = new DataFormatter();

    public Anschriftliste(Config config) throws IOException {
        File file = new File(config.anschriftliste);
        FileInputStream fis = new FileInputStream(file);

        XSSFWorkbook wb = new XSSFWorkbook(fis);

        XSSFSheet sh = wb.getSheetAt(0);

        int lastRowNum = sh.getLastRowNum();

        for (int i = 1; i <= lastRowNum; i++) {

            Row row = sh.getRow(i);

            int column = 0;

            String klasseString = getSafeCellvalue(row, column++);
            String rufname = getSafeCellvalue(row, column++);
            String familienname = getSafeCellvalue(row, column++);
            Date geburtsdatum = row.getCell(column++).getDateCellValue();
            String namensbestandteileVorangestellt = getSafeCellvalue(row, column++);
            String namensbestandteileNachgestellt = getSafeCellvalue(row, column++);
            String geschlecht = getSafeCellvalue(row, column++);

            String erzb1_familienname = getSafeCellvalue(row, column++);
            String erzb1_akadgrad = getSafeCellvalue(row, column++);
            String erzb1_vornamen = getSafeCellvalue(row, column++);
            String erzb1_anschriftZweizeilig = getSafeCellvalue(row, column++);
            String erzb1_strasse = getSafeCellvalue(row, column++);
            String erzb1_hausnummer = getSafeCellvalue(row, column++);
            String erzb1_plz = getSafeCellvalue(row, column++);
            String erzb1_ort = getSafeCellvalue(row, column++);
            String erzb1_vollstaendigerName = getSafeCellvalue(row, column++);
            String erzb1_artSchluessel = getSafeCellvalue(row, column++);

            String erzb2_familienname = getSafeCellvalue(row, column++);
            String erzb2_akadgrad = getSafeCellvalue(row, column++);
            String erzb2_vornamen = getSafeCellvalue(row, column++);
            String erzb2_anschriftZweizeilig = getSafeCellvalue(row, column++);
            String erzb2_strasse = getSafeCellvalue(row, column++);
            String erzb2_hausnummer = getSafeCellvalue(row, column++);
            String erzb2_plz = getSafeCellvalue(row, column++);
            String erzb2_ort = getSafeCellvalue(row, column++);
            String erzb2_vollstaendigerName = getSafeCellvalue(row, column++);
            String erzb2_artSchluessel = getSafeCellvalue(row, column++);

            ASchueler schueler = new ASchueler(klasseString, rufname, familienname, geburtsdatum,
                    namensbestandteileVorangestellt, namensbestandteileNachgestellt, geschlecht);

            Erziehungsberechtigter erzb1 = new Erziehungsberechtigter(erzb1_familienname, erzb1_akadgrad, erzb1_vornamen,
                    erzb1_strasse, erzb1_hausnummer, erzb1_plz, erzb1_ort,
                    erzb1_vollstaendigerName, erzb1_artSchluessel);

            schueler.addErziehungsberechtigter(erzb1);

            if (erzb2_familienname != null && !erzb2_familienname.isEmpty()) {

                Erziehungsberechtigter erzb2 = new Erziehungsberechtigter(erzb2_familienname, erzb2_akadgrad, erzb2_vornamen,
                        erzb2_strasse, erzb2_hausnummer, erzb2_plz, erzb2_ort,
                        erzb2_vollstaendigerName, erzb2_artSchluessel);

                schueler.addErziehungsberechtigter(erzb2);

            }

            add(schueler);

        }

        wb.close();

    }

    private String getSafeCellvalue(Row row, int column) {

        Cell cell = row.getCell(column);
        if(cell == null){
            return "";
        } else {
            return dataFormatter.formatCellValue(cell);
        }
        
    }

    public ASchueler findSchueler(IPSchueler schueler){

        for(ASchueler s: this){

            if(!schueler.getFamilienname().contains(s.getFamilienname())){
                continue;
            }

            if(!s.getRufname().equals(schueler.getRufname())){
                continue;
            }

            if(!s.getGeburtsdatumString().equals(schueler.getGeburtsdatum())){
                continue;
            }

            return s;

        }

        return null;

    }

}
