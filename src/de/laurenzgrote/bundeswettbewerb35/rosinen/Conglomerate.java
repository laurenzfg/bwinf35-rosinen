package de.laurenzgrote.bundeswettbewerb35.rosinen;

import java.util.*;

/**
 * Repraesentation eines Firmenkonglemerates
 */
public class Conglomerate {
    // Anzahl der Firmen im Konglomerat
    private int companyCount;
    // Adjazenzlisten über die Abhängigkeiten beim Unternehmenskauf
    private Company[] companys;
    // Liste der vollständigen Dependencys
    private BitSet[] connectedCompanys;
    // Umgekerhter Dependency-Graph
    private List<Integer>[] revAdj;
    // DP-Array halt
    private Set<BitSet>[] dpArray;

    @SuppressWarnings("unchecked")
    public Conglomerate(Company[] companys) {
        this.companys = companys;
        companyCount = companys.length;

        connectedCompanys = new BitSet[companyCount];
        revAdj = new List[companyCount];
        // Initialiseren von leeren ArrayLists
        for (int i = 0; i < companyCount; i++) {
            revAdj[i] = new ArrayList<>();
        }
        int maxPossibleRevenue = calculateMaxPossibleRevenue();
        dpArray = new Set[maxPossibleRevenue];
        // Initialisieren von leeren DP-Sets
        for (int i = 0; i < maxPossibleRevenue; i++) {
            dpArray[i] = new HashSet<>();
        }
    }

    private int calculateMaxPossibleRevenue() {
        int maxPossibleRevenue = 0;
        for (int i = 0; i < companyCount; i++) {
            double val = getImmediateValue(i);
            if (val > 0.0) {
                maxPossibleRevenue += val;
            }
        }
        return maxPossibleRevenue;
    }

    public void addDependency (int ifYouBuyThat, int buyThat) {
        companys[ifYouBuyThat].addDependency(buyThat);
        // Umgedrehte Dependency einfügen
        revAdj[buyThat].add(ifYouBuyThat);
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
        BitSet companys = connectedCompanys[of];
        return getValue(companys);
    }

    private int getValue(BitSet bs) {
        int val = 0;
        for (int i = 0; i < companyCount; i++)
            if (bs.get(i))
                val += getImmediateValue(i);
        return val;
    }

    public void calculateDependencyLists() {
        for (int of = 0; of < companyCount; of++) {
            // Bitset als VisitedArray
            BitSet includedCompanys = new BitSet(companyCount);
            // Stack für Tiefensuche
            Stack<Integer> s = new Stack<>();
            s.add(of);

            // Tiefensuche: GO
            while (!s.empty()) {
                int cur = s.pop();
                if (!includedCompanys.get(cur)) {
                    includedCompanys.set(cur, true);
                    s.addAll(getDependentCompanys(cur));
                }
            }

            // Speichern der Liste
            connectedCompanys[of] = includedCompanys;
        }
    }

    public int bestBuy() {
        int bestCombination = 0;
        BitSet nothingPurchased = new BitSet(companyCount);
        dpArray[0].add(nothingPurchased);
        for (int i = 0; i < companyCount; i++) {
            BitSet selectedPurchase = connectedCompanys[i];
            Set<BitSet>[] newDPArray = dpArray.clone();
            for (int j = 0; j <= bestCombination; j++) {
                for (BitSet bs : dpArray[j]) {
                    BitSet combinedBS = (BitSet) bs.clone();
                    combinedBS.or(selectedPurchase);
                    int combinedBSValue = getValue(combinedBS);
                    if (combinedBSValue > 0)
                        newDPArray[combinedBSValue].add(combinedBS);
                    bestCombination = Math.max(bestCombination, combinedBSValue);
                }
            }
            dpArray = newDPArray;
        }
        return bestCombination;
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
            sb.append("\n");
        }

        return sb.toString();
    }
}
