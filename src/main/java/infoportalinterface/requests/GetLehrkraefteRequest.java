package infoportalinterface.requests;

import infoportalinterface.InfoPortalInterface;
import infoportalinterface.model.IPLehrkraft;
import infoportalinterface.model.IPLehrkraftListe;
import infoportalinterface.tools.httpclient.HttpClientInterface;
import infoportalinterface.tools.string.Finder;
import infoportalinterface.tools.string.WordNotFoundException;

public class GetLehrkraefteRequest extends BaseRequest {

	public IPLehrkraftListe lehrkraefte = new IPLehrkraftListe();

	private String mainPage;

	public GetLehrkraefteRequest(InfoPortalInterface infoPortalInterface, String mainPage) {
		super(infoPortalInterface);
		this.mainPage = mainPage;
	}

	@Override
	public String execute() throws Exception {

		HttpClientInterface httpClient = getHttpClient();

		fetchIdsMitNamen(httpClient);

		fetchDienstgradAkademischerGrad(httpClient);

		return mainPage;

	}

	private void fetchDienstgradAkademischerGrad(HttpClientInterface httpClient)
			throws WordNotFoundException, Exception, ResponseNotExpectedException {
		Finder finder = new Finder(mainPage);
		String url = finder.findLinkURL("Verwaltung");

		String response = httpClient.get(url);
		finder.setText(response);

		// Link "Listen Kollegium"
		url = finder.findLinkURL("Kollegium");
		response = httpClient.get(url);
		finder.setText(response);

		url = finder.findLinkURL("Berufsbezeichnung erzeugen...");
		response = httpClient.get(url);
		finder.setText(response);

		expectResponseContains(response, "Liste");
		expectResponseContains(response, "Kollegiums");

		finder.jumpTo("Kollegiums vom");

		while (finder.find("<table")) {

			String tableText = finder.markBegin().jumpTo("</table>").markEnd().getMarkedText();
			Finder tableFinder = new Finder(tableText);

			while (tableFinder.find("<tr")) {
				String rowText = tableFinder.markBegin().jumpTo("</tr>").markEnd().getMarkedText();
				Finder rowFinder = new Finder(rowText);

				String famNameKommaLeerzAkadLeerzRufname = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord()
						.markBegin().jumpTo("</td>").markEnd().getMarkedText().trim();
				String dienstgrad = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText().trim();
				
				dienstgrad = dienstgrad.replace("&nbsp;", "");

				Finder nameFinder = new Finder(famNameKommaLeerzAkadLeerzRufname);
				String familienname = nameFinder.markBegin().jumpTo(", ").markEnd().getMarkedText();

				String rufname = nameFinder.skipFoundWord().markBegin().jumpToEnd().markEnd().getMarkedText();

				String akadGrad = "";
				if (rufname.contains(" ") && (rufname.contains("ipl") || rufname.contains("Dr"))) {
					int i = rufname.indexOf(" ");
					akadGrad = rufname.substring(0, i);
					rufname = rufname.substring(i + 1);
				}

				IPLehrkraft lk = lehrkraefte.findByRufnameLeerzeichenFamilienname(rufname + " " + familienname);
				if (lk != null) {
					lk.setAkadGrad(akadGrad);
					lk.setDienstgrad(dienstgrad);
				}

			}

		}

	}

	private void fetchIdsMitNamen(HttpClientInterface httpClient)
			throws WordNotFoundException, Exception, ResponseNotExpectedException {
		Finder finder = new Finder(mainPage);
		String url = finder.findLinkURL("Administration");

		String response = httpClient.get(url);
		finder.setText(response);

		url = finder.findLinkURL("User &rarr;");
		response = httpClient.get(url);
		finder.setText(response);

		url = finder.findLinkURL("Ändern");
		response = httpClient.get(url);
		finder.setText(response);

		expectResponseContains(response, "Benutzername");
		expectResponseContains(response, "Stundenplan-ID");

		finder.jumpTo("Benutzername").jumpTo("Stundenplan-ID");

		// Auf Tabelle der Portalbenutzer einschränken
		finder.markBegin().jumpTo("</table>").markEnd().cropToSelection();

		lehrkraefte.clear();

		while (finder.find("<tr")) {

			String row = finder.markBegin().jumpTo("</tr>").markEnd().getMarkedText();

			// Die künstlichen Zeile, die nur einen Buchstaben des Alphabets
			// enthalten, arbeiten mit rowspan
			if (!row.contains("colspan")) {

				Finder rowFinder = new Finder(row);

				String userId = rowFinder.jumpTo("name='").skipFoundWord().markBegin().jumpTo("'").markEnd()
						.getMarkedText();

				String familienname = rowFinder.jumpTo(">").skipFoundWord().markBegin().jumpTo(", ").markEnd()
						.getMarkedText().trim();

				String rufname = rowFinder.skipFoundWord().markBegin().jumpTo("<").markEnd().getMarkedText().trim();

				String benutzername = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText().trim();

				String stundenplan_id = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText();

				IPLehrkraft lehrkraft = new IPLehrkraft(userId, familienname, rufname, benutzername, stundenplan_id);
				;

				lehrkraefte.add(lehrkraft);

			}

		}
	}

	public IPLehrkraftListe getLehrkraefte() {
		return lehrkraefte;
	}

}
