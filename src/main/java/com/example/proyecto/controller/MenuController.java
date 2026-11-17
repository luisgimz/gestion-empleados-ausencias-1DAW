package com.example.proyecto.controller;

import com.example.proyecto.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class MenuController {

    @FXML private VBox contenedor;
    @FXML private HBox menuBar;
    @FXML private Button btnEmpleados;
    @FXML private Button btnGestAusencias;
    @FXML private Button btnRegAusencias;
    @FXML private Button btnSolAusencias;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        menuBar.setVisible(false);
        menuBar.setManaged(false);
    }

    public void cargarLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/login.fxml"));
            Node panel = loader.load();
            LoginController c = loader.getController();
            c.setMenuController(this);
            contenedor.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(Usuario usuario) {
        this.usuarioActual = usuario;
        menuBar.setVisible(true);
        menuBar.setManaged(true);
        String rol = usuario.getRol().getNombre().toUpperCase();
        boolean esAdmin      = rol.equals("ADMIN");
        boolean esModerador  = rol.equals("MODERADOR");
        btnEmpleados.setVisible(esAdmin);
        btnEmpleados.setManaged(esAdmin);
        btnGestAusencias.setVisible(esAdmin || esModerador);
        btnGestAusencias.setManaged(esAdmin || esModerador);
        btnRegAusencias.setVisible(esAdmin || esModerador);
        btnRegAusencias.setManaged(esAdmin || esModerador);
        btnSolAusencias.setVisible(true);
        btnSolAusencias.setManaged(true);
        cargarPerfil();
    }

    @FXML
    private void cargarEmpleados() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/gest-empleados.fxml"));
            Node panel = loader.load();
            contenedor.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarGestAusencias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/gest-ausencias.fxml"));
            Node panel = loader.load();
            contenedor.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarRegAusencias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/reg-ausencias.fxml"));
            Node panel = loader.load();
            contenedor.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarSolAusencias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/sol-ausencias.fxml"));
            Node panel = loader.load();
            contenedor.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/perfil.fxml"));
            Node panel = loader.load();
            PerfilController c = loader.getController();
            c.initData(usuarioActual);
            contenedor.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        SesionManager.logout();
        usuarioActual = null;
        menuBar.setVisible(false);
        menuBar.setManaged(false);
        cargarLogin();
    }
}