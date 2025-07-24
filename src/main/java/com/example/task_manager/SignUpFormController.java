package com.example.task_manager;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class SignUpFormController {
    public AnchorPane signUpPane;
    public TextField txtUsername;
    public TextField txtConfirmPassword;
    public TextField txtPassword;
    public TextField txtEmail;
    public Label txtError1;
    public Label txtError2;
    public Button signUpBtn;
    public Label txtErrorUsername;
    public Label txtErrorEmail;
    public String id = null;

    public void initialize(){
        hideCommon(false);
    }

    public void hideCommon(boolean isVisible){
        txtErrorUsername.setVisible(isVisible);
        txtErrorEmail.setVisible((isVisible));
        txtError1.setVisible(isVisible);
        txtError2.setVisible(isVisible);
    }

    public void signUpBtnOnAction(ActionEvent actionEvent) {
        register();
    }

    public void passwordConfirmOnAction(ActionEvent actionEvent) {
        register();
    }

    public void register(){
        String username = txtUsername.getText().strip();
        String email = txtEmail.getText().strip();
        String newPassword = txtPassword.getText().strip();
        String confirmPassword = txtConfirmPassword.getText().strip();

        id = autoGenerateID();

        hideCommon(false);

        if(newPassword.equals(confirmPassword)  && !username.isEmpty() && !email.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()){

            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,username);
                preparedStatement.setObject(3,email);
                preparedStatement.setObject(4,confirmPassword);

                int i = preparedStatement.executeUpdate();

                if(i !=0){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"success...");
                    alert.showAndWait();

                    Parent parent = FXMLLoader.load(this.getClass().getResource("WelcomePageForm.fxml"));
                    Scene scene = new Scene(parent);
                    Stage primaryStage = (Stage) signUpPane.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Welcome to Task Manager");
                    primaryStage.centerOnScreen();

                }else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"something went wrong...");
                    alert.showAndWait();
                }

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }

        }else{
            if(username.isEmpty()){
                txtErrorUsername.setVisible(true);
            }
            if(email.isEmpty()){
                txtErrorEmail.setVisible((true));
            }
            if(newPassword.isEmpty()){
                txtError1.setVisible(true);
            }
            if(confirmPassword.isEmpty()){
                txtError2.setVisible(true);
            }
            if(!newPassword.equals(confirmPassword)){
                txtError1.setVisible(true);
                txtError2.setVisible(true);
            }
        }
    }

    public  String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1,oldId.length());
                int intID = Integer.parseInt(oldId);
                intID = intID +1;

                if(intID<10){
                    id = "U00" + intID;
                    return id;
                }else if(intID<100){
                    id = "U0" + intID;
                    return id;
                }else{
                    id = "U" + intID;
                    return id;
                }

            }else{
                id = "U001";
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
