package de.jnd.hki.controller;

import org.apache.commons.lang3.time.StopWatch;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.InputPreProcessor;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkController {
    private static Logger log = LoggerFactory.getLogger(NetworkController.class);
    private static ArrayList<Integer> labelList = new ArrayList<Integer>() {{
        add(0);
        add(1);
        add(2);
        add(3);
        add(4);
        add(5);
        add(6);
        add(7);
        add(8);
        add(9);
    }};


    public static MultiLayerNetwork loadNetwork(String path) throws IOException {
        return ModelSerializer.restoreMultiLayerNetwork(new File(path));
    }

    public static void saveNetwork(MultiLayerNetwork network, String path, boolean deletePrev) throws IOException {
        File file = new File(path);
        file.mkdirs();
        if (file.exists()) {
            if (deletePrev) {
                file.delete();
                ModelSerializer.writeModel(network, file, false);
            } else {
                log.error("File found.");
            }
        } else {
            ModelSerializer.writeModel(network, file, false);

        }
        log.info("Network saved");
    }

    public static MultiLayerConfiguration getNetworkConfigForMNIST(int seed, int inputs, int outputs, Map<Integer, InputPreProcessor> inputPreProcessorMap) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputs) // Number of input datapoints.
                        .nOut(1000) // Number of output datapoints.
                        .activation(Activation.RELU) // Activation function.
                        .weightInit(WeightInit.XAVIER) // Weight initialization.
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(1000)
                        .nOut(outputs)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true)
                .build();
        conf.setInputPreProcessors(inputPreProcessorMap);

        log.info("Network configuration created");
        return conf;
    }

    public static MultiLayerNetwork createNetwork() {
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

        log.info("Network created");
        return model;
    }

    public static int testImage(NativeImageLoader loader, MultiLayerNetwork model) throws IOException {
        return testImage(BaseUtils.fileChose(null), loader, model);
    }

    public static int testImage(File file, NativeImageLoader loader, MultiLayerNetwork model) throws IOException {
        if (file == null)
            System.exit(0);

        INDArray image = loader.asMatrix(file);
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.transform(image);

        INDArray output = model.output(image);
        float best = 0;
        int bestIndex = 0;

        for (int i = 0; i < 10; i++) {
            float val = output.data().asFloat()[i];
            if (best < val) {
                best = val;
                bestIndex = i;
            }
        }

        log.info(String.format("The expected number is %s", labelList.get(bestIndex)));
        return labelList.get(bestIndex);
    }

    public static StringBuilder trainNetwork(MultiLayerNetwork model, int numEpochs, int printIteration) throws IOException {
        StopWatch stopWatch = new StopWatch();
        log.info("Training network ...");
        int batchSize = 128; // batch size for each epoch
        int rngSeed = 123; // random number seed for reproducibility
        //Get the DataSetIterators:
        MnistDataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
        MnistDataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);

        if (printIteration > 0)
            model.setListeners(new ScoreIterationListener(printIteration));
        stopWatch.start();
        for (int i = 1; i < numEpochs; i++) {
            model.fit(mnistTrain);
        }
        stopWatch.stop();


        Evaluation evaluation = model.evaluate(mnistTest);
        StringBuilder strBuilder = new StringBuilder();

        // print the basic statistics about the trained classifier
        strBuilder.append("\nAccuracy: " + evaluation.accuracy());
        strBuilder.append("\nPrecision: " + evaluation.precision());
        strBuilder.append("\nRecall: " + evaluation.recall());

        // in more complex scenarios, a confusion matrix is quite helpful
        strBuilder.append("\n" + evaluation.confusionToString());

        log.info(String.format("Network trained: %sms",stopWatch.getNanoTime()));
        return strBuilder;
    }
}
