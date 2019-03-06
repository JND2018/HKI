package de.jnd.hki.controller;

import de.jnd.hki.application.Gui;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewController {
    public static Stage openView(String name) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getClassLoader().getResource("view/" + name + ".fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        return stage;
    }
}
