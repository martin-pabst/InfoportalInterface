import pabstsoftware.infoportalinterface.model.Sitzungsleiter;
import pabstsoftware.infoportalinterface.model.SitzungsleiterList;
import org.junit.Test;

public class GetSitzungsleiterTest {

	@Test
	public void test() throws Exception {

		SitzungsleiterList sitzungsleiterList = new SitzungsleiterList();
		for(Sitzungsleiter sl: sitzungsleiterList){
			System.out.println(sl);
		}

	}
}
