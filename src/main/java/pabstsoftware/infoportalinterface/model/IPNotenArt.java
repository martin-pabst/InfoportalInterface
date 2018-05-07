package pabstsoftware.infoportalinterface.model;

public enum IPNotenArt {

    schulaufgabe("SA"), //
    kurzarbeit("KA"), //
    stegreifaufgabe("ST"), //
    mündlich("M");

    private String text;

    IPNotenArt(String text) {

        this.text = text;

    }

    public static IPNotenArt findByKurzform(String kurzform) throws Exception {
        for (IPNotenArt ipNotenArt : IPNotenArt.values()) {
            if(ipNotenArt.text.equals(kurzform)){
                return ipNotenArt;
            }
        }

        throw new Exception("IPNotenart zur Kurzform " + kurzform + " nicht gefunden.");

    }

    public boolean isSchriftlich() {
        return this == schulaufgabe || this == stegreifaufgabe || this == kurzarbeit;
    }
}
