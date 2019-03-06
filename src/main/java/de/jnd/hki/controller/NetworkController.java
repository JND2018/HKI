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
    private static ArrayList<Integer> numberList = new ArrayList<Integer>() {{
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

    public static MultiLayerNetwork loadNetwork(File file) throws IOException {
        return ModelSerializer.restoreMultiLayerNetwork(file);
    }

    public static MultiLayerNetwork loadNetwork(String path) throws IOException {
        return loadNetwork(new File(path));
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
        log.info(String.format("Network saved under %s",file.getAbsolutePath()));
    }

    public static MultiLayerConfiguration getNetworkConfigForMNIST(int seed,int hiddenOutputs , int outputs, Map<Integer, InputPreProcessor> inputPreProcessorMap) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(28 *28)
                        .nOut(hiddenOutputs)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
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
        int outputNum = 10;
        int randomSeed =  (int) (Math.random() * 999);

        Map<Integer, InputPreProcessor> inputPreProcessorMap = new HashMap<>();
        inputPreProcessorMap.put(0, new CnnToFeedForwardPreProcessor(28,28));

        MultiLayerConfiguration conf = NetworkController.getNetworkConfigForMNIST(randomSeed, 1000, outputNum, inputPreProcessorMap);

        MultiLayerNetwork model = new MultiLayerNetwork(conf);

        model.init();

        log.info("Network created");
        return model;
    }

    public static int testImage(INDArray image, NativeImageLoader loader, MultiLayerNetwork model) throws IOException {
        if (image == null) {
            log.error("Empty image");
            System.exit(1);
        }

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

        log.info(String.format("The expected number is %s", numberList.get(bestIndex)));
        return numberList.get(bestIndex);
    }

    public static StringBuilder trainNetwork(MultiLayerNetwork model, int numEpochs, int printIteration, int batchSize) throws IOException {
        StopWatch stopWatch = new StopWatch();
        log.info("Training network ...");
        int seed = 1337;

        MnistDataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, seed);
        MnistDataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, seed);

        if (printIteration > 0)
            model.setListeners(new ScoreIterationListener(printIteration));
        stopWatch.start();
        for (int i = 1; i < numEpochs; i++) {
            model.fit(mnistTrain);
        }
        stopWatch.stop();

        Evaluation evaluation = model.evaluate(mnistTest);
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("\nAccuracy: " + evaluation.accuracy());

        strBuilder.append("\n" + evaluation.confusionToString());

        log.info(String.format("Network trained: %s seconds",stopWatch.getTime()/1000));
        return strBuilder;
    }
}
