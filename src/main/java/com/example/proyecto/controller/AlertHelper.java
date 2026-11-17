package com.example.proyecto.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertHelper {

    private static void aplicarCSS(Alert alert) {
        DialogPane dp = alert.getDialogPane();
        dp.getStylesheets().add(
                AlertHelper.class.getResource(
                        "/com/example/proyecto/css/styles.css").toExternalForm());

        alert.setOnShown(e -> {
            Stage alertStage = (Stage) dp.getScene().getWindow();
            alertStage.getIcons().add(new Image(
                    AlertHelper.class.getResourceAsStream(
                            "/com/example/proyecto/images/app-icon.png")));
        });
    }
    public static void warning(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarCSS(alert);
        alert.showAndWait();
    }

    public static void error(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarCSS(alert);
        alert.showAndWait();
    }

    public static void info(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarCSS(alert);
        alert.showAndWait();
    }

    public static Optional<ButtonType> confirmation(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarCSS(alert);
        return alert.showAndWait();
    }
}