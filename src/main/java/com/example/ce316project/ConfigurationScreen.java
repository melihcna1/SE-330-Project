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
            choiceboxToolType.setValue(config.getTools().getType().toString());
            txtfieldToolLocation.setText(config.getTools().getLocation());
            txtfieldRunCall.setText(config.getRunCall());
            txtfieldCompilerArguments.setText(config.getCompilerArguments());
        }
    }

    @FXML
    public void initialize() {
        choiceboxToolType.getItems().addAll(ToolType.COMPILER.toString(), ToolType.INTERPRETER.toString());
        choiceboxToolType.setValue("");
        txtfieldToolLocation.setDisable(true);
        txtfieldCompilerArguments.setDisable(true);
        txtfieldRunCall.setDisable(true);
        btnToolLocation.setDisable(false);

        choiceboxSelect();
    }

    @FXML
    private void getToolLocation() {
        txtfieldToolLocation.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Tool");
        File file = fileChooser.showOpenDialog(thisStage);
        if (file != null) {
            txtfieldToolLocation.setText(file.getPath());
        }
    }

    @FXML
    private void choiceboxSelect() {
        choiceboxToolType.setOnAction(event -> {
            txtfieldRunCall.setDisable(false);
            txtfieldToolLocation.setDisable(false);
            btnToolLocation.setDisable(false);
            String currentTool = String.valueOf(choiceboxToolType.getValue());
            if (currentTool.equals("INTERPRETER")) {
                txtfieldCompilerArguments.clear();
                txtfieldCompilerArguments.setDisable(true);
            } else if (currentTool.equals("COMPILER")) {
                txtfieldCompilerArguments.setDisable(false);
            }
        });
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
            ConfigurationIO.save(newConfig, configPath);
            
            showSuccessDialog();
            thisStage.close();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to save configuration: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (txtfieldConfigurationName.getText().isBlank()) {
            showAlert("NAME IS EMPTY", "Please Enter the Configuration Name.");
            return false;
        }
        if (txtfieldLanguage.getText().isBlank()) {
            showAlert("LANGUAGE IS EMPTY", "Please Enter the Configuration Language.");
            return false;
        }
        if (choiceboxToolType.getValue() == null) {
            showAlert("TOOL TYPE IS EMPTY", "Please Enter the Tool Type.");
            return false;
        }
        if (txtfieldToolLocation.getText().isBlank()) {
            showAlert("TOOL LOCATION IS EMPTY", "Please Enter the Tool Location.");
            return false;
        }
        if (txtfieldRunCall.getText().isBlank()) {
            showAlert("RUN CALL IS EMPTY", "Please Enter the Run Call.");
            return false;
        }
        if (txtfieldCompilerArguments.getText().isBlank() && 
            choiceboxToolType.getValue().equals(ToolType.COMPILER.toString())) {
            showAlert("COMPILER ARGUMENTS IS EMPTY", "Please Enter the Compiler Arguments.");
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