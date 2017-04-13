package pabstsoftware.infoportalinterface.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;
import pabstsoftware.infoportalinterface.tools.string.Finder;
import pabstsoftware.infoportalinterface.tools.string.WordNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetKlassenStundenplaeneRequest extends BaseRequest {

    private IPKlassenListe klassen;

    private IPLehrkraftListe lehrkraefte;

    private String mainPage;

    public GetKlassenStundenplaeneRequest(InfoPortalInterface infoPortalInterface, IPKlassenListe klassen, IPLehrkraftListe lehrkraefte, String mainPage) {
        super(infoPortalInterface);
        this.klassen = klassen;
        this.lehrkraefte = lehrkraefte;
        this.mainPage = mainPage;
    }

    @Override
    public String execute() throws Exception {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Hole Klassenstundenpläne aus dem Info-Portal...");


        HttpClientInterface httpClient = getHttpClient();

        fetchKlassenStundenplaene(httpClient);

        return mainPage;

    }


    private void fetchKlassenStundenplaene(HttpClientInterface httpClient)
            throws WordNotFoundException, Exception, ResponseNotExpectedException {

        Finder finder = new Finder(mainPage);
        String url = finder.findLinkURL("Klassen");

        String response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Klassenliste");

        url = finder.findLinkURL("Stundenpläne");

        response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Schuljahr");

        finder.jumpTo("<body>").jumpTo("<table").jumpTo("Schuljahr").jumpTo("Klassen");
        finder.markBegin().jumpTo("</table>").markEnd().cropToSelection();

        Map<IPKlasse, String> klassenLinkList = new HashMap<>();

        for (IPKlasse klasse : klassen) {
            String link = finder.jumpToBegin().findLinkURL(klasse.getName());
            klassenLinkList.put(klasse, link);
        }

        for (IPKlasse klasse : klassen) {
            fetchKlassenStundenplan(klasse, klassenLinkList.get(klasse), httpClient);

            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info("Stundenplan der Klasse " + klasse.getName() + " geholt.");

        }


    }

    private void fetchKlassenStundenplan(IPKlasse klasse, String url, HttpClientInterface httpClient) throws Exception {

        String response = httpClient.get(url);
        Finder finder = new Finder(response);

        String[] weekDays = new String[]{"Mo", "Di", "Mi", "Do", "Fr"};

        for (String weekDay : weekDays) {
            finder.jumpTo(weekDay);
        }


        finder.jumpTo("<tr").markBegin().jumpTo("</table>").markEnd().cropToSelection();

        while (finder.find("<tr")) {
            String line = finder.getXMLText("tr");

            Finder finderLine = new Finder(line);

            fetchStundenplanLine(finderLine, klasse);


        }


    }

    private void fetchStundenplanLine(Finder finderLine, IPKlasse klasse) {

        while (finderLine.find("<td")) {

            String line = finderLine.getXMLText("td");

            Finder finderData = new Finder(line);

            String fachString = finderData.markBegin().jumpTo("<br").markEnd().getMarkedText();

            ArrayList<IPFachEnum> faecher = new ArrayList<>();
            ArrayList<IPLehrkraft> lehrerkraefteList = new ArrayList<>();

            boolean allFound = true;

            String[] faecherKuerzel = fachString.split(", ");
            for (String fk : faecherKuerzel) {
                IPFachEnum fach = IPFachEnum.findByKurzform(stripDigits(fk));
                if (fach != null) {
                    faecher.add(fach);
                } else {
                    allFound = false;
                }
            }

            while (finderData.find("<a")) {
                String lehrerkuerzel = finderData.getXMLText("a");
                IPLehrkraft lehrkraft = lehrkraefte.findByKuerzel(lehrerkuerzel);
                if (lehrkraft != null) {
                    lehrerkraefteList.add(lehrkraft);
                } else {
                    allFound = false;
                }
            }
                if (allFound && faecher.size() == lehrerkraefteList.size()) {

                    for (int i = 0; i < faecher.size(); i++) {
                        klasse.getKlassenteam().add(lehrerkraefteList.get(i), faecher.get(i));
                    }

                }

        }

    }

    private String stripDigits(String s) {
        if (s != null) {

            while (s.length() > 0 && Character.isDigit(s.charAt(s.length() - 1))) {
                s = s.substring(0, s.length() - 1);
            }

            return s;

        } else {
            return null;
        }
    }


}
