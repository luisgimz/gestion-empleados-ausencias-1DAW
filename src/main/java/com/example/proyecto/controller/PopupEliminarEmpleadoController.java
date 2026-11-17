package com.example.proyecto.controller;

import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.EmpleadoService;
import com.example.proyecto.service.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class PopupEliminarEmpleadoController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tablaEmpleados;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colDni;
    @FXML private TableColumn<Usuario, String> colCuenta;

    private UsuarioService usuarioService;
    private EmpleadoService empleadoService;
    private ObservableList<Usuario> listaUsuarios;

    @FXML
    public void initialize() {
        try {
            usuarioService  = new UsuarioService();
            empleadoService = new EmpleadoService();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        colNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmpleado().getNombre() + " " + c.getValue().getEmpleado().getApellidos()
        ));
        colDni.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmpleado().getDni()
        ));

        colCuenta.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getUsername().equals("Sin cuenta") ? "Sin cuenta" :
                        c.getValue().isActivo() ? "Activo" : "Inactivo"
        ));

        listaUsuarios = FXCollections.observableArrayList(usuarioService.obtenerTodosConEmpleados());
        FilteredList<Usuario> listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(u -> {
                if (newVal == null || newVal.isBlank()) return true;
                String filtro = newVal.toLowerCase();
                return (u.getEmpleado().getNombre() + " " + u.getEmpleado().getApellidos())
                        .toLowerCase().contains(filtro)
                        || u.getEmpleado().getDni().toLowerCase().contains(filtro);
            });
        });

        tablaEmpleados.setItems(listaFiltrada);
        tablaEmpleados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    @FXML
    private void eliminarSeleccionado() {
        Usuario u = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (u == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado antes de eliminar.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar empleado completo");
        confirm.setHeaderText("⚠ Esta acción es irreversible");
        confirm.setContentText("Se eliminarán todos los datos de " +
                u.getEmpleado().getNombre() + " " + u.getEmpleado().getApellidos() +
                ", incluyendo su usuario, solicitudes y registros.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = empleadoService.eliminarEmpleadoCompleto(u.getEmpleado().getDni());
                if (ok) {
                    listaUsuarios.remove(u);
                    new Alert(Alert.AlertType.INFORMATION, "Empleado eliminado correctamente.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar. Inténtalo de nuevo.").showAndWait();
                }
            }
        });
    }
}