module com.example.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.proyecto to javafx.fxml;
    exports com.example.proyecto;
    exports com.example.proyecto.controller;
    opens com.example.proyecto.controller to javafx.fxml;
}