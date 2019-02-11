package de.jnd.hki.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;

public class Gui extends Application {
	private static Logger log = Logger.getLogger(Gui.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage primaryStage) throws Exception {
		log.info("Gui app loaded.");

		FXMLLoader outerLoader = new FXMLLoader(Gui.class.getClassLoader().getResource("view/start.fxml"));

		Scene scene = new Scene(outerLoader.load());

		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
