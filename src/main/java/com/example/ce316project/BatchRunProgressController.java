package com.example.ce316project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML
    private Label lblProgress;

    private ObservableList<StudentResult> progressList = FXCollections.observableArrayList();
    private MainController mainController;
    private boolean running = false;
    private Thread batchThread;
    private int processedCount = 0;
    private int totalCount = 0;

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
        totalCount = results.size();
        processedCount = 0;
        updateProgressLabel();
        
        running = true;
        btnStop.setDisable(false);
        btnBack.setDisable(true);

        batchThread = new Thread(() -> {
            for (StudentResult result : progressList) {
                if (!running) break;
                try {
                    // Simulating work - actual work is done in MainController's processBatchZipSubmissions
                    Thread.sleep(500);
                    Platform.runLater(() -> {
                        // Update status based on actual result processing
                        if (result.getStatus().equals("Processing")) {
                            result.setStatus("Extracting");
                        }
                        tableProgress.refresh();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // Wait for actual processing to complete
            while (running && !allProcessingComplete()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            Platform.runLater(() -> {
                running = false;
                btnStop.setDisable(true);
                btnBack.setDisable(false);
                updateProgressLabel();
            });
        });
        batchThread.setDaemon(true);
        batchThread.start();
    }
    
    /**
     * Updates a specific student result in the table
     */
    public void updateStudentResult(StudentResult updatedResult) {
        Platform.runLater(() -> {
            for (int i = 0; i < progressList.size(); i++) {
                StudentResult existingResult = progressList.get(i);
                if (existingResult.getStudentId().equals(updatedResult.getStudentId())) {
                    progressList.set(i, updatedResult);
                    processedCount++;
                    updateProgressLabel();
                    break;
                }
            }
            tableProgress.refresh();
        });
    }
    
    private boolean allProcessingComplete() {
        for (StudentResult result : progressList) {
            if (result.getStatus().equals("Processing") || 
                result.getStatus().equals("Extracting")) {
                return false;
            }
        }
        return true;
    }
    
    private void updateProgressLabel() {
        if (lblProgress != null) {
            lblProgress.setText(String.format("Progress: %d of %d submissions processed", 
                processedCount, totalCount));
        }
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