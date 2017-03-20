package infoportalinterface.model;

import java.util.ArrayList;

public class IPErziehungsberechtigter {

	private String name;

	private boolean isMaennlich;

	private String StrasseNummer;
	private String postleitzahl;
	private String ort;

	private ArrayList<String> telefonnummern = new ArrayList<String>();
	private String mail;

	@Override
	public String toString() {

		String out = name + "(" + (isMaennlich ? "m" : "w") + "), " + StrasseNummer + ", " + postleitzahl + " " + ort;
		
		return out;
	
	}
	
	public IPErziehungsberechtigter(String rufname, boolean isMaennlich, String strasseNummer, String postleitzahl,
			String ort) {
		super();
		this.name = rufname;
		this.isMaennlich = isMaennlich;
		StrasseNummer = strasseNummer;
		this.postleitzahl = postleitzahl;
		this.ort = ort;
	}

	public String getName() {
		return name;
	}

	public boolean isMaennlich() {
		return isMaennlich;
	}

	public String getStrasseNummer() {
		return StrasseNummer;
	}

	public String getPostleitzahl() {
		return postleitzahl;
	}

	public String getOrt() {
		return ort;
	}

	public ArrayList<String> getTelefonnummern() {
		return telefonnummern;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void addTelefonnummer(String telefonnummer){
		telefonnummern.add(telefonnummer);
	}
	
}
