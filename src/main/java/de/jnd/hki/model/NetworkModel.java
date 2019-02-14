package de.jnd.hki.model;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class NetworkModel {
    private MultiLayerNetwork network;
    private int height = 28;
    private int width = 28;
    private int channels = 1;
    private NativeImageLoader loader = new NativeImageLoader(height, width, channels);

    public NetworkModel() {
    }

    public NetworkModel(MultiLayerNetwork network) {
        this.network = network;
    }

    public MultiLayerNetwork getNetwork() {
        return network;
    }

    public void setNetwork(MultiLayerNetwork network) {
        this.network = network;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public NativeImageLoader getLoader() {
        return loader;
    }

    public void setLoader(NativeImageLoader loader) {
        this.loader = loader;
    }
}
