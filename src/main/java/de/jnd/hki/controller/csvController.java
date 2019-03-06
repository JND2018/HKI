package de.jnd.hki.controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class csvController {
    private static final int DEFAULTCELLCOLS = 14;
    private static final int DEFAULTCELLROWS = 22;

    public static void writeCSV(List<Integer> result, String file) throws IOException {
        if (file.isEmpty()) throw new IOException();
        if (!file.endsWith(".csv")) {
            file+= ".csv";
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

        for (int row = 0; row < DEFAULTCELLROWS; row++) {
            printer.printRecord(
                    result.subList(
                            row * DEFAULTCELLCOLS,
                            row * DEFAULTCELLCOLS + DEFAULTCELLCOLS
                    )
            );
        }
        printer.flush();
        writer.close();
    }
}
