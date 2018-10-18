package pabstsoftware.jahreszeugnis.bestenliste;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.config.Config;
import pabstsoftware.infoportalinterface.model.IPKlasse;
import pabstsoftware.infoportalinterface.model.IPSchueler;
import pabstsoftware.jahreszeugnis.ScheinerJahreszeugnisMain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Martin on 10.04.2017.
 */
public class Bestenliste {

    private ScheinerJahreszeugnisMain scheinerJahreszeugnisMain;

    private Workbook workbook;

    public HashMap<String, CellStyle> cellstyles = new HashMap<>();



    public Bestenliste(ScheinerJahreszeugnisMain scheinerJahreszeugnisMain){
        this.scheinerJahreszeugnisMain = scheinerJahreszeugnisMain;
    }

    public void execute() throws IOException, URISyntaxException {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe Bestenliste...");

        prepareWorkbook();

        runAuswertung();

        writeWorkbook();

    }

    private void runAuswertung() {

        Sheet sheet = workbook.createSheet("Bestenliste");

        int rowNum = 0;
        int colNum = 0;

        Row row = sheet.createRow(rowNum++);

        String[] headers = new String[]{"Klasse", "Familienname", "Rufname", "Durchschnitt"};

        for (String header : headers) {

            Cell cell = row.createCell(colNum++);

            cell.setCellValue(header);
            cell.setCellStyle(cellstyles.get("bold"));

        }


        ArrayList<IPSchueler> schuelerListe = new ArrayList<>();

        for(IPKlasse klasse: scheinerJahreszeugnisMain.getIp().getKlassen()){
            schuelerListe.addAll(klasse.getSchuelerList());
        }

        Collections.sort(schuelerListe, new Comparator<IPSchueler>(){

            @Override
            public int compare(IPSchueler s1, IPSchueler s2) {
                return s1.getDurchschnittVorrueckungsfaecher().compareTo(s2.getDurchschnittVorrueckungsfaecher());
            }

        });

        int nr = 1;

        for(IPSchueler schueler: schuelerListe){

            row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(schueler.getKlasse().getName());
            row.createCell(1).setCellValue(schueler.getFamilienname());
            row.createCell(2).setCellValue(schueler.getRufname());
            row.createCell(3).setCellValue(schueler.getDurchschnittVorrueckungsfaecher2Dez());

        }

        for(int i = 0; i < 4; i++){
            sheet.autoSizeColumn(i);
        }

        sheet.createFreezePane(0, 1);

    }

    private void writeWorkbook() {

        Config config = scheinerJahreszeugnisMain.getConfig();

        String outputDir = config.outputfolder + "/";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String heute = sdf.format(Calendar.getInstance().getTime());

        String outputFilename = outputDir + "Bestenliste_" + heute + ".xlsx";

        try {
            Files.createDirectories(Paths.get(outputDir));

            workbook.write(new FileOutputStream(new File(outputFilename)));

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void prepareWorkbook(){
        workbook = new XSSFWorkbook();

        workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        CellStyle styleBold = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        styleBold.setFont(font);

        cellstyles.put("bold", styleBold);


        CellStyle percent = workbook.createCellStyle();
        percent.setDataFormat(workbook.createDataFormat().getFormat("0.0%"));
        cellstyles.put("percent", percent);

        CellStyle date = workbook.createCellStyle();
        CreationHelper ch = workbook.getCreationHelper();
        date.setDataFormat(ch.createDataFormat().getFormat("d.m.yyyy"));
        cellstyles.put("date",date);

    }

}
