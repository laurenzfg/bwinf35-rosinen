package de.laurenzgrote.bundeswettbewerb35.rosinen;

import de.laurenzgrote.bundeswettbewerb35.rosinen.util.DPMap;

import java.util.*;

/**
 * Repraesentation eines Firmenkonglemerates
 * Klasse insofern schlecht implementiert, dass die
 * Konsumentenklasse nach dem Speichern der Dependencys
 * calculateDependencyLists() aufrufen muss.
 * Da die Klasse aber intern ist und außerhalb des Paketes nicht genutzt wird,
 * ist das i.O..
 */
class Conglomerate {
    // Anzahl der Firmen im Konglomerat
    private int companyCount;
    // Repräsentation einer Firma im Speicjher
    private Company[] companys;
    // BitSet[0..companyCount] über alle Firmen die mit der Firma n mit erworben werden müssen
    // (1) Firmenkauf bei Kauf von n erforderlich (0) Firmenkauf bei Kauf von n optional
    private BitSet[] connectedCompanys;

    /**
     * @param companys Array der Firmen
     */
    @SuppressWarnings("unchecked")
    Conglomerate(Company[] companys) {
        this.companys = companys;
        companyCount = companys.length;

        // Ein BitSet je Firma
        connectedCompanys = new BitSet[companyCount];
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

    /**
     * @param ifYouBuyThat
     * @param buyThat
     */
    void addDependency(int ifYouBuyThat, int buyThat) {
        companys[ifYouBuyThat].addDependency(buyThat);
    }

    private List<Integer> getDependentCompanys(int of) {
        return companys[of].getDependencys();
    }

    private double getImmediateValue(int of) {
        return companys[of].getValue();
    }

    private double getImpliedValue(int of) {
        BitSet companys = connectedCompanys[of];
        return getValue(companys);
    }

    private double getValue(BitSet bs) {
        double val = 0;
        for (int i = 0; i < companyCount; i++)
            if (bs.get(i))
                val += getImmediateValue(i);
        return val;
    }

    void calculateDependencyLists() {
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

    String bestBuy() {
        // DP-Map für Berechnung der optimalen Kombination
        DPMap<Double, BitSet> dpMap = new DPMap<>();

        BitSet nothingPurchased = new BitSet(companyCount);

        dpMap.put(0.0, nothingPurchased);
        dpMap.flushStack();

        double bestCombinationValue = 0.0;
        BitSet bestCombinationCompanys = nothingPurchased;

        for (int i = 0; i < companyCount; i++) {
            BitSet selectedPurchase = connectedCompanys[i];
            for (Set<BitSet> bsSet : dpMap.getValues()) {
                for (BitSet bs : bsSet) {
                    BitSet newSubset = (BitSet) bs.clone();
                    // Verknüpfen der beiden Käufe
                    newSubset.or(selectedPurchase);
                    // Berechnen des Wertes der neuen Teilmenge
                    double combinedBSValue = getValue(newSubset);
                    // Neue Teilemenge auf die SChreibliste der DPMap setzen
                    dpMap.put(combinedBSValue, newSubset);
                    // Haben wir ein neues Maximum gefunden?
                    if (combinedBSValue > bestCombinationValue) {
                        bestCombinationValue = combinedBSValue;
                        bestCombinationCompanys = newSubset;
                    }
                }
            }
            // Schreiben der neuen Einkäufe
            dpMap.flushStack();
        }

        // Output-Teil
        StringBuilder sb = new StringBuilder();
        sb.append("# Anzahl der Knoten in der Teilmenge\n");
        sb.append(bestCombinationCompanys.cardinality());
        sb.append("\n# Gesamtwert der Knoten\n");
        sb.append(bestCombinationValue);
        sb.append("\n# Nummern der Knoten\n");
        for (int i = 0; i < companyCount; i++) {
            if (bestCombinationCompanys.get(i)) {
                sb.append(i);
                sb.append("\n");
            }
        }
        return sb.toString();
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
