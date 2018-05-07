package pabstsoftware.tools.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

public class PdfboxReader {

    public static String readPdf(InputStream is){

        try {
            PDDocument doc = PDDocument.load(is);
//            PDDocument doc = PDDocument.load(new File("c:/temp/lkliste.pdf"));
            String text = new PDFTextStripper().getText(doc);
                doc.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
