package pabstsoftware.infoportalinterface.model;

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
	    if(valueAsText == null){
	        return "null";
        }
		return valueAsText.replace(".", ",");
	}

	public IPNote(String valueAsText, double value, IPTendenz tendenz) {
		this.valueAsText = valueAsText;
		this.value = value;
		this.tendenz = tendenz;
	}

	/**
	 * parst noten der Form
	 * 3,00
	 * 3-
	 * 3
	 * 3+
	 * @param text
	 */
	public IPNote(String text){

		int i = 0;
		while(text.length() > i &&
                (Character.isDigit(text.charAt(i)) || text.charAt(i) == ',')){
			i++;
		}

        String zahlString = text.substring(0, i);
		zahlString = zahlString.replace(",", ".");
		valueAsText = zahlString;
        value = Double.parseDouble(zahlString);

        if(text.contains("+")){
            tendenz = IPTendenz.plus;
        } else if(text.contains("-")){
            tendenz = IPTendenz.minus;
        } else {
            tendenz = IPTendenz.keine;
        }



	}
}
