package de.jnd.hki.controller;

import de.jnd.hki.model.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.apache.log4j.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

//import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDetectionController {
    private static Logger log = Logger.getLogger(TemplateController.class);
    @FXML
    private Canvas calculatedNumberCnv;
    @FXML
    private TextField filePath;

    @FXML
    public void initialize() {
        switchNumber(0);
    }

    public void switchNumber(int number) {
        GraphicsContext gc = calculatedNumberCnv.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, calculatedNumberCnv.getWidth(), calculatedNumberCnv.getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(number + "", Math.round(calculatedNumberCnv.getWidth() / 2), Math.round(calculatedNumberCnv.getHeight() / 2));
    }

    @FXML
    void onFileSelection(ActionEvent event) {
        File file = BaseUtils.fileChose(ViewModel.getCurrentStage());
        filePath.setText(file.getAbsolutePath());
    }

    @FXML
    void onTestSingleFile(ActionEvent event) {
        String file = filePath.getText();
        if (file.isEmpty()) {
            // do nothing if no file is selected
            return;
        }

        NativeImageLoader loader = ViewModel.getCurrentNetworkModel().getLoader();
        MultiLayerNetwork network = ViewModel.getCurrentNetworkModel().getNetwork();

        try {
            INDArray image = loader.asMatrix(new File(file));
            switchNumber(NetworkController.testImage(image, network));
            log.info(String.format("Selected file: %s", file));
        } catch (IOException e) {
            log.error("Failed at testing image.", e);
        }
    }

    @FXML
    void onTestMultiFile(ActionEvent event) {
        String file = filePath.getText();
        if (file.isEmpty()) {
            // do nothing if no file is selected
            return;
        }

        NativeImageLoader loader = ViewModel.getCurrentNetworkModel().getLoader();
        MultiLayerNetwork network = ViewModel.getCurrentNetworkModel().getNetwork();

        try {
            if (file != null) {
                List<INDArray> characters;
                characters = InputController.loadImage(file);
                List<Integer> result = new ArrayList<>();
                for (INDArray character: characters) {
                    result.add(NetworkController.testImage(character, network));
                }
            }
            log.info(String.format("Selected file: %s", file));
        } catch (InputException e) {
            log.error("Failed at testing image.", e);
        }
    }

    void writeCSV(List<Integer> result, String file) {
        if (!file.endsWith(".csv")) {
            file+= ".csv";
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            log.error("Failed to write to csv file. Filename: " + file);
        }
    }
}
