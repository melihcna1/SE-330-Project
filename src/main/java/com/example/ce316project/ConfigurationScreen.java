package com.example.ce316project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationScreen {

    private Stage thisStage;
    private Configuration configuration;
    private boolean isEditMode = false;

    @FXML
    public TextField txtfieldConfigurationName, txtfieldLanguage, txtfieldToolLocation, txtfieldCompilerArguments, txtfieldRunCall;
    public ChoiceBox<String> choiceboxToolType;
    public Button btnSaveConfiguration, btnToolLocation;

    public void setConfiguration(Configuration config) {
        this.configuration = config;
        this.isEditMode = true;
        if (config != null) {
            txtfieldConfigurationName.setText(config.getName());
            txtfieldLanguage.setText(config.getLanguage());
            choiceboxToolType.setValue(config.getTool().getType().toString());
            txtfieldToolLocation.setText(config.getTool().getLocation());
            txtfieldRunCall.setText(config.getRunCall());
            txtfieldCompilerArguments.setText(config.getCompilerArguments());
        }
    }

    @FXML
    public void initialize() {
        // Initialize with all available tool types
        choiceboxToolType.getItems().addAll(
            ToolType.COMPILER.toString(),
            ToolType.INTERPRETER.toString(),
            ToolType.CUSTOM.toString()
        );

        // Set default selection and field states
        choiceboxToolType.setValue("");
        updateFieldStates();

        // Add listener for tool type changes
        choiceboxToolType.setOnAction(event -> {
            updateFieldStates();
            String selectedType = choiceboxToolType.getValue();
            if (selectedType != null) {
                switch (ToolType.valueOf(selectedType)) {
                    case COMPILER -> {
                        txtfieldCompilerArguments.setDisable(false);
                        txtfieldCompilerArguments.setPromptText("-d out -encoding UTF-8");
                    }
                    case INTERPRETER -> {
                        txtfieldCompilerArguments.setDisable(true);
                        txtfieldCompilerArguments.clear();
                        txtfieldRunCall.setPromptText("python script.py");
                    }
                    case CUSTOM -> {
                        txtfieldCompilerArguments.setDisable(false);
                        txtfieldCompilerArguments.setPromptText("Custom arguments");
                    }
                }
            }
        });
    }

    private void updateFieldStates() {
        boolean hasToolType = !choiceboxToolType.getValue().isEmpty();
        txtfieldToolLocation.setDisable(!hasToolType);
        txtfieldRunCall.setDisable(!hasToolType);
        btnToolLocation.setDisable(!hasToolType);
    }

    @FXML
    private void getToolLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Tool Executable");

        // Set initial directory based on tool type
        String toolType = choiceboxToolType.getValue();
        if (toolType != null) {
            String defaultPath = switch (ToolType.valueOf(toolType)) {
                case COMPILER -> "C:\\Program Files\\Java\\jdk-17\\bin";
                case INTERPRETER -> "C:\\Python312";
                default -> System.getProperty("user.home");
            };
            File initialDir = new File(defaultPath);
            if (initialDir.exists()) {
                fileChooser.setInitialDirectory(initialDir);
            }
        }

        // Set extension filters based on OS
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Executables", "*.exe"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        } else {
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*")
            );
        }

        File file = fileChooser.showOpenDialog(thisStage);
        if (file != null) {
            txtfieldToolLocation.setText(file.getAbsolutePath());

            // Auto-fill run call with executable name
            if (txtfieldRunCall.getText().isEmpty()) {
                String executableName = file.getName();
                txtfieldRunCall.setText(executableName);
            }
        }
    }

    public void setStage(Stage stage) {
        this.thisStage = stage;
    }

    @FXML
    private void saveConfiguration() {
        if (!validateInputs()) {
            return;
        }

        try {
            ensureConfigDirectoryExists();
            Path configPath = getConfigPath();
            
            if (isEditMode && configuration != null) {
                Files.deleteIfExists(configPath);
            }

            Configuration newConfig = createConfiguration();

            // Validate the configuration by testing the tool
            ToolValidator validator = new ToolValidator();
            ValidationResult validationResult = validator.validate(newConfig.getTool());

            if (!validationResult.isAvailable()) {
                showErrorDialog("Tool Validation Failed",
                    "The selected tool is not available or not working properly: " +
                    validationResult.getErrorMessage());
                return;
            }

            ConfigurationIO.save(newConfig, configPath);
            showSuccessDialog();
            thisStage.close();

        } catch (IOException e) {
            showErrorDialog("Error", "Failed to save configuration: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (txtfieldConfigurationName.getText().isBlank()) {
            showAlert("Name is Empty", "Please enter the configuration name.");
            return false;
        }
        if (txtfieldLanguage.getText().isBlank()) {
            showAlert("Language is Empty", "Please enter the language.");
            return false;
        }
        if (choiceboxToolType.getValue() == null || choiceboxToolType.getValue().isBlank()) {
            showAlert("Tool Type is Empty", "Please select a tool type.");
            return false;
        }
        if (txtfieldToolLocation.getText().isBlank()) {
            showAlert("Tool Location is Empty", "Please select the tool location.");
            return false;
        }
        if (txtfieldRunCall.getText().isBlank()) {
            showAlert("Run Call is Empty", "Please enter the run command.");
            return false;
        }
        if (!txtfieldCompilerArguments.isDisable() && txtfieldCompilerArguments.getText().isBlank()) {
            showAlert("Arguments Required", "Please enter the required arguments for this tool type.");
            return false;
        }

        // Validate file existence
        File toolFile = new File(txtfieldToolLocation.getText());
        if (!toolFile.exists()) {
            showAlert("Invalid Tool Location", "The specified tool executable does not exist.");
            return false;
        }
        if (!toolFile.canExecute()) {
            showAlert("Invalid Tool Permissions", "The specified tool file is not executable.");
            return false;
        }

        return true;
    }

    private void ensureConfigDirectoryExists() throws IOException {
        File configDir = new File("src/CONFIGURATIONS");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    private Path getConfigPath() {
        return Paths.get("src/CONFIGURATIONS", txtfieldConfigurationName.getText() + ".json");
    }

    private Configuration createConfiguration() {
        return new Configuration(
            txtfieldConfigurationName.getText(),
            txtfieldLanguage.getText(),
            choiceboxToolType.getValue(),
            txtfieldRunCall.getText(),
            txtfieldCompilerArguments.getText(),
            txtfieldToolLocation.getText()
        );
    }

    private void showSuccessDialog() {
        showAlert("Success", "Configuration saved successfully!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

