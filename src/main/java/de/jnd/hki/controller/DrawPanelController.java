package de.jnd.hki.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import org.apache.log4j.Logger;


public class DrawPanelController {

    private Path path;
    private Group lineGroup;
    private static final double DEFAULTTHICKNESS = 3.0;
    private static final double MAXTHICKNESS = 30.0;
    private static final double MINTHICKNESS = 1.0;


    private static Logger log = Logger.getLogger(DrawPanelController.class);

    @FXML
    private HBox utilBox;

    @FXML
    private Button clearBtn;

    @FXML
    private Slider lineThickness;

    @FXML
    private StackPane stackPane;

    @FXML
    private Rectangle drawPanelCnv;


    @FXML
    void clearButtonOnMousePressed(ActionEvent event) {
        lineGroup.getChildren().removeAll(lineGroup.getChildren());
    }

    //Utilbox
    public void setUtilBox(HBox utilBox) {
        utilBox.getChildren().addAll(clearBtn, lineThickness);
    }

    //lineThicknessSlider
    public void setLineThickness(Slider lineThickness) {
        lineThickness.setMin(MINTHICKNESS);
        lineThickness.setMax(MAXTHICKNESS);
        lineThickness.setValue(DEFAULTTHICKNESS);
    }

    //lineThickness Anzeige
    Line sampleLine = new Line(0,0,140,0);

    public void setSampleLine(Line sampleLine) {
        sampleLine.strokeWidthProperty().bind(lineThickness.valueProperty());
    }

    public void setStackPane(StackPane stackPane) {
        stackPane.setPrefHeight(MAXTHICKNESS);
        stackPane.getChildren().addAll(sampleLine);
    }

    //Rectangle
    public void setDrawPanelCnv(Rectangle drawPanelCnv) {
        drawPanelCnv.setCursor(Cursor.CROSSHAIR);
        drawPanelCnv.setX(20);
        drawPanelCnv.setY(20);
    }

    @FXML
    void drawPanelOnMouseDragged(MouseEvent event) {
        if(drawPanelCnv.getBoundsInLocal().contains(event.getX(), event.getY())){
            path.getElements().add(new MoveTo(event.getSceneX(), event.getSceneY()));
        }
    }

    @FXML
    void drawPanelOnMousePressed(MouseEvent event) {
        path = new Path();
        path.setMouseTransparent(true);
        path.setStrokeWidth(sampleLine.getStrokeWidth());
        path.setStroke(sampleLine.getStroke());
        lineGroup.getChildren().add(path);
        path.getElements().add(new MoveTo(event.getSceneX(), event.getSceneY()));
    }

    @FXML
    void drawPanelOnMouseReleased(MouseEvent event) {
        path = null;
    }
}
