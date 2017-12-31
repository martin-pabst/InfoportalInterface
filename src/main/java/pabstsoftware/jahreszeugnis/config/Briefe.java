package pabstsoftware.jahreszeugnis.config;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Martin on 11.04.2017.
 */
@Root(name = "briefe")
public class Briefe {

    @ElementList(entry = "brief", inline = true)
    public List<String> briefe;

}
