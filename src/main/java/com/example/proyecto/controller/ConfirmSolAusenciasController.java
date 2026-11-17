package com.example.proyecto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmSolAusenciasController {

    @FXML private Label lblMensajePrincipal;
    @FXML private Label lblMensajeSecundario;

    private static boolean aceptado = false;

    public static boolean fueAceptado() {
        return aceptado;
    }

    public void setMensajes(String mensajePrincipal, String mensajeSecundario) {
        aceptado = false;
        lblMensajePrincipal.setText(mensajePrincipal);
        lblMensajeSecundario.setText(mensajeSecundario);
    }

    @FXML
    private void aceptarSolicitud() {
        aceptado = true;
        cerrarVentana();
    }

    @FXML
    private void volverFormulario() {
        aceptado = false;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblMensajePrincipal.getScene().getWindow();
        stage.close();
    }
}