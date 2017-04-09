package de.laurenzgrote.bundeswettbewerb35.rosinen;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return ".txt-Dateiein";
            }

        });
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Conglomerate conglomerate = conglomerateFromFile(file);
            System.out.println(conglomerate.toString());
            System.out.println(conglomerate.bestBuy());
        } else {
            System.err.println("Nutzer hat den Vorgang abgebrochen!");
        }
    }

    public static Conglomerate conglomerateFromFile (File file) {
        Conglomerate c = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            @SuppressWarnings("ConstantConditions")
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
            // Konglomerat durchrechnen
            c.calculateDependencyLists();
        } catch (IOException e) {
            System.err.println("Eingegebene Datei nicht gefunden oder sonstiger IO-Fehler!");
        } catch (NumberFormatException e) {
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
        } catch (IndexOutOfBoundsException|NullPointerException e) {
            return null;
        }
    }
}
