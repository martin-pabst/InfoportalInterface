package pabstsoftware.infoportalinterface.model;

import java.util.ArrayList;
import java.util.HashMap;

public class IPKoppelgruppe {

    private String name;
    private IPFachEnum fach;
    private IPLehrkraft lehrkraft;
    private HashMap<IPKlasse, ArrayList<IPSchueler>> schuelerMap = new HashMap<>();
    private ArrayList<IPKlasse> klassen = new ArrayList<>();

    @Override
    public String toString() {
        String s = "IPKoppelgruppe{" +
                "name='" + name + '\'' +
                ", fach=" + fach +
                ", lehrkraft=" + lehrkraft +
                "}, Klassen: {";
        for (IPKlasse ipKlasse : klassen) {
             s += ipKlasse.getName() + ": " + schuelerMap.get(ipKlasse).size() + " Schüler, ";
        }

        s += "}";

        return s;
    }

    public IPKoppelgruppe(String name, IPFachEnum fach, IPLehrkraft lehrkraft) {
        this.name = name;
        this.fach = fach;
        this.lehrkraft = lehrkraft;
    }

    public void addSchueler(IPSchueler schueler){

        IPKlasse klasse = schueler.getKlasse();
        if(!klassen.contains(klasse)){
            klassen.add(klasse);
            schuelerMap.put(klasse, new ArrayList<>());
        }

        schuelerMap.get(klasse).add(schueler);

    }

    public String getName() {
        return name;
    }

    public IPFachEnum getFach() {
        return fach;
    }

    public IPLehrkraft getLehrkraft() {
        return lehrkraft;
    }

    public HashMap<IPKlasse, ArrayList<IPSchueler>> getSchuelerMap() {
        return schuelerMap;
    }

    public ArrayList<IPKlasse> getKlassen() {
        return klassen;
    }
}
