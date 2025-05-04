package com.example.ce316project;

import javafx.fxml.FXML;
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

public class ConfigurationScreenEdit {

    Stage thisStage;

    Configuration configuration;

    @FXML
    public TextField txtfieldConfigurationName, txtfieldLanguage, txtfieldToolLocation, txtfieldCompilerArguments, txtfieldRunCall;
    public ChoiceBox<String> choiceboxToolType;
    public Button btnSaveConfiguration, btnToolLocation;


    @FXML
    public void initialize() {

            txtfieldConfigurationName.setText(configuration.getName());
            txtfieldLanguage.setText(configuration.getLanguage());
            choiceboxToolType.setValue(configuration.getTools().getType().toString());
            txtfieldToolLocation.setText(configuration.getTools().getLocation());
            txtfieldRunCall.setText(configuration.getRunCmd());
            txtfieldCompilerArguments.setText(configuration.getCompileCmd());

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
            String currentTool = String.valueOf(choiceboxToolType.getValue());
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
        } else if (choiceboxToolType.getValue() == null) {
            showAlert("TOOL TYPE IS EMPTY", "Please Enter the Tool Type.");
        } else if (txtfieldToolLocation.getText().isBlank()) {
            showAlert("TOOL LOCATION IS EMPTY", "Please Enter the Tool Location.");
        } else if (txtfieldRunCall.getText().isBlank()) {
            showAlert("RUN CALL IS EMPTY", "Please Enter the Run Call.");
        } else if (txtfieldCompilerArguments.getText().isBlank() && choiceboxToolType.getValue().equals(ToolType.COMPILER)) {
            showAlert("COMPILER ARGUMENTS IS EMPTY", "Please Enter the Compiler Arguments.");
        } else {
            try {
                new File("src/CONFIGURATIONS").mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String s = "src/CONFIGURATIONS/";
                s += txtfieldConfigurationName.getText() + ".json";
                Path path = Paths.get(s);
                File configurationFile = Files.createFile(path).toFile();

                ToolSpec tool = new ToolSpec(
                        choiceboxToolType.getValue(),
                        txtfieldRunCall.getText(),
                        txtfieldCompilerArguments.getText(),
                        txtfieldToolLocation.getText()
                );
                Configuration config = new Configuration(
                        txtfieldConfigurationName.getText(),
                        txtfieldLanguage.getText(),
                        tool
                );
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


}