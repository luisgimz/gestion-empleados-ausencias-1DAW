package com.example.proyecto.controller;

import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CambiarPasswordController {

    @FXML private PasswordField txtNueva;
    @FXML private PasswordField txtConfirmar;
    @FXML private TextField txtNuevaVisible;
    @FXML private TextField txtConfirmarVisible;
    @FXML private Button btnOjoNueva;
    @FXML private Button btnOjoConfirmar;

    private boolean mostrandoNueva = false;
    private boolean mostrandoConfirmar = false;

    private Usuario usuario;
    private UsuarioService usuarioService;

    public void initData(Usuario u) {
        this.usuario = u;
        try {
            usuarioService = new UsuarioService();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtNueva.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtNuevaVisible.getText().equals(newVal))
                txtNuevaVisible.setText(newVal);
        });
        txtNuevaVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtNueva.getText().equals(newVal))
                txtNueva.setText(newVal);
        });

        txtConfirmar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtConfirmarVisible.getText().equals(newVal))
                txtConfirmarVisible.setText(newVal);
        });
        txtConfirmarVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtConfirmar.getText().equals(newVal))
                txtConfirmar.setText(newVal);
        });
    }

    @FXML
    private void toggleNueva() {
        mostrandoNueva = !mostrandoNueva;
        txtNueva.setVisible(!mostrandoNueva);
        txtNueva.setManaged(!mostrandoNueva);
        txtNuevaVisible.setVisible(mostrandoNueva);
        txtNuevaVisible.setManaged(mostrandoNueva);
        btnOjoNueva.setText(mostrandoNueva ? "🙈" : "👁");
    }

    @FXML
    private void toggleConfirmar() {
        mostrandoConfirmar = !mostrandoConfirmar;
        txtConfirmar.setVisible(!mostrandoConfirmar);
        txtConfirmar.setManaged(!mostrandoConfirmar);
        txtConfirmarVisible.setVisible(mostrandoConfirmar);
        txtConfirmarVisible.setManaged(mostrandoConfirmar);
        btnOjoConfirmar.setText(mostrandoConfirmar ? "🙈" : "👁");
    }

    @FXML
    private void guardar() {
        String nueva = txtNueva.getText().trim();
        String confirmar = txtConfirmar.getText().trim();

        if (nueva.isEmpty() || confirmar.isEmpty()) {
            AlertHelper.warning("Rellena ambos campos.");
            return;
        }
        if (!nueva.equals(confirmar)) {
            AlertHelper.warning("Las contraseñas no coinciden.");
            return;
        }
        if (nueva.length() < 6) {
            AlertHelper.warning("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        try {
            boolean ok = usuarioService.cambiarPassword(usuario.getUsername(), nueva);
            if (ok) {
                AlertHelper.info("Contraseña actualizada correctamente.");
                ((Stage) txtNueva.getScene().getWindow()).close();
            } else {
                AlertHelper.error("Error al cambiar la contraseña.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.error("Error inesperado.");
        }
    }

    @FXML
    private void cancelar() {
        ((Stage) txtNueva.getScene().getWindow()).close();
    }
}