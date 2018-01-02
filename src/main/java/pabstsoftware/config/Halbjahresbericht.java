package pabstsoftware.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Martin on 10.04.2017.
 */
@Root(name = "halbjahresbericht")
public class Halbjahresbericht {

    @Element
    public String datumnotenbildbericht;

    @Attribute
    public String folder;

    @Element
    public String paedagogischekonferenzprotokoll;

    @Element
    public String warnungen;

    @Element
    public String notendurchschnittliste;




    @Element
    public Briefe briefe;
}
