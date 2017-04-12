package anschriftliste;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Martin on 11.04.2017.
 */
public class ASchueler {

    private String klasse;
    private String namensbestandteileVorangestellt;
    private String namensbestandteileNachgestellt;
    private String rufname;
    private String familienname;
    private boolean isMaennlich;
    private Date geburtsdatum;

    private ArrayList<Erziehungsberechtigter> erziehungsberechtigte = new ArrayList<>();


    public ASchueler(String klasseString, String rufname, String familienname, Date geburtsdatum,
                     String namensbestandteileVorangestellt, String namensbestandteileNachgestellt,
                     String geschlecht) {

        this.klasse = klasse;
        this.namensbestandteileVorangestellt = namensbestandteileVorangestellt;
        this.namensbestandteileNachgestellt = namensbestandteileNachgestellt;
        this.rufname = rufname;
        this.familienname = familienname;
        isMaennlich = geschlecht.equals("männlich");
        this.geburtsdatum = geburtsdatum;

    }

    public void addErziehungsberechtigter(Erziehungsberechtigter erzb){
        erziehungsberechtigte.add(erzb);
    }

    public ArrayList<Briefdaten> getBriefdaten(){

        ArrayList<Briefdaten> briefdatenListe = new ArrayList<>();

        if(erziehungsberechtigte.size() == 1){
            briefdatenListe.add(erziehungsberechtigte.get(0).getBriefdaten());
            return briefdatenListe;
        }

        Erziehungsberechtigter erzb1 = erziehungsberechtigte.get(0);
        Erziehungsberechtigter erzb2 = erziehungsberechtigte.get(1);

        // Frau zuerst!
        if(erzb1.isMaennlich()){
            Erziehungsberechtigter erzbz = erzb1;
            erzb1 = erzb2;
            erzb2 = erzbz;
        }

        if(!erzb1.hatSelbeAnschriftWie(erzb2)){
            briefdatenListe.add(erzb1.getBriefdaten());
            briefdatenListe.add(erzb2.getBriefdaten());
            return briefdatenListe;
        }

        // Kombinierter Brief an Mutter und Vater

        Briefdaten bd = new Briefdaten();
        bd.mutterVaterBeide = "beide";
        bd.anschriftzeilen.add(erzb1.getAnredeAnschrit());
        bd.anschriftzeilen.add(erzb2.getAnredeAnschrit());
        bd.anschriftzeilen.add(erzb1.getAnschrift1());
        bd.anschriftzeilen.add(erzb1.getAnschrift2());

        bd.anredezeilen.add(erzb1.getAnredeSehrGeehrt(true));
        bd.anredezeilen.add(erzb2.getAnredeSehrGeehrt(false));

        briefdatenListe.add(bd);
        return briefdatenListe;

    }

    public String getKlasse() {
        return klasse;
    }

    public String getNamensbestandteileVorangestellt() {
        return namensbestandteileVorangestellt;
    }

    public String getNamensbestandteileNachgestellt() {
        return namensbestandteileNachgestellt;
    }

    public String getRufname() {
        return rufname;
    }

    public String getFamilienname() {
        return familienname;
    }

    public boolean isMaennlich() {
        return isMaennlich;
    }

    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getGeburtsdatumString(){

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(geburtsdatum);
    }

}
