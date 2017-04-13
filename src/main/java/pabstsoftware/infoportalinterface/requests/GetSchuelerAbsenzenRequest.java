package pabstsoftware.infoportalinterface.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;
import pabstsoftware.infoportalinterface.tools.string.Finder;
import pabstsoftware.infoportalinterface.tools.string.WordNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetSchuelerAbsenzenRequest extends BaseRequest {

    private IPKlassenListe klassen;

    private IPLehrkraftListe lehrkraefte;

    private String mainPage;

    public GetSchuelerAbsenzenRequest(InfoPortalInterface infoPortalInterface, IPKlassenListe klassen, IPLehrkraftListe lehrkraefte, String mainPage) {
        super(infoPortalInterface);
        this.klassen = klassen;
        this.lehrkraefte = lehrkraefte;
        this.mainPage = mainPage;
    }

    @Override
    public String execute() throws Exception {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Hole Absenzen der Schüler aus dem Info-Portal...");

        HttpClientInterface httpClient = getHttpClient();

        fetchSchuelerAbsenzen(httpClient);

        return mainPage;

    }


    private void fetchSchuelerAbsenzen(HttpClientInterface httpClient)
            throws WordNotFoundException, Exception, ResponseNotExpectedException {

        Finder finder = new Finder(mainPage);
        String url = finder.findLinkURL("Schulleitung");

        String response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Absenzen");

        url = finder.findLinkURL("Absenzen &rarr;");

        response = httpClient.get(url);
        finder.setText(response);

        url = finder.findLinkURL("Schüler");

        response = httpClient.get(url);
        finder.setText(response);


        finder.jumpTo("Absenzen - Schüler").jumpTo("Klassen").jumpTo("<td");
        finder.markBegin().jumpTo("</table>").markEnd().cropToSelection();

        Map<IPKlasse, String> klassenLinkList = new HashMap<>();

        for (IPKlasse klasse : klassen) {
            String link = finder.jumpToBegin().findLinkURL(klasse.getNameWithout0());
            klassenLinkList.put(klasse, link);
        }

        for (IPKlasse klasse : klassen) {
            fetchAbsenzenJeKlasse(klasse, klassenLinkList.get(klasse), httpClient);

            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info("Absenzen der Klasse " + klasse.getName() + " geholt.");
        }


    }

    private void fetchAbsenzenJeKlasse(IPKlasse klasse, String url, HttpClientInterface httpClient) throws Exception {

        String response = httpClient.get(url);
        Finder finder = new Finder(response);

        finder.jumpTo("Absenzen - Schüler").jumpTo("<form").markBegin().jumpTo("</form>").markEnd().cropToSelection();

        finder.jumpTo("<tr").skipFoundWord().jumpTo("<tr").skipFoundWord();

        while (finder.find("<tr")) {
            String visibleLine = finder.getXMLText("tr");

            Finder finderVisibleLine = new Finder(visibleLine);

            String familienname = finderVisibleLine.getXMLText("a");
            String rufname = finderVisibleLine.getXMLText("span");
            int spacePosition = rufname.indexOf(" ");

            if (spacePosition > 0) {
                rufname = rufname.substring(0, spacePosition);
            }

            finder.jumpTo("<tr").markBegin().skipNext("</table").skipNext("</table").skipNext("</tr>").markEnd();

            String invisibleLine = finder.getMarkedText();

            Finder finderInvisibleLine = new Finder(invisibleLine);

            IPSchueler schueler = klasse.findSchueler(rufname, familienname);

            if (schueler != null) {
                fetchAbsenzenLine(finderInvisibleLine, schueler);
            }

        }

    }

    private void fetchAbsenzenLine(Finder finder, IPSchueler schueler) {

        schueler.clearAbsenzen();

        finder.skipNext("<table").skipNext("<table").markBegin().jumpTo("</table>").markEnd().cropToSelection();

        while (finder.find("<tr")) {

            String line = finder.getXMLText("tr");

            if (!line.contains("Datum von-bis") && !line.contains("Summen")) {

                Finder finderLine = new Finder(line);

                String datumVonBis = finderLine.getXMLText("td");
                String tage = finderLine.getXMLText("td");
                String stunden = finderLine.getXMLText("td");
                String minuten = finderLine.getXMLText("td");
                String art = finderLine.getXMLText("td");

                addAbsenz(schueler, datumVonBis, tage, stunden, minuten, art);

            }

        }

    }

    private void addAbsenz(IPSchueler schueler, String datumVonBis, String tage, String stunden, String minuten, String art) {

        String datumVon = datumVonBis;
        String datumBis = null;

        if (datumVonBis.contains(" - ")) {
            int i = datumVonBis.indexOf(" - ");
            datumVon = datumVonBis.substring(0, i);
            datumBis = datumVonBis.substring(i + 3);
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        Date dateVon = null;
        Date dateBis = null;

        try {
            dateVon = df.parse(datumVon);
            if (datumBis != null) {
                dateBis = df.parse(datumBis);
            }
        } catch (ParseException e) {
        }

        int tageInt = parseIntSecure(tage);
        int stundenInt = parseIntSecure(stunden);
        int minutenInt = parseIntSecure(minuten);

        IPAbsenzArt absenzArt = IPAbsenzArt.fromText(art);

        IPAbsenz absenz = new IPAbsenz(dateVon, dateBis, tageInt, stundenInt, minutenInt, art, absenzArt, null, null);

        schueler.addAbsenz(absenz);

    }


    private int parseIntSecure(String s) {

        try {
            return Integer.parseInt(s);
        } catch (Exception ex) {
            return 0;
        }

    }

}
