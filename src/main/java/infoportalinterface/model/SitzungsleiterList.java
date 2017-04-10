package infoportalinterface.model;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Martin on 10.04.2017.
 */
public class SitzungsleiterList extends ArrayList<Sitzungsleiter> {

    public SitzungsleiterList() throws IOException {
        File file = new File("data/Sitzungsleiter.xlsx");
        FileInputStream fis = new FileInputStream(file);

        XSSFWorkbook wb = new XSSFWorkbook(fis);

        XSSFSheet sh = wb.getSheet("Sitzungsplan");

        int lastRowNum = sh.getLastRowNum();

        for(int i = 1; i <= lastRowNum; i++){

            Row row = sh.getRow(i);

            String klasse = row.getCell(0).getStringCellValue();
            String sitzungsleiter = row.getCell(1).getStringCellValue();
            String von = row.getCell(2).getStringCellValue();
            String bis = row.getCell(3).getStringCellValue();
            String datum = row.getCell(4).getStringCellValue();

            Sitzungsleiter sl = new Sitzungsleiter(klasse, sitzungsleiter, von, bis, datum);
            add(sl);
        }

        wb.close();

    }

    public Sitzungsleiter findByKlassenname(String klasse) {

        for(Sitzungsleiter sl: this){
            if(klasseEquals(sl.getKlasse(), klasse)){
                return sl;
            }
        }

        return null;
    }

    private boolean klasseEquals(String name1, String name2){
        if(name1 == null || name2 == null){
            return false;
        }

        if(name1.startsWith("0")){
            name1 = name1.substring(1);
        }

        if(name2.startsWith("0")){
            name2 = name2.substring(1);
        }

        return name1.equals(name2);

    }

}
