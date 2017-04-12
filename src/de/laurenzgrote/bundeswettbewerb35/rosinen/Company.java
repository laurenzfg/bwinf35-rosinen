package de.laurenzgrote.bundeswettbewerb35.rosinen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repräsentation einer Firma eines Konglomerates
 */
@SuppressWarnings("WeakerAccess") // Standard POJO, daher alles Public
public class Company {
    private int id;
    private double value; // Wert d. Firma einzelnd betrachtet
    private List<Integer> dependencys; // Adhazenzliste

    public Company(int id, double value) {
        this.id = id;
        this.value = value;

        dependencys = new ArrayList<>();
    }
    
    public void addDependency (int which) {
        dependencys.add(which);
    }
    public boolean isDependent (int which) {
        return  dependencys.contains(which);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Integer> getDependencys() {
        return dependencys;
    }

    public void setDependencys(List<Integer> dependencys) {
        this.dependencys = dependencys;
    }
}
