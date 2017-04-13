import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTest {

	@Test
	public void test() throws Exception {

		Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Info log message");

        logger.debug("Debug log message");
		logger.error("Error log message");

	}

}
