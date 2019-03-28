package de.jnd.hki.application;

import de.jnd.hki.controller.BaseUtils;
import org.apache.commons.cli.*;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);

    //mode=gui,console(default)
    public static void main(String[] args) {
        Loader.load(opencv_java.class); // load native openCV functions
        log.info("Debug: "+BaseUtils.isDebug());
        log.info("Eclipse: "+BaseUtils.isEclipse());
        log.info("IntelliJ: "+BaseUtils.isIntelliJ());

        Options options = new Options();

        Option mode = new Option("m", "mode", true, "mode option");
        mode.setRequired(true);
        options.addOption(mode);

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


        switch (cmd.getOptionValue("mode")) {
            case "gui":
                Gui.main(null);
                break;
            case "console":
                Console.main(null);
                break;
            default:
                log.info("default");
                break;
        }
    }
}
