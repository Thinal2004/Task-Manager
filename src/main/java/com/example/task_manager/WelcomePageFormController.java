package com.example.task_manager;

import db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomePageFormController {
    
    public TextField txtUsername;
    public Label helloLbl;
    public TextField txtPassword;
    public Button loginBtn;
    public Label signUpLbl;
    public Pane loginPane;
    public AnchorPane root;
    public static String loginUserID;
    public static String loginUsername;

    public void initialize(){
        Platform.runLater(() -> {
            root.requestFocus();
        });
    }
    public void signUpOnAction(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("SignUpForm.fxml"));
        loginPane.getChildren().removeAll();
        loginPane.getChildren().setAll(parent);
    }

    public void loginBtnOnAction(ActionEvent actionEvent) throws IOException {

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where user_name = ? and password = ?");
            preparedStatement.setObject(1,username);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                loginUserID = resultSet.getString(1);
                loginUsername = resultSet.getString(2);

                Parent parent = FXMLLoader.load(this.getClass().getResource("DashboardForm.fxml"));
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
                Stage primaryStage = (Stage)root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Dashboard");
                primaryStage.centerOnScreen();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR,"Username or password is incorrect.");
                alert.showAndWait();
                txtPassword.clear();
                txtUsername.clear();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }
}
