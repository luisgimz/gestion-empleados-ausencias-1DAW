package com.example.proyecto.controller;

import com.example.proyecto.model.PuestoTrabajo;
import com.example.proyecto.service.EmpleadoService;
import com.example.proyecto.service.PuestoTrabajoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;

public class AnyadirEmpleadoController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmailCorporativo;
    @FXML private TextField txtEmailPersonal;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtNumeroSs;
    @FXML private TextField txtDiasVacaciones;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbRol;
    @FXML private ComboBox<PuestoTrabajo> cmbPuesto;
    @FXML private Label lblDepartamento;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private DatePicker dpFechaAlta;
    @FXML private DatePicker dpFechaBaja;

    private EmpleadoService empleadoService;
    private PuestoTrabajoService puestoTrabajoService;

    public AnyadirEmpleadoController() {
        try {
            empleadoService      = new EmpleadoService();
            puestoTrabajoService = new PuestoTrabajoService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cmbEstado.getItems().addAll("Activo", "Teletrabajo", "Suspendido", "Despedido");
        dpFechaBaja.setPromptText("Puedes dejarlo sin fecha");
        dpFechaBaja.setStyle("-fx-prompt-text-fill: gray;");
        cmbRol.getItems().addAll("ADMIN", "MODERADOR", "USER");
        cmbRol.setValue("USER");
        txtDiasVacaciones.setEditable(false);
        txtDiasVacaciones.setStyle(
                "-fx-control-inner-background: #e0e0e0;" +
                        "-fx-opacity: 1;" +
                        "-fx-text-fill: #555;"
        );
        txtDiasVacaciones.setText("22");

        ArrayList<PuestoTrabajo> puestos = puestoTrabajoService.obtenerTodos();
        cmbPuesto.setItems(FXCollections.observableArrayList(puestos));
        cmbPuesto.setConverter(new StringConverter<>() {
            @Override
            public String toString(PuestoTrabajo p) {
                return p != null ? p.getNombre() : "";
            }
            @Override
            public PuestoTrabajo fromString(String s) { return null; }
        });

        cmbPuesto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getDepartamento() != null) {
                lblDepartamento.setText(newVal.getDepartamento().getNombre());
            } else {
                lblDepartamento.setText("-");
            }
        });
    }

    @FXML
    private void anyadirEmpleado() {
        try {
            String nombre    = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();

            if (cmbPuesto.getValue() == null) {
                AlertHelper.warning("El puesto de trabajo es obligatorio.");
                return;
            }

            if (dpFechaNacimiento.getValue() == null) {
                AlertHelper.warning("La fecha de nacimiento es obligatoria.");
                return;
            }

            boolean ok = empleadoService.insertarEmpleado(
                    txtDni.getText().trim(),
                    nombre,
                    apellidos,
                    txtEmailCorporativo.getText().trim(),
                    txtEmailPersonal.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtDireccion.getText().trim(),
                    txtNumeroSs.getText().trim(),
                    dpFechaNacimiento.getValue(),
                    dpFechaAlta.getValue(),
                    dpFechaBaja.getValue(),
                    cmbEstado.getValue(),
                    cmbRol.getValue(),
                    cmbPuesto.getValue()
            );

            if (ok) {
                String username = nombre.toLowerCase() + "." +
                        apellidos.split(" ")[0].toLowerCase();
                AlertHelper.info("Empleado añadido correctamente.\nUsuario: " + username + "\nContraseña: 123456");
                limpiarFormulario();
            } else {
                AlertHelper.error("No se pudo añadir el empleado.");
            }

        } catch (IllegalArgumentException e) {
            AlertHelper.warning(e.getMessage());
        } catch (Exception e) {
            AlertHelper.error("Error inesperado.");
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtDni.clear();
        txtNombre.clear();
        txtApellidos.clear();
        txtEmailCorporativo.clear();
        txtEmailPersonal.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtNumeroSs.clear();
        cmbEstado.setValue(null);
        cmbPuesto.setValue(null);
        lblDepartamento.setText("-");
        dpFechaNacimiento.setValue(null);
        dpFechaAlta.setValue(null);
        dpFechaBaja.setValue(null);
    }

    @FXML
    private void cancelar() {
        ((Stage) txtDni.getScene().getWindow()).close();
    }
}