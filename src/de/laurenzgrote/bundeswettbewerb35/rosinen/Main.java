package de.laurenzgrote.bundeswettbewerb35.rosinen;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            // Batch-Modus
            File inputFile = new File(args[0]);
            Conglomerate conglomerate = conglomerateFromFile(inputFile, 100000, 25);
            String bestBuy = conglomerate.bestBuy();
            System.out.print(bestBuy);
        } else {
            // Interaktiver Modus
            runInteractively();
        }
    }

    private static void runInteractively() throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
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
            File inputFile = fileChooser.getSelectedFile();
            int heuristicMaxItemCount,  heuristicPercentage;
            System.out.println("Maximale Setgröße für Heuristik angeben: [100000]");
            String answer = console.readLine();
            if (answer.equals("")) {
                heuristicMaxItemCount = 100000;
            } else {
                heuristicMaxItemCount = Integer.parseInt(answer);
            }
            System.out.println("Anzahl der zu behaltenden Zwischenergebnisse in %:" +
                    " Bitte ohne Prozentzeichen angeben! [25]");
            answer = console.readLine();
            if (answer.equals("")) {
                heuristicPercentage = 25;
            } else {
                heuristicPercentage = Integer.parseInt(answer);
            }

            Conglomerate conglomerate = conglomerateFromFile(inputFile, heuristicMaxItemCount, heuristicPercentage);
            String bestBuy = conglomerate.bestBuy();
            System.out.println(bestBuy);

            System.out.print("Ideale Teilmenge Speichern (y/n): ");
            if (console.readLine().charAt(0) == 'y') {
                returnVal = fileChooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File outputFile = fileChooser.getSelectedFile();
                    PrintWriter pw = new PrintWriter(outputFile);
                    pw.write(bestBuy);
                    pw.close();
                } else {
                    System.err.println("Nutzer hat den Speichervorgang abgebrochen!");
                }
            }
        } else {
            System.err.println("Nutzer hat den Öffnungsvorgang abgebrochen!");
        }
    }

    private static Conglomerate conglomerateFromFile(File file, int heuristicMaxItemCount, int heuristicPercentage) {
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
            // Dependencies
            String line = getNextLine(br);
            while (line != null) {
                String[] lineSplit = line.split(" ");
                int ifYouBuyThat = Integer.parseInt(lineSplit[0]);
                int buyThat = Integer.parseInt(lineSplit[1]);
                companys[ifYouBuyThat].addDependency(buyThat);
                line = getNextLine(br);
            }
            // Kongelemerat erstellen
            c = new Conglomerate(companys, heuristicMaxItemCount, heuristicPercentage);
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
