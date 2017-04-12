package infoportalinterface.requests;

import infoportalinterface.InfoPortalInterface;
import infoportalinterface.model.*;
import infoportalinterface.tools.httpclient.HttpClientInterface;
import infoportalinterface.tools.string.Finder;
import infoportalinterface.tools.string.WordNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GetKlassenRequest extends BaseRequest {

	private IPKlassenListe klassen = new IPKlassenListe();

	private IPLehrkraftListe lehrkraefte;

	private String mainPage;

	public GetKlassenRequest(InfoPortalInterface infoPortalInterface, String mainPage, IPLehrkraftListe lehrkraefte) {
		super(infoPortalInterface);
		this.mainPage = mainPage;
		this.lehrkraefte = lehrkraefte;
	}

	@Override
	public String execute() throws Exception {

		HttpClientInterface httpClient = getHttpClient();

		fetchKlassenMitKlassenleitungen(httpClient);

		fetchSchuelerdaten(httpClient);

		return mainPage;

	}

	private void fetchSchuelerdaten(HttpClientInterface httpClient)
			throws WordNotFoundException, Exception, ResponseNotExpectedException {

		Finder finder = new Finder(mainPage);
		String url = finder.findLinkURL("Klassen");

		String response = httpClient.get(url);
		finder.setText(response);

		url = finder.findLinkURL("Klassenliste");

		response = httpClient.get(url);
		finder.setText(response);

		expectResponseContains(response, "Schuljahr");
		expectResponseContains(response, "geteilte");

		finder.jumpTo("Schuljahr").jumpTo("Klassen").markBegin().jumpTo("geteilte").markEnd();

		String klassenLinks = finder.getMarkedText();
		finder.setText(klassenLinks);

		Map<IPKlasse, String> klassenLinkList = new HashMap<>();

		for (IPKlasse klasse : klassen) {
			String link = finder.jumpToBegin().findLinkURL(klasse.getName());
			klassenLinkList.put(klasse, link);
		}

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
		List<Callable<Void>> tasks = new ArrayList<>();



		for (IPKlasse klasse : klassen) {
			fetchSchuelerdatenInKlasse(klasse, klassenLinkList.get(klasse), httpClient);
		}

//			for (IPKlasse klasse : klassen) {
//
//			Callable<Void> task = () -> {
//
//				try {
//					HttpClientInterface httpClientForked = httpClient.forkClient();
//					fetchSchuelerdatenInKlasse(klasse, klassenLinkList.findByKlassenname(klasse), httpClient);
//					httpClientForked.close();
//				} catch (Exception e) {
//
//				}
//
//				return null;
//			};
//
//			tasks.add(task);
//
//		}
//
//		executor.invokeAll(tasks);

	}

	private void fetchSchuelerdatenInKlasse(IPKlasse klasse, String url, HttpClientInterface httpClient)
			throws WordNotFoundException, Exception, ResponseNotExpectedException {

		String response = httpClient.get(url);

		Finder finder = new Finder(response);

		finder.jumpTo("Excel-Liste").jumpTo("</tr>").skipFoundWord().markBegin();
		String tableSchuelerList = finder.jumpTo("</table>").markEnd().getMarkedText();

		finder.setText(tableSchuelerList);

		Finder finder1 = new Finder("");

		while (finder.find("<tr ")) {

			String rowText = finder.markBegin().jumpTo("</tr>").markEnd().getMarkedText();
			finder1.setText(rowText);

			IPSchueler schueler = getSchuelerFromRow(finder1, klasse);
			klasse.addSchueler(schueler);

		}

		System.out.println("Klasse " + klasse.getName() + " geholt.");

	}

	/**
	 * <code>
	 * <tr class='liste_weiss' style=
	'border-bottom:1px solid #000000;'><td width='5%' align='left' valign=
	'top' class='text_8pt' style='border-bottom:1px solid #000000;'>2</td>
	<td width='25%' align='left' valign='top' class='text_8pt_bold' style=
	'border-bottom:1px solid #000000;'> Brecht,<br />Tom</td>
	<td width='30%' align='left' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>Annie Brecht, Bergstr. 8a, 85139&nbsp;Wettstetten, 0841/993 343 9, 08450/33349 - 15, 0176/333 42222 11, sdf.ee@google.de (Erzber.)<br /><br />Merte Brecht, Bergstr. 7a, 85139&nbsp;Wettstetten, 0841/333333, 0172/342 333 7, jlkjdsf.sdfsd@audi.de (Weiterer Erzber.)<br /><br /></td>
	<td width='5%' align='center' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>m</td>
	<td width='5%' align='center' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>RK/RK</td>
	<td width='10%' align='left' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>E<br />GY</td>
	<td width='10%' align='left' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>19.08.2005<br /> <br /> </td>
	<td width='5%' align='left' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>&nbsp;</td>
	<td width='5%' align='left' valign='top' class='text_8pt' style=
	'border-bottom:1px solid #000000;'>&nbsp;</td></tr>
	
	 * </code>
	 */

	/**
	 * 
	 * @param finder
	 * @param klasse
	 * @return
	 */

	private IPSchueler getSchuelerFromRow(Finder finder, IPKlasse klasse) {

		String lfdNum = finder.getXMLText("td"); // z.B. "1"
		String name = finder.getXMLText("td"); // z.B. "Albrecht,<br />Tom"
		String erziehungsberechtigte = finder.getXMLText("td");
		/**
		 * z.B. Annie Brecht, Bergstr. 8a, 85139 Wettstetten, 0841/993 3439,
		 * 08450/33349 - 15, 0176/333 42222 11, sdf.ee@google.de (Erzber.)<br/>
		 * <br />
		 * Merte Brecht, Bergstr. 7a, 85139 Wettstetten, 0841/333333, 0172/342
		 * 3337, jlkjdsf.sdfsd@audi.de (Weiterer Erzber.)<br />
		 * <br />
		 **/

		String geschlecht = finder.getXMLText("td"); // "m" oder "w"
		String religion = finder.getXMLText("td"); // z.B. "RK/RK": zuerst
													// Konfession, dann bes.
													// Rel. U.
		String fremdsprachenfolgeBildungsgang = finder.getXMLText("td"); // z.B.
																			// "ELF<br
																			// />GY_SG_8"
		String geburtsdatum = finder.getXMLText("td"); // z.B. "12.07.2008<br />OGS"
		geburtsdatum = geburtsdatum.substring(0, 10);
		
		// System.out.println(lfdNum + ", " + name + ", " +
		// erziehungsberechtigte + ", " + geschlecht + ", " + religion
		// + ", " + fremdsprachenfolgeBildungsgang + ", " + geburtsdatum);

		Finder finderName = new Finder(name);
		String familienname = finderName.markBegin().jumpTo(",<br />").markEnd().getMarkedText();
		String rufname = finderName.skipFoundWord().markBegin().jumpToEnd().markEnd().getMarkedText();

		Finder finderErzb = new Finder(erziehungsberechtigte);
		finderErzb.markBegin().jumpTo("<br /><br />").markEnd().cropToSelection();

		IPErziehungsberechtigter erz1 = null, erz2 = null;

		if (finder.getLength() > 5) {

			erz1 = getErziehungsberechtiger(finderErzb);

		}

		finderErzb = new Finder(erziehungsberechtigte);
		finderErzb.jumpTo("<br /><br />").markBegin().skipFoundWord().jumpToEnd().markEnd().cropToSelection();
		finderErzb.replaceAll("<br />", "");

		if (finder.getLength() > 15) {

			erz2 = getErziehungsberechtiger(finderErzb);

		}

		boolean isMaennlich = (geschlecht != null && geschlecht.equalsIgnoreCase("m"));

		// TODO: Religion, FremdsprachenfolgeBildungsgang

		Finder reliFinder = new Finder(religion);
		String konfession = reliFinder.markBegin().jumpTo("/").markEnd().getMarkedText();
		String besuchterReliUnterricht = reliFinder.skipFoundWord().markBegin().jumpToEnd().markEnd().getMarkedText();

		IPSchueler schueler = new IPSchueler(rufname, familienname, klasse, isMaennlich, konfession,
				besuchterReliUnterricht, geburtsdatum);

		addFremdsprachen(schueler, fremdsprachenfolgeBildungsgang);

		if (erz1 != null) {
			schueler.getErziehungsberechtigte().add(erz1);
		}

		if (erz2 != null) {
			schueler.getErziehungsberechtigte().add(erz2);
		}

		return schueler;
	}

	private void addFremdsprachen(IPSchueler schueler, String fremdsprachenfolgeBildungsgang) {

		List<String> fremdsprachen = schueler.getFremdsprachen();

		Finder f = new Finder(fremdsprachenfolgeBildungsgang);
		String fsFolge = f.markBegin().jumpTo("<").markEnd().getMarkedText();
		String bldGang = f.jumpTo(">").skipFoundWord().markBegin().jumpToEnd().markEnd().getMarkedText();

		String[] fsList = new String[] { "E", "F", "L", "Sp", "Ch", "Ru", "Sps" };

		boolean found = true;
		
		while (found) {

			found = false;
			for (String fs : fsList) {
				if (fsFolge.startsWith(fs)) {
					fremdsprachen.add(fs);
					fsFolge = fsFolge.substring(fs.length());
					found = true;
					break;
				}
			}
			
		}
		
		schueler.setBildungsgang(bldGang);

	}

	private IPErziehungsberechtigter getErziehungsberechtiger(Finder finder) {

		// Bsp.: "Annie Brecht, Bergstr. 8a, 85139 Wettstetten, 0841/993
		// 343 9, 08450/33349 - 15, 0176/333 42222 11, sdf.ee@google.de
		// (Erzber.)"

		String name = finder.markBegin().jumpTo(", ").markEnd().getMarkedText();
		name = name.replace("<br />", "");
		
		String strasseNr = finder.skipFoundWord().markBegin().jumpTo(", ").markEnd().getMarkedText();
		String plz = finder.skipFoundWord().markBegin().jumpTo(" ").markEnd().getMarkedText();
		String ort = finder.skipFoundWord().markBegin().jumpTo(", ").markEnd().getMarkedText();

		IPErziehungsberechtigter iperz = new IPErziehungsberechtigter(name, true, strasseNr, plz, ort);

		while (finder.find(", ")) {

			String telMail = finder.skipFoundWord().markBegin().jumpTo(", ").markEnd().getMarkedText();

			telMail.replace("(Erzber.", "");
			telMail.replace("(weiterer Erzber.)", "");
			telMail.replace("(Schüler)", "");

			if (telMail.contains("@")) {
				iperz.setMail(telMail);
			} else {
				iperz.addTelefonnummer(telMail);
			}

		}

		return iperz;
	}

	private void fetchKlassenMitKlassenleitungen(HttpClientInterface httpClient)
			throws WordNotFoundException, Exception, ResponseNotExpectedException {

		Finder finder = new Finder(mainPage);
		String url = finder.findLinkURL("Klassen");

		String response = httpClient.get(url);
		finder.setText(response);

		expectResponseContains(response, "Klassenleitung:");
		expectResponseContains(response, "Klassensprecher:");

		finder.jumpTo("Raum").jumpTo("qm");

		// Auf Tabelle der Klassen einschränken
		finder.markBegin().jumpTo("</table>").markEnd().cropToSelection();

		klassen.clear();

		while (finder.find("<tr")) {

			String row = finder.markBegin().jumpTo("</tr>").markEnd().getMarkedText();

			// Die Zeilen, die Klassensprecher
			// enthalten, arbeiten mit rowspan
			if (!row.contains("colspan")) {

				Finder rowFinder = new Finder(row);

				String klassenName = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText();

				String id = rowFinder.jumpTo("<td").jumpTo("href").jumpTo("k=").skipFoundWord().markBegin().jumpTo("'")
						.markEnd().getMarkedText();

				for (int i = 0; i < 4; i++) {
					rowFinder.skipFoundWord().jumpTo("</td>");
				}

				String klassenleitung1 = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText();

				String klassenleitung2 = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText();

				String klassenleitung3 = rowFinder.jumpTo("<td").jumpTo(">").skipFoundWord().markBegin().jumpTo("</td>")
						.markEnd().getMarkedText();

				IPKlasse klasse = new IPKlasse(id, klassenName, klassenleitung1, klassenleitung2, klassenleitung3,
						lehrkraefte);

				if(klasse.getName().contains("10A")){
					klassen.add(klasse);
				}


			}

		}

	}

	public IPKlassenListe getKlassen() {

		return klassen;

	}

}
