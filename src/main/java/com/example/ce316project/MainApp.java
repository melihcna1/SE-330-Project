package com.example.ce316project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;


    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("IAE - Integrated Assignment Environment");

        // Load the main layout from FXML
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/example/ce316project/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Give the controller access to the main app if needed
        MainController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        stage.setScene(scene);
        stage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}