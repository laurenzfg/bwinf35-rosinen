package de.laurenzgrote.bundeswettbewerb35.rosinen;

import de.laurenzgrote.bundeswettbewerb35.rosinen.util.BufferedSet;

import java.util.*;

/**
 * Repraesentation eines Firmenkonglemerates
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

        // Druchrechnen der vollstädnigen Abhängigkeitsbäume
        calculateDependencyLists();
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

    private void calculateDependencyLists() {
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
        // Set mit allen Möglichen käufen
        BufferedSet<BitSet> bufferedSet = new BufferedSet<>();

        BitSet initialStatus = new BitSet(companyCount);

        bufferedSet.put(initialStatus);
        bufferedSet.flushBuffer();

        // Wert der besten Teilmenge + entsprechende Teilmenge
        double bestCombinationValue = getValue(initialStatus);
        BitSet bestCombinationCompanys = initialStatus;

        // Für jede Firma
        for (int i = 0; i < companyCount; i++) {
            // Entsprechende Dependencys laden
            BitSet selectedPurchase = connectedCompanys[i];
            // Und mit allen bisher errechneten Möglichkeiten Verknüpfen
            for (BitSet bs : bufferedSet.getSet()) {
                // Bisher errechnete Möglichkeit für Kombi klonen
                BitSet newSubset = (BitSet) bs.clone();
                // Verknüpfen der beiden Käufe
                newSubset.or(selectedPurchase);
                // Berechnen des Wertes der neuen Teilmenge
                double combinedBSValue = getValue(newSubset);
                // Neue Teilemenge auf die Schreibliste der BufferedSet setzen
                bufferedSet.put(newSubset);
                // Haben wir ein neues Maximum gefunden?
                if (combinedBSValue > bestCombinationValue) {
                    bestCombinationValue = combinedBSValue;
                    bestCombinationCompanys = newSubset;
                }
            }
            // Schreiben der neuen Einkäufe
            bufferedSet.flushBuffer();
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
