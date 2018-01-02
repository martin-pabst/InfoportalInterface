package pabstsoftware.halbjahresbericht.konferenzprotokoll;

import pabstsoftware.infoportalinterface.model.IPFachEnum;
import pabstsoftware.infoportalinterface.model.IPLehrkraft;

import java.util.ArrayList;

/**
 * Created by Martin on 10.04.2017.
 */
public class LehrkraftImFach implements Comparable<LehrkraftImFach> {

    private IPLehrkraft lehrkraft;
    private ArrayList<IPFachEnum> faecher = new ArrayList<>();
    private boolean stimmberechtigt;

    public LehrkraftImFach(IPLehrkraft lehrkraft, boolean stimmberechtigt) {
        this.lehrkraft = lehrkraft;
        this.stimmberechtigt = stimmberechtigt;
    }

    public void addFach(IPFachEnum fach) {
        faecher.add(fach);
    }

    @Override
    public int compareTo(LehrkraftImFach lk) {

        if (lehrkraft != null && lk.lehrkraft != null) {

            if (stimmberechtigt && !lk.stimmberechtigt) {
                return -1;
            }

            if (lk.stimmberechtigt && !stimmberechtigt) {
                return 1;
            }

            String name = lehrkraft.getFamilienname() + lehrkraft.getRufname();
            String lkName = lk.lehrkraft.getFamilienname() + lk.lehrkraft.getRufname();

            return name.compareTo(lkName);

        }

        return 0;
    }

    public String getLkListeName() {

        String fachString = "";

        for (IPFachEnum fach : faecher) {
            fachString += fach.getKurzform() + ", ";
        }

        if(!stimmberechtigt){
            fachString += "nicht stimmber., ";
        }


        if (fachString.length() > 2) {
            fachString = fachString.substring(0, fachString.length() - 2);
        }


        return lehrkraft.getNameMitDienstgrad() + " (" + fachString + ")";
    }

    public void bereinigeSport() {

        if(faecher.contains(IPFachEnum.S)){
            if(faecher.contains(IPFachEnum.Sm) || faecher.contains(IPFachEnum.Sw)){
                faecher.remove(IPFachEnum.S);
            }
        }

    }
}
