package anschriftliste;

/**
 * Created by Martin on 11.04.2017.
 */
public class Erziehungsberechtigter {

    private boolean isMaennlich;

    private String familienname;
    private String akadGrad;
    private String vornamen;
    private String vollstaendigerName;

    private String strasse;
    private String hausnummer;
    private String plz;
    private String ort;

    public String getAnschrift1(){
        return strasse + " " + hausnummer;
    }

    public String getAnschrift2(){
        return plz + " " + ort;
    }

    public String getHerrFrau(){
        return isMaennlich ? "Herr" : "Frau";
    }

    public String getHerrnFrau(){
        return isMaennlich ? "Herrn" : "Frau";
    }

    public String getSehrgeehrter(boolean gross){

        String s = gross ? "S" : "s";

        return s + (isMaennlich ? "ehr geehrter" : "ehr geehrte");


    }

    public Briefdaten getBriefdaten(){
        Briefdaten bd = new Briefdaten();
        bd.anschriftzeilen.add(getAnredeAnschrit());
        bd.anschriftzeilen.add(getAnschrift1());
        bd.anschriftzeilen.add(getAnschrift2());

        bd.anredezeilen.add("");
        bd.anredezeilen.add(getAnredeSehrGeehrt(true));

        bd.mutterVaterBeide = isMaennlich ? "Vater" : "Mutter";

        return bd;

    }
    
    public String getAnredeAnschrit(){
        return getHerrnFrau() + " " + vollstaendigerName;
    }

    public String getAnredeSehrGeehrt(boolean gross){
        String nameOhneRufname = vollstaendigerName.replace(vornamen, "");
        nameOhneRufname = nameOhneRufname.replace("  ", " ");
        return getSehrgeehrter(gross) + " " + getHerrFrau() + nameOhneRufname + ",";
    }

    public Erziehungsberechtigter(String erzb1_familienname, String erzb1_akadgrad, String erzb1_vornamen,
                                  String strasse, String hausnummer, String plz, String ort,
                                  String erzb1_vollstaendigerName, String erzb1_artSchluessel) {

        familienname = erzb1_familienname;
        akadGrad = erzb1_akadgrad;
        vornamen = erzb1_vornamen;
        vollstaendigerName = erzb1_vollstaendigerName;

        isMaennlich = erzb1_artSchluessel.equals("2");

        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.ort = ort;

    }

    public boolean hatSelbeAnschriftWie(Erziehungsberechtigter erzb){

        return safeCompare(strasse, erzb.strasse) && safeCompare(hausnummer, erzb.hausnummer)
                && safeCompare(plz, erzb.plz);

    }

    private boolean safeCompare(String s1, String s2){
        if(s1 == null && s2 == null){
            return true;
        }

        if(s1 == null || s2 == null){
            return false;
        }

        return s1.equals(s2);
    }


    public boolean isMaennlich() {
        return isMaennlich;
    }
}
