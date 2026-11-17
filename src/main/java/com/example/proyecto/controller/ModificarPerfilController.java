package com.example.proyecto.controller;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.service.EmpleadoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;

public class ModificarPerfilController {

    @FXML private ImageView imgFotoPreview;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtEmailPersonal;

    private Empleado empleado;
    private PerfilController perfilController;
    private EmpleadoService empleadoService;
    private byte[] nuevaFoto;

    public void initData(Empleado emp, PerfilController parent) {
        this.empleado = emp;
        this.perfilController = parent;
        try {
            empleadoService = new EmpleadoService();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtTelefono.setText(emp.getTelefono() != null ? emp.getTelefono() : "");
        txtDireccion.setText(emp.getDireccion() != null ? emp.getDireccion() : "");
        txtEmailPersonal.setText(emp.getEmailPersonal() != null ? emp.getEmailPersonal() : "");

        if (emp.getFoto() != null && emp.getFoto().length > 0) {
            imgFotoPreview.setImage(new Image(new ByteArrayInputStream(emp.getFoto())));
        } else {
            imgFotoPreview.setImage(new Image(
                    getClass().getResourceAsStream("/com/example/proyecto/images/default-avatar.png")));
        }
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar foto de perfil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png", "*.gif"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));

        File file = chooser.showOpenDialog(imgFotoPreview.getScene().getWindow());
        if (file != null) {
            try {
                nuevaFoto = Files.readAllBytes(file.toPath());
                imgFotoPreview.setImage(new Image(new ByteArrayInputStream(nuevaFoto)));
            } catch (Exception e) {
                AlertHelper.error("No se pudo cargar la imagen.");
            }
        }
    }

    @FXML
    private void guardar() {

        try {
            String telefono = txtTelefono.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String emailPersonal = txtEmailPersonal.getText().trim();

            boolean okPerfil = empleadoService.actualizarPerfil(
                    empleado.getDni(),
                    telefono,
                    direccion,
                    emailPersonal
            );

            if (!okPerfil) {
                AlertHelper.error("Error al guardar los datos.");
                return;
            }

            if (nuevaFoto != null) {
                boolean okFoto = empleadoService.actualizarFoto(
                        empleado.getDni(),
                        nuevaFoto
                );

                if (!okFoto) {
                    AlertHelper.error("Datos guardados pero error al guardar la foto.");
                }
            }

            perfilController.refrescarPerfil();
            ((Stage) txtTelefono.getScene().getWindow()).close();

        } catch (IllegalArgumentException e) {
            AlertHelper.warning(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        ((Stage) txtTelefono.getScene().getWindow()).close();
    }
}