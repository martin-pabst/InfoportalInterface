package pabstsoftware.infoportalinterface.model;

/**
 * Created by Martin on 18.03.2017.
 */
public enum IPFachEnum {

    D("Deutsch", "D"),

    M("Mathematik", "M"),
    Ph("Physik", "Ph"),
    C("Chemie", "C"),
    B("Biologie", "B"),
    Inf("Informatik", "Inf"),
    NuT("Natur und Technik", "NuT"),

    E("Englisch", "E"),
    L("Latein", "L"),
    F("Französisch", "F"),
    Ru("Russisch", "Ru"),
    Sps("Spanisch", "Sp"),
    Ch("Chinesisch", "Ch"),

    WR("Wirtschafts- und Rechtslehre", "WR"),
    Geo("Geographie", "Geo"),
    Sk("Sozialkunde", "Sk"),

    K("Kath. Religionslehre", "K"),
    Ev("Evang. Religionslehre", "Ev"),
    Eth("Ethik", "Eth"),

    Ku("Kunst", "Ku"),
    Mu("Musik", "Mu"),
    S("Sport", "Smw"),
    Sm("Sport männlich", "Sm"),
    Sw("Sport weiblich", "Sw");

    private String anzeigeform;
    private String kurzform;

    private IPFachEnum(String anzeigeform, String kurzform) {

        this.anzeigeform = anzeigeform;
        this.kurzform = kurzform;

    }

    public static IPFachEnum findByKurzform(String kurzform){

        for(IPFachEnum ipfe: IPFachEnum.values()){
            if(ipfe.kurzform.equalsIgnoreCase(kurzform)){
                return ipfe;
            }

        }

        if(kurzform.equals("NT")){
            return NuT;
        }

        return null;
    }

    public String getAnzeigeform() {
        return anzeigeform;
    }

    public String getKurzform() {
        return kurzform;
    }

    public static IPFachEnum findByAnzeigeform(String anzeigeform) {
        for(IPFachEnum ipfe: IPFachEnum.values()){
            if(ipfe.anzeigeform.equalsIgnoreCase(anzeigeform)){
                return ipfe;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return kurzform;
    }

    public boolean istVorrueckungsfach(int jahrgangsstufe){

        if(this == S || this == Sw || this == Sm){
            return false;
        }

        if(this == Mu && jahrgangsstufe == 5){
            return false;
        }

        return true;

    }

}
