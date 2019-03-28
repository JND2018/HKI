package de.jnd.hki.controller;


import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class DrawPanelController {

    private static final double DEFAULTTHICKNESS = 5.0;
    private static final double MAXTHICKNESS = 20.0;
    private static final double MINTHICKNESS = 5.0;

    private final int canvasWidth = 600;
    private final int canvasHeight = 600;

    @FXML
    private Canvas drawPanelCnv;

    @FXML
    private Slider lineThickness;

    @FXML
    private Line sampleLine;


    public void initialize() {

        setSampleLine(sampleLine);
        setLineThickness(lineThickness);
        setDrawPanelCnv(drawPanelCnv);

        GraphicsContext graphicsContext = drawPanelCnv.getGraphicsContext2D();

        drawPanelCnv.setOnMouseDragged(e -> {

            double x = e.getX();
            double y = e.getY();

            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(x, y, sampleLine.getStrokeWidth(), sampleLine.getStrokeWidth());
        });
    }

    //lineThicknessSlider
    public void setLineThickness(Slider lineThickness) {
        lineThickness.setMin(MINTHICKNESS);
        lineThickness.setMax(MAXTHICKNESS);
        lineThickness.setValue(DEFAULTTHICKNESS);
    }


    public void setSampleLine(Line sampleLine) {
        sampleLine.strokeWidthProperty().bind(lineThickness.valueProperty());
    }

    public void setDrawPanelCnv(Canvas drawPanelCnv){
        drawPanelCnv.setHeight(canvasHeight);
        drawPanelCnv.setWidth(canvasWidth);
        GraphicsContext gc = drawPanelCnv.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, drawPanelCnv.getWidth(), drawPanelCnv.getHeight());
    }

    public void onSave() {
//        WritableImage writableImage = new WritableImage(canvasWidth, canvasHeight);
//        BufferedImage bImage = SwingFXUtils.fromFXImage(writableImage, null);
//        ImageIO.write(bImage, ".png", file);
    }

    public void onClear() {
        drawPanelCnv.getGraphicsContext2D().clearRect(0,0, drawPanelCnv.getWidth(), drawPanelCnv.getHeight());
        setDrawPanelCnv(drawPanelCnv);
    }


}