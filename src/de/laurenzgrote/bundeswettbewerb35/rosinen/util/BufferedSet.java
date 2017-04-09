package de.laurenzgrote.bundeswettbewerb35.rosinen.util;

import java.util.*;

public class BufferedSet<T> {
    private Stack<T> stack = new Stack<>();
    private Set<T> set = new HashSet<>();

    public void put (T value) {
        stack.push(value);
    }

    public void flushBuffer() {
        while (!stack.empty()) {
            T t = stack.pop();
            set.add(t);
        }
    }

    public Set<T> getSet() {
        return set;
    }
}
