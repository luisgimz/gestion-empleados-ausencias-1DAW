package com.example.proyecto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.proyecto.model.*;
import com.example.proyecto.service.SolicitudAusenciaService;

import java.sql.SQLException;

public class PopUpGestAusenciasController {

    @FXML private Label lblNombre;
    @FXML private Label lblTipo;
    @FXML private Label lblDuracion;
    @FXML private Label lblMotivo;
    @FXML private Label lblFechaSol;
    @FXML private Label lblEstado;
    @FXML private TextArea txtComentario;

    private SolicitudAusencia solicitud;
    private SolicitudAusenciaService solicitudService;

    public PopUpGestAusenciasController() {
        try {
            solicitudService = new SolicitudAusenciaService();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initData(SolicitudAusencia solicitud) {
        this.solicitud = solicitud;
        lblNombre.setText(solicitud.getEmpleado().getNombre() + " " +
                solicitud.getEmpleado().getApellidos());
        lblTipo.setText(solicitud.getTipoAusencia().getNombre());
        lblDuracion.setText(solicitud.getFechaInicio() + " - " + solicitud.getFechaFin()
                + " (" + solicitud.getDiasSolicitados() + " días)");
        lblMotivo.setText(solicitud.getMotivo());
        lblFechaSol.setText(solicitud.getFechaSolicitud().toString());
        lblEstado.setText(solicitud.getEstado());
    }

    @FXML
    private void handleAceptar() {
        String comentario = txtComentario.getText();
        Usuario admin = SesionManager.getUsuario();

        boolean ok = solicitudService.aprobarSolicitud(solicitud, admin, comentario);
        if (!ok) {
            AlertHelper.error("No se pudo aprobar la solicitud. Inténtalo de nuevo.");
            return;
        }
        cerrar();
    }

    @FXML
    private void handleRechazar() {
        try {
            boolean ok = solicitudService.rechazarSolicitud(
                    solicitud, SesionManager.getUsuario(), txtComentario.getText());
            if (!ok) {
                AlertHelper.error("No se pudo rechazar la solicitud. Inténtalo de nuevo.");
                return;
            }
        } catch (IllegalArgumentException e) {
            AlertHelper.warning(e.getMessage());
            return;
        }
        cerrar();
    }

    private void cerrar() {
        ((Stage) lblNombre.getScene().getWindow()).close();
    }
}