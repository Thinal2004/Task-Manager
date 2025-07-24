module com.example.task_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.task_manager to javafx.fxml;
    exports com.example.task_manager;
}