import org.junit.Test;

public class RundenTest {

	@Test
	public void test() throws Exception {

		double note = 5.49999;
		int ganzerAnteil = (int)note;
		double rest = note - ganzerAnteil;

		int zeugnisnote = ganzerAnteil;

		if(rest >= 0.5 - 0.00000001){
			zeugnisnote++;
		}

		System.out.println("Note: " + note + ", ganzer Anteil: " + ganzerAnteil + ", Zeugnisnote: " + zeugnisnote);


	}

}
