package de.jnd.hki.controller;


import de.jnd.hki.model.ViewModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.*;
import java.awt.image.*;

import java.io.IOException;


public class DrawPanelController {

    private static final double DEFAULTTHICKNESS = 10.0;

    @FXML
    private Canvas drawPanelCnv;

    @FXML
    private Canvas outputCnv;

    @FXML
    private Slider lineThickness;

    private GraphicsContext graphicsContext;

    public void initialize() {
        switchNumber(0);

        setLineThickness(lineThickness);

        graphicsContext = drawPanelCnv.getGraphicsContext2D();
        initDraw(graphicsContext);
    }

    @FXML
    void canvasOnMouseDragged(MouseEvent event) {
        graphicsContext.lineTo(event.getX(), event.getY());
        graphicsContext.stroke();
    }

    @FXML
    void canvasOnMousePressed(MouseEvent event) {
        graphicsContext.beginPath();
        graphicsContext.moveTo(event.getX(), event.getY());
        graphicsContext.stroke();
    }

    @FXML
    void canvasOnMouseReleased(MouseEvent event) {

        NativeImageLoader loader = ViewModel.getCurrentNetworkModel().getLoader();
        MultiLayerNetwork network = ViewModel.getCurrentNetworkModel().getNetwork();

        WritableImage image = drawPanelCnv.snapshot(null, null);

        BufferedImage scaledImg = getScaledImage(drawPanelCnv);

        try {
            INDArray indArray = loader.asMatrix(scaledImg);
            int number = NetworkController.testImage(indArray, ViewModel.getCurrentNetworkModel().getNetwork());
            switchNumber(number);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BufferedImage getScaledImage(Canvas canvas) {
        WritableImage writableImage = new WritableImage((int)Math.round(canvas.getWidth()), (int)Math.round(canvas.getHeight()));
        canvas.snapshot(null, writableImage);
        Image tmp = SwingFXUtils.fromFXImage(writableImage, null).getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        BufferedImage scaledImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = scaledImg.getGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();
        return scaledImg;
    }

    public void switchNumber(int number) {
        GraphicsContext gc = outputCnv.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, outputCnv.getWidth(), outputCnv.getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(number + "", Math.round(outputCnv.getWidth() / 2), Math.round(outputCnv.getHeight() / 2));
    }


    private void initDraw(GraphicsContext gc){
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.BLACK);

        gc.rect(
                0,
                0,
                canvasWidth,
                canvasHeight);
        gc.fill();


        gc.setStroke(Color.WHITE);
        gc.setLineWidth(DEFAULTTHICKNESS);
    }

    //lineThicknessSlider
    public void setLineThickness(Slider lineThickness) {
        lineThickness.setValue(DEFAULTTHICKNESS);
    }


    public void onClear() {
            initDraw(graphicsContext);
        }
    }
