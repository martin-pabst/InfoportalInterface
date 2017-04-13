package pabstsoftware.infoportalinterface.model;

import java.util.ArrayList;

public class IPSchuelerListe extends ArrayList<IPSchueler> {

    public IPSchueler findSchueler(String rufname, String familienname) {

        return this.stream().filter(schueler -> schueler.hasName(rufname, familienname)).findFirst().orElse(null);

    }
}
