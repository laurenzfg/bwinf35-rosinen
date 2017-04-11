package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import de.laurenzgrote.bundeswettbewerb35.rosinen.Conglomerate;
import java.util.*;
import java.util.BitSet;

public class BufferedSet {
    private Stack<BitSet> stack = new Stack<>();
    private Set<BitSet> set = new HashSet<>();
    private long maxItemCount;
    private Conglomerate conglomerate;

    public BufferedSet(long maxBits, int bitsPerItem, Conglomerate conglomerate) {
        maxItemCount = maxBits/bitsPerItem;
        this.conglomerate = conglomerate;
    }

    public void put (BitSet value) {
        stack.push(value);
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
            Arrays.parallelSort(sortedContents, new BitSetComparator(conglomerate));
            set.clear();
            for (int i = sortedContents.length - 1; i > sortedContents.length - 0.1*maxItemCount; i--) {
                set.add(sortedContents[i]);
            }
        }
    }

    public Set<BitSet> getSet() {
        return set;
    }
}
