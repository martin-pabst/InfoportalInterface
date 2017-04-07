import infoportalinterface.InfoPortalInterface;
import org.junit.Test;
import tools.word.WordTool;

import java.util.ArrayList;

public class AlterWordDocumentTest {

	@Test
	public void test() throws Exception {

		WordTool wt = new WordTool("data/test/Klassenkonferenz Protokoll 2016.docx",
				"data/test/TestProtokoll.docx");

		String lineXML = wt.extractLineXML("$L1");

        ArrayList<String> lines = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            String s =lineXML;
            s = s.replace("$L1", "Lehrkraft Links Nr. " + i);
            s = s.replace("$L2", "Lehrkraft Rechts Nr. " + i);
            lines.add(s);
        }

        wt.replaceLineXML("$L1", lines);

		wt.write(wt.getXml());


	}

}
