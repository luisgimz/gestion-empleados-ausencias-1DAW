package com.example.proyecto;

import com.example.proyecto.controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import javafx.scene.image.Image;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("menu.fxml"));
        BorderPane root = loader.load();
        MenuController menuController = loader.getController();

        menuController.cargarLogin();

        Scene scene = new Scene(root, 1420, 700);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/proyecto/css/styles.css").toExternalForm());
        stage.getIcons().add(new Image(
                getClass().getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
        stage.setTitle("Panel de gestión");
        stage.setScene(scene);
        stage.setMinWidth(1450);
        stage.setMinHeight(730);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}