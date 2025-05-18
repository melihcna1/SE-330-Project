package com.example.ce316project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectSetupController {

    @FXML
    private TextField txtConfigDir;
    @FXML
    private TextField txtSubmissionsDir;
    @FXML
    private Button btnSelectConfigDir;
    @FXML
    private Button btnSelectSubmissionsDir;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    private Stage dialogStage;
    private MainController mainController;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void selectConfigDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Configuration Directory");
        File selectedDir = directoryChooser.showDialog(dialogStage);
        if (selectedDir != null) {
            txtConfigDir.setText(selectedDir.getAbsolutePath());
        }
    }

    @FXML
    private void selectSubmissionsDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Submissions Directory");
        File selectedDir = directoryChooser.showDialog(dialogStage);
        if (selectedDir != null) {
            txtSubmissionsDir.setText(selectedDir.getAbsolutePath());
        }
    }

    @FXML
    private void createProject() {
        String configDirPath = txtConfigDir.getText();
        String submissionsDirPath = txtSubmissionsDir.getText();

        if (configDirPath.isEmpty() || submissionsDirPath.isEmpty()) {
            mainController.showErrorDialog("Error", "Please select both configuration and submissions directories!");
            return;
        }

        File configDir = new File(configDirPath);
        File submissionsDir = new File(submissionsDirPath);

        if (!configDir.exists() || !configDir.isDirectory() || !submissionsDir.exists() || !submissionsDir.isDirectory()) {
            mainController.showErrorDialog("Error", "Invalid directories selected!");
            return;
        }

        try {
            // Create project directories if they don't exist
            File projectsDir = new File("src/PROJECTS");
            if (!projectsDir.exists()) {
                projectsDir.mkdirs();
            }

            // Create a new project
            Project.TestCase testCase = new Project.TestCase(
                "input.txt",  // Default input file
                "expected_output.txt"  // Default output file
            );
            
            Project project = new Project(
                "New Project",
                configDirPath,
                submissionsDirPath,
                "results",
                testCase
            );

            // Save the project
            Path projectPath = Paths.get("src/PROJECTS", "new_project.json");
            ProjectIO.save(project, projectPath);

            mainController.setCurrentProject(project);
            dialogStage.close();
            
        } catch (IOException e) {
            mainController.showErrorDialog("Error", "Failed to create project: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }
}