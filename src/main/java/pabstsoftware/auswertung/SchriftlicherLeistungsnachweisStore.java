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

    public SchriftlicherLeistungsnachweis getLNW(IPFachEnum fach, IPKlasse klasse, Date datum, IPNotenArt art){
        return map.get(getHash(fach,klasse,datum, art));
    }

    public String getHash(IPFachEnum fach, IPKlasse klasse, Date datum, IPNotenArt art){
        return fach.toString() + klasse.getName() + datum.toString() + art.toString();
    }

    public void analysiere(IPSchueler schueler){
        for (IPEinzelnote note : schueler.getEinzelnoten()) {
            analysiere(note, schueler);
        }
    }

    private void analysiere(IPEinzelnote note, IPSchueler schueler){

        if(!note.isNachholschulaufgabe() && !note.isGefehlt() && note.isAuswertbar() && note.getNotenArt().isSchriftlich()){

            SchriftlicherLeistungsnachweis lnw = getLNW(note.getFach(), schueler.getKlasse(), note.getDatum(), note.getNotenArt());

            if(lnw == null){
                lnw = new SchriftlicherLeistungsnachweis(schueler.getKlasse(), note.getLehrkraft(), note.getDatum(), note.getNotenArt(), note.getFach());
                add(lnw);
            }

            lnw.addNote(note);

        }


    }

}
