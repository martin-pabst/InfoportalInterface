package pabstsoftware.infoportalinterface.model;

/**
 * Created by martin on 07.04.2017.
 */
public enum IPAbsenzArt {

    krank, stundenweiseBefreit, verspaetet, klassenabsenz;

    public static IPAbsenzArt fromText(String art) {

        art = art.toLowerCase();

        if(art.contains("schul") || art.contains("klasse") || art.contains("jahrgang")){
            return klassenabsenz;
        }

        if(art.contains("krank")) return krank;
        if(art.contains("befreit") && art.contains("bis")) return stundenweiseBefreit;
        if(art.contains("versp")) return verspaetet;

        return krank;

    }
}
