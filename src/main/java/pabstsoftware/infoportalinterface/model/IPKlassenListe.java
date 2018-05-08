package pabstsoftware.infoportalinterface.model;

import java.util.ArrayList;

public class IPKlassenListe extends ArrayList<IPKlasse> {

	@Override
	public String toString() {

		String s = "";

		for(IPKlasse k: this){
			s += k.toString() + "\n";
		}
	
		return s;
	}

    public IPKlasse findByName(String klasseText) {

		if(klasseText.startsWith("0")){
			klasseText = klasseText.substring(1);
		}

		for (IPKlasse ipKlasse : this) {
			String klName = ipKlasse.getName();
			if(klName.startsWith("0")){
				klName = klName.substring(1);
			}
			if(klasseText.equalsIgnoreCase(klName)){
				return ipKlasse;
			}
		}

		return null;

    }

	public IPSchueler findSchuelerByName(String familienname, String rufname, IPKlasse klasse) {

	    IPSchueler schueler = null;

		if(klasse != null) {
			schueler = klasse.findSchueler(rufname, familienname);
			if (schueler != null) {
				return schueler;
			}
		}

		for (IPKlasse ipKlasse : this) {
			schueler = ipKlasse.findSchueler(rufname, familienname);
			if(schueler != null){
				return schueler;
			}
		}

		return null;

	}
}
