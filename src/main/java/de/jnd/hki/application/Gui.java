package de.jnd.hki.application;

import de.jnd.hki.controller.BaseUtils;
import de.jnd.hki.controller.NetworkController;
import de.jnd.hki.model.NetworkModel;
import de.jnd.hki.model.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
		ViewModel.setCurrentStage(primaryStage);
        ViewModel.setCurrentNetworkModel(new NetworkModel(NetworkController.loadNetwork(BaseUtils.getTargetLocation()+"/networks/model25.zip")));
        FXMLLoader outerLoader = new FXMLLoader(Gui.class.getClassLoader().getResource("view/template.fxml"));

		Scene scene = new Scene(outerLoader.load());

		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
