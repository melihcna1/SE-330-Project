package com.example.ce316project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ProjectScreen {

    Stage thisStage;


    @FXML
    public TextField txtfieldProjectName, txtfieldConfiguration, txtfieldInputFile, txtfieldexpectedOutputFile, txtfieldSubmissionFolder, txtfieldResultFolder;
    public Button btnConfiguration, btnInputFile, btnExpectedOutputFile, btnSubmissionFolder, btnResultFolder, btnSaveProject;



    @FXML
    public void initialize() {

    }

    @FXML
    private void getConfiguration() {
        txtfieldConfiguration.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Configuration");
        File initialDirectory = new File("src/CONFIGURATIONS");
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showOpenDialog(thisStage);
        if (file != null) {
            txtfieldConfiguration.setText(file.getPath());
        }

    }

    @FXML
    private void getInputFile() {
        txtfieldInputFile.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input File");

        File file = fileChooser.showOpenDialog(thisStage);
        if (file != null) {
            txtfieldInputFile.setText(file.getPath());
        }

    }

    @FXML
    private void getExpectedOutputFile() {
        txtfieldexpectedOutputFile.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Output File");

        File file = fileChooser.showOpenDialog(thisStage);
        if (file != null) {
            txtfieldexpectedOutputFile.setText(file.getPath());
        }

    }

    @FXML
    private void getSubmissionFolder() {
        txtfieldSubmissionFolder.clear();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(thisStage);
        if (folder != null) {
            txtfieldSubmissionFolder.setText(folder.getPath());
        }

    }

    @FXML
    private void getResultFolder() {
        txtfieldResultFolder.clear();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(thisStage);
        if (folder != null) {
            txtfieldResultFolder.setText(folder.getPath());
        }

    }


    @FXML
    private void saveProject() {
        if (txtfieldProjectName.getText().isBlank()) {
            showAlert("NAME IS EMPTY", "Please Enter the Project Name.");
        } else if (txtfieldConfiguration.getText().isBlank()) {
            showAlert("CONFIGURATION IS EMPTY", "Please Enter the Configuration.");
        } else if (txtfieldInputFile.getText().isBlank()) {
            showAlert("INPUT FILE IS EMPTY", "Please Enter the Input File.");

        } else if (txtfieldexpectedOutputFile.getText().isBlank()) {
            showAlert("EXPECTED OUTPUT FILE IS EMPTY", "Please Enter the Expected Output File.");

        } else if (txtfieldSubmissionFolder.getText().isBlank()) {
            showAlert("SUBMISSION FOLDER IS EMPTY", "Please Enter the Submission Folder.");

        } else if (txtfieldResultFolder.getText().isBlank()) {
            showAlert("RESULT FOLDER IS EMPTY", "Please Enter the Result Folder.");

        } else {
            try {
                new File("src/PROJECTS").mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                String s = "src/PROJECTS/";
                s += txtfieldProjectName.getText() + ".json";
                Path path = Paths.get(s);
                File projectFile = Files.createFile(path).toFile();
                Project.TestCase testCase = new Project.TestCase(txtfieldInputFile.getText(), txtfieldexpectedOutputFile.getText());
                Project project = new Project(txtfieldProjectName.getText(), txtfieldConfiguration.getText(), txtfieldSubmissionFolder.getText(), txtfieldResultFolder.getText(), testCase);
                ProjectIO.save(project, path);
            } catch (FileAlreadyExistsException e) {
                showAlert("FILE ALREADY EXISTS", "Please Enter a different name or delete the other file.");

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}








