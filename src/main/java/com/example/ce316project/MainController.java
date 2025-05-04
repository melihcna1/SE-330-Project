package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController {

    @FXML
    private BorderPane mainPane;

    private MainApp mainApp;
    private Project currentProject;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        showMainView();
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }

    public void showMainView() {

        if (mainPane == null) {
            throw new IllegalStateException("mainPane is not initialized");
        }

    }

    @FXML
    private void handleNewProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/ProjectScreen.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Project Setup");
            dialogStage.initOwner(mainApp.getPrimaryStage());
            dialogStage.setScene(new Scene(loader.load()));

            ProjectScreen controller = loader.getController();
            controller.thisStage = dialogStage;
            //    controller.setMainController(this);

            dialogStage.showAndWait();

            if (currentProject != null) {
                showDetailedResults();
            }
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to open project setup wizard: " + e.getMessage());
        }
    }

    public void initializeNewProject(File configDir, File submissionsDir) {
        System.out.println("New Project Initialized!");
        System.out.println("Config Dir: " + configDir.getAbsolutePath());
        System.out.println("Submissions Dir: " + submissionsDir.getAbsolutePath());
        this.currentProject = new Project("New Project", configDir.getAbsolutePath(), submissionsDir.getAbsolutePath(), "results", new Project.TestCase("input.txt", "expected_output.txt"));
        List<StudentResult> sampleResults = new ArrayList<>();
        sampleResults.add(new StudentResult("S123", "Passed", "No errors", "Program ran successfully", "Output matches", System.currentTimeMillis()));
        sampleResults.add(new StudentResult("S124", "Failed", "Compile error", "Failed to compile", "No output", System.currentTimeMillis()));
        currentProject.setResults(sampleResults);

        List<Configuration> sampleConfigs = new ArrayList<>();
        sampleConfigs.add(new Configuration("JavaConfig", "Java", new ToolSpec(ToolType.COMPILER, "javac", "-d"), ToolType.COMPILER));
        sampleConfigs.add(new Configuration("PythonConfig", "Python", new ToolSpec(ToolType.INTERPRETER, "python", Arrays.asList("-t")), ToolType.INTERPRETER));
        currentProject.setConfigurations(sampleConfigs);
    }

    @FXML
    private void handleNewConfiguration() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/ConfigurationScreen.fxml"));
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Configuration");
        dialogStage.initOwner(mainApp.getPrimaryStage());
        dialogStage.setScene(new Scene(loader.load()));

        ConfigurationScreen controller = loader.getController();
        controller.thisStage = dialogStage;
        //    controller.setMainController(this);
        dialogStage.showAndWait();
    }


    @FXML
    private void handleOpenProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IAE Project Files", "*.json"));
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            System.out.println("Opening project: " + file.getPath());
            showPlaceholderDialog("Open Project", "Load project from: " + file.getName());
        }
    }

    @FXML
    private void handleSaveProject() {
        if (currentProject == null) {
            showErrorDialog("Error", "No project is currently open!");
            return;
        }
        showPlaceholderDialog("Save Project", "Save current project state here.");
    }

    @FXML
    private void handleSaveProjectAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IAE Project Files", "*.json"));
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file != null) {
            String filePath = file.getPath();
            if (!filePath.endsWith(".iae_proj")) {
                filePath += ".iae_proj";
            }
            System.out.println("Saving project as: " + filePath);
            showPlaceholderDialog("Save Project As", "Save project to: " + file.getName());
        }
    }

    @FXML
    private void handleExportToCSV() {
        if (currentProject == null || currentProject.getResults() == null || currentProject.getResults().isEmpty()) {
            showErrorDialog("Error", "No results available to export!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Results to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file != null) {
            exportResultsToCSV(file);
        }
    }

    @FXML
    private void handleExportToHTML() {
        if (currentProject == null || currentProject.getResults() == null || currentProject.getResults().isEmpty()) {
            showErrorDialog("Error", "No results available to export!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Results to HTML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files", "*.html"));
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file != null) {
            exportResultsToHTML(file);
        }
    }

    @FXML
    private void handleRunBatch() {
        if (currentProject == null) {
            showErrorDialog("Error", "No project is currently open!");
            return;
        }
        System.out.println("Run Batch clicked");
        showBatchRunProgress();
    }

    @FXML
    private void handleManageConfigurations() {
        if (currentProject == null) {
            showErrorDialog("Error", "No project is currently open!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/configuration-management-view.fxml"));
            mainPane.setCenter(loader.load());

            ConfigManagementController controller = loader.getController();
            controller.setMainController(this);
            controller.setConfigurations(FXCollections.observableArrayList(currentProject.getConfigurations()));
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load configuration management view: " + e.getMessage());
        }
    }

    @FXML
    private void handleHelpManual() {
        System.out.println("Help Manual clicked");
        showPlaceholderDialog("Help", "Help manual should open here.");
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About IAE");
        alert.setHeaderText("Integrated Assignment Environment");
        alert.setContentText("Version 1.0\nDeveloped by Team Green, Blue, Purple, Orange");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    public void showPlaceholderDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Placeholder: " + content);
        alert.showAndWait();
    }

    public void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showDetailedResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/detailed-result-view.fxml"));
            mainPane.setCenter(loader.load());

            DetailedResultViewController controller = loader.getController();
            controller.setMainController(this);
            controller.setResults(FXCollections.observableArrayList(currentProject.getResults()));
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load detailed result view: " + e.getMessage());
        }
    }

    public void showBatchRunProgress() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/batch-run-progress-view.fxml"));
            mainPane.setCenter(loader.load());

            BatchRunProgressController controller = loader.getController();
            controller.setMainController(this);
            controller.startBatchRun(currentProject.getResults());
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load batch run progress view: " + e.getMessage());
        }
    }

    private void exportResultsToCSV(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Student ID,Status\n");
            for (StudentResult result : currentProject.getResults()) {
                writer.write(String.format("%s,%s\n", result.getStudentId(), result.getStatus()));
            }
            showPlaceholderDialog("Success", "Results exported to CSV successfully!");
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to export to CSV: " + e.getMessage());
        }
    }

    private void exportResultsToHTML(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<!DOCTYPE html>\n<html>\n<head>\n");
            writer.write("<title>Project Results</title>\n");
            writer.write("<style>table, th, td {border: 1px solid black; border-collapse: collapse; padding: 5px;}</style>\n");
            writer.write("</head>\n<body>\n");
            writer.write("<h2>Project Results</h2>\n");
            writer.write("<table>\n");
            writer.write("<tr><th>Student ID</th><th>Status</th></tr>\n");
            for (StudentResult result : currentProject.getResults()) {
                writer.write(String.format("<tr><td>%s</td><td>%s</td></tr>\n", result.getStudentId(), result.getStatus()));
            }
            writer.write("</table>\n</body>\n</html>");
            showPlaceholderDialog("Success", "Results exported to HTML successfully!");
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to export to HTML: " + e.getMessage());
        }
    }
}