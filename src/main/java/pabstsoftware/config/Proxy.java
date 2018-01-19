package pabstsoftware.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "proxy")
public class Proxy {

    @Element
    String url;

    @Element
    int port;

    @Element(required = false)
    String username;

    @Element(required = false)
    String password;

}
