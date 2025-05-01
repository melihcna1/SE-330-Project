package com.example.ce316project;


import com.example.ce316project.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import com.example.ce316project.Project;
 import com.example.ce316project.ConfigurationIO;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainPane; // Reference to the main container

    private MainApp mainApp;
    // private Project currentProject;

    // Called by MainApp to give a reference back to itself
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        // Initialize things that depend on mainApp or primaryStage if needed
    }

    @FXML
    private void handleNewProject() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/example/ce316project/project-setup-view.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Project Setup");
            dialogStage.initOwner(mainApp.getPrimaryStage());
            dialogStage.setScene(new Scene(loader.load()));

            ProjectSetupController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to open project setup wizard: " + e.getMessage());
        }
    }

    public void initializeNewProject(File configDir, File submissionsDir) {
        System.out.println("New Project Initialized!");
        System.out.println("Config Dir: " + configDir.getAbsolutePath());
        System.out.println("Submissions Dir: " + submissionsDir.getAbsolutePath());

    }


    @FXML
    private void handleOpenProject() {
        System.out.println("Open Project clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project File");
        // Assuming project files are saved with a specific extension, e.g., .iae_proj
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IAE Project Files", "*.iae_proj"));
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            System.out.println("Opening project: " + file.getPath());
            // TODO: Implement loading project logic using ProjectIO (needs to be created)
            // loadProject(file);
            showPlaceholderDialog("Open Project", "Load project from: " + file.getName());
        }
    }

    @FXML
    private void handleSaveProject() {
        System.out.println("Save Project clicked");
        // TODO: Implement saving project logic
        // if (currentProject != null && currentProject.getProjectPath() != null) {
        //     saveProject(currentProject.getProjectPath());
        // } else {
        //     handleSaveProjectAs(); // If no path exists, force Save As
        // }
        showPlaceholderDialog("Save Project", "Save current project state here.");
    }

    @FXML
    private void handleSaveProjectAs() {
        System.out.println("Save Project As clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IAE Project Files", "*.iae_proj"));
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file != null) {
            // Ensure the extension is added if the user didn't type it
            String filePath = file.getPath();
            if (!filePath.endsWith(".iae_proj")) {
                filePath += ".iae_proj";
            }
            System.out.println("Saving project as: " + filePath);
            // TODO: Implement saving project logic to the new file path
            // saveProject(new File(filePath));
            showPlaceholderDialog("Save Project As", "Save project to: " + file.getName());
        }
    }


    @FXML
    private void handleRunBatch() {
        System.out.println("Run Batch clicked");
        // TODO: Implement logic to start the batch processing
        // Needs: currentProject, configuration, submissions directory
        // Needs to interact with Executor and update UI (Batch Run Progress view)
        showPlaceholderDialog("Run Batch", "Batch processing should start here.");
    }

    @FXML
    private void handleManageConfigurations() {
        System.out.println("Manage Configurations clicked");
        // TODO: Open the Configuration Management view/window
        // try {
        //    showConfigurationManager();
        // } catch (IOException e) {
        //    showErrorDialog("Error loading configuration manager", e.getMessage());
        // }
        showPlaceholderDialog("Manage Configurations", "Configuration management window should open here.");
    }


    @FXML
    private void handleHelpManual() {
        System.out.println("Help Manual clicked");
        // TODO: Implement opening the help manual (e.g., PDF or HTML file)
        // Example: Use Desktop.getDesktop().browse(new URI("path/to/manual.html"));
        showPlaceholderDialog("Help", "Help manual should open here.");
    }

    @FXML
    private void handleAbout() {
        System.out.println("About clicked");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About IAE");
        alert.setHeaderText("Integrated Assignment Environment");
        alert.setContentText("Version 1.0\nDeveloped by Team Green, Blue, Purple, Orange");
        alert.showAndWait();
    }


    @FXML
    private void handleExit() {
        System.out.println("Exit clicked");
        // Optional: Ask for confirmation if changes are unsaved
        System.exit(0);
    }

    // Helper method for placeholder dialogs
    private void showPlaceholderDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Placeholder: " + content);
        alert.showAndWait();
    }

    // Helper method for error dialogs
    public void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Methods to load different views into the center of the BorderPane would go here
    // e.g., public void showProjectOverview(Project project) { ... }
    // e.g., public void showResultsView(List<StudentResult> results) { ... }

}