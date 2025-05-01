package com.example.ce316project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProjectSetupController {

    @FXML
    private TextField configDirField;

    @FXML
    private TextField submissionsDirField;

    private File configDir;
    private File submissionsDir;

    private Stage dialogStage;
    private MainController mainController;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleBrowseConfig() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Configuration Directory");
        File selected = chooser.showDialog(dialogStage);
        if (selected != null) {
            configDir = selected;
            configDirField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleBrowseSubmissions() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Submissions Directory");
        File selected = chooser.showDialog(dialogStage);
        if (selected != null) {
            submissionsDir = selected;
            submissionsDirField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleStartProject() {
        if (configDir != null && submissionsDir != null) {
            mainController.initializeNewProject(configDir, submissionsDir);
            dialogStage.close();
        } else {
            mainController.showErrorDialog("Missing Directories", "Please select both configuration and submissions directories.");
        }
    }
}
