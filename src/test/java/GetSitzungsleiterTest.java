import infoportalinterface.model.Sitzungsleiter;
import infoportalinterface.model.SitzungsleiterList;
import org.junit.Test;
import tools.word.RowChanger;
import tools.word.WordTool;

public class GetSitzungsleiterTest {

	@Test
	public void test() throws Exception {

		SitzungsleiterList sitzungsleiterList = new SitzungsleiterList();
		for(Sitzungsleiter sl: sitzungsleiterList){
			System.out.println(sl);
		}

	}
}
