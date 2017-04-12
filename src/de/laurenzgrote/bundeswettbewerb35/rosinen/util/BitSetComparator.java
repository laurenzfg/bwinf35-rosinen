package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.BitSet;
import java.util.Comparator;
import java.util.Map;

/**
 * Vergleicht BitSets nach dem Wert ihrer Firmen
 */
public class BitSetComparator implements Comparator<BitSet> {
    private Map<BitSet, Double> valueMap;

    // Für Effizienz Map über die Werte der Firmen als Referenz übergeben
    public BitSetComparator(Map<BitSet, Double> valueMap) {
        this.valueMap = valueMap;
    }

    // Standard-Komperator für aufsteigende Sortierung
    @Override
    public int compare(BitSet ao, BitSet bo) {
        double a = valueMap.get(ao);
        double b = valueMap.get(bo);

        return (a < b ? -1 : (a==b ? 0 : 1));
    }
}
