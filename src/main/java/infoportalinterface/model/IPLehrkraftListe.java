package infoportalinterface.model;

import java.util.ArrayList;

public class IPLehrkraftListe extends ArrayList<IPLehrkraft> {

	public IPLehrkraft findByRufnameLeerzeichenFamilienname(String rufnameLeerzeichenFamilienname) {

		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace("&nbsp;", " ");
		
		for (IPLehrkraft lehrkraft : this) {
			String rlf = lehrkraft.getRufname() + " " + lehrkraft.getFamilienname();
			if (rlf.equals(rufnameLeerzeichenFamilienname)) {
				return lehrkraft;
			}
		}

		return null;

	}

    public IPLehrkraft findByKuerzel(String lehrerkuerzel) {
		for (IPLehrkraft lehrkraft : this) {

			if (lehrkraft.getStundenplan_id().equals(lehrerkuerzel)) {
				return lehrkraft;
			}
		}

		return null;
    }
}
