package de.jnd.hki.application;

import de.jnd.hki.controller.NetworkController;
import de.jnd.hki.model.ConsoleModel;
import de.jnd.hki.model.NetworkModel;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.io.IOException;

public class Console {
    private static Logger log = Logger.getLogger(Console.class);

    public static void main(CommandLine cmd) {
        log.info("Console app loaded.");


        if (cmd.hasOption("network")) {
            try {
                ConsoleModel.setCurrentNetworkModel(new NetworkModel(NetworkController.loadNetwork(cmd.getOptionValue("network"))));
            } catch (IOException e) {
                log.error("Couldn't load network");
            }
        } else {
            ConsoleModel.setCurrentNetworkModel(new NetworkModel(NetworkController.createNetwork()));
        }

        if (cmd.hasOption("train")) {
            try {
                NetworkController.trainNetwork(ConsoleModel.getCurrentNetworkModel().getNetwork(), Integer.parseInt(cmd.getOptionValue("train")), 100, 128);
            } catch (IOException e) {
                log.error("Couldn't train network");
            }
        }

        if (cmd.hasOption("input")) {
            NativeImageLoader loader = ConsoleModel.getCurrentNetworkModel().getLoader();
            MultiLayerNetwork networkModel = ConsoleModel.getCurrentNetworkModel().getNetwork();

            INDArray image = null;
            try {
                image = loader.asMatrix(new File(cmd.getOptionValue("input")));

            } catch (IOException e) {
                log.error("Couldn't train image");
            }
            int number = NetworkController.testImage(image, networkModel);
            log.info(String.format("Expected Number: %s",number));
        }

        if(cmd.hasOption("save")){
            try {
                NetworkController.saveNetwork(ConsoleModel.getCurrentNetworkModel().getNetwork(),cmd.getOptionValue("save"),true);
            } catch (IOException e) {
                log.error("Couldn't save network");
            }
        }

        if(cmd.hasOption("stats")){
            try {
                log.info(NetworkController.trainNetwork(ConsoleModel.getCurrentNetworkModel().getNetwork(),1,0,1));
            } catch (IOException e) {
                log.error("Couldn't train network");
            }
        }
    }
}
