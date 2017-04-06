package de.laurenzgrote.bundeswettbewerb35.rosinen;

import java.util.List;
import java.util.Stack;

/**
 * Repraesentation eines Firmenkonglemerates
 */
public class Conglomerate {

    // Anzahl der Firmen im Konglomerat
    private int companyCount;
    // Adjazenzlisten über die Abhängigkeiten beim Unternehmenskauf
    private Company[] companys;

    public Conglomerate(Company[] companys) {
        this.companys = companys;
        companyCount = companys.length;
    }

    public void addDependency (int ifYouBuyThat, int buyThat) {
        companys[ifYouBuyThat].addDependency(buyThat);
    }

    public boolean isDependent(int bought, int candidate) {
        return companys[bought].isDependent(candidate);
    }

    public List<Integer> getDependentCompanys(int of) {
        return companys[of].getDependencys();
    }

    public double getImmediateValue (int of) {
        return companys[of].getValue();
    }

    public double getImpliedValue (int of) {
        // Visited-Array für die Tiefensuche im Graphen
        boolean[] includedInPrice = new boolean[companyCount];
        // Stack für Tiefensuche
        Stack<Integer> s = new Stack<>();
        s.add(of);

        double val = 0.0;

        // Tiefensuche: GO
        while (!s.empty()) {
            int cur = s.pop();
            if (!includedInPrice[cur]) {
                val += getImmediateValue(cur);
                includedInPrice[cur] = true;
                s.addAll(getDependentCompanys(cur));
            }
        }

        return val;
    }

    /**
     * @return Darstellung des Konglomerats als String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < companyCount; i++) {
            sb.append(i);
            sb.append(" (");
            sb.append(getImmediateValue(i));
            sb.append("€): ");
            for (int ziel: getDependentCompanys(i)) {
                sb.append(ziel);
                sb.append(" ");
            }
            sb.append("Gesamtwert: ");
            sb.append(getImpliedValue(i));
            sb.append("€\n");
        }

        return sb.toString();
    }
}
