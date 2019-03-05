package de.jnd.hki.application;

import de.jnd.hki.model.ViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getClassLoader().getResource("view/loadNetwork.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Load Network");
        stage.setScene(new Scene(root1));
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if(ViewModel.getCurrentNetworkModel() != null){
                    ViewModel.setCurrentStage(primaryStage);
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
