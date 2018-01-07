package pabstsoftware.auswertung;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.config.Config;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.InfoPortalInterfaceFactory;
import pabstsoftware.infoportalinterface.Klassenfilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Martin on 10.04.2017.
 */
public class ScheinerAuswertung implements Klassenfilter {

    private InfoPortalInterface ip;

    private Config config;

    private long time;

    private Workbook workbook;

    public HashMap<String, CellStyle> cellstyles = new HashMap<>();


    /**
     * Für Testzwecke kann hier die Menge der Klassen eingeschränkt werden, um die Laufzeit niedrig zu halten
     *
     * @param name
     * @return
     */
    public boolean holeKlasse(String name) {


        boolean alle = true;

        if (alle) {
            return true;
        }


        if (name.toLowerCase().startsWith("09a")) {
            return true;
        } else {
            return false;
        }

    }

    public static void main(String[] args) {
        try {

            ScheinerAuswertung scheinerAuswertung = new ScheinerAuswertung();

            scheinerAuswertung.fetchInfoportalData();

            scheinerAuswertung.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() throws IOException {

        prepareAuswertungWorkbook(config);


        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Schreibe die Schülernotenliste...");

        try {

            SchuelerNotenListe snl = new SchuelerNotenListe(workbook, this, ip);
            snl.execute();

            Fachproblemliste fpl = new Fachproblemliste(workbook, this, ip);
            fpl.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        writeAuswertungWorkbook(config);

        time = System.currentTimeMillis() - time;
        time /= 1000;

        long min = time / 60;
        long s = time - min * 60;

        logger.info("Arbeiten abgeschlossen in " + min + " min " + s + " s.");


    }

    public ScheinerAuswertung() throws Exception {

        time = System.currentTimeMillis();

        String pathname = "data/Config.xml";
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Lese Konfiguration aus der Datei " + pathname);

        File source = new File(pathname);


        Serializer serializer = new Persister();
        config = serializer.read(Config.class, source);

    }

    public InfoPortalInterface getIp() {
        return ip;
    }

    public Config getConfig() {
        return config;
    }


    public void fetchInfoportalData() throws Exception {

        ip = InfoPortalInterfaceFactory.getInfoPortalInterface(config);
        ip.setKlassenfilter(this);

        ip.login();

        ip.fetchLehrkraefte();

        ip.fetchKlassen(ip.getLehrkraefte());

        ip.fetchAbsenzen();

        ip.fetchNoten();

        ip.logout();

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

        cellstyles.put("bold", styleBold);


        CellStyle percent = workbook.createCellStyle();
        percent.setDataFormat(workbook.createDataFormat().getFormat("0.0%"));
        cellstyles.put("percent", percent);

        return workbook;
    }

    private void writeAuswertungWorkbook(Config config) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String heute = sdf.format(Calendar.getInstance().getTime());

        String filename = config.outputfolder + "/CSG-Auswertung_" + heute + ".xlsx";

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
