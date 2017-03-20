package config;

import org.simpleframework.xml.Root;

/**
 * Created by martin on 20.03.2017.
 */
@Root(name = "credentials")
public class TestCredentials {
    public String username;
    public String password;
    public String baseurl;
}
