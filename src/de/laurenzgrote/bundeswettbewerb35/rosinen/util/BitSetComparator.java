package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.BitSet;
import java.util.Comparator;
import java.util.Map;

public class BitSetComparator implements Comparator<BitSet> {
    // Refernz auf Konglomerat zur Berechnung nötig
    private Map<BitSet, Double> valueMap;

    public BitSetComparator(Map<BitSet, Double> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public int compare(BitSet ao, BitSet bo) {
        double a = valueMap.get(ao);
        double b = valueMap.get(bo);

        return (a < b ? -1 : (a==b ? 0 : 1));
    }
}
