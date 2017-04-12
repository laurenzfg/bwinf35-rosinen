package de.laurenzgrote.bundeswettbewerb35.rosinen;

import de.laurenzgrote.bundeswettbewerb35.rosinen.util.BufferedMap;

import java.util.*;

/**
 * Repraesentation eines Firmenkonglemerates
 */
public class Conglomerate {
    // Anzahl der Firmen im Konglomerat
    private int companyCount;
    // Repräsentation einer Firma im Speicjher
    private Company[] companys;
    // BitSet[0..companyCount] über alle Firmen die mit der Firma n mit erworben werden müssen
    // (1) Firmenkauf bei Kauf von n erforderlich (0) Firmenkauf bei Kauf von n optional
    private BitSet[] connectedCompanys;
    // Wir speichern die Teilmengen die nur positive Firmen beinhalten
    private boolean[] onlyPositiveCompanys;

    // Konstanten für die Heuristik
    private int heuristicMaxItemCount, heuristicPercentage;

    /**
     * @param companys Array der Firmen
     */
    Conglomerate(Company[] companys, int heuristicMaxItemCount, int heuristicPercentage) {
        this.companys = companys;
        companyCount = companys.length;
        this.heuristicMaxItemCount = heuristicMaxItemCount;
        this.heuristicPercentage = heuristicPercentage;

        // Ein BitSet für die vollständigen Abhängigkeitslisten je Firma
        connectedCompanys = new BitSet[companyCount];
        onlyPositiveCompanys = new boolean[companyCount];

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

            // Standard-Tiefensuche mit Überprüfung ob nur positive Firmen (mit 0) enthalten sind
            boolean onlyPositive = true;
            while (!s.empty()) {
                int cur = s.pop();
                // Sodenn die Firma noch nicht besucht wurde
                if (!includedCompanys.get(cur)) {
                    // Speicher sie als besucht
                    includedCompanys.set(cur, true);
                    // Und setze alle von ihr abhängigen Firmen auf den Zu-Besuchen-Stack
                    s.addAll(getDependentCompanys(cur));
                    if (getImmediateValue(cur) < 0)
                        onlyPositive = false; // Firma mit negativem Gewicht gefunden
                }
            }
            // Speichern des DFS-Visited-Arrays im entsprechenden Feld der Klasse
            connectedCompanys[of] = includedCompanys;
            // Speichern, ob Firmen mit negativem Gewicht enthalten sind
            onlyPositiveCompanys[of] = onlyPositive;
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
        // Counter für Gesamtwert
        double val = 0;
        // Wert aller Firmen, die in der Abhängikeitsliste enthalten sind auf den Counter aufaddieren
        for (int i = 0; i < companyCount; i++)
            if (bs.get(i))
                val += getImmediateValue(i);
        return val;
    }

    /**
     * Berechnen der besten Teilmenge
     * @return Beste Teilmenge im BwInf-Ausgabeformat
     */
    public String bestBuy() {
        // Wir brauchen für diese vollständige Suche ein Liste der bekannten Statusse
        // Duden sagt es heißt Status, aber das ist mir egal
        // Wer kann dan noch unterscheiden ob es Plural oder Singular ist?
        // Deutsch ist nichtdeterministisch!
        BufferedMap bufferedMap = new BufferedMap(heuristicMaxItemCount, heuristicPercentage);

        // Immer gekaufte Firmen sind die Teilmengen, ausschließlich Positive Firmen beinhalten
        // Weil warum sollte man die nicht haben wollen? (aka "Win-Win-Menge")
        BitSet winWinMenge = new BitSet(companyCount); // Leere Menge
        for (int i = 0; i < companyCount; i++) { // Verknüpfen mit den 100% positivien Firmen
            if (onlyPositiveCompanys[i])
                winWinMenge.or(connectedCompanys[i]);
        }
        double winWinWert = getValue(winWinMenge);

        // Das ist dann die Wurzel des binären Baumes
        bufferedMap.put(winWinMenge, winWinWert);
        bufferedMap.flushBuffer(); // Schreiben des Buffers in die Map

        // Wert der besten Teilmenge + entsprechende Teilmenge muss seperat gespeichert werden
        // (für Ausgabe, damit die Map nicht durchsucht werden muss)
        double bestCombinationValue = winWinWert;
        BitSet bestCombinationCompanys = winWinMenge;

        // Zu beachtende Firmenkäufe bestimmen (PM), (aka Firmen auf der linken Seite der Vereinigungsgleichung)
        // Eine Firma mit negativen direkten Wert oder ohne Wert zu kaufen macht keinen Sinn
        // Und Teilmengen nur aus positiven Firmen sind alle in der Wurzel
        Set<BitSet> pm = new HashSet<>();
        for (int i = 0; i <companyCount; i++)
            if (getImmediateValue(i) > 0 && !onlyPositiveCompanys[i])
                pm.add(connectedCompanys[i]);

        // Für jede Firma aus pm
        for (BitSet selectedPurchase : pm) {
            // Kann man die mit den bisherigen Blättern d. Baumes/Statussen verknüpfen
            for (BitSet bs : bufferedMap.getSet()) {
                // Bisher errechneten Status klonen, weil nicht kaufen ist auch eine option
                BitSet newSubset = (BitSet) bs.clone();
                // Verknüpfen des bisherigen Statussen mit dem neuem Kauf
                newSubset.or(selectedPurchase);
                // Berechnen des Wertes der neuen Teilmenge
                double combinedBSValue = getValue(newSubset);
                // Neue Teilemenge auf die Schreibliste der BufferedMap setzen
                bufferedMap.put(newSubset, combinedBSValue);
                // Haben wir ein neues Maximum gefunden?
                if (combinedBSValue > bestCombinationValue) {
                    // Weil dann sollten wir das speichern
                    bestCombinationValue = combinedBSValue;
                    bestCombinationCompanys = newSubset;
                }
            }
            // Schreiben des Buffers in die Map
            bufferedMap.flushBuffer();
        }

        // Feddic
        // Output-Teil
        // Ist jetzt nicht unbedingt guter Stil das als String auszugeben, aber für die Lesbarkeit habe ich das so implementiert
        StringBuilder sb = new StringBuilder();
        sb.append("# Anzahl der Knoten in der Teilmenge\n");
        sb.append(bestCombinationCompanys.cardinality()); // Kardinalität = Firmenzahl. Coole Eigenschaft oder?
        sb.append("\n# Gesamtwert der Knoten\n");
        sb.append(bestCombinationValue);
        sb.append("\n# Nummern der Knoten\n");
        for (int i = 0; i < companyCount; i++) {
            if (bestCombinationCompanys.get(i)) {
                sb.append(i);
                sb.append("\n");
            }
        }
        sb.append("# Anzahl der Heuristikaufrufe: "); // Wichtig um zu wissen, ob das Ergebnis ein sicheres ist
        sb.append(bufferedMap.getHeuristikCount());
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
