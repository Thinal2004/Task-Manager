package com.example.task_manager;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.CompletedTM;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;

public class DashboardFormController {
    public Label lblDay;
    public Pane datePane;
    public AnchorPane subRoot;
    public TextField txtAddNewTask;
    public Button addBtn;
    public Label hiUserLbl;
    public Label userIDLbl;
    public ListView<ToDoTM> listToDo;
    public ListView<CompletedTM> listComplete;
    public TextField txtSelectedTask;
    public Button deleteBtn;
    public Button moveBtn;
    public Button editBtn;
    public AnchorPane root;
    public Label lblDate;

    public void initialize(){
        root.requestFocus();

        hiUserLbl.setText("Hi " + WelcomePageFormController.loginUsername);
        userIDLbl.setText(WelcomePageFormController.loginUserID);

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        lblDay.setText(dayOfWeek.toString().substring(0,1) + dayOfWeek.toString().substring(1).toLowerCase());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        lblDate.setText(today.format(formatter));

        txtAddNewTask.setDisable(true);
        addBtn.setDisable(true);
        setDisablePane(true);

        loadToDoList();
        loadCompletedList();

        listToDo.getSelectionModel().clearSelection();
        listComplete.getSelectionModel().clearSelection();

        // For listToDo
        listToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observableValue, ToDoTM oldVal, ToDoTM newVal) {
                if (newVal == null) return;

                // Deselect from the other list
                listComplete.getSelectionModel().clearSelection();

                setDisablePane(false);
                txtSelectedTask.setDisable(false);
                editBtn.setDisable(false);
                moveBtn.setDisable(false);
                deleteBtn.setDisable(false);

                txtSelectedTask.setText(newVal.getDescription());
            }
        });

// For listComplete
        listComplete.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CompletedTM>() {
            @Override
            public void changed(ObservableValue<? extends CompletedTM> observableValue, CompletedTM oldVal, CompletedTM newVal) {
                if (newVal == null) return;

                // Deselect from the other list
                listToDo.getSelectionModel().clearSelection();

                deleteBtn.setDisable(false);
                txtSelectedTask.setDisable(true);
                editBtn.setDisable(true);
                moveBtn.setDisable(true);

                txtSelectedTask.setText(newVal.getDescription());
            }
        });

    }

    public void setDisablePane(boolean isDisable){
        txtSelectedTask.setDisable(isDisable);
        deleteBtn.setDisable(isDisable);
        editBtn.setDisable(isDisable);
        moveBtn.setDisable(isDisable);
    }

    public void addBtnOnAction(ActionEvent actionEvent) {
        String id = autoGenerateID();
        String description = txtAddNewTask.getText();
        String user_id = userIDLbl.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo (id,description,user_id) values(?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_id);

            preparedStatement.executeUpdate();

            txtAddNewTask.clear();
            loadToDoList();

            txtAddNewTask.setDisable(true);
            addBtn.setDisable(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void newTaskBtnOnAction(ActionEvent actionEvent) {
        txtAddNewTask.setDisable(false);
        addBtn.setDisable(false);
        txtAddNewTask.requestFocus();
    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        String id = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1,oldId.length());
                int intID = Integer.parseInt(oldId);
                intID = intID +1;


                if(intID<10){
                    id = "T00" + intID;
                }else if(intID<100){
                    id = "T0" + intID;
                }else{
                    id = "T" + intID;
                }

            }else{
                id = "T001";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    public void loadToDoList(){
        ObservableList<ToDoTM> items = listToDo.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ? and status = 'active' ");
            preparedStatement.setObject(1,WelcomePageFormController.loginUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id,description,user_id);
                items.add(toDoTM);
            }
            listToDo.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadCompletedList(){
        ObservableList<CompletedTM> items = listComplete.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ? and status = 'complete' ");
            preparedStatement.setObject(1,WelcomePageFormController.loginUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                CompletedTM completedTM = new CompletedTM(id,description,user_id);
                items.add(completedTM);
            }
            listToDo.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        String text = txtSelectedTask.getText();

        ToDoTM selectedItem = listToDo.getSelectionModel().getSelectedItem();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,text);
            preparedStatement.setObject(2,selectedItem.getId());

            preparedStatement.executeUpdate();
            loadToDoList();
            txtSelectedTask.clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnMoveOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to move this task?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            ToDoTM selectedItem = listToDo.getSelectionModel().getSelectedItem();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("update todo set status = 'complete' where id = ? ");
                preparedStatement.setObject(1,selectedItem.getId());

                preparedStatement.executeUpdate();
                loadToDoList();
                loadCompletedList();
                txtSelectedTask.clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this task?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            ToDoTM selectedItemToDo = listToDo.getSelectionModel().getSelectedItem();
            CompletedTM selectedItemComplete = listComplete.getSelectionModel().getSelectedItem();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ? ");
                if(selectedItemToDo == null){
                    preparedStatement.setObject(1,selectedItemComplete.getId());
                }
                else{
                    preparedStatement.setObject(1,selectedItemToDo.getId());
                }

                preparedStatement.executeUpdate();
                loadToDoList();
                loadCompletedList();
                txtSelectedTask.clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void logOutBtnOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do You Want to Logout ?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("WelcomePageForm.fxml"));
            Scene scene = new Scene(parent);

            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Welcome to Task Manager");
            primaryStage.centerOnScreen();
        }
    }
}
