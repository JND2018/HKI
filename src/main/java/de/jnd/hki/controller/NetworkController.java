package de.jnd.hki.controller;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.InputPreProcessor;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class NetworkController {
    private static Logger log = LoggerFactory.getLogger(NetworkController.class);


    public static MultiLayerNetwork loadNetwork(String path) {
        try {
            return ModelSerializer.restoreMultiLayerNetwork(new File(NetworkController.class.getClassLoader().getResource(path).getFile()));
        } catch (IOException e) {
            log.error("Failed at loading the File", e);
        }
        return null;
    }

    public static void saveNetwork(MultiLayerNetwork network, String path, boolean deletePrev) {
        try {
            File file = new File(NetworkController.class.getClassLoader().getResource(path).getFile());
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
        } catch (IOException e) {
            log.error("Failed at loading the File", e);
        }
    }

    public static MultiLayerConfiguration getNetworkConfigForMNIST(int seed, int inputs, int outputs, Map<Integer, InputPreProcessor> inputPreProcessorMap){
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
        return conf;
    }

}
