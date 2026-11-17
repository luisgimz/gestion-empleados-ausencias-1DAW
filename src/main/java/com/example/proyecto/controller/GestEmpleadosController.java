package com.example.proyecto.controller;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.service.EmpleadoService;
import com.example.proyecto.service.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.example.proyecto.model.Usuario;

import java.sql.SQLException;

public class GestEmpleadosController {

    @FXML private TableView<Usuario> tablaEmpleados;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colDepartamento;
    @FXML private TableColumn<Usuario, String> colPuesto;
    @FXML private TableColumn<Usuario, String> colDni;
    @FXML private TableColumn<Usuario, String> colActivo;
    @FXML private TableColumn<Usuario, String> colEstadoLaboral;
    @FXML private TableColumn<Usuario, String> colFechaAlta;
    @FXML private TableColumn<Usuario, String> colFechaBaja;

    private UsuarioService usuarioService;

    public GestEmpleadosController() {
        try {
            usuarioService = new UsuarioService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getId())
        ));
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getNombre() + " " + c.getValue().getEmpleado().getApellidos()
        ));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getEmailCorporativo()
        ));
        colRol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getRol().getNombre()
        ));
        colDepartamento.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getEmpleado().getPuestoActual() != null &&
                                c.getValue().getEmpleado().getPuestoActual().getDepartamento() != null
                                ? c.getValue().getEmpleado().getPuestoActual().getDepartamento().getNombre()
                                : ""
                ));
        colPuesto.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getEmpleado().getPuestoActual() != null
                                ? c.getValue().getEmpleado().getPuestoActual().getNombre()
                                : ""
                ));
        colDni.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getDni()
        ));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().isActivo() ? "Si" : "No"
        ));
        colEstadoLaboral.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getEstadoLaboral()
        ));
        colFechaAlta.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getFechaAlta() != null
                        ? c.getValue().getEmpleado().getFechaAlta().toString()
                        : ""
        ));
        colFechaBaja.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getFechaBaja() != null
                        ? c.getValue().getEmpleado().getFechaBaja().toString()
                        : "No tiene fecha"
        ));

        cargarEmpleados();
        tablaEmpleados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarEmpleados() {
        tablaEmpleados.getItems().setAll(usuarioService.obtenerTodos());
    }

    @FXML
    private void anyadirEmpleado() {
        GestorEscenas.cargarAnyadirEmpleado();
        cargarEmpleados();
    }

    @FXML
    private void editarEmpleado() {
        Usuario u = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (u == null) {
            AlertHelper.warning("Selecciona un empleado de la tabla antes de editar.");
            return;
        }

        EmpleadoService empleadoService;
        try {
            empleadoService = new EmpleadoService();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.error("Error al cargar los datos del empleado.");
            return;
        }

        Empleado empleadoCompleto = empleadoService.obtenerPorDni(u.getEmpleado().getDni());
        if (empleadoCompleto == null) {
            AlertHelper.error("No se pudo cargar el empleado.");
            return;
        }

        GestorEscenas.cargarEditarEmpleado(empleadoCompleto, u);
        cargarEmpleados();
    }

    @FXML
    private void eliminarEmpleado() {
        Usuario u = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (u == null) {
            AlertHelper.warning("Selecciona un empleado de la tabla antes de eliminar.");
            return;
        }

        boolean esAdmin = u.getRol().getNombre().equalsIgnoreCase("ADMIN");

        AlertHelper.confirmation(
                (esAdmin ? "El usuario tiene rol ADMIN. " : "") +
                        "¿Estas seguro de que quieres eliminar a " +
                        u.getEmpleado().getNombre() + " " + u.getEmpleado().getApellidos() +
                        "? Esta accion no se puede deshacer."
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = usuarioService.eliminarUsuario(u.getUsername());
                if (!ok) {
                    AlertHelper.error("No se pudo eliminar el empleado. Intentalo de nuevo.");
                }
                cargarEmpleados();
            }
        });
    }

    @FXML
    private void reiniciarVacaciones() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reiniciar vacaciones");
        confirm.setHeaderText("¿Reiniciar los días de vacaciones de todos los empleados?");
        confirm.setContentText("Esta acción establecerá los días disponibles a 22 para todos los empleados activos. Normalmente se hace una vez al año.");
        confirm.getDialogPane().getStylesheets().add(
                GestorEscenas.class.getResource("/com/example/proyecto/css/styles.css").toExternalForm());
        confirm.getDialogPane().getScene().getWindow();
        ((Stage) confirm.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image(GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = usuarioService.reiniciarVacacionesTodos();
                if (ok) {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Vacaciones reiniciadas correctamente para todos los empleados activos.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "No se pudieron reiniciar las vacaciones. Inténtalo de nuevo.").showAndWait();
                }
                cargarEmpleados();
            }
        });
    }

    @FXML
    private void eliminarEmpleadoCompleto() {
        GestorEscenas.cargarPopupEliminarEmpleado();
        cargarEmpleados();
    }
}