package de.laurenzgrote.bundeswettbewerb35.rosinen;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Conglomerate conglomerate = conglomerateFromFile(file);
            System.out.println(conglomerate.toString());
        } else {
            System.err.println("Nutzer hat den Vorgang abgebrochen!");
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static Conglomerate conglomerateFromFile (File file) {
        Conglomerate c = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            int companyCount = Integer.parseInt(getNextLine(br));
            Company[] companys = new Company[companyCount];
            // Firmen mit Wert
            for (int i = 0; i < companyCount; i++) {
                String line = getNextLine(br);
                String[] lineSplit = line.split(" ");
                int id = Integer.parseInt(lineSplit[0]);
                double val = Double.parseDouble(lineSplit[1]);
                companys[id] = new Company(id, val);
            }
            // Kongelemerat
            c = new Conglomerate(companys);
            // Dependencies
            String line = getNextLine(br);
            while (line != null) {
                String[] lineSplit = line.split(" ");
                int ifYouBuyThat = Integer.parseInt(lineSplit[0]);
                int buyThat = Integer.parseInt(lineSplit[1]);
                c.addDependency(ifYouBuyThat, buyThat);
                line = getNextLine(br);
            }
        } catch (IOException e) {
            System.err.println("Eingegebene Datei nicht gefunden oder sonstiger IO-Fehler!");
        } catch (NumberFormatException|NullPointerException e) {
            System.err.println("Eingegebene Datei entspricht nicht der Spezifikation!");
        }
        return c;
    }
    private static String getNextLine (BufferedReader br) throws IOException {
        try {
            String line;
            do {
                line = br.readLine();
            } while (line.charAt(0) == '#');
            return line;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
