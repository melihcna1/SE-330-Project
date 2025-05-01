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

public class ConfigurationScreen extends Application {

    Stage thisStage;


    @FXML
    public TextField txtfieldConfigurationName, txtfieldLanguage, txtfieldToolLocation, txtfieldCompilerArguments, txtfieldRunCall;
    public ChoiceBox<String> choiceboxToolType;
    public Button btnSaveConfiguration, btnToolLocation;

    @Override
    public void start(Stage stage) throws Exception {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigurationScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setScene(scene);
            thisStage = stage;
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        choiceboxToolType.setValue("");
        choiceboxToolType.getItems().addAll(ToolType.COMPILER.toString(), ToolType.INTERPRETER.toString());
        txtfieldToolLocation.setDisable(true);
        txtfieldCompilerArguments.setDisable(true);
        txtfieldRunCall.setDisable(true);
        btnToolLocation.setDisable(true);

        choiceboxSelect();

    }

    @FXML
    private void getToolLocation() {

        txtfieldToolLocation.clear();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Tool");

        File file = fileChooser.showOpenDialog(thisStage);
        if (file != null) {
            System.out.println("Opening project: " + file.getPath());
            txtfieldToolLocation.setText(file.getPath());
        }

    }

    @FXML
    private void choiceboxSelect() {

        choiceboxToolType.setOnAction(event -> {
            txtfieldRunCall.setDisable(false);
            txtfieldToolLocation.setDisable(false);
            btnToolLocation.setDisable(false);
            String currentTool = choiceboxToolType.getValue();
            if (currentTool.equals("INTERPRETER")) {

                txtfieldCompilerArguments.clear();
                txtfieldCompilerArguments.setDisable(true);
            } else if (currentTool.equals("COMPILER")) {

                txtfieldCompilerArguments.setDisable(false);
            }
        });

    }


    @FXML
    private void saveConfiguration() {
        if (txtfieldConfigurationName.getText().isBlank()) {
            showAlert("NAME IS EMPTY", "Please Enter the Configuration Name.");
        } else if (txtfieldLanguage.getText().isBlank()) {
            showAlert("LANGUAGE IS EMPTY", "Please Enter the Configuration Language.");
        } else if (choiceboxToolType.getValue().isBlank()) {
            showAlert("TOOL TYPE IS EMPTY", "Please Enter the Tool Type.");

        } else if (txtfieldToolLocation.getText().isBlank()) {
            showAlert("TOOL LOCATION IS EMPTY", "Please Enter the Tool Location.");

        } else if (txtfieldRunCall.getText().isBlank()) {
            showAlert("RUN CALL IS EMPTY", "Please Enter the Run Call.");

        } else if (txtfieldCompilerArguments.getText().isBlank() && choiceboxToolType.getValue().equals("COMPILER")) {
            showAlert("COMPILER ARGUMENTS IS EMPTY", "Please Enter the Compiler Arguments.");
        } else {

            try {
                String s = "src/CONFIGURATIONS/";
                s += txtfieldConfigurationName.getText() + ".json";
                Path path = Paths.get(s);
                File configurationFile = Files.createFile(path).toFile();

                ToolSpec tool = new ToolSpec(ToolType.COMPILER, txtfieldRunCall.getText(), txtfieldCompilerArguments.getText());
                Configuration config = new Configuration(txtfieldConfigurationName.getText(), txtfieldLanguage.getText(), tool);
                ConfigurationIO.save(config, path);
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


    public static void main(String[] args) {
        launch(args);
    }
}
