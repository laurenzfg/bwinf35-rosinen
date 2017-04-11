package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import de.laurenzgrote.bundeswettbewerb35.rosinen.Conglomerate;

import java.util.BitSet;
import java.util.Comparator;

public class BitSetComparator implements Comparator<BitSet> {
    // Refernz auf Konglomerat zur Berechnung nötig
    private Conglomerate conglomerate;

    public BitSetComparator(Conglomerate conglomerate) {
        this.conglomerate = conglomerate;
    }

    @Override
    public int compare(BitSet ao, BitSet bo) {
        double a = conglomerate.getValue(ao);
        double b = conglomerate.getValue(bo);

        return (a < b ? -1 : (a==b ? 0 : 1));
    }
}
