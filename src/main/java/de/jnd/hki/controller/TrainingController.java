package de.jnd.hki.controller;

import de.jnd.hki.model.ViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class TrainingController {

    private static Logger log = Logger.getLogger(TrainingController.class);

    @FXML
    private AnchorPane root;

    @FXML
    private TextField epochsField;

    @FXML
    private ProgressIndicator trainingIndicator;

    @FXML
    private TextField networkName;

    @FXML
    private CheckBox saveCheckbox;

    @FXML
    public void initialize() {
        epochsField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    epochsField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    void onTraning(ActionEvent event) {
        Consumer<Boolean> indicatorToggle = this::toggleLoader;
        Runnable task = () -> {
            indicatorToggle.accept(true);
            try {
                log.info(NetworkController.trainNetwork(ViewModel.getCurrentNetworkModel().getNetwork(), Integer.parseInt(epochsField.getText()), 100, 128));
            } catch (IOException e) {
                log.error("Failed at training", e);
            }

            if (saveCheckbox.isSelected()) {
                try {
                    NetworkController.saveNetwork(ViewModel.getCurrentNetworkModel().getNetwork(), String.format("%s/networks/%s.zip", BaseUtils.getJarFolder(), networkName.getText()), true);
                } catch (IOException e) {
                    log.error("Couldn't save network");
                } catch (URISyntaxException e) {
                    log.error("Failed to retrieve jar location");
                }
            }
            indicatorToggle.accept(false);
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    public void toggleLoader(boolean visible) {
        for (Node n : root.getChildren()) {
            if (!(n instanceof ProgressIndicator))
                n.setDisable(visible);
        }
        trainingIndicator.setVisible(visible);
    }

    @FXML
    void onSaveChanged(ActionEvent event) {
        CheckBox chk = (CheckBox) event.getSource();
        networkName.setDisable(!chk.isSelected());
    }

}
