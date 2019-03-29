package de.jnd.hki.model;

public class ConsoleModel {
    private static NetworkModel currentNetworkModel;

    public static NetworkModel getCurrentNetworkModel() {
        return currentNetworkModel;
    }

    public static void setCurrentNetworkModel(NetworkModel currentNetworkModel) {
        ConsoleModel.currentNetworkModel = currentNetworkModel;
    }
}
