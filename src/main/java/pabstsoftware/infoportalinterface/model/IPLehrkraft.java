package pabstsoftware.infoportalinterface.model;

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

	public String getAkadGrad() {
		return akadGrad;
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

    public String getNameMitDienstgrad() {
        String s = "";

        if(dienstgrad != null){
        	s += dienstgrad + " ";
		}

		if(akadGrad != null && !akadGrad.isEmpty()){
        	s += akadGrad + " ";
		}

        if(rufname != null){
            s += rufname + " ";
        }

        if(familienname != null){
            s += familienname;
        }


		return s;
    }

    public String getUnterzeichnername() {
        String s = "";

        if(akadGrad != null && !akadGrad.isEmpty()){
            s += " " + akadGrad;
        }

        if(rufname != null){
            s += rufname;
        }

        if(familienname != null){
            s += " " + familienname;
        }

        if(dienstgrad != null){
            s += ", " + dienstgrad;
        }

        return s;

    }

    public String getKlassenleiterIn(){
		if(dienstgrad != null){
			if(dienstgrad.contains("in") || dienstgrad.contains("LaV")){
				return "Klassenleiterin";
			} else {
				return "Klassenleiter";
			}
		}

		return "Klassenleiter/in";
	}


    public String getDienstgrad() {
        return dienstgrad;
    }
}
