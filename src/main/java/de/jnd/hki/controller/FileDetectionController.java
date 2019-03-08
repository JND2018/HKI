package de.jnd.hki.controller;

import de.jnd.hki.model.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.apache.log4j.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
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
    private Label status;
    @FXML
    private TextField outputFile;

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
            status.setText("File is empty");
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
            status.setText("File is empty");
            return;
        }

        MultiLayerNetwork network = ViewModel.getCurrentNetworkModel().getNetwork();
        List<Integer> result = new ArrayList<>();

        try {
            List<INDArray> characters;
            characters = InputController.loadImage(file);
            for (INDArray character: characters) {
                result.add(NetworkController.testImage(character, network));
            }
            csvController.writeCSV(result, outputFile.getText());
            status.setText("Finished");
        } catch (IOException | InputException e) {
            if (result.isEmpty()) {
                status.setText("Failed at testing image");
                log.error("Failed at testing image.", e);
            } else {
                status.setText("Could not write to csv. Is the output path correct?");
                log.error("Failed to write to csv file. Filename: " + outputFile.getText());
            }
        }
    }
}
