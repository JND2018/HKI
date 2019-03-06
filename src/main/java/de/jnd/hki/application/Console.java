package de.jnd.hki.application;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

public class Console {
    private static Logger log = Logger.getLogger(Console.class);

    public static void main(String[] args) {
        log.info("Console app loaded.");

        Options options = new Options();

        Option input = new Option("i", "input", true, "input file");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }


    }
}
