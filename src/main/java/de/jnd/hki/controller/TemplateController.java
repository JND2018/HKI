package de.jnd.hki.controller;

import de.jnd.hki.application.Gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TemplateController {

    private static Logger log = Logger.getLogger(TemplateController.class);

    @FXML
    private FlowPane root;

    @FXML
    private AnchorPane topBar;

    @FXML
    private BorderPane innerPane;


    @FXML
    public void initialize() {
        topBar.prefWidthProperty().bind(root.widthProperty());
    }

    @FXML
    void onFileLoad(ActionEvent event) {
        switchInner("view/fileDetection.fxml");
    }



    @FXML
    void onTrainNetwork(ActionEvent event) {
        switchInner("view/training.fxml");
    }

    public void switchInner(String path) {
        try {
            FXMLLoader outerLoader = new FXMLLoader(Gui.class.getClassLoader().getResource(path));
            outerLoader.setRoot(innerPane);
            innerPane = outerLoader.load();
        } catch (IOException e) {
            log.error("Failed at loading inner", e);
        }
    }

    @FXML
    void openAbout(ActionEvent event) {
        try {
            Stage stage = ViewController.openView("about");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDraw(ActionEvent event) {
        switchInner("view/drawPanelFX.fxml");
    }
}
