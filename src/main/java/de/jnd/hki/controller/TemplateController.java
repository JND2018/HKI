package de.jnd.hki.controller;

import de.jnd.hki.application.Gui;
import de.jnd.hki.model.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class TemplateController {

    private static Logger log = Logger.getLogger(TemplateController.class);

    @FXML
    private FlowPane root;

    @FXML
    private AnchorPane topBar;

    @FXML
    private Canvas calculatedNumberCnv;

    @FXML
    private Button loadImageBtn;

    @FXML
    private BorderPane innerPane;

    @FXML
    void onFileLoad(ActionEvent event) {

        try {
            FXMLLoader outerLoader = new FXMLLoader(Gui.class.getClassLoader().getResource("view/inner1.fxml"));
            outerLoader.setRoot(innerPane);
            innerPane = outerLoader.load();
        } catch (IOException e) {
            log.error("Failed at loading inner", e);
        }

        File file = BaseUtils.fileChose(ViewModel.getCurrentStage());

        try {
            if (file != null)
                switchNumber(NetworkController.testImage(file, ViewModel.getCurrentNetworkModel().getLoader(), ViewModel.getCurrentNetworkModel().getNetwork()));
            log.info(String.format("Selected file: %s",file));
        } catch (IOException e) {
            log.error("Failed at testing image.", e);
        }
    }

    @FXML
    public void initialize() {

        topBar.prefWidthProperty().bind(root.widthProperty());
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
}
