package com.example.proyecto.controller;

import com.example.proyecto.model.SolicitudAusencia;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DetalleAusenciaSimpleController {

    @FXML private Label lblNombre;
    @FXML private Label lblTipo;
    @FXML private Label lblDuracion;
    @FXML private Label lblMotivo;
    @FXML private Label lblFechaSolicitud;
    @FXML private Label lblEstado;

    public void initData(SolicitudAusencia s, String nombreEmpleado) {
        lblNombre.setText(nombreEmpleado);
        lblTipo.setText(s.getTipoAusencia().getNombre());
        lblDuracion.setText(s.getFechaInicio() + " - " + s.getFechaFin() + " (" + s.getDiasSolicitados() + " días)");
        lblMotivo.setText(s.getMotivo());
        lblFechaSolicitud.setText(s.getFechaSolicitud().toString());
        lblEstado.setText(s.getEstado());
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblNombre.getScene().getWindow()).close();
    }
}