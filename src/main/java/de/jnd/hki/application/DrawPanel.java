package de.jnd.hki.application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class DrawPanel extends Application {

    private Path path;
    private Group lineGroup;
    private static final double DEFAULTTHICKNESS = 3.0;
    private static final double MAXTHICKNESS = 30.0;
    private static final double MINTHICKNESS = 1.0;


    public static void main(String[]args){
        Application.launch(args);
    }

    @Override
    public void start(Stage mainStage){
        mainStage.setTitle("Draw Panel HKI");
        final Group root = new Group();

        mainStage.setResizable(true);
        mainStage.initStyle(StageStyle.UTILITY);

        Scene scene = new Scene(root, 300,400);

        //beinhaltet alle gezeichneten linien
        lineGroup = new Group();

        Button btnClear = new Button();
        btnClear.setText("Clear");
        btnClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lineGroup.getChildren().removeAll(lineGroup.getChildren());
            }
        });

        //lineThickness Slider
        Slider lineThickness = new Slider(MINTHICKNESS, MAXTHICKNESS, DEFAULTTHICKNESS);
        Label labelStroke = new Label("Stroke Thickness");
        VBox utilBox = new VBox(10);
        utilBox.setAlignment(Pos.TOP_CENTER);
        utilBox.getChildren().addAll(btnClear, labelStroke, lineThickness);

        HBox toolBox = new HBox(10);
        toolBox.setAlignment(Pos.TOP_CENTER);
        toolBox.getChildren().addAll(utilBox);

        //lineThickness Anzeige
        final Line sampleLine = new Line(0, 0, 140, 0);
        sampleLine.strokeWidthProperty().bind(lineThickness.valueProperty());
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(MAXTHICKNESS);
        stackPane.getChildren().addAll(sampleLine);

        //Canvas
        final Rectangle canvas = new Rectangle();
        canvas.setCursor(Cursor.CROSSHAIR);
        canvas.setX(20);
        canvas.setY(20);
        canvas.setHeight(500);
        canvas.setWidth(400);
        canvas.setFill(Color.LIGHTGRAY);
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                path = new Path();
                path.setMouseTransparent(true);
                path.setStrokeWidth(sampleLine.getStrokeWidth());
                path.setStroke(sampleLine.getStroke());
                lineGroup.getChildren().add(path);
                path.getElements().add(new MoveTo(event.getSceneX(), event.getSceneY()));
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(canvas.getBoundsInLocal().contains(event.getX(), event.getY())){
                    path.getElements().add(new LineTo(event.getSceneX(), event.getSceneY()));
                }
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                path = null;
            }
        });


        VBox vb = new VBox(20);
        vb.setPrefWidth(scene.getWidth() - 20);
        vb.setLayoutX(20);
        vb.setLayoutY(10);
        vb.getChildren().addAll(toolBox, stackPane, canvas);
        root.getChildren().addAll(vb, lineGroup);
        mainStage.setScene(scene);
        mainStage.show();
    }

}

