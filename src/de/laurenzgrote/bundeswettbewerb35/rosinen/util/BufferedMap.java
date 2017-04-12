package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.*;
import java.util.BitSet;

public class BufferedMap {
    class MapEntry {
        // Ein kleines Helper-POJO
        private BitSet k;
        private double v;

        MapEntry(BitSet k, double v) {
            this.k = k;
            this.v = v;
        }
    }

    private Stack<MapEntry> stack = new Stack<>();
    private Map<BitSet, Double> map = new HashMap<>();
    private int maxItemCount;
    private int shrinkTo;

    private int heuristikCount = 0;

    /**
     * Initialisiert eine BufferedMap mit eingebauter Heuristik
     * @param maxItemCount Datenzahl, bei der die Heuristik greifen soll
     * @param percentage 0..99% der Daten werden bei einem Heuristikdurchlauf behalten
     */
    public BufferedMap(int maxItemCount, int percentage) {
        this.maxItemCount = maxItemCount;
        // Brainfuck^10:
        // Rechnet aus wievel percentage von maxItemCount ist
        this.shrinkTo = (int) Math.round((double) percentage/100*maxItemCount);
    }

    public void put (BitSet k, double v) {
        stack.push(new MapEntry(k, v));
    }

    public void flushBuffer() {
        while (!stack.empty()) {
            MapEntry mapEntry = stack.pop();
            map.put(mapEntry.k, mapEntry.v);
        }
        // Haben wir jetzt zu viele Daten?
        int itemCount = map.size();
        if (itemCount > maxItemCount) {
            // Dann behalten wir nur die oberen Prozent, wie als Argument Spezifiziert
            // SortedSet/Map geht nicht, da es gleichwertige Einträge gibt
            BitSet sortedContents[] = map.keySet().toArray(new BitSet[0]);
            // Sorten wir das mal auf allen Kernen durch
            Arrays.parallelSort(sortedContents, new BitSetComparator(map));
            // Und jetzt killen wir soviele Einträge sodass die Map shrinkTo groß ist
            for (int i = 0; i < itemCount - shrinkTo; i++) {
                map.remove(sortedContents[i]);
            }
            // Für die Statistik
            heuristikCount++;
        }
    }

    public int getHeuristikCount() {
        return heuristikCount;
    }
    public Set<BitSet> getSet() {
        return map.keySet();
    }
}
