package com.example.ce316project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private BorderPane mainPane;

    private MainApp mainApp;
    private Project currentProject;
    private BatchRunProgressController batchRunProgressController;

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

//    public void initializeNewProject(File configDir, File submissionsDir) {
//        System.out.println("New Project Initialized!");
//        System.out.println("Config Dir: " + configDir.getAbsolutePath());
//        System.out.println("Submissions Dir: " + submissionsDir.getAbsolutePath());
//        this.currentProject = new Project("New Project", configDir.getAbsolutePath(), submissionsDir.getAbsolutePath(), "results", new Project.TestCase("input.txt", "expected_output.txt"));
//        List<StudentResult> sampleResults = new ArrayList<>();
//        sampleResults.add(new StudentResult("S123", "Passed", "No errors", "Program ran successfully", "Output matches", System.currentTimeMillis()));
//        sampleResults.add(new StudentResult("S124", "Failed", "Compile error", "Failed to compile", "No output", System.currentTimeMillis()));
//        currentProject.setResults(sampleResults);
//
//        List<Configuration> sampleConfigs = new ArrayList<>();
//        sampleConfigs.add(new Configuration("JavaConfig", "Java", new ToolSpec(ToolType.COMPILER, "javac", "-d"), ToolType.COMPILER));
//        sampleConfigs.add(new Configuration("PythonConfig", "Python", new ToolSpec(ToolType.INTERPRETER, "python", Arrays.asList("-t")), ToolType.INTERPRETER));
//        currentProject.setConfigurations(sampleConfigs);
//    }

    @FXML
    private void handleNewConfiguration() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/ConfigurationScreen.fxml"));
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Configuration");
        dialogStage.initOwner(mainApp.getPrimaryStage());
        dialogStage.setScene(new Scene(loader.load()));

        ConfigurationScreen controller = loader.getController();
       // controller.mode = "CREATE";
        controller.thisStage = dialogStage;
        //    controller.setMainController(this);
        dialogStage.showAndWait();
    }

    @FXML
    private void handleEditConfiguration() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/CONFIGURATIONS"));
        fileChooser.setTitle("Edit Configuration File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IAE Configuration Files", "*.json"));
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            System.out.println("Opening Configuration: " + file.getPath());
            try {
                Configuration configuration = ConfigurationIO.load(Path.of(file.getPath()));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/ConfigurationScreenEdit.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit Configuration");
                dialogStage.initOwner(mainApp.getPrimaryStage());

              //  System.out.println(configuration.getCompileCmd());
              //  System.out.println(configuration.getRunCmd());

                ConfigurationScreenEdit controller = new ConfigurationScreenEdit(configuration);
                loader.setController(controller);
                controller.thisStage = dialogStage;
                dialogStage.setScene(new Scene(loader.load()));

                //    controller.setMainController(this);
                dialogStage.showAndWait();
            } catch (ConfigurationIO.InvalidFormatException e) {
                System.err.println("Could not open Configuration File");
            }
        }


    }

    @FXML
    private void handleDeleteConfiguration() {
        // Show configuration selection dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Configuration to Delete");
        fileChooser.setInitialDirectory(new File("src/CONFIGURATIONS"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File configFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if (configFile != null) {
            // Confirm deletion
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Configuration");
            confirm.setHeaderText("Are you sure you want to delete this configuration?");
            confirm.setContentText("Configuration: " + configFile.getName());

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Delete the file
                    Files.delete(configFile.toPath());

                    // Show success message
                    showPlaceholderDialog("Success", "Configuration deleted successfully!");

                } catch (IOException e) {
                    showErrorDialog("Error", "Failed to delete configuration: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleOpenProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project File");
        fileChooser.setInitialDirectory(new File("src/PROJECTS"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IAE Project Files", "*.json"));
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            try {
                Path path = file.toPath();
                Project project = ProjectIO.load(path); 
                setCurrentProject(project);
                showDetailedResults();
                showPlaceholderDialog("Success", "Project loaded successfully: " + file.getName());
            } catch (IOException e) {
                showErrorDialog("Error", "Failed to load project: " + e.getMessage());
            }
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
        processBatchZipSubmissions();
    }

    /**
     * Processes all ZIP submissions in the project's submission directory.
     */
    private void processBatchZipSubmissions() {
        try {
            // First verify that project exists and has configurations
            if (currentProject == null) {
                throw new IllegalStateException("No project is currently loaded");
            }

            // Load project configurations
            List<Configuration> configs = currentProject.getConfigurations();
            System.out.println("Project configurations: " + (configs != null ? configs.size() : "null"));

            if (configs == null || configs.isEmpty()) {
                // Try loading configuration from configurationPath
                try {
                    Configuration loadedConfig = ConfigurationIO.load(Path.of(currentProject.getConfigurationPath()));
                    if (loadedConfig != null) {
                        configs = new ArrayList<>();
                        configs.add(loadedConfig);
                        currentProject.setConfigurations(configs);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to load configuration: " + e.getMessage());
                }
            }

            // Verify configurations again after potential load
            if (configs == null || configs.isEmpty()) {
                throw new IllegalStateException("No configuration available");
            }

            // Get the first configuration and verify it's valid
            Configuration config = configs.get(0);
            if (config == null || config.getTools() == null) {
                throw new IllegalStateException("Invalid configuration");
            }

            // Create results and extract directories
            String extractPath = currentProject.getResultDir() + File.separator + "extracted";
            File extractDir = new File(extractPath);
            extractDir.mkdirs();

            // Process ZIP files
            ZipProcessor zipProcessor = new ZipProcessor(currentProject.getSubmissionsDir(), extractPath);
            List<StudentResult> zipResults = zipProcessor.processAllZipFiles();

            if (zipResults.isEmpty()) {
                throw new IllegalStateException("No submissions found to process");
            }

            // Store initial results and show progress
            currentProject.setResults(zipResults);
            showBatchRunProgress();

            // Process the extracted files in a background thread
            new Thread(() -> {
                try {
                    File inputFile = new File(currentProject.getTestCase().getInputFile());
                    File outputFile = new File(currentProject.getTestCase().getExpectedOutputFile());
                    File[] submissionDirs = new File(extractPath).listFiles(File::isDirectory);

                    if (submissionDirs == null || submissionDirs.length == 0) {
                        throw new IllegalStateException("No submission directories found");
                    }

                    Runner runner = new Runner(config, submissionDirs, inputFile, outputFile);
                    StudentResult[] results = runner.run();

                    // Update results in the UI
                    for (StudentResult result : results) {
                        if (result != null) {
                            Platform.runLater(() -> {
                                batchRunProgressController.updateStudentResult(result);
                            });
                        }
                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showErrorDialog("Processing Error", "Failed to process submissions: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showErrorDialog("Error", "Failed to process submissions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleManageConfigurations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/config-management-view.fxml"));
            Parent root = loader.load();

            ConfigManagementController controller = loader.getController();
            controller.setMainController(this);

            // Replace current content with config management view
            mainPane.setCenter(root);

        } catch (IOException e) {
            showErrorDialog("Error", "Could not load configuration management view: " + e.getMessage());
        }
    }

    @FXML
    private void handleHelpManual() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help Manual");
        alert.setHeaderText("IAE - Integrated Assignment Environment");

        String content = """
        Welcome to IAE! Here's how to use the application:
        
        1. Project Management
           • Create New Project: File > New Project
           • Open Project: File > Open Project
           • Save Project: File > Save or Save As
        
        2. Configuration Management
           • Create Configuration: File > New Configuration
           • Edit Configuration: File > Edit Configurations
           • Delete Configuration: File > Delete Configurations
           • Manage Project Configs: Run > Manage Configurations
        
        3. Running Assignments
           • Batch Processing: Run > Run Batch
           • Supported file types: .zip submissions
           • Results show compilation and test status
        
        4. Results & Export
           • View detailed results in results table
           • Export results to CSV: File > Export to CSV
           • Export results to HTML: File > Export to HTML 
           • Save individual logs using "Save Log" button
           • Print logs using "Print Log" button
        
        5. Key Features
           • Automatic ZIP extraction
           • Multiple compiler/interpreter support
           • Detailed error reporting
           • Batch processing progress tracking
           • Individual result inspection
        
        For technical support or bug reports, please contact the development team.
        Version: 1.0
        """;

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(50);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
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

            batchRunProgressController = loader.getController(
            );
            batchRunProgressController.setMainController(this);
            batchRunProgressController.startBatchRun(currentProject.getResults());
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