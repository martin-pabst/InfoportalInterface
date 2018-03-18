package pabstsoftware.infoportalinterface.model;

import java.util.ArrayList;

public class IPLehrkraftListe extends ArrayList<IPLehrkraft> {

	public IPLehrkraft findByRufnameLeerzeichenFamilienname(String rufnameLeerzeichenFamilienname) {

		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace(" &nbsp;", " ");
		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace("&nbsp;", " ");

		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace("Diplom-Biologin", "");
		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace("Dr.", "");
		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace("  ", " ");

		rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.replace("2", "");
        rufnameLeerzeichenFamilienname = rufnameLeerzeichenFamilienname.trim();

		for (IPLehrkraft lehrkraft : this) {
			String rlf = lehrkraft.getRufname() + " " + lehrkraft.getFamilienname();

			boolean übereinstimmung = rlf.equals(rufnameLeerzeichenFamilienname);

			if(lehrkraft.getAkadGrad() != null && !lehrkraft.getAkadGrad().isEmpty()){
				rlf = lehrkraft.getAkadGrad() + " " + rlf;
			}

			übereinstimmung =  übereinstimmung || rlf.equalsIgnoreCase(rufnameLeerzeichenFamilienname);

			if (übereinstimmung) {
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
