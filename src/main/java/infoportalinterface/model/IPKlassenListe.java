package infoportalinterface.model;

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
	
}
