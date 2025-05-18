package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ConfigManagementController {
    @FXML
    private TableView<Configuration> tableConfigurations;
    @FXML
    private TableColumn<Configuration, String> colName;
    @FXML
    private TableColumn<Configuration, String> colLanguage;
    @FXML
    private TableColumn<Configuration, String> colCompileCommand;
    @FXML
    private TableColumn<Configuration, String> colRunCommand;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnImport;
    @FXML
    private Button btnExport;

    private ObservableList<Configuration> configurationList = FXCollections.observableArrayList();
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setConfigurations(ObservableList<Configuration> configurations) {
        if (configurations != null) {
            this.configurationList.setAll(configurations);
        } else {
            this.configurationList.clear();
        }
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLanguage.setCellValueFactory(new PropertyValueFactory<>("language"));
        colCompileCommand.setCellValueFactory(new PropertyValueFactory<>("compileCmd"));
        colRunCommand.setCellValueFactory(new PropertyValueFactory<>("runCmd"));

        // Load configurations from both project and CONFIGURATIONS directory
        loadConfigurations();
        
        tableConfigurations.setItems(configurationList);
    }

    private void loadConfigurations() {
        configurationList.clear();
        
        // First load configurations from the current project
        if (mainController != null && mainController.getCurrentProject() != null) {
            List<Configuration> projectConfigs = mainController.getCurrentProject().getConfigurations();
            if (projectConfigs != null) {
                configurationList.addAll(projectConfigs);
            }
        }

        // Then load configurations from the CONFIGURATIONS directory
        File configDir = new File("src/CONFIGURATIONS");
        if (configDir.exists() && configDir.isDirectory()) {
            File[] configFiles = configDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (configFiles != null) {
                for (File configFile : configFiles) {
                    try {
                        Configuration config = ConfigurationIO.load(configFile.toPath());
                        // Only add if not already in the list
                        if (configurationList.stream().noneMatch(c -> c.getName().equals(config.getName()))) {
                            configurationList.add(config);
                        }
                    } catch (IOException | ConfigurationIO.InvalidFormatException e) {
                        System.err.println("Failed to load configuration from " + configFile.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    @FXML
    private void refreshConfigurations() {
        loadConfigurations();
        tableConfigurations.refresh();
    }

    @FXML
    private void createConfiguration() {
        showConfigurationDialog(null);
    }

    @FXML
    private void editConfiguration() {
        Configuration selectedConfig = tableConfigurations.getSelectionModel().getSelectedItem();
        if (selectedConfig == null) {
            mainController.showErrorDialog("Error", "Please select a configuration to edit!");
            return;
        }
        showConfigurationDialog(selectedConfig);
    }

    @FXML
    private void deleteConfiguration() {
        Configuration selectedConfig = tableConfigurations.getSelectionModel().getSelectedItem();
        if (selectedConfig == null) {
            mainController.showErrorDialog("Error", "Please select a configuration to delete!");
            return;
        }
        configurationList.remove(selectedConfig);
        mainController.getCurrentProject().getConfigurations().remove(selectedConfig);
    }

    @FXML
    private void backToMain() {
        mainController.showMainView();
    }

    /**
     * Export the selected configuration to a file that can be shared
     */
    @FXML
    private void exportConfiguration() {
        Configuration selectedConfig = tableConfigurations.getSelectionModel().getSelectedItem();
        if (selectedConfig == null) {
            mainController.showErrorDialog("Error", "Please select a configuration to export!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.config.json"));
        fileChooser.setInitialFileName(selectedConfig.getName() + ".config.json");
        
        File exportFile = fileChooser.showSaveDialog(mainController.getMainApp().getPrimaryStage());
        if (exportFile != null) {
            try {
                ConfigurationIO.save(selectedConfig, exportFile.toPath());
                mainController.showPlaceholderDialog("Success", "Configuration exported successfully to: " + exportFile.getPath());
            } catch (IOException e) {
                mainController.showErrorDialog("Export Error", "Failed to export configuration: " + e.getMessage());
            }
        }
    }

    /**
     * Import a configuration from a file
     */
    @FXML
    private void importConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.config.json", "*.json"));
        
        File importFile = fileChooser.showOpenDialog(mainController.getMainApp().getPrimaryStage());
        if (importFile != null) {
            try {
                Configuration importedConfig = ConfigurationIO.load(importFile.toPath());
                
                // Check if a configuration with the same name already exists
                boolean nameExists = configurationList.stream()
                    .anyMatch(c -> c.getName().equals(importedConfig.getName()));
                
                if (nameExists) {
                    // Add a suffix to ensure uniqueness
                    importedConfig.setName(importedConfig.getName() + "_imported");
                }
                
                // Add to the current project and update the view
                configurationList.add(importedConfig);
                mainController.getCurrentProject().getConfigurations().add(importedConfig);
                
                // Save the imported configuration to the configurations directory for future use
                try {
                    // Ensure CONFIGURATIONS directory exists
                    File configDir = new File("src/CONFIGURATIONS");
                    if (!configDir.exists()) {
                        configDir.mkdirs();
                    }
                    
                    Path targetPath = Path.of("src/CONFIGURATIONS", importedConfig.getName() + ".json");
                    ConfigurationIO.save(importedConfig, targetPath);
                } catch (IOException e) {
                    // Just log this - not critical since the configuration is already added to the project
                    System.err.println("Could not save imported configuration to CONFIGURATIONS directory: " + e.getMessage());
                }
                
                mainController.showPlaceholderDialog("Success", "Configuration imported successfully: " + importedConfig.getName());
            } catch (IOException | ConfigurationIO.InvalidFormatException e) {
                mainController.showErrorDialog("Import Error", "Failed to import configuration: " + e.getMessage());
            }
        }
    }

    private void showConfigurationDialog(Configuration config) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/configuration-edit-view.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(config == null ? "Create Configuration" : "Edit Configuration");
            dialogStage.initOwner(mainController.getMainApp().getPrimaryStage());
            dialogStage.setScene(new Scene(loader.load()));

            ConfigurationEditController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainController(mainController);
            controller.setConfiguration(config);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                Configuration updatedConfig = controller.getConfiguration();
                if (config == null) {
                    configurationList.add(updatedConfig);
                    mainController.getCurrentProject().getConfigurations().add(updatedConfig);
                } else {
                    int index = configurationList.indexOf(config);
                    configurationList.set(index, updatedConfig);
                    mainController.getCurrentProject().getConfigurations().set(index, updatedConfig);
                }
            }
        } catch (IOException e) {
            mainController.showErrorDialog("Error", "Failed to open configuration dialog: " + e.getMessage());
        }
    }
}