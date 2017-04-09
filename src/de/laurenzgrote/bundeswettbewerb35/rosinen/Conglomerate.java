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
    Conglomerate(Company[] companys) {
        this.companys = companys;
        companyCount = companys.length;

        // Ein BitSet für die vollständigen Abhängigkeitslisten je Firma
        connectedCompanys = new BitSet[companyCount];

        // Berechnen der vollständigen Abhängigkeitslisten
        calculateDependencyBitSets();
    }

    /**
     * Berechnet die vollständige Abhängigkeitsliste jeder Firma im Konglomerat
     */
    private void calculateDependencyBitSets() {
        // FÜR jede Firma
        for (int of = 0; of < companyCount; of++) {
            // Die Abhängigkeiten einer Firma entsprechen den Firmen, die bei einer DFS
            // von der Startfirma besucht werden
            // --> Abhängigkeitsliste == DFS-Visited-Array
            BitSet includedCompanys = new BitSet(companyCount);
            // Stack für DFS
            Stack<Integer> s = new Stack<>();
            s.add(of);

            // Standard-Tiefensuche
            while (!s.empty()) {
                int cur = s.pop();
                // Sodenn die Firma noch nicht besucht wurde
                if (!includedCompanys.get(cur)) {
                    // Speicher sie als besucht
                    includedCompanys.set(cur, true);
                    // Und setze alle von ihr abhängigen Firmen auf den Zu-Besuchen-Stack
                    s.addAll(getDependentCompanys(cur));
                }
            }
            // Speichern des DFS-Visited-Arrays im entsprechenden Feld der Klasse
            connectedCompanys[of] = includedCompanys;
        }
    }

    /**
     * @param of Ausgangsfirma
     * @return Liste der von of unmittelbar abhängigen Firmen
     */
    private List<Integer> getDependentCompanys(int of) {
        return companys[of].getDependencys();
    }

    /**
     * @param of Ausgangsfirma
     * @return Einzelwert der Firma of
     */
    private double getImmediateValue(int of) {
        return companys[of].getValue();
    }

    /**
     * @param of Ausgangsfirma
     * @return Wert einer Firma unter Berücksichtigung all ihrer Abhängigkeiten
     */
    private double getImpliedValue(int of) {
        // Vorberechnete vollständige Abhängigkeitsliste laden
        BitSet companys = connectedCompanys[of];
        // Wert dieser Liste berechnen
        return getValue(companys);
    }

    /**
     * @param bs vollständie Abhängigkeitsliste
     * @return Wert der vollständigen Abhängigkeitsliste
     */
    private double getValue(BitSet bs) {
        // Counter
        double val = 0;
        // Alle Firmen, die in der Abhängikeitsliste enthalten sind auf den Counter aufaddieren
        for (int i = 0; i < companyCount; i++)
            if (bs.get(i))
                val += getImmediateValue(i);
        return val;
    }

    /**
     * Berechnen der besten Teilmenge
     * @return Beste Teilmenge im BwInf-Ausgabeformat
     */
    String bestBuy() {
        // Wir brauchen für diese vollständige Suche ein Liste der bekannten Statusse
        // Duden sagt es heißt Status, aber das ist mir egal
        // Wer kann dan noch unterscheiden ob es Plural oder Singular ist?
        // Deutsch ist nichtdeterministisch!
        BufferedSet<BitSet> bufferedSet = new BufferedSet<>();

        // Initialer gekaufte Firmen
        BitSet initialStatus = new BitSet(companyCount);

        // Das ist dann der erste bekannte Status
        bufferedSet.put(initialStatus);
        bufferedSet.flushBuffer(); // Schreiben des Buffers ins Set

        // Wert der besten Teilmenge + entsprechende Teilmenge muss seperat gespeichert werden
        double bestCombinationValue = getValue(initialStatus);
        BitSet bestCombinationCompanys = initialStatus;

        // Für jede Firma
        for (int i = 0; i < companyCount; i++) {
            // Entsprechende Dependencys laden
            BitSet selectedPurchase = connectedCompanys[i];
            // Und mit allen bisher errechneten Statussen logisch verknüpfen
            for (BitSet bs : bufferedSet.getSet()) {
                // Bisher errechneten Status klonen
                BitSet newSubset = (BitSet) bs.clone();
                // Verknüpfen der beiden Käufe
                newSubset.or(selectedPurchase);
                // Berechnen des Wertes der neuen Teilmenge
                double combinedBSValue = getValue(newSubset);
                // Neue Teilemenge auf die Schreibliste der BufferedSet setzen
                bufferedSet.put(newSubset);
                // Haben wir ein neues Maximum gefunden?
                if (combinedBSValue > bestCombinationValue) {
                    // Weil dann sollten wir das speichern
                    bestCombinationValue = combinedBSValue;
                    bestCombinationCompanys = newSubset;
                }
            }
            // Schreiben des Buffers ins Set
            bufferedSet.flushBuffer();
        }

        // Output-Teil
        StringBuilder sb = new StringBuilder();
        sb.append("# Anzahl der Knoten in der Teilmenge\n");
        sb.append(bestCombinationCompanys.cardinality()); // Coole Funktion oder?
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
     * @return Darstellung des Konglomerats als String (für Debugzwecke)
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
