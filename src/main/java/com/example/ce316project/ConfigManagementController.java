package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.File;

public class ConfigManagementController {

    @FXML
    private TableView<ConfigurationEntry> configTable;

    @FXML
    private TableColumn<ConfigurationEntry, String> nameColumn;

    @FXML
    private TableColumn<ConfigurationEntry, String> pathColumn;

    private ObservableList<ConfigurationEntry> configList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        pathColumn.setCellValueFactory(cellData -> cellData.getValue().pathProperty());

        // Dummy Data
        configList.add(new ConfigurationEntry("Default Config", "/path/to/config1"));
        configList.add(new ConfigurationEntry("Test Config", "/path/to/config2"));

        configTable.setItems(configList);
    }

    @FXML
    private void handleNew() {
        // Show dialog to add a new config (can be a new window or file chooser)
        System.out.println("New clicked");
    }

    @FXML
    private void handleEdit() {
        ConfigurationEntry selected = configTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Edit clicked on: " + selected.getName());
        }
    }

    @FXML
    private void handleDelete() {
        ConfigurationEntry selected = configTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            configList.remove(selected);
        }
    }
}
