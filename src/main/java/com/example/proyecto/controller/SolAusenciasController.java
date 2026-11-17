package com.example.proyecto.controller;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.service.SolicitudAusenciaService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SolAusenciasController {

    @FXML private ComboBox<String> cmbTipoPermiso;
    @FXML private DatePicker       dpDesde;
    @FXML private DatePicker       dpHasta;
    @FXML private TextArea         txtMotivo;

    private SolicitudAusenciaService solicitudService;

    @FXML
    public void initialize() {
        try {
            solicitudService = new SolicitudAusenciaService();
            cmbTipoPermiso.getItems().addAll(solicitudService.obtenerNombresTipos());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void enviarSolicitud() {
        if (cmbTipoPermiso.getValue() == null
                || dpDesde.getValue() == null
                || dpHasta.getValue() == null) {
            mostrarError("Campos obligatorios", "Rellena todos los campos marcados con *.");
            return;
        }

        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        if (hasta.isBefore(desde)) {
            mostrarError("Fechas incorrectas", "La fecha fin no puede ser anterior a la fecha inicio.");
            return;
        }

        String tipoSeleccionado = cmbTipoPermiso.getValue();
        Empleado empleado = SesionManager.getUsuario().getEmpleado();

        long diasSolicitados = ChronoUnit.DAYS.between(desde, hasta) + 1;

        String mensajeSecundario;
        try {
            mensajeSecundario = solicitudService.obtenerMensajeConfirmacion(
                    empleado.getDni(), tipoSeleccionado, desde, hasta);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError("Solicitud no permitida", e.getMessage());
            return;
        }

        GestorEscenas.cargarConfirmSolAusencias(
                "Estás solicitando un permiso de " + diasSolicitados
                        + " días de " + tipoSeleccionado + ".",
                mensajeSecundario
        );

        if (ConfirmSolAusenciasController.fueAceptado()) {

            SolicitudAusencia solicitud = new SolicitudAusencia();
            solicitud.setEmpleado(empleado);
            solicitud.setTipoAusencia(solicitudService.obtenerTipoPorNombre(tipoSeleccionado));
            solicitud.setFechaInicio(desde);
            solicitud.setFechaFin(hasta);
            solicitud.setMotivo(txtMotivo.getText());
            solicitud.setEstado("PENDIENTE");
            solicitud.setFechaSolicitud(LocalDate.now());

            try {
                boolean ok = solicitudService.registrarSolicitud(solicitud);
                if (ok) {
                    borrarFormulario();
                    mostrarAviso("Solicitud enviada", "Tu solicitud ha sido enviada correctamente.");
                } else {
                    mostrarError("Error", "No se pudo guardar la solicitud. Inténtalo de nuevo.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                mostrarError("Solicitud no permitida", e.getMessage());
            }
        }
    }

    @FXML
    private void borrarFormulario() {
        cmbTipoPermiso.setValue(null);
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        txtMotivo.clear();
    }

    private void mostrarAviso(String titulo, String mensaje) {
        AlertHelper.info(mensaje);
    }

    private void mostrarError(String titulo, String mensaje) {
        AlertHelper.error(mensaje);
    }
}