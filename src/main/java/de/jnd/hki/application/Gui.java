package de.jnd.hki.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Gui extends Application {
	private static Logger log = Logger.getLogger(Gui.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage primaryStage) throws Exception {
		log.info("Gui app loaded.");

		Parent root = FXMLLoader.load(Gui.class.getClassLoader().getResource("resources/view/start.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
