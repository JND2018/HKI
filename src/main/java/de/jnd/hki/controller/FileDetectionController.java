package de.jnd.hki.controller;

import de.jnd.hki.model.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class FileDetectionController {
    private static Logger log = Logger.getLogger(TemplateController.class);
    @FXML
    private Canvas calculatedNumberCnv;

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

        try {
            if (file != null)
                switchNumber(NetworkController.testImage(file, ViewModel.getCurrentNetworkModel().getLoader(), ViewModel.getCurrentNetworkModel().getNetwork()));
            log.info(String.format("Selected file: %s", file));
        } catch (IOException e) {
            log.error("Failed at testing image.", e);
        }
    }
}
