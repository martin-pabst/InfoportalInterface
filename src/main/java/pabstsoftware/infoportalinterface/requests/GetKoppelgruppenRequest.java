package pabstsoftware.infoportalinterface.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pabstsoftware.infoportalinterface.InfoPortalInterface;
import pabstsoftware.infoportalinterface.model.*;
import pabstsoftware.infoportalinterface.tools.httpclient.HttpClientInterface;
import pabstsoftware.infoportalinterface.tools.string.Finder;
import pabstsoftware.infoportalinterface.tools.string.WordNotFoundException;

import java.util.ArrayList;

public class GetKoppelgruppenRequest extends BaseRequest {

	private IPKlassenListe klassen;

	private IPLehrkraftListe lehrkraefte;

	private String mainPage;

	ArrayList<IPKoppelgruppe> koppelgruppen = new ArrayList<>();

	public GetKoppelgruppenRequest(InfoPortalInterface infoPortalInterface, String mainPage, IPLehrkraftListe lehrkraefte, IPKlassenListe klassen) {
		super(infoPortalInterface);
		this.mainPage = mainPage;
		this.lehrkraefte = lehrkraefte;
		this.klassen = klassen;
	}

	@Override
	public String execute() throws Exception {

		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.info("Hole Daten der Klassen aus dem Infoportal");

		HttpClientInterface httpClient = getHttpClient();

		fetchKoppelgruppen(httpClient);

		return mainPage;

	}


	private void fetchKoppelgruppen(HttpClientInterface httpClient)
			throws WordNotFoundException, Exception, ResponseNotExpectedException {

        koppelgruppen = new ArrayList<>();

		Finder finder = new Finder(mainPage);
		String url = finder.findLinkURL("Klassen");

		String response = httpClient.get(url);
		finder.setText(response);

		expectResponseContains(response, "Klassenleitung:");
		expectResponseContains(response, "Klassensprecher:");

		finder.setText(response);
        url = finder.findLinkURL("Klassenliste");

        response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Schuljahr");
        expectResponseContains(response, "geteilte");

        finder.jumpTo("Schuljahr");
        finder.jumpTo("Datum");
        finder.jumpTo("geteilte Klassen");

        finder.setText(finder.getXMLText("tr"));

        while(finder.find("<a")){

            url = finder.jumpTo("href=\"").skipFoundWord().markBegin().jumpTo("\"").markEnd().getMarkedText();
            String lehrkraft = finder.jumpTo("title=\"").skipFoundWord().markBegin().jumpTo("\"").markEnd().getMarkedText();
            String koppelgruppenBezeichner = finder.jumpTo(">").skipFoundWord().markBegin().jumpTo("</a>").markEnd().getMarkedText();

            IPLehrkraft ipLehrkraft = lehrkraefte.findByFamiliennameKommaLeerRufname(lehrkraft);

            fetchKoppelgruppe(httpClient, url, ipLehrkraft, koppelgruppenBezeichner);

        }

	}

    private void fetchKoppelgruppe(HttpClientInterface httpClient, String url, IPLehrkraft lehrkraft, String koppelgruppenBezeichner) throws Exception {

        Finder finder = new Finder(mainPage);

        String response = httpClient.get(url);
        finder.setText(response);

        expectResponseContains(response, "Schuljahr");
        expectResponseContains(response, "geteilte Klassen");

        finder.jumpTo("geteilte Klassen").jumpTo("<table");
        finder.setText(finder.getXMLText("table"));

        IPKlasse klasse = null;
        IPFachEnum fach = null;
        IPKoppelgruppe koppelgruppe = null;

        while(finder.find("<tr")){

            String trText = finder.getXMLText("tr");
            Finder finder1 = new Finder(trText);
            if(trText.contains("Drucken")){
                // Fach extrahieren
                finder1.jumpTo("<a").jumpTo("<span").jumpTo(">");
                String fachtext = finder.jumpTo("\"").skipFoundWord().markBegin().jumpTo("\"").markEnd().getMarkedText();
                fach = IPFachEnum.findByAnzeigeform(fachtext);
            } else if(trText.contains("text_12pt_bold")){
                // Klasse extrahieren
                String klasseText = finder1.getXMLText("td");
                klasse = klassen.findByName(klasseText);
            } else {
                // Schüler/in extrahieren
                finder.getXMLText("td");
                String name = finder.getXMLText("td");
                name = name.replace("<br>", " ").trim();
                int kommaIndex = name.indexOf(",");
                String familienname = name.substring(0, kommaIndex).trim();
                String rufname = name.substring(kommaIndex + 1, name.length()).trim();
                IPSchueler schueler = klassen.findSchuelerByName(familienname, rufname, klasse);

                if(schueler != null && klasse != null && fach != null && lehrkraft != null){
                    if(koppelgruppe == null){
                        koppelgruppe = new IPKoppelgruppe(koppelgruppenBezeichner, fach, lehrkraft);
                        koppelgruppen.add(koppelgruppe);
                    }
                    koppelgruppe.addSchueler(schueler);
                }

            }

        }




    }


}
