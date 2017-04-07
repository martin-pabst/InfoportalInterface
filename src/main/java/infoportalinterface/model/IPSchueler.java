package infoportalinterface.model;

import java.util.ArrayList;
import java.util.List;

public class IPSchueler {

	private String rufname;
	private String familienname;
	private IPKlasse klasse;
	private boolean isMaennlich;

	private ArrayList<IPErziehungsberechtigter> erziehungsberechtigte = new ArrayList<>();

	private ArrayList<String> fremdsprachen = new ArrayList<>();

	private String konfession;
	private String besuchterReligionsunterricht;

	private String geburtsdatum;
	private String bildungsgang;

	private List<IPWiederholung> wiederholungen = new ArrayList<>();

	private List<IPFach> faecher = new ArrayList<>();

	private List<IPAbsenz> absenzen = new ArrayList<>();

	@Override
	public String toString() {

		String out = familienname + ", " + rufname + "(" + (isMaennlich ? "m" : "w") + ") ";

		for (String fs : fremdsprachen) {
			out += fs + " ";
		}

		out += " (" + konfession + "; " + besuchterReligionsunterricht + ") ";
		out += geburtsdatum + ", " + bildungsgang;

		for (IPErziehungsberechtigter erzb : erziehungsberechtigte) {
			out += "[";
			out += erzb.toString();
			out += "]";
		}

		for(IPFach fach: faecher){
		    out += "[" + fach.toString() + " | " + "]";
        }


		return out;

	}

	public IPSchueler(String rufname, String familienname, IPKlasse klasse, boolean isMaennlich, String konfession,
			String besuchterReligionsunterricht, String geburtsdatum) {
		super();
		this.rufname = rufname;
		this.familienname = familienname;
		this.klasse = klasse;
		this.isMaennlich = isMaennlich;
		this.konfession = konfession;
		this.besuchterReligionsunterricht = besuchterReligionsunterricht;
		this.geburtsdatum = geburtsdatum;
	}

	public String getRufname() {
		return rufname;
	}

	public String getFamilienname() {
		return familienname;
	}

	public IPKlasse getKlasse() {
		return klasse;
	}

	public boolean isMaennlich() {
		return isMaennlich;
	}

	public ArrayList<IPErziehungsberechtigter> getErziehungsberechtigte() {
		return erziehungsberechtigte;
	}

	public ArrayList<String> getFremdsprachen() {
		return fremdsprachen;
	}

	public String getKonfession() {
		return konfession;
	}

	public String getBesuchterReligionsunterricht() {
		return besuchterReligionsunterricht;
	}

	public String getGeburtsdatum() {
		return geburtsdatum;
	}

	public String getBildungsgang() {
		return bildungsgang;
	}

	public void setBildungsgang(String bldGang) {

		this.bildungsgang = bldGang;

	}

	public boolean hasName(String rufname, String familienname) {

		if(rufname == null | familienname == null){
			return false;
		}

		return rufname.equalsIgnoreCase(this.rufname) && familienname.equalsIgnoreCase(this.familienname);

	}

	public void addWiederholung(IPWiederholung ipw) {

		wiederholungen.add(ipw);

	}

	public List<IPWiederholung> getWiederholungen() {
		return wiederholungen;
	}

	public void setNotenForFach(IPFachEnum ipfe, IPNote jz, IPNote zz, IPNote sg, List<IPNote> schulaufgaben, IPNote gl, IPNote kl) {

		IPFach fach = getOrCreateFach(ipfe);
		fach.setNoten(jz, zz, sg, schulaufgaben, gl, kl);

	}

	private IPFach getOrCreateFach(IPFachEnum ipfe){

	    IPFach fach = getFach(ipfe);

		if(fach != null){
		    return fach;
        }

        fach = new IPFach(ipfe);
		faecher.add(fach);

        return fach;
	}

	public IPFach getFach(IPFachEnum ipfe){
		return faecher.stream().filter(ipfach -> ipfach.hasFach(ipfe) ).findFirst().orElse(null);
	}

	public void addAbsenz(IPAbsenz absenz) {

		absenzen.add(absenz);

	}

	public void clearAbsenzen(){
		absenzen.clear();
	}
}


