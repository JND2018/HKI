package de.jnd.hki.application;

import de.jnd.hki.controller.BaseUtils;
import org.apache.commons.cli.*;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Loader.load(opencv_java.class); // load native openCV functions
        log.info("Debug: "+BaseUtils.isDebug());
        log.info("Eclipse: "+BaseUtils.isEclipse());
        log.info("IntelliJ: "+BaseUtils.isIntelliJ());
        try {
            log.info("Jar Location: " + new File(BaseUtils.getJarFolder()).getParent());
        } catch (URISyntaxException e) {
            log.error("Failed to retrieve jar location");
        }

        Options options = new Options();

        Option gui = new Option("g", "gui", false, "gui option");
        gui.setRequired(false);
        options.addOption(gui);

        Option network = new Option("n", "network", true, "network path");
        network.setRequired(false);
        options.addOption(network);

        Option trainEpochs = new Option("t", "train", true, "train epochs");
        trainEpochs.setRequired(false);
        options.addOption(trainEpochs);

        Option input = new Option("i", "input", true, "input file");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        Option save = new Option("s", "save", true, "save network");
        save.setRequired(false);
        options.addOption(save);

        Option stats = new Option("ss", "stats", false, "show stats");
        stats.setRequired(false);
        options.addOption(stats);

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


        if(cmd.hasOption("gui")){
            Gui.main(args);
        }else{
            Console.main(cmd);
        }
    }
}
