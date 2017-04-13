import org.junit.Test;
import pabstsoftware.tools.word.RowChanger;
import pabstsoftware.tools.word.WordTool;

public class AlterWordDocumentTest {

	@Test
	public void test() throws Exception {

		WordTool wt = new WordTool("data/test/Klassenkonferenz Protokoll 2016.docx",
				"data/test/TestProtokoll.docx");

		String hint = "$L1";


        for(int i = 0; i < 10; i++){

            RowChanger rc = wt.getRowChanger(hint);

            rc.set("$L1", "Lehrkraft links Nr. " + i);
            rc.set("$L2", "Lehrkraft rechts Nr. " + i);

        }

		wt.write();


	}

}
