package infoportalinterface.requests;

import infoportalinterface.InfoPortalInterface;
import infoportalinterface.model.*;
import infoportalinterface.tools.httpclient.HttpClientInterface;
import infoportalinterface.tools.string.Finder;
import infoportalinterface.tools.string.WordNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class GetKlassenteamsRequest extends BaseRequest {

    private IPKlassenListe klassen;

    private IPLehrkraftListe lehrkraefte;

    private String mainPage;

    public GetKlassenteamsRequest(InfoPortalInterface infoPortalInterface, IPKlassenListe klassen, IPLehrkraftListe lehrkraefte, String mainPage) {
        super(infoPortalInterface);
        this.klassen = klassen;
        this.lehrkraefte = lehrkraefte;
        this.mainPage = mainPage;
    }

    @Override
    public String execute() throws Exception {

        HttpClientInterface httpClient = getHttpClient();

        fetchKlassenteams(httpClient);

        return mainPage;

    }


    private void fetchKlassenteams(HttpClientInterface httpClient)
            throws WordNotFoundException, Exception, ResponseNotExpectedException {

        Finder finder = new Finder(mainPage);
        String url = finder.findLinkURL("Klassenteams");

        String response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Klassenteams");
        expectResponseContains(response, "Schulleitung");

        finder.jumpTo("<body>").jumpTo("Klassenteams").jumpTo("<hr").jumpTo("<table").jumpTo("<table");
        finder.markBegin().jumpTo("</table>").markEnd().cropToSelection();

        Map<IPKlasse, String> klassenLinkList = new HashMap<>();

        for (IPKlasse klasse : klassen) {
            String link = finder.jumpToBegin().findLinkURL(klasse.getName());
            klassenLinkList.put(klasse, link);
        }

        for (IPKlasse klasse : klassen) {
            fetchKlassenTeam(klasse, klassenLinkList.get(klasse), httpClient);
            System.out.println("Klasesnteam der Klasse " + klasse.getName() + " geholt.");
        }



    }

    private void fetchKlassenTeam(IPKlasse klasse, String url, HttpClientInterface httpClient) throws Exception {

        String response = httpClient.get(url);
        Finder finder = new Finder(response);

        finder.jumpTo("CSG-Datei").jumpTo("<table").markBegin().jumpTo("</table>").markEnd().cropToSelection();

        while(finder.find("<tr")){
            String line = finder.getXMLText("tr");

            Finder finderLine = new Finder(line);

            String fachText = finderLine.getXMLText("td");
            String lehrkraefteText = finderLine.getXMLText("td");

            if(!fachText.startsWith("Klassenleitung") && !fachText.contains("Lehrkraft")){
                addFachLehrkraefte(fachText, lehrkraefteText, klasse);
            }

        }


    }

    private void addFachLehrkraefte(String fachText, String lehrkraefteText, IPKlasse klasse) {

        IPFachEnum fach = IPFachEnum.findByAnzeigeform(fachText);

        if(fach != null){

            //Bauer, Tobias<br />Zinck, Gertrud<br />

            String[] lehrkraftList = lehrkraefteText.split("<br />");

            for(String lk: lehrkraftList){

                lk = lk.trim();

                if(lk.length() > 4){
                    // Familienname und Rufname trennen
                    String[] famNameRufname = lk.split(", ");
                    if(famNameRufname.length == 2){
                        String familienname = famNameRufname[0];
                        String rufname = famNameRufname[1];

                        IPLehrkraft lehrkraft = lehrkraefte.findByRufnameLeerzeichenFamilienname(rufname + " " + familienname);

                        if(lehrkraft != null){
                            klasse.getKlassenteam().add(lehrkraft, fach);
                        }

                    }
                }

            }

        }

    }


}
