package config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by martin on 20.03.2017.
 */
@Root
public class TestConfig {

    @Element
    private TestCredentials credentials;

    public TestConfig() {
    }

    public TestCredentials getCredentials() {
        return credentials;
    }
}
