package de.jnd.hki.model;

import javafx.stage.Stage;

public class ViewModel {
    private static NetworkModel currentNetworkModel;
    private static Stage currentStage;

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void setCurrentStage(Stage currentStage) {
        ViewModel.currentStage = currentStage;
    }

    public static NetworkModel getCurrentNetworkModel() {
        return currentNetworkModel;
    }

    public static void setCurrentNetworkModel(NetworkModel currentNetworkModel) {
        ViewModel.currentNetworkModel = currentNetworkModel;
    }
}
