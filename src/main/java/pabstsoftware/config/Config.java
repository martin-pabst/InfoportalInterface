package pabstsoftware.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by martin on 20.03.2017.
 */
@Root
public class Config {

    @Element
    private Credentials credentials;

    @Element(required = false)
    public Proxy proxy;

    @Element
    public Templates templates;

    @Element
    public String outputfolder;

    @Element
    public String schuljahr;

    @Element
    public String datumlehrerkonferenz;


    @Element
    public String anschriftliste;

    public Config() {
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
