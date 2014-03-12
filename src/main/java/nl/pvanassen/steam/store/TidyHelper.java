package nl.pvanassen.steam.store;

import org.w3c.tidy.Tidy;

class TidyHelper {
    private static final TidyHelper INSTANCE = new TidyHelper();

    static Tidy getTidy() {
        return INSTANCE.tidy;
    }

    private final Tidy tidy = new Tidy();

    private TidyHelper() {
        tidy.setQuiet( true );
        tidy.setOnlyErrors( true );
        tidy.setShowWarnings( false );
    }
}
