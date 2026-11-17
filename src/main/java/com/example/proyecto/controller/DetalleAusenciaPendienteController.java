package com.example.proyecto.controller;

import com.example.proyecto.service.SolicitudAusenciaService;
import com.example.proyecto.model.SolicitudAusencia;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class DetalleAusenciaPendienteController {

    @FXML private Label lblNombre;
    @FXML private Label lblTipo;
    @FXML private Label lblDuracion;
    @FXML private Label lblMotivo;
    @FXML private Label lblFechaSolicitud;
    @FXML private Label lblEstado;

    private SolicitudAusencia solicitud;
    private SolicitudAusenciaService solicitudService;

    public DetalleAusenciaPendienteController() {
        try {
            solicitudService = new SolicitudAusenciaService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initData(SolicitudAusencia solicitud, String nombreEmpleado) {
        this.solicitud = solicitud;
        lblNombre.setText(nombreEmpleado);
        lblTipo.setText(solicitud.getTipoAusencia().getNombre());
        lblDuracion.setText(solicitud.getFechaInicio() + " - " + solicitud.getFechaFin()
                + " (" + solicitud.getDiasSolicitados() + " días)");
        lblMotivo.setText(solicitud.getMotivo());
        lblFechaSolicitud.setText(solicitud.getFechaSolicitud().toString());
        lblEstado.setText(solicitud.getEstado());
    }

    @FXML
    private void handleEliminar() {
        AlertHelper.confirmation("¿Estás seguro de que quieres cancelar esta solicitud? Esta acción no se puede deshacer.")
                .ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    solicitudService.cancelarSolicitud(solicitud);
                    handleCerrar();
                } catch (IllegalStateException e) {
                    AlertHelper.warning(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblNombre.getScene().getWindow()).close();
    }
}