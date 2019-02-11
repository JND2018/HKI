package de.jnd.hki.application;

import de.jnd.hki.controller.BaseUtils;
import de.jnd.hki.controller.NetworkController;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.InputPreProcessor;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);

//    public static void main(String[] args) throws IOException, InterruptedException {
//        int height = 28;
//        int width = 28;
//        int channels = 1;
//
//        // recordReader.getLabels()
//        // In this version Labels are always in order
//        // So this is no longer needed
//        //List<Integer> labelList = Arrays.asList(2,3,7,1,6,4,0,5,8,9);
//        ArrayList<Integer> labelList = new ArrayList<Integer>() {{
//            add(0);
//            add(1);
//            add(2);
//            add(3);
//            add(4);
//            add(5);
//            add(6);
//            add(7);
//            add(8);
//            add(9);
//        }};
//
//        MultiLayerNetwork model = NetworkController.loadNetwork("networks/model.zip");
//        NativeImageLoader loader = new NativeImageLoader(height, width, channels);
//
//        while (true) {
//            String filechose = fileChose().toString();
//            File file = new File(filechose);
//
//            if(file == null)
//                System.exit(0);
//
//            INDArray image = loader.asMatrix(file);
//            DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
//            scaler.transform(image);
//
//            INDArray output = model.output(image);
//            float best = 0;
//            int bestIndex = 0;
//
//            for(int i = 0;i<10;i++){
//                float val = output.data().asFloat()[i];
//                if(best < val){
//                    best = val;
//                    bestIndex = i;
//                }
//            }
//            System.out.println(labelList.get(bestIndex));
//        }
//    }

    public static String fileChose() {
        JFileChooser fc = new JFileChooser();
        int ret = fc.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String filename = file.getAbsolutePath();
            return filename;
        } else {
            return null;
        }
    }

    public static MultiLayerNetwork createNetwork() throws IOException {
        //number of rows and columns in the input pictures
        int numRows = 28;
        int numColumns = 28;
        int outputNum = 10; // number of output classes
        int batchSize = 128; // batch size for each epoch
        int rngSeed = 123; // random number seed for reproducibility
        int numEpochs = 15; // number of epochs to perform

        Map<Integer, InputPreProcessor> inputPreProcessorMap = new HashMap<>();
        inputPreProcessorMap.put(0, new CnnToFeedForwardPreProcessor(numRows, numColumns));

        MultiLayerConfiguration conf = NetworkController.getNetworkConfigForMNIST(rngSeed, numColumns * numRows, outputNum, inputPreProcessorMap);

        MultiLayerNetwork model = new MultiLayerNetwork(conf);

        model.init();
        //print the score with every 1 iteration
        model.setListeners(new ScoreIterationListener(100));

        //Get the DataSetIterators:
        MnistDataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
        MnistDataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);


        for (int i = 1; i < numEpochs; i++) {
            model.fit(mnistTrain);
        }

        Evaluation evaluation = model.evaluate(mnistTest);

        // print the basic statistics about the trained classifier
        log.info("Accuracy: " + evaluation.accuracy());
        log.info("Precision: " + evaluation.precision());
        log.info("Recall: " + evaluation.recall());

        // in more complex scenarios, a confusion matrix is quite helpful
        log.info(evaluation.confusionToString());

        NetworkController.saveNetwork(model, "networks/model.zip", true);
        return model;
    }


    //mode=gui,console(default)
    public static void main(String[] args) {
        Map<String, String> argsMap = BaseUtils.convertArgsToMap(args);

        switch (argsMap.get("mode") == null ? "" : argsMap.get("mode")) {
            case "gui":
                Gui.main(null);
                break;
            case "console":
                break;
            default:
                System.out.println("default");
                break;
        }
    }
}
