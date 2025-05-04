package com.example.ce316project;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Collections;

public class ConfigurationEditController {
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtLanguage;
    @FXML
    private ChoiceBox<ToolType> choiceToolType;
    @FXML
    private TextField txtExecutable;
    @FXML
    private TextField txtCompilerArgs;

    private Stage dialogStage;
    private MainController mainController;
    private Configuration configuration;
    private boolean saved = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setConfiguration(Configuration config) {
        this.configuration = config;
        if (config != null) {
            txtName.setText(config.getName());
            txtLanguage.setText(config.getLanguage());
            ToolSpec tool = config.getTools();
            if (tool != null) {
                txtExecutable.setText(tool.getExecutable());
                txtCompilerArgs.setText(tool.getCompilerArgs() != null ? tool.getCompilerArgs() : "");
                choiceToolType.setValue(tool.getType());
            } else {
                choiceToolType.setValue(ToolType.COMPILER);
            }
        } else {
            choiceToolType.setValue(ToolType.COMPILER);
        }
    }

    public Configuration getConfiguration() {
        ToolSpec tool = new ToolSpec(
                choiceToolType.getValue(),
                txtExecutable.getText(),
                txtCompilerArgs.getText()
        );
        return new Configuration(txtName.getText(), txtLanguage.getText(), tool, choiceToolType.getValue());
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    public void initialize() {
        choiceToolType.getItems().setAll(ToolType.values());
    }

    @FXML
    private void saveConfiguration() {
        String name = txtName.getText();
        String language = txtLanguage.getText();
        String executable = txtExecutable.getText();
        String compilerArgs = txtCompilerArgs.getText();
        ToolType toolType = choiceToolType.getValue();

        if (name.isEmpty() || language.isEmpty() || executable.isEmpty() || toolType == null) {
            mainController.showErrorDialog("Error", "Name, Language, Executable, and Tool Type are required!");
            return;
        }

        configuration = getConfiguration();
        saved = true;
        dialogStage.close();
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }
}