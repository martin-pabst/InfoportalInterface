package pabstsoftware.jahreszeugnis.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by martin on 20.03.2017.
 */
@Root(name = "credentials")
public class Credentials {
    @Attribute
    public String username;

    @Attribute
    public String password;

    @Attribute
    public String baseurl;
}
