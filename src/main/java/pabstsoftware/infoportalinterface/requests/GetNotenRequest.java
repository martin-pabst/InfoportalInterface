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
import java.util.List;
import java.util.Map;

public class GetNotenRequest extends BaseRequest {

    private IPKlassenListe klassen;

    private IPLehrkraftListe lehrkraefte;

    private String mainPage;


    public GetNotenRequest(InfoPortalInterface infoPortalInterface, String mainPage, IPLehrkraftListe lehrkraefte,
                           IPKlassenListe klassen) {
        super(infoPortalInterface);
        this.mainPage = mainPage;
        this.lehrkraefte = lehrkraefte;
        this.klassen = klassen;
    }

    @Override
    public String execute() throws Exception {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Hole Noten der Schüler aus dem Info-Portal...");

        HttpClientInterface httpClient = getHttpClient();

        fetchNoten(httpClient);

        return mainPage;

    }

    private void fetchNoten(HttpClientInterface httpClient)
            throws WordNotFoundException, Exception, ResponseNotExpectedException {

        Finder finder = new Finder(mainPage);
        String url = finder.findLinkURL("Schulleitung");

        String response = httpClient.get(url);
        finder.setText(response);

        url = finder.findLinkURL("Leistungsübersicht");

        response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Leistungsübersicht");
        expectResponseContains(response, "Historie");

        finder.jumpTo("Leistungsübersicht").jumpTo("<hr").jumpTo("<table").markBegin().jumpTo("Internat").markEnd();

        String klassenLinks = finder.getMarkedText();
        finder.setText(klassenLinks);

        Map<IPKlasse, String> klassenLinkList = new HashMap<>();

        for (IPKlasse klasse : klassen) {
            String link = finder.jumpToBegin().findLinkURL(klasse.getName());
            klassenLinkList.put(klasse, link);
        }
        for (IPKlasse klasse : klassen) {
            fetchNotenInKlasse(klasse, klassenLinkList.get(klasse), httpClient);

            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info("Noten der Klasse " + klasse.getName() + " geholt.");

        }

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
//        List<Callable<Void>> tasks = new ArrayList<>();
//
//        for (IPKlasse klasse : klassen) {
//
//            Callable<Void> task = () -> {
//
//                try {
//                    //HttpClientInterface httpClientForked = httpClient.forkClient();
//                    fetchNotenInKlasse(klasse, klassenLinkList.findByKlassenname(klasse), httpClient);
//                    //httpClientForked.close();
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//
//                return null;
//            };
//
//            tasks.add(task);
//
//        }
//
//        executor.invokeAll(tasks);

    }

    private void fetchNotenInKlasse(IPKlasse klasse, String url, HttpClientInterface httpClient)
            throws WordNotFoundException, Exception, ResponseNotExpectedException {

        String response = httpClient.get(url);

        Finder finder = new Finder(response);

        finder.jumpTo("Klasse:").jumpTo("<table").jumpTo("<tr").markBegin().jumpTo("</tr>").markEnd();
        String tableFaecherList = finder.getMarkedText();

        ArrayList<String> faecherList = getFaecherList(tableFaecherList);


        String tableSchuelerList = finder.jumpTo("<tr").markBegin().jumpTo("</table>").markEnd().getMarkedText();

        finder.setText(tableSchuelerList);

        Finder finder1 = new Finder("");

        while (finder.find("<tr ")) {

            String rowText = finder.markBegin().jumpTo("</tr>").markEnd().getMarkedText();
            finder1.setText(rowText);

            getSchuelerNotenFromRow(finder1, klasse, faecherList);

        }


    }

    private void getSchuelerNotenFromRow(Finder finder, IPKlasse klasse, ArrayList<String> faecherList) {

        String familienname = finder.getXMLText("a");
        String rufname = finder.getXMLText("span");

        IPSchueler schueler = klasse.findSchueler(rufname, familienname);

        finder.jumpTo("Vorjahren").jumpTo("</a>").skipFoundWord();

        String wiederholungText = finder.markBegin().jumpTo("</td>").markEnd().getMarkedText();

        if (wiederholungText.contains("Jgst.")) {

            Finder finder1 = new Finder(wiederholungText);

            wiederholungText = finder1.getXMLText("span");
            findWiederholungen(schueler, wiederholungText);

        }

        finder.skipFoundWord().jumpTo("</td>").skipFoundWord(); // Überspringen: <td  ... >JZ<br />ZZ<br />SG<br />ESA<br />&Oslash;GL<br />&Oslash;KL<br /></td>


        for (String fach : faecherList) {

            String notenString = finder.getXMLText("td");

            String[] noten = notenString.split("<br />");

            findNoten(schueler, noten, fach);

        }

    }

