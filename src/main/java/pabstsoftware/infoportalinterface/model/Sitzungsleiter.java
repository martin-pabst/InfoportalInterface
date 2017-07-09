package pabstsoftware.infoportalinterface.model;

/**
 * Created by Martin on 10.04.2017.
 */
public class Sitzungsleiter {

    private String klasse;
    private String sitzungsleiter;
    private String von;
    private String bis;
    private String datum;

    public Sitzungsleiter(String klasse, String sitzungsleiter, String von, String bis, String datum) {
        this.klasse = klasse;
        this.sitzungsleiter = sitzungsleiter;
        this.von = von;
        this.bis = bis;
        this.datum = datum;
    }

    public String getKlasse() {
        return klasse;
    }

    public String getSitzungsleiterUnterschriftKlassensitzung() {
        return sitzungsleiter;
    }

    public String getSitzungsleiterUnterschriftAlsSchulleitung() {

        if(sitzungsleiter.contains("Pabst")){
            return "i.V. " + sitzungsleiter;
        }

        if(sitzungsleiter.contains("Fischer") ||
                sitzungsleiter.contains("Schleßing") ||
                sitzungsleiter.contains("Badum")){
            return "i.A. " + sitzungsleiter;
        }

        return sitzungsleiter;

    }

    public String getSitzungsleiterIn(){
        if(sitzungsleiter.contains("Maier") || sitzungsleiter.contains("Pabst")){
            return "Sitzungsleiter";
        }

        if(sitzungsleiter.contains("Fischer")
                || sitzungsleiter.contains("Schleßing")
                || sitzungsleiter.contains("Badum")){
            return "Sitzungsleiterin";
        }

        return "Sitzungsleiter/in";
    }

    public String getVon() {
        return von;
    }

    public String getBis() {
        return bis;
    }

    public String getDatum() {
        return datum;
    }

    @Override
    public String toString() {
        return klasse + ": " + sitzungsleiter + ", " + von + ", " + bis + ", " + datum;
    }
}
