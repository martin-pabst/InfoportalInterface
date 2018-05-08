package pabstsoftware.infoportalinterface.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;
import pabstsoftware.infoportalinterface.tools.string.Finder;
import pabstsoftware.infoportalinterface.tools.string.WordNotFoundException;

import java.text.SimpleDateFormat;
import java.util.*;

public class GetNotenRequest extends BaseRequest {

    private final boolean mitEinzelnoten;
    private IPKlassenListe klassen;

    private IPLehrkraftListe lehrkraefte;

    private String mainPage;


    public GetNotenRequest(InfoPortalInterface infoPortalInterface, String mainPage, IPLehrkraftListe lehrkraefte,
                           IPKlassenListe klassen, boolean mitEinzelnoten) {
        super(infoPortalInterface);
        this.mainPage = mainPage;
        this.lehrkraefte = lehrkraefte;
        this.klassen = klassen;
        this.mitEinzelnoten = mitEinzelnoten;
    }

    @Override
    public String execute() throws Exception {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Hole Noten der Schüler aus dem Info-Portal...");

        HttpClientInterface httpClient = getHttpClient();

        fetchNoten(httpClient, mitEinzelnoten);

        return mainPage;

    }

    private void fetchNoten(HttpClientInterface httpClient, boolean mitEinzelnoten)
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

            getSchuelerNotenFromRow(finder1, klasse, faecherList, httpClient);

        }


    }

    private void getSchuelerNotenFromRow(Finder finder, IPKlasse klasse, ArrayList<String> faecherList, HttpClientInterface httpClient) throws Exception {

        int pos = finder.getPos();
        String familienname = finder.getXMLText("a");
        finder.setPos(pos);
        String url = finder.findLinkURL(familienname);

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

        if (url.length() > 5 && schueler != null && mitEinzelnoten) {
            fetchEinzelnoten(url, schueler, klasse, httpClient);
            System.out.print("/");
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

        for (int i = 0; i < whArray.length; i++) {

            String jgst = whArray[i].replace(". Jgst.", "");

            int jgstInt = 0;

            try {

                jgstInt = Integer.parseInt(jgst);

                String zusatztext = "";

                if (i + 1 < whArray.length && !whArray[i + 1].contains("Jgst.")) {
                    zusatztext = whArray[i + 1];
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


    private void fetchEinzelnoten(String url, IPSchueler schueler, IPKlasse klasse, HttpClientInterface httpClient) throws Exception {

        String response = httpClient.get(url);
        expectResponseContains(response, "Leistungs");
        expectResponseContains(response, "Datum");

        Finder finder = new Finder(response);
        finder.find("Leistungs");
        finder.find("Datum");
        finder.jumpTo("<table").markBegin().jumpTo("Nach oben").findBackward("</table>");
        String table = finder.markEnd().getMarkedText();

        finder = new Finder(table);

        ArrayList<String> notenartenDoppelpunkt = new ArrayList<>();
        for (IPNotenArt notenArt : IPNotenArt.values()) {
            notenartenDoppelpunkt.add(notenArt.getKurzform().toUpperCase() + ":");
        }

        IPFachEnum ipFach = null;

        while (finder.find("<tr")) {

            String trString = finder.getXMLText("tr");

            boolean isNotenzeile = false;
            for (String na : notenartenDoppelpunkt) {
                if(trString.contains(na)){
                    isNotenzeile = true;
                    break;
                }
            }

            if(trString.contains("Jahreszeugnis")){
                // Durchschnittszeile => nichts zu tun
            } else if(isNotenzeile){
                fetchEinzelnotenJeArt(trString, schueler, klasse, ipFach);
            } else {
                Finder fachFinder = new Finder(trString);
                String fachLang = fachFinder.getXMLText("td");

                IPFachEnum ipFachz = IPFachEnum.findByAnzeigeform(fachLang);
                if(ipFachz != null){
                    ipFach = ipFachz;
                }

                fachFinder.getXMLText("td");

//                String lehrkraftLang = fachFinder.getXMLText("td").replace("&nbsp;", " ");
//
//                lehrkraftLang = lehrkraftLang.replace("<br />", "");
//                lehrkraftLang = lehrkraftLang.trim();
            }

        }


    }

    private void fetchEinzelnotenJeArt(String html, IPSchueler schueler, IPKlasse klasse, IPFachEnum ipFach) throws Exception {

        html = html.replace("&nbsp;", " ");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Finder finder = new Finder(html);
        finder.setText(finder.getXMLText("table"));

        String notenartString = finder.getXMLText("td");
        if (notenartString.endsWith(":")) {
            notenartString = notenartString.substring(0, notenartString.length() - 1);
        }

        IPNotenArt notenArt = null;
        // Wir verwerfen die übergebene Notenart und halte uns an den Text der Tabelle:
        try {
            notenArt = IPNotenArt.findByKurzform(notenartString);
        } catch (Exception ex) {
            System.out.println("Fehler!" + this.getClass().toString());
        }

        while (notenArt != null && finder.find("<td")) {

            String text = finder.getXMLText("td");
            if (text.contains("-") && text.length() > 5) {
                Finder f = new Finder(text);
                String datumLehrkraft = f.markBegin().jumpTo("<br />").markEnd().getMarkedText();
                String noteFaktor = f.skipFoundWord().markBegin().jumpToOrToEnd("<br />").markEnd().getMarkedText();
                f.skipFoundWord();
                String zusatz = "";
                if (!f.textEnded()) {
                    zusatz = f.markBegin().jumpToEnd().markEnd().getMarkedText();
                }

                boolean isNachholschulaufgabe = false;

                if (zusatz.contains("NS:")) {
                    zusatz = "";
                    isNachholschulaufgabe = true;
                }

                int bindestrichIndex = datumLehrkraft.lastIndexOf("-");
                String datumString = datumLehrkraft.substring(0, bindestrichIndex);

                boolean isTest = false;

                if (datumString.startsWith("T-")) {
                    datumString = datumString.substring(2);
                    isTest = true;
                }

                if (datumString.charAt(1) == '-') {
                    datumString = datumString.substring(2);
                }


                Date date = sdf.parse(datumString);
                String lkKuerzel = datumLehrkraft.substring(bindestrichIndex + 1, datumLehrkraft.length());
                IPLehrkraft ipLehrkraft = lehrkraefte.findByKuerzel(lkKuerzel);

                IPNote ipNote = null;
                double faktor = 1.00;
                boolean gefehlt = false;

                if (noteFaktor.contains("gefehlt")) {
                    gefehlt = true;
                } else {
                    ipNote = new IPNote(noteFaktor);

                    if (noteFaktor.contains("x")) {
                        int faktorBegin = noteFaktor.indexOf("x") + 1;
                        int faktorEnd = noteFaktor.indexOf(" )");
                        String faktorString = noteFaktor.substring(faktorBegin, faktorEnd);

                        faktor = Double.parseDouble(faktorString);
                    }
                }


                if (notenArt != null) {
                    IPEinzelnote einzelnote = new IPEinzelnote(ipFach, notenArt, ipLehrkraft, date, faktor,
                            gefehlt, ipNote, zusatz, isTest, isNachholschulaufgabe);

                    schueler.addEinzelnote(einzelnote);
                }
            }


        }

    }


}
