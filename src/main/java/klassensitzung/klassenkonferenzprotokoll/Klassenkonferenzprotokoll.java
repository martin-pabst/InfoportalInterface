package klassensitzung.klassenkonferenzprotokoll;

import infoportalinterface.model.*;
import klassensitzung.ScheinerKlassensitzung;
import klassensitzung.config.Config;
import tools.word.RowChanger;
import tools.word.WordTool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Martin on 10.04.2017.
 */
public class Klassenkonferenzprotokoll {

    private ScheinerKlassensitzung scheinerKlassensitzung;
    private IPKlasse klasse;
    private WordTool wt;

    public Klassenkonferenzprotokoll(ScheinerKlassensitzung scheinerKlassensitzung, IPKlasse klasse) {
        this.scheinerKlassensitzung = scheinerKlassensitzung;
        this.klasse = klasse;
    }

    public void execute() throws IOException, URISyntaxException {

        Config config = scheinerKlassensitzung.getConfig();

        String templateFilename = config.templates.folder + "/" + config.templates.jahreszeugnis.folder + "/" + config.templates.jahreszeugnis.klassenkonferenzprotokoll;

        String outputDir = config.outputfolder + "/" + klasse.getName();
        Files.createDirectories(Paths.get(outputDir));

        String outputFilename = outputDir + "/" + config.templates.jahreszeugnis.klassenkonferenzprotokoll;

        wt = new WordTool(templateFilename, outputFilename);

        schreibeSitzungsleiterDatumUhrzeit();

        schreibeLehrerDerKlasse();

        schreibeAbsenzen();

        wt.write();

    }

    private void schreibeAbsenzen(){

        for(IPSchueler schueler: klasse.getSchuelerList()){

            int tage = 0;
            int stunden = 0;

            for(IPAbsenz absenz: schueler.getAbsenzen()){



            }


        }

    }


    private void schreibeSitzungsleiterDatumUhrzeit() {

        Sitzungsleiter sl = scheinerKlassensitzung.getSitzungsleiterListe().findByKlassenname(klasse.getName());

        wt.replace("$DA", sl.getDatum());
        wt.replace("$VO", sl.getVon());
        wt.replace("$BI", sl.getBis());
        wt.replace("$KL", klasse.getName());


        String klassenleiter = klasse.getKlassenleitung1().getNameMitDienstgrad();
        if(klasse.getKlassenleitung2() != null){
            klassenleiter += "; " + klasse.getKlassenleitung2().getNameMitDienstgrad();
        }

        wt.replace("$KT", klassenleiter);

        wt.replace("$K1", klasse.getKlassenleitung1().getUnterzeichnername());

        wt.replace("$SZ", "" + klasse.getSchuelerList().size());

        wt.replace("$SL", sl.getSitzungsleiter());


    }

    private void schreibeLehrerDerKlasse() {

        IPKlassenteam klassenteam = klasse.getKlassenteam();

        ArrayList<LehrkraftImFach> lehrkraftImFachListe = new ArrayList<>();
        HashMap<IPLehrkraft, LehrkraftImFach> lehrkraftMap = new HashMap<>();

        klassenteam.getKlassenteamMap().forEach((ipFach, lehrkraefte) -> {

            for(IPLehrkraft lehrkraft: lehrkraefte){

                boolean stimmberechtigt = !(lehrkraefte.size() > 0 && lehrkraft.getStundenplan_id().length() == 4);

                LehrkraftImFach lkImFach = lehrkraftMap.get(lehrkraft);

                if(lkImFach == null){

                    lkImFach = new LehrkraftImFach(lehrkraft, stimmberechtigt);
                    lehrkraftImFachListe.add(lkImFach);
                    lehrkraftMap.put(lehrkraft, lkImFach);

                }

                lkImFach.addFach(ipFach);

            }

        });

        Collections.sort(lehrkraftImFachListe);

        for(int i = 0; i < lehrkraftImFachListe.size(); i += 2){

            RowChanger rc = wt.getRowChanger("$L1");
            LehrkraftImFach lkLinkeSeite = lehrkraftImFachListe.get(i);
            rc.set("$L1", "" + (i+1) + ". " + lkLinkeSeite.getLkListeName());

            if(i+1 < lehrkraftImFachListe.size()){

                LehrkraftImFach lkRechteSeite = lehrkraftImFachListe.get(i + 1);
                rc.set("$L2", "" + (i+2) + ". " + lkRechteSeite.getLkListeName());

            } else {
                rc.set("$L2", "");
            }

        }


    }


}
