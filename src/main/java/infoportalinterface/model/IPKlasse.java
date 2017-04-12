package infoportalinterface.model;

public class IPKlasse {

	private String id;

	private String name;

	private IPSchuelerListe schuelerList = new IPSchuelerListe();

	private IPLehrkraft klassenleitung1, klassenleitung2, klassenleitung3;

	private IPKlassenteam klassenteam = new IPKlassenteam();

	private int jahrgangsstufe;

	public IPKlasse(String id, String name, String klassenleitung1, String klassenleitung2, String klassenleitung3,
			IPLehrkraftListe lehrkraefte) {
		super();
		this.name = name;
		this.id = id;

		this.klassenleitung1 = lehrkraefte.findByRufnameLeerzeichenFamilienname(klassenleitung1);
		this.klassenleitung2 = lehrkraefte.findByRufnameLeerzeichenFamilienname(klassenleitung2);
		this.klassenleitung3 = lehrkraefte.findByRufnameLeerzeichenFamilienname(klassenleitung3);

		jahrgangsstufe = convertNameToJahrgangsstufe(name);


	}

	private int convertNameToJahrgangsstufe(String name) {

		while(name.startsWith("0")){
			name = name.substring(1);
		}

		while(!Character.isDigit(name.charAt(name.length() - 1))){
			name = name.substring(0, name.length() - 1);
		}

		int jgst = 10;

		try{
			jgst = Integer.parseInt(name);
		} catch (NumberFormatException ex){

		}

		return jgst;

	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		
		sb.append("Klasse " + name + ", id: " + id);
		
		if(klassenleitung1 != null){
			sb.append(", Kl1: ");
			sb.append(klassenleitung1.getBenutzername());
		}

		if(klassenleitung2 != null){
			sb.append(", Kl2: ");
			sb.append(klassenleitung2.getBenutzername());
		}

		if(klassenleitung3 != null){
			sb.append(", Kl3: ");
			sb.append(klassenleitung3.getBenutzername());
		}

		sb.append("\n");
		
		for(IPSchueler schueler: schuelerList){
			sb.append(schueler);
			sb.append("\n");
		}
		
		sb.append("\n\n");

		
		
		return sb.toString();

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public IPSchuelerListe getSchuelerList() {
		return schuelerList;
	}

	public IPLehrkraft getKlassenleitung1() {
		return klassenleitung1;
	}

	public IPLehrkraft getKlassenleitung2() {
		return klassenleitung2;
	}

	public IPLehrkraft getKlassenleitung3() {
		return klassenleitung3;
	}

	public IPKlassenteam getKlassenteam() {
		return klassenteam;
	}

	public void addSchueler(IPSchueler schueler) {
		
		this.schuelerList.add(schueler);
		
	}

    public IPSchueler findSchueler(String rufname, String familienname) {

		return schuelerList.findSchueler(rufname, familienname);

	}

    public String getNameWithout0() {

		String name = getName();
		if(name.startsWith("0")){
			return name.substring(1);
		} else {
			return name;
		}

    }

    public int getJahrgangsstufe() {
        return jahrgangsstufe;
    }
}
