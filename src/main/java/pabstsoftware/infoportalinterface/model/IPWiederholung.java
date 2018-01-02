package pabstsoftware.infoportalinterface.model;

/**
 * Created by Martin on 18.03.2017.
 */
public class IPWiederholung {

    private int jahrgangsstufe;
    private String art;

    // 8. Jgst.<br />Pflichtwiederholung an der eigenen Schulart<br />10. Jgst.<br /><br />
    // 6. Jgst.<br />Freiwilliger Rücktritt an der eigenen Schulart<br />


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

    public boolean isFreiwillig(){
        return art.contains("eiwillig");
    }

}
