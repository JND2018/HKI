package de.jnd.hki.application;

import de.jnd.hki.controller.NetworkController;
import de.jnd.hki.model.NetworkModel;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Console {
    private static Logger log = Logger.getLogger(Console.class);

    public static void main(String[] args) {
        NetworkModel model = new NetworkModel();
        model.setNetwork(NetworkController.createNetwork());
        try {
//            model.setNetwork(NetworkController.loadNetwork(BaseUtils.getTargetLocation()+"/networks/model25.zip"));
            log.info(NetworkController.trainNetwork(model.getNetwork(),1,200));
//            NetworkController.saveNetwork(model.getNetwork(), BaseUtils.getTargetLocation()+"/networks/model50.zip",true);

//            log.info(NetworkController.testImage(model.getLoader(), model.getNetwork()) + "");

//            log.info(NetworkController.trainNetwork(model.getNetwork(),1,0));

        } catch (IOException e) {
            log.error("Failed to load Image.", e);
        }
    }
}
