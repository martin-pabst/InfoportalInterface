package pabstsoftware.infoportalinterface.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IPSchueler implements Comparable<IPSchueler> {

    private String rufname;
    private String familienname;
    private IPKlasse klasse;
    private boolean isMaennlich;

    private ArrayList<IPEinzelnote> einzelnoten = new ArrayList<>();

    private ArrayList<IPErziehungsberechtigter> erziehungsberechtigte = new ArrayList<>();

    private ArrayList<String> fremdsprachen = new ArrayList<>();

    private String konfession;
    private String besuchterReligionsunterricht;

    private String geburtsdatum;
    private String bildungsgang;

    private List<IPWiederholung> wiederholungen = new ArrayList<>();

    private List<IPFach> faecher = new ArrayList<>();

    private List<IPAbsenz> absenzen = new ArrayList<>();

    private int absenzTageGesamt = 0;
    private int absenzStundenGesamt = 0;
    private boolean sehrGefährdet;
    private boolean gefährdet;
    private boolean beiWeiteremAbsinken;

    private ArrayList<IPFach> schlechteNoten = new ArrayList<>();

    public ArrayList<IPEinzelnote> getEinzelnoten() {
        return einzelnoten;
    }

    public boolean darfWiederholen() {

        int aktuelleJahrgangsstufe = klasse.getJahrgangsstufe();

        // In der Unterstufe darf nur einmal wiederholt werden
        if (aktuelleJahrgangsstufe <= 7 && hatWiederholungenVonJgstBisJgst(5, 7)) {
            return false;
        }

        // Dieselbe Jahrgangsstufe oder die nächstfolgende darf nicht wiederholt werden:
        if (hatWiederholungenVonJgstBisJgst(aktuelleJahrgangsstufe - 1, aktuelleJahrgangsstufe)) {
            return false;
        }

        return true;

    }

    public boolean hatWiederholungenVonJgstBisJgst(int vonJgst, int bisJgst) {

        for (IPWiederholung wh : wiederholungen) {
            if (wh.getJahrgangsstufe() >= vonJgst && wh.getJahrgangsstufe() <= bisJgst && !wh.isFreiwillig()) {
                return true;
            }
        }

        return false;

    }


    @Override
    public String toString() {

        String out = familienname + ", " + rufname + "(" + (isMaennlich ? "m" : "w") + ") ";

        for (String fs : fremdsprachen) {
            out += fs + " ";
        }

        out += " (" + konfession + "; " + besuchterReligionsunterricht + ") ";
        out += geburtsdatum + ", " + bildungsgang;

        for (IPErziehungsberechtigter erzb : erziehungsberechtigte) {
            out += "[";
            out += erzb.toString();
            out += "]";
        }

        for (IPFach fach : faecher) {
            out += "[" + fach.toString() + " | " + "]";
        }

        if(einzelnoten.size() > 0){
            out += "\nNoten: ";
            for (IPEinzelnote einzelnote : einzelnoten) {
                out += einzelnote.toString() + "; ";
            }
        }

        return out;

    }

    public IPSchueler(String rufname, String familienname, IPKlasse klasse, boolean isMaennlich, String konfession,
                      String besuchterReligionsunterricht, String geburtsdatum) {
        super();
        this.rufname = rufname;
        this.familienname = familienname;
        this.klasse = klasse;
        this.isMaennlich = isMaennlich;
        this.konfession = konfession;
        this.besuchterReligionsunterricht = besuchterReligionsunterricht;
        this.geburtsdatum = geburtsdatum;
    }

    public List<IPFach> getFaecher() {
        return faecher;
    }

    public int getAbsenzTageGesamt() {
        return absenzTageGesamt;
    }

    public void setAbsenzTageGesamt(int absenzTageGesamt) {
        this.absenzTageGesamt = absenzTageGesamt;
    }

    public int getAbsenzStundenGesamt() {
        return absenzStundenGesamt;
    }

    public void setAbsenzStundenGesamt(int absenzStundenGesamt) {
        this.absenzStundenGesamt = absenzStundenGesamt;
    }

    public String getRufname() {
        return rufname;
    }

    public String getFamilienname() {
        return familienname;
    }

    public IPKlasse getKlasse() {
        return klasse;
    }

    public boolean isMaennlich() {
        return isMaennlich;
    }

    public ArrayList<IPErziehungsberechtigter> getErziehungsberechtigte() {
        return erziehungsberechtigte;
    }

    public ArrayList<String> getFremdsprachen() {
        return fremdsprachen;
    }

    public String getKonfession() {
        return konfession;
    }

    public String getBesuchterReligionsunterricht() {
        return besuchterReligionsunterricht;
    }

    public String getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getBildungsgang() {
        return bildungsgang;
    }

    public void setBildungsgang(String bldGang) {

        this.bildungsgang = bldGang;

    }

    public boolean hasName(String rufname, String familienname) {

        if (rufname == null | familienname == null) {
            return false;
        }


        return rufname.equalsIgnoreCase(this.rufname) &&
                stripFamilienname(familienname).equalsIgnoreCase(stripFamilienname(this.familienname));

    }

    public String stripFamilienname(String name) {
        String[] adelstitel = new String[]{
                "Edler", "von", "de", "auf", "Freiherr", "Prinz", "zu", "Rohrbach"
        };

        for (String titel : adelstitel) {
            int i = name.indexOf(titel);
            if (i > -1 && (i == 0 || name.charAt(i - 1) == ' ')) {
                if (name.length() == i + titel.length() || name.charAt(i + titel.length()) == ' ') {
                    name = name.substring(0, i) + name.substring(i + titel.length());
                    if (i > 0 && i < name.length() && name.charAt(i) == ' ') {
                        name = name.substring(0, i) + name.substring(i + 1);
                    }
                }
            }
        }
        return name.trim();
    }


    public void addWiederholung(IPWiederholung ipw) {

        wiederholungen.add(ipw);

    }

    public List<IPWiederholung> getWiederholungen() {
        return wiederholungen;
    }

    public void setNotenForFach(IPFachEnum ipfe, IPNote jz, IPNote zz, IPNote sg, List<IPNote> schulaufgaben, IPNote gl, IPNote kl) {

        IPFach fach = getOrCreateFach(ipfe);
        fach.setNoten(jz, zz, sg, schulaufgaben, gl, kl);

    }

    private IPFach getOrCreateFach(IPFachEnum ipfe) {

        IPFach fach = getFach(ipfe);

        if (fach != null) {
            return fach;
        }

        fach = new IPFach(ipfe);
        faecher.add(fach);

        return fach;
    }

    public IPFach getFach(IPFachEnum ipfe) {
        return faecher.stream().filter(ipfach -> ipfach.hasFach(ipfe)).findFirst().orElse(null);
    }

    public void addAbsenz(IPAbsenz absenz) {

        absenzen.add(absenz);

    }

    public void clearAbsenzen() {
        absenzen.clear();
    }

    public List<IPAbsenz> getAbsenzen() {
        return absenzen;
    }

    @Override
    public int compareTo(IPSchueler s) {

        if (s.rufname == null || rufname == null || s.familienname == null || familienname == null) {
            return 0;
        }

        return (familienname + rufname).compareTo(s.familienname + s.rufname);

    }

    public String getFamiliennameRufname() {

        String s = "";

        if (familienname != null) {
            s += familienname;
        }

        if (rufname != null) {
            s += ", " + rufname;
        }

        return s;
    }

    public String getDurchschnittVorrueckungsfaecher2Dez() {

        double durchschnitt = getDurchschnittVorrueckungsfaecher();

        if (durchschnitt < 0.001) {
            return "---";
        } else {

            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(durchschnitt).replace(".", ",");

        }

    }

    public Double getDurchschnittVorrueckungsfaecher() {

        double notensumme = 0;
        int anzahl = 0;

        for (IPFach fach : faecher) {
            if (fach.getFachEnum().istVorrueckungsfach(klasse.getJahrgangsstufe())
                    && fach.getJahreszeugnisNote() != null && fach.getJahreszeugnisNote() > 0) {
                anzahl++;
                notensumme += fach.getJahreszeugnisNote();
            }
        }

        if (anzahl > 0) {

            return notensumme / anzahl;

        } else {
            return 0d;
        }

    }

    public String getRufnameFamilienname() {
        String s = "";

        if (rufname != null) {
            s += rufname;
        }

        if (familienname != null) {
            s += " " + familienname;
        }

        return s;
    }

    public boolean isVolljaehrig() {

        int tag = secureInt(geburtsdatum.substring(0, 2));
        int monat = secureInt(geburtsdatum.substring(3, 5));
        int jahr = secureInt(geburtsdatum.substring(6, 10));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Calendar cal = Calendar.getInstance();
        int tagHeute = cal.get(Calendar.DAY_OF_MONTH);
        int monatHeute = cal.get(Calendar.MONTH) + 1;
        int jahrHeute = cal.get(Calendar.YEAR);

        if (jahrHeute - jahr > 18) {
            return true;
        }

        if (jahrHeute - jahr < 18) {
            return false;
        }

        if (monatHeute > monat) {
            return true;
        }

        if (monatHeute < monat) {
            return false;
        }

        if (tagHeute >= tag) {
            return true;
        }

        return false;

    }

    private Integer secureInt(String s) {

        if (s == null || s.isEmpty()) {
            return null;
        }

        while (s.startsWith("0")) {
            s = s.substring(1);
        }

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }

    }

    public String debugOutputDurchschnitVorrueckungsfaecher() {

        String s = familienname + " " + rufname + "\n";

        double notensumme = 0;
        int anzahl = 0;

        for (IPFach fach : faecher) {
            if (fach.getFachEnum().istVorrueckungsfach(klasse.getJahrgangsstufe()) && fach.getJahreszeugnisNote() != null
                    && fach.getJahreszeugnisNote() > 0) {
                anzahl++;
                notensumme += fach.getJahreszeugnisNote();
                s += fach.getFachEnum().getKurzform() + " (" + fach.getJahreszeugnisNote() + "), ";
            }
        }

        double durchschnitt = 0;

        if (anzahl > 0) {

            durchschnitt = notensumme / anzahl;

        } else {
            durchschnitt = 0d;
        }

        DecimalFormat df = new DecimalFormat("#.00");
        s += " => " + df.format(durchschnitt).replace(".", ",");


        return s;

    }

    public void setSehrGefährdet(boolean sehrGefährdet) {
        this.sehrGefährdet = sehrGefährdet;
    }

    public void setGefährdet(boolean gefährdet) {
        this.gefährdet = gefährdet;
    }

    public void setBeiWeiteremAbsinken(boolean beiWeiteremAbsinken) {
        this.beiWeiteremAbsinken = beiWeiteremAbsinken;
    }

    public boolean isSehrGefährdet() {
        return sehrGefährdet;
    }

    public boolean isGefährdet() {
        return gefährdet;
    }

    public boolean isBeiWeiteremAbsinken() {
        return beiWeiteremAbsinken;
    }

    public void addSchlechteNoten(ArrayList<IPFach> schlechteNoten) {

        this.schlechteNoten.addAll(schlechteNoten);

    }

    public ArrayList<IPFach> getSchlechteNoten() {
        return schlechteNoten;
    }

    public void addEinzelnote(IPEinzelnote einzelnote) {

        einzelnoten.add(einzelnote);

    }
}


