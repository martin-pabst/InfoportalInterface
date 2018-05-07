package pabstsoftware.auswertung;

import pabstsoftware.infoportalinterface.model.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SchriftlicherLeistungsnachweis {

    private ArrayList<IPEinzelnote> noten = new ArrayList<>();
    private IPKlasse klasse;
    private IPLehrkraft lehrkraft;
    private Date datum;
    private IPNotenArt art;
    private IPFachEnum fach;
    private int nummer;

    public SchriftlicherLeistungsnachweis(IPKlasse klasse, IPLehrkraft lehrkraft, Date datum, IPNotenArt art, IPFachEnum fach) {
        this.klasse = klasse;
        this.lehrkraft = lehrkraft;
        this.datum = datum;
        this.art = art;
        this.fach = fach;
    }

    public void addNote(IPEinzelnote note) {
        noten.add(note);
    }

    public String getHash() {
        return fach.toString() + klasse.getName() + datum.toString() + art.toString();
    }

    public int getJahrgangsstufe() {
        return klasse.getJahrgangsstufe();
    }

    public IPKlasse getKlasse() {
        return klasse;
    }

    public IPLehrkraft getLehrkraft() {
        return lehrkraft;
    }

    public Date getDatum() {
        return datum;
    }

    public IPNotenArt getArt() {
        return art;
    }

    public IPFachEnum getFach() {
        return fach;
    }

    public String getDatumString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(datum);
    }

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public double getDurchschnitt() {
        int anzahl = 0;
        double summe = 0;

        for (IPEinzelnote no : noten) {
            if(no.getNote() != null) {
                summe += no.getNote().getValue();
                anzahl++;
            }
        }

        if (anzahl > 0) {
            return summe / anzahl;
        }

        return 0.0;
    }

    public int getAnzahl(int note) {
        int anzahl = 0;

        for (IPEinzelnote no : noten) {
            if (no.getNote() != null && Math.abs(no.getNote().getValue() - note) < 0.001) {
                anzahl++;
            }
        }

        return anzahl;
    }

}
