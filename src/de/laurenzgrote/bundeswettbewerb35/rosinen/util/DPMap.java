package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.*;

public class DPMap<K, V> {
    private class Job {
        private K key;
        private V value;

        Job(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
    private Stack<Job> stack = new Stack<>();
    private Map<K, Set<V>> map = new HashMap<>();

    public void put (K key, V value) {
        stack.push(new Job(key, value));
    }

    public void flushStack() {
        while (!stack.empty()) {
            Job job = stack.pop();
            K key = job.getKey();
            V value = job.getValue();
            if (map.containsKey(key)) {
                map.get(key).add(value);
            } else {
                Set<V> newSet = new HashSet<V>();
                newSet.add(value);
                map.put(key, newSet);
            }
        }
    }

    public Set<V> getSet (K key) {
        return map.get(key);
    }

    public Set<K> getKeys() {
        return map.keySet();
    }
    public Collection<Set<V>> getValues() {
        return map.values();
    }
}
