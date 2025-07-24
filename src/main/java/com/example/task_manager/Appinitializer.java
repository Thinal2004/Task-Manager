package com.example.task_manager;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Appinitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("WelcomePageForm.fxml"));
        Scene scene = new Scene(parent);

        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome to Task Manager");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
