package de.jnd.hki.application;

import de.jnd.hki.controller.ViewController;
import de.jnd.hki.model.ViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Gui extends Application {
    private static Logger log = Logger.getLogger(Gui.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Gui app loaded.");
        ViewModel.setCurrentStage(primaryStage);
        Stage stage = ViewController.openView("loadNetwork");
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if(ViewModel.getCurrentNetworkModel() != null){
                    FXMLLoader outerLoader = new FXMLLoader(Gui.class.getClassLoader().getResource("view/template.fxml"));
                    try {
                        primaryStage.setScene(new Scene(outerLoader.load()));
                    } catch (IOException e) {
                        log.error("Failed in inner window close event",e);
                    }

                    primaryStage.setResizable(false);
                    ViewModel.getCurrentStage().show();
                }else {
                    Platform.exit();
                }
                stage.close();
            }
        });
    }
}
