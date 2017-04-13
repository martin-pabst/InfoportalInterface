package pabstsoftware.klassensitzung.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Martin on 10.04.2017.
 */
@Root(name = "jahreszeugnis")
public class Jahreszeugnis {

    @Attribute
    public String folder;

    @Element
    public String klassenkonferenzprotokoll;

    @Element
    public String notendurchschnittliste;

    @Element
    public String nichtvorrueckerliste;

    @Element
    public Briefe briefe;
}
