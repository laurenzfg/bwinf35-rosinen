package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.*;
import java.util.BitSet;

import static java.lang.Math.sqrt;

public class BufferedSet {
    private Stack<BitSet> stack = new Stack<>();
    private Set<BitSet> set = new HashSet<>();
    private int maxItemCount;
    private Map<BitSet, Double> valueMap = new HashMap<>();

    public BufferedSet(int maxBits, int bitsPerItem) {
        maxItemCount = maxBits/bitsPerItem;
    }

    public void put (BitSet bitSet, double value) {
        stack.push(bitSet);
        valueMap.put(bitSet, value);
    }

    public void flushBuffer() {
        while (!stack.empty()) {
            BitSet t = stack.pop();
            set.add(t);
        }
        // Haben wir jetzt zu viele Daten?
        if (set.size() > maxItemCount) {
            // Dann behalten wir nur die besten 10%
            // SortedSet/Map geht nicht, da es gleichwertige Einträge gibt
            //noinspection unchecked
            BitSet sortedContents[] = set.toArray(new BitSet[set.size()]);
            //noinspection unchecked
            Arrays.parallelSort(sortedContents, new BitSetComparator(valueMap));
            set.clear();
            for (int i = sortedContents.length - 1; i > sortedContents.length - sqrt(maxItemCount); i--) {
                set.add(sortedContents[i]);
            }
        }
    }

    public Set<BitSet> getSet() {
        return set;
    }
}
