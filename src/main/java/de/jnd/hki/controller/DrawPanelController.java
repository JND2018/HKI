package de.jnd.hki.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.apache.log4j.Logger;


public class DrawPanelController{

    private Path path = null;
    private Group lineGroup;
    private static final double DEFAULTTHICKNESS = 5.0;
    private static final double MAXTHICKNESS = 20.0;
    private static final double MINTHICKNESS = 5.0;


    private static Logger log = Logger.getLogger(DrawPanelController.class);

    @FXML
    private AnchorPane mainWindow;

    @FXML
    private Button clearBtn;

    @FXML
    private Line sampleLine;

    @FXML
    private Slider lineThickness;

    @FXML
    private Rectangle drawPanelCnv;

    @FXML
    private Group root;


    public DrawPanelController() {
    }

    @FXML
    public void initialize() {
        lineGroup = new Group();
        setDrawPanelCnv(drawPanelCnv);
        setSampleLine(sampleLine);
        setLineThickness(lineThickness);
    }

    @FXML
    void clearButtonOnMousePressed(ActionEvent event) {
        lineGroup.getChildren().removeAll(lineGroup.getChildren());
    }

    //lineThicknessSlider
    public void setLineThickness(Slider lineThickness) {
        lineThickness.setMin(MINTHICKNESS);
        lineThickness.setMax(MAXTHICKNESS);
        lineThickness.setValue(DEFAULTTHICKNESS);
    }


    public void setSampleLine(Line sampleLine) {
        sampleLine.setFill(Color.WHITE);
        sampleLine.strokeWidthProperty().bind(lineThickness.valueProperty());
        //mainWindow.setPrefHeight(MAXTHICKNESS);
    }



    //Rectangle
    public void setDrawPanelCnv(Rectangle drawPanelCnv) {
        drawPanelCnv.setCursor(Cursor.CROSSHAIR);
        drawPanelCnv.setFill(Color.BLACK);
        //drawPanelCnv.setX(20);
        //drawPanelCnv.setY(20);
    }


    @FXML
    void drawPanelOnMouseDragged(MouseEvent event) {
        if(drawPanelCnv.getBoundsInLocal().contains(event.getX(), event.getY())){
            path.getElements().add(new LineTo(event.getSceneX(), event.getSceneY()));
        }
    }


    @FXML
    void drawPanelOnMousePressed(MouseEvent event) {
        path = new Path();
        path.setVisible(true);
        path.setStroke(Color.WHITE);
        path.setStrokeWidth(sampleLine.getStrokeWidth());
        path.setStroke(sampleLine.getStroke());
        lineGroup.getChildren().add(path);
        path.getElements().add(new MoveTo(event.getSceneX(), event.getSceneY()));
        test();

    }

    @FXML
    void drawPanelOnMouseReleased(MouseEvent event) {
        path = null;
    }

    void test(){
        root.getChildren().addAll(drawPanelCnv, lineGroup);
    }
}
