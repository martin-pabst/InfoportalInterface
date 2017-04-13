package pabstsoftware.infoportalinterface.model;

/**
 * Created by martin on 07.04.2017.
 */
public enum IPAbsenzArt {

    krank, stundenweiseBefreit, verspaetet;

    public static IPAbsenzArt fromText(String art) {

        if(art.contains("krank")) return krank;
        if(art.contains("befreit") && art.contains("bis")) return stundenweiseBefreit;
        if(art.contains("versp")) return verspaetet;

        return krank;

    }
}
