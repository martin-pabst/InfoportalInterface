package infoportalinterface.model;

/**
 * Created by Martin on 18.03.2017.
 */
public class IPWiederholung {

    private int jahrgangsstufe;
    private String art;

    public IPWiederholung(int jahrgangsstufe, String art) {
        this.jahrgangsstufe = jahrgangsstufe;
        this.art = art;
    }

    public int getJahrgangsstufe() {
        return jahrgangsstufe;
    }

    public String getArt() {
        return art;
    }
}
