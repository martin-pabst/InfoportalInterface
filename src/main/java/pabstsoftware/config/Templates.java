package pabstsoftware.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Martin on 10.04.2017.
 */
@Root(name = "templates")
public class Templates {

    @Attribute
    public String folder;

    @Element
    public Jahreszeugnis jahreszeugnis;

}
