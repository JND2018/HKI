package de.jnd.hki.application;

import de.jnd.hki.controller.BaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);

    //mode=gui,console(default)
    public static void main(String[] args) {
        log.info("Debug: "+BaseUtils.isDebug());
        log.info("Eclipse: "+BaseUtils.isEclipse());
        log.info("IntelliJ: "+BaseUtils.isIntelliJ());

        Map<String, String> argsMap = BaseUtils.convertArgsToMap(args);
        switch (argsMap.get("mode") == null ? "" : argsMap.get("mode")) {
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
