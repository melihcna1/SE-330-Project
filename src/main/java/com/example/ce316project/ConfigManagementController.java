package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

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
        colCompileCommand.setCellValueFactory(new PropertyValueFactory<>("compileCommand"));
        colRunCommand.setCellValueFactory(new PropertyValueFactory<>("runCommand"));

        tableConfigurations.setItems(configurationList);
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