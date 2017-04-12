package infoportalinterface.model;

public class IPNote {

	private String valueAsText;

	private double value;

	private IPTendenz tendenz = IPTendenz.keine;

	public String getValueAsText() {
		return valueAsText;
	}

	public double getValue() {
		return value;
	}

	public IPTendenz getTendenz() {
		return tendenz;
	}

	@Override
	public String toString() {
		return valueAsText.replace(".", ",");
	}

	public IPNote(String valueAsText, double value, IPTendenz tendenz) {
		this.valueAsText = valueAsText;
		this.value = value;
		this.tendenz = tendenz;
	}
}
