package com.example.ce316project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class BatchRunProgressController {
    @FXML
    private TableView<StudentResult> tableProgress;
    @FXML
    private TableColumn<StudentResult, String> colStudentId;
    @FXML
    private TableColumn<StudentResult, String> colStatus;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnBack;

    private ObservableList<StudentResult> progressList = FXCollections.observableArrayList();
    private MainController mainController;
    private boolean running = false;
    private Thread batchThread;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableProgress.setItems(progressList);
    }

    public void startBatchRun(List<StudentResult> results) {
        progressList.setAll(results);
        running = true;
        btnStop.setDisable(false);
        btnBack.setDisable(true);

        batchThread = new Thread(() -> {
            for (StudentResult result : progressList) {
                if (!running) break;
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        result.setStatus(result.getStatus().equals("Passed") ? "Completed" : "Failed");
                        tableProgress.refresh();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            Platform.runLater(() -> {
                running = false;
                btnStop.setDisable(true);
                btnBack.setDisable(false);
            });
        });
        batchThread.setDaemon(true);
        batchThread.start();
    }

    @FXML
    private void stopBatchRun() {
        running = false;
        if (batchThread != null) {
            batchThread.interrupt();
        }
        btnStop.setDisable(true);
        btnBack.setDisable(false);
    }

    @FXML
    private void backToResults() {
        mainController.showDetailedResults();
    }
}