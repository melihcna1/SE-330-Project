package com.example.ce316project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

public class ConfigurationEditController {
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtLanguage;
    @FXML
    private ChoiceBox<String> choiceToolType;
    @FXML
    private TextField txtExecutable;
    @FXML
    private TextField txtCompilerArgs;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

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

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        if (configuration != null) {
            txtName.setText(configuration.getName());
            txtLanguage.setText(configuration.getLanguage());
            choiceToolType.setValue(configuration.getToolType().toString());
            txtExecutable.setText(configuration.getRunCall());
            txtCompilerArgs.setText(configuration.getCompilerArguments());
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    public void initialize() {
        choiceToolType.setItems(FXCollections.observableArrayList(
            "COMPILER",
            "INTERPRETER",
            "UNZIP",
            "DIFF",
            "CUSTOM"
        ));
    }

    @FXML
    private void saveConfiguration() {
        String name = txtName.getText();
        String language = txtLanguage.getText();
        String executable = txtExecutable.getText();
        String compilerArgs = txtCompilerArgs.getText();
        String toolTypeStr = choiceToolType.getValue();

        if (name.isEmpty() || language.isEmpty() || executable.isEmpty() || toolTypeStr == null) {
            mainController.showErrorDialog("Error", "Name, Language, Executable, and Tool Type are required!");
            return;
        }

        try {
            ToolType toolType = ToolType.valueOf(toolTypeStr);
            ToolSpec tool = new ToolSpec(toolType, executable, compilerArgs, executable);
            configuration = new Configuration(name, language, tool, toolType);
            saved = true;
            dialogStage.close();
        } catch (IllegalArgumentException e) {
            mainController.showErrorDialog("Error", "Invalid tool type: " + toolTypeStr);
        }
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }
}