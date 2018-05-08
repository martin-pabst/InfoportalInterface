package pabstsoftware.auswertung;

import pabstsoftware.infoportalinterface.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SchriftlicherLeistungsnachweisStore extends ArrayList<SchriftlicherLeistungsnachweis> {

    public HashMap<String, SchriftlicherLeistungsnachweis> map = new HashMap<>();

    @Override
    public boolean add(SchriftlicherLeistungsnachweis lnw){
        map.put(lnw.getHash(), lnw);
        return super.add(lnw);
    }

    public SchriftlicherLeistungsnachweis getLNW(IPFachEnum fach, IPKlasse klasse, IPKoppelgruppe koppelgruppe, Date datum, IPNotenArt art){
        return map.get(SchriftlicherLeistungsnachweis.getHash(fach,klasse, koppelgruppe, datum, art));
    }



    public void analysiere(IPSchueler schueler, ArrayList<IPKoppelgruppe> koppelgruppen){
        for (IPEinzelnote note : schueler.getEinzelnoten()) {
            analysiere(note, schueler, koppelgruppen);
        }
    }

    private void analysiere(IPEinzelnote note, IPSchueler schueler, ArrayList<IPKoppelgruppe> koppelgruppen){

        if(!note.isNachholschulaufgabe() && !note.isGefehlt() && note.isAuswertbar() && note.getNotenArt().isSchriftlich()){

            IPKlasse klasse = schueler.getKlasse();
            IPKoppelgruppe koppelgruppe = null;

            for (IPKoppelgruppe kg : koppelgruppen) {
                if(note.getLehrkraft() == kg.getLehrkraft() && note.getFach() == kg.getFach()){
                    ArrayList<IPSchueler> schuelerliste = kg.getSchuelerMap().get(klasse);
                    if(schuelerliste != null){
                        if(schuelerliste.contains(schueler)){
                            koppelgruppe = kg;
                            klasse = null;
                            break;
                        }
                    }
                }
            }

            SchriftlicherLeistungsnachweis lnw = getLNW(note.getFach(), klasse, koppelgruppe, note.getDatum(), note.getNotenArt());

            if(lnw == null){
                lnw = new SchriftlicherLeistungsnachweis(schueler.getKlasse(), note.getLehrkraft(), note.getDatum(), note.getNotenArt(), note.getFach());
                add(lnw);
            }

            lnw.addNote(note);

        }


    }

}
