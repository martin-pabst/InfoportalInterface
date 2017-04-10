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

}
