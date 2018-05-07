package pabstsoftware.infoportalinterface.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IPEinzelnote {

    private IPFachEnum fach;
    private IPNotenArt notenArt;
    private IPLehrkraft lehrkraft;
    private Date datum;
    private double faktor;
    private boolean gefehlt;
    private IPNote note;
    private String zusatz;
    private boolean isTest;
    private boolean isNachholschulaufgabe;


    public IPEinzelnote(IPFachEnum fach, IPNotenArt notenArt, IPLehrkraft lehrkraft, Date datum, double faktor, boolean gefehlt, IPNote note, String zusatz, boolean isTest, boolean isNachholschulaufgabe) {
        this.fach = fach;
        this.notenArt = notenArt;
        this.lehrkraft = lehrkraft;
        this.datum = datum;
        this.faktor = faktor;
        this.gefehlt = gefehlt;
        this.note = note;
        this.zusatz = zusatz;
        this.isTest = isTest;
        this.isNachholschulaufgabe = isNachholschulaufgabe;
    }

    @Override
    public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        if(lehrkraft == null || notenArt == null || fach == null){
            return "Fehler: Lehrkraft oder Notenart oder Fach unbekannt";
        }

        String s = notenArt + "(" + fach.getKurzform() + ": " + lehrkraft.getBenutzername() + " " + sdf.format(datum) +
                " " + note +
                (isNachholschulaufgabe ? " -> NSchA!" : "") + ")";
        return s;

    }

    public IPNotenArt getNotenArt() {
        return notenArt;
    }

    public IPLehrkraft getLehrkraft() {
        return lehrkraft;
    }

    public Date getDatum() {
        return datum;
    }

    public double getFaktor() {
        return faktor;
    }

    public boolean isGefehlt() {
        return gefehlt;
    }

    public IPNote getNote() {
        return note;
    }

    public String getZusatz() {
        return zusatz;
    }

    public IPFachEnum getFach() {
        return fach;
    }

    public boolean isTest() {
        return isTest;
    }

    public boolean isNachholschulaufgabe() {
        return isNachholschulaufgabe;
    }

    public boolean isAuswertbar() {
        return datum != null && fach != null && notenArt != null;
    }
}
