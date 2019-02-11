package de.jnd.hki.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class StartController {

	private static Logger log = Logger.getLogger(StartController.class);

	@FXML private FlowPane root;

	@FXML private AnchorPane topBar;

	@FXML private Canvas calculatedNumberCnv;

	@FXML private Button switchInnterBtn;

	@FXML private BorderPane insertionPoint;

	@FXML public void initialize() {

		topBar.prefWidthProperty().bind(root.widthProperty());
	}

	private int num = 0;
	@FXML void onSwitchInnerAction(ActionEvent event) {
		switchNumber(num++);
	}

	public void switchNumber(int number) {
		GraphicsContext gc = calculatedNumberCnv.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, calculatedNumberCnv.getWidth(), calculatedNumberCnv.getHeight());
		gc.setFill(Color.WHITE);
		gc.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.fillText(number+"", Math.round(calculatedNumberCnv.getWidth() / 2), Math.round(calculatedNumberCnv.getHeight() / 2));
	}
}
