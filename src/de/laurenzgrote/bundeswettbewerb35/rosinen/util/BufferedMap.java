package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.*;
import java.util.BitSet;

/**
 * Initialisiert eine BufferedMap mit eingebauter Heuristik
 */
public class BufferedMap {
    // Ein kleines Helper-POJO
    class MapEntry {
        private BitSet k;
        private double v;

        MapEntry(BitSet k, double v) {
            this.k = k;
            this.v = v;
        }
    }

    private Stack<MapEntry> stack = new Stack<>(); // Buffer
    private Map<BitSet, Double> map = new HashMap<>(); // Map

    private int maxItemCount; // Wann soll die Heuristik greifen?
    private int shrinkTo; // Bis wohin soll die Map verkleinert werden

    private int heuristikCount = 0; // Wie oft hat die Heuristik gegriffen

    /**
     * @param maxItemCount Zahl der Zwischenergebnisse, bei der die Heuristik greifen soll
     * @param percentage 0..99% der Daten werden bei einem Heuristikdurchlauf behalten
     */
    public BufferedMap(int maxItemCount, int percentage) {
        this.maxItemCount = maxItemCount;
        // Brainfuck^10:
        // Rechnet aus wievel percentage von maxItemCount ist
        this.shrinkTo = (int) Math.round((double) percentage/100*maxItemCount);
    }

    /**
     * Fügt ein Item dem Buffer hinzu
     * @param k Key
     * @param v Value
     */
    public void put (BitSet k, double v) {
        stack.push(new MapEntry(k, v));
    }

    /**
     * Schreibt den Buffer in die Map und führt ggfs. die Heuristik aus
     */
    public void flushBuffer() {
        // Schreiben
        while (!stack.empty()) {
            MapEntry mapEntry = stack.pop();
            map.put(mapEntry.k, mapEntry.v);
        }
        // Haben wir jetzt zu viele Daten?
        int itemCount = map.size();
        if (itemCount > maxItemCount) {
            // Dann behalten wir nur die oberen Prozent, wie als Argument Spezifiziert
            // Sortieren der Keys nach Values
            BitSet sortedContents[] = map.keySet().toArray(new BitSet[0]);
            Arrays.parallelSort(sortedContents, new BitSetComparator(map)); // MultithreadingFTW
            // Und jetzt löschen wir Einträge sodass die Map shrinkTo groß wird
            for (int i = 0; i < itemCount - shrinkTo; i++) {
                map.remove(sortedContents[i]);
            }
            // Für die Statistik
            heuristikCount++;
        }
    }

    /**
     * @return Ausführungszahl der Heuristik
     */
    public int getHeuristikCount() {
        return heuristikCount;
    }

    /**
     * @return Alle BitSets die geschrieben wurden
     */
    public Set<BitSet> getSet() {
        return map.keySet();
    }
}