    private void findNoten(IPSchueler schueler, String[] noten, String fach) {

        IPFachEnum ipfe = IPFachEnum.findByKurzform(fach);

        if (ipfe != null && noten.length >= 6) {

            IPNote jz = cleanNote(noten[0]); // z.B. "&nbsp;" oder "<span class=''>2,00</span>" oder ""
            IPNote zz = cleanNote(noten[1]);
            IPNote sg = cleanNote(noten[2]);

            List<IPNote> schulaufgaben = getSchulaufgaben(noten[3]); // z.B. "3&nbsp;5-&nbsp;" oder ""

            IPNote gl = cleanNote(noten[4]);
            IPNote kl = cleanNote(noten[5]);

            schueler.setNotenForFach(ipfe, jz, zz, sg, schulaufgaben, gl, kl);

        }
    }

    /**
     * z.B. "3&nbsp;5-&nbsp;" oder ""
     *
     * @param s
     * @return
     */
    private List<IPNote> getSchulaufgaben(String s) {

        List<IPNote> noten = new ArrayList<>();

        if (s != null) {

            s = s.trim();

            String[] snoten = s.split(" ");

            for (String snote : snoten) {

                IPTendenz tendenz = IPTendenz.keine;

                String snote1 = snote;

                if (snote.endsWith("-")) {
                    tendenz = IPTendenz.minus;
                    snote1 = snote.substring(0, snote.length() - 1);
                }

                if (snote.startsWith("+")) {
                    tendenz = IPTendenz.plus;
                    snote1 = snote.substring(1);
                }

                try {
                    int note = Integer.parseInt(snote1);
                    noten.add(new IPNote(snote, note, tendenz));

                } catch (NumberFormatException ex) {

                }

            }

        }


        return noten;
    }

    /**
     * "&nbsp;" oder "<span class=''>2,00</span>" oder ""
     *
     * @param s
     * @return
     */
    private IPNote cleanNote(String s) {

        if (s == null) {
            return null;
        }

        s = s.replace("&nbsp;", "");
        s = s.trim();
        if (s.startsWith("<span")) {
            Finder finder = new Finder(s);
            s = finder.getXMLText("span");
        }

        if (s.isEmpty()) {
            return null;
        }

        try {
            s = s.replace(",", ".");
            Double note = Double.parseDouble(s);

            return new IPNote(s, note, IPTendenz.keine);

        } catch (NumberFormatException ex) {

        }

        return null;
    }

    private void findWiederholungen(IPSchueler schueler, String wiederholungText) {
        // 8. Jgst.<br />Pflichtwiederholung an der eigenen Schulart<br />10. Jgst.<br /><br />
        // 6. Jgst.<br />Freiwilliger Rücktritt an der eigenen Schulart<br />

        String[] whArray = wiederholungText.split("<br />");

        for (int i = 0; i < whArray.length; i ++) {

            String jgst = whArray[i].replace(". Jgst.", "");

            int jgstInt = 0;

            try {

                jgstInt = Integer.parseInt(jgst);

                String zusatztext = "";

                if(i + 1 < whArray.length && !whArray[i+1].contains("Jgst.")){
                    zusatztext = whArray[i+1];
                    i++;
                }

                IPWiederholung ipw = new IPWiederholung(jgstInt, zusatztext);

                schueler.addWiederholung(ipw);

            } catch (NumberFormatException ex) {

            }

        }
    }

    private ArrayList<String> getFaecherList(String tableFaecherList) {

        Finder finder = new Finder(tableFaecherList);

        finder.jumpTo("&Oslash;");

        ArrayList<String> faecherliste = new ArrayList<>();

        while (finder.find("<a")) {
            String fach = finder.getXMLText("a");
            faecherliste.add(fach);
        }

        return faecherliste;

    }


}
