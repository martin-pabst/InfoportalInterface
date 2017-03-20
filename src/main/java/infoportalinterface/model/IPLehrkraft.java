package infoportalinterface.model;

public class IPLehrkraft {

	private String id;

	private String familienname;

	private String rufname;

	private String benutzername = "";

	private String stundenplan_id;

	private String akadGrad = "";

	private String dienstgrad = "";

	public IPLehrkraft(String id, String familienname, String rufname, String benutzername, String stundenplan_id) {
		super();
		this.id = id;
		this.familienname = familienname;
		this.rufname = rufname;
		this.benutzername = benutzername;
		this.stundenplan_id = stundenplan_id;
	}

	@Override
	public String toString() {

		return "Lehrkraft " + familienname + ", " + rufname + " (" + benutzername + ", " + stundenplan_id + ", " + id
				 + ", " + akadGrad + ", " + dienstgrad + ")";

	}

	public String getId() {
		return id;
	}

	public String getFamilienname() {
		return familienname;
	}

	public String getRufname() {
		return rufname;
	}

	public String getBenutzername() {
		return benutzername;
	}

	public String getStundenplan_id() {
		return stundenplan_id;
	}

	public void setDienstgrad(String dienstgrad) {

		this.dienstgrad = dienstgrad;

	}

	public void setAkadGrad(String akadGrad) {

		this.akadGrad = akadGrad;

	}

}
