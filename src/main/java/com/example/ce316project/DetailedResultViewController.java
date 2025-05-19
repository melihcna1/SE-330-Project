package com.example.ce316project;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DetailedResultViewController {
    @FXML
    private TableView<StudentResult> tableResults;
    @FXML
    private TableColumn<StudentResult, String> colStudentId;
    @FXML
    private TableColumn<StudentResult, String> colStatus;
    @FXML
    private TableColumn<StudentResult, Void> colActions;
    @FXML
    private TextArea txtDetails;
    @FXML
    private Button btnRefresh, btnBack;

    private ObservableList<StudentResult> resultList = FXCollections.observableArrayList();
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setResults(ObservableList<StudentResult> results) {
        if (results != null) {
            this.resultList.setAll(results);
        } else {
            this.resultList.clear();
        }
    }

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colActions.setCellFactory(param -> new TableCell<StudentResult, Void>() {
            private final Button saveButton = new Button("Save Log");
            private final Button printButton = new Button("Print Log");

            {
                saveButton.setOnAction(event -> {
                    StudentResult result = getTableView().getItems().get(getIndex());
                    saveLog(result);
                });
                printButton.setOnAction(event -> {
                    StudentResult result = getTableView().getItems().get(getIndex());
                    printLog(result);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, saveButton, printButton);
                    setGraphic(hbox);
                }
            }
        });

        tableResults.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtDetails.setText("Student ID: " + newSelection.getStudentId() + "\n" +
                        "Status: " + newSelection.getStatus() + "\n" +
                        "Errors:\n" + newSelection.getErrors() + "\n" +
                        "Log:\n" + newSelection.getLog() + "\n" +
                        "Diff Output:\n" + newSelection.getDiffOutput());
            } else {
                txtDetails.setText("");
            }
        });

        tableResults.setItems(resultList);
    }

    private void saveLog(StudentResult result) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Log for " + result.getStudentId());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Student ID: " + result.getStudentId() + "\n");
                writer.write("Status: " + result.getStatus() + "\n");
                writer.write("Errors:\n" + result.getErrors() + "\n");
                writer.write("Log:\n" + result.getLog() + "\n");
                writer.write("Diff Output:\n" + result.getDiffOutput() + "\n");
                mainController.showPlaceholderDialog("Success", "Log saved successfully!");
            } catch (IOException e) {
                mainController.showErrorDialog("Error", "Failed to save log: " + e.getMessage() + "\nPath: " + file.getAbsolutePath());
            }
        }
    }

    private void printLog(StudentResult result) {
        TextArea textArea = new TextArea();
        textArea.setText("Student ID: " + result.getStudentId() + "\n" +
                "Status: " + result.getStatus() + "\n" +
                "Errors:\n" + result.getErrors() + "\n" +
                "Log:\n" + result.getLog() + "\n" +
                "Diff Output:\n" + result.getDiffOutput());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Log for " + result.getStudentId());
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    private void refreshResults() {
        if (mainController != null && mainController.getCurrentProject() != null && mainController.getCurrentProject().getResults() != null) {
            setResults(FXCollections.observableArrayList(mainController.getCurrentProject().getResults()));
        } else {
            resultList.clear();
            mainController.showErrorDialog("Error", "No project or results available to refresh!");
        }
    }

    @FXML
    private void backToMain() {
        mainController.showMainView();
    }
}