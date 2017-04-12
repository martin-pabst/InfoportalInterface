package klassensitzung.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by martin on 20.03.2017.
 */
@Root
public class Config {

    @Element
    private Credentials credentials;

    @Element
    public Templates templates;

    @Element
    public String outputfolder;

    @Element
    public String schuljahr;

    @Element
    public String datumlehrerkonferenz;

    @Element
    public String datumklassenlehrersprechstunde;

    @Element
    public String fristanmeldungnachpruefung;

    @Element
    public String zeitraumnachpruefung;

    @Element
    public String datumzeugnis;

    @Element
    public String anschriftliste;

    public Config() {
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
