package com.example.ce316project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchRunProgressController {
    @FXML private TableView<StudentResult> tableProgress;
    @FXML private TableColumn<StudentResult, String> colStudentId;
    @FXML private TableColumn<StudentResult, String> colStatus;
    @FXML private Button btnStop;
    @FXML private Button btnBack;
    @FXML private Label lblProgress;
    @FXML private ProgressBar progressBar;
    @FXML private Label lblTotal;
    @FXML private Label lblPassed;
    @FXML private Label lblFailed;
    @FXML private Label lblProcessing;

    private ObservableList<StudentResult> progressList = FXCollections.observableArrayList();
    private MainController mainController;
    private boolean running = false;
    private Thread batchThread;
    private AtomicInteger processedCount = new AtomicInteger(0);
    private AtomicInteger totalCount = new AtomicInteger(0);
    private AtomicInteger passedCount = new AtomicInteger(0);
    private AtomicInteger failedCount = new AtomicInteger(0);
    private AtomicInteger processingCount = new AtomicInteger(0);

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add CSS styles based on status
        colStatus.setCellFactory(column -> new TableCell<StudentResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Passed" -> setStyle("-fx-text-fill: green;");
                        case "Failed" -> setStyle("-fx-text-fill: red;");
                        case "Processing" -> setStyle("-fx-text-fill: blue;");
                        default -> setStyle("");
                    }
                }
            }
        });

        tableProgress.setItems(progressList);
    }

    public void startBatchRun(List<StudentResult> results) {
        progressList.setAll(results);
        totalCount.set(results.size());
        processedCount.set(0);
        passedCount.set(0);
        failedCount.set(0);
        processingCount.set(0);

        updateProgressDisplay();
        running = true;
        btnStop.setDisable(false);
        btnBack.setDisable(true);

        batchThread = new Thread(() -> {
            try {
                for (StudentResult result : progressList) {
                    if (!running) break;
                    Platform.runLater(() -> {
                        if (!result.getStatus().equals("Failed") && !result.getStatus().equals("Passed")) {
                            result.setStatus("Processing");
                            processingCount.incrementAndGet();
                            updateProgressDisplay();
                            tableProgress.refresh();
                        }
                    });
                    Thread.sleep(100);
                }

                while (running && !allProcessingComplete()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                Platform.runLater(() -> {
                    running = false;
                    btnStop.setDisable(true);
                    btnBack.setDisable(false);
                    updateProgressDisplay();
                });
            }
        });
        batchThread.setDaemon(true);
        batchThread.start();
    }

    public void updateStudentResult(StudentResult updatedResult) {
        Platform.runLater(() -> {
            for (int i = 0; i < progressList.size(); i++) {
                StudentResult existingResult = progressList.get(i);
                if (existingResult.getStudentId().equals(updatedResult.getStudentId())) {
                    String oldStatus = existingResult.getStatus();
                    String newStatus = updatedResult.getStatus();

                    // Update counters
                    if (oldStatus.equals("Processing")) {
                        processingCount.decrementAndGet();
                    }
                    if (newStatus.equals("Passed")) {
                        passedCount.incrementAndGet();
                    } else if (newStatus.equals("Failed")) {
                        failedCount.incrementAndGet();
                    }

                    progressList.set(i, updatedResult);
                    processedCount.incrementAndGet();
                    updateProgressDisplay();
                    tableProgress.refresh();
                    break;
                }
            }
        });
    }

    private boolean allProcessingComplete() {
        for (StudentResult result : progressList) {
            String status = result.getStatus();
            if (status.equals("Processing") || status.equals("Ready") || status.equals("Extracting")) {
                return false;
            }
        }
        return true;
    }

    private void updateProgressDisplay() {
        int total = totalCount.get();
        int processed = processedCount.get();
        int passed = passedCount.get();
        int failed = failedCount.get();
        int processing = processingCount.get();

        progressBar.setProgress((double) processed / total);
        lblProgress.setText(String.format("Progress: %d of %d submissions processed", processed, total));
        lblTotal.setText(String.valueOf(total));
        lblPassed.setText(String.valueOf(passed));
        lblFailed.setText(String.valueOf(failed));
        lblProcessing.setText(String.valueOf(processing));

        if (processed == total) {
            lblProgress.setText(String.format("Completed: %d passed, %d failed", passed, failed));
            btnBack.setDisable(false);
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
        lblProgress.setText("Processing stopped by user");
    }

    @FXML
    private void backToResults() {
        mainController.showDetailedResults();
    }
}

