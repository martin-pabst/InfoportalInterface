package infoportalinterface.model;

import java.util.Date;

/**
 * Created by martin on 07.04.2017.
 */
public class IPAbsenz {

    private Date von;
    private Date bis;
    private Integer tage, stunden, minuten;

    private String artText;
    private IPAbsenzArt art;

    private Integer stundeVon;
    private Integer stundeBis;

    public IPAbsenz(Date von, Date bis, int tage, int stunden, int minuten, String artText, IPAbsenzArt art, Integer stundeVon, Integer stundeBis) {
        this.von = von;
        this.bis = bis;
        this.tage = tage;
        this.stunden = stunden;
        this.minuten = minuten;
        this.artText = artText;
        this.art = art;
        this.stundeVon = stundeVon;
        this.stundeBis = stundeBis;
    }
}
