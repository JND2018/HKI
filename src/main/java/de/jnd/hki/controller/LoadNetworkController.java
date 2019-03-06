package de.jnd.hki.controller;

import de.jnd.hki.model.NetworkModel;
import de.jnd.hki.model.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LoadNetworkController {
    private static Logger log = LoggerFactory.getLogger(LoadNetworkController.class);

    @FXML
    private TableView<File> networks;

    @FXML
    public void initialize() {
        networks.setRowFactory( tv -> {
            TableRow<File> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    loadNetwork(new ActionEvent(networks,networks));
                }
            });
            return row ;
        });

        File[] models = new File(BaseUtils.getTargetLocation() + "/networks/").listFiles();
        ObservableList<File> data = FXCollections.observableArrayList();
        if (models != null) {
            data.addAll(models);
        }
        networks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory("name"));
        networks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory("path"));
        networks.setItems(data);
    }

    @FXML
    void onAdd(ActionEvent event) {
        MultiLayerNetwork newNetwork = NetworkController.createNetwork();
        String generatedLocation = String.format("%s/networks/%s.zip", BaseUtils.getTargetLocation(), RandomStringUtils.random(15, true, false));
        try {
            NetworkController.saveNetwork(newNetwork, generatedLocation, true);
        } catch (IOException e) {
            log.error("Failed at saving new Network", e);
        }
        networks.getItems().add(new File(generatedLocation));
    }

    @FXML
    void onSub(ActionEvent event) {
        File selectedItem = networks.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.delete()) {
            networks.getItems().remove(selectedItem);
        }
    }

    @FXML
    void loadNetwork(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        File selectedItem = networks.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            try {
                ViewModel.setCurrentNetworkModel(new NetworkModel(NetworkController.loadNetwork(selectedItem)));
                ViewModel.setCurrentStage(ViewController.openView("template"));
                ViewModel.getCurrentStage().setResizable(false);
                ViewModel.getCurrentStage().show();
                stage.close();
            } catch (IOException e) {
                log.error("Failed at loading network",e);
            }
        }
    }

}
