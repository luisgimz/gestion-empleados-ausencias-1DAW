package com.example.proyecto.controller;

import com.example.proyecto.model.RegistroAusencia;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class DetalleRegistroController {

    @FXML private Label lblId;
    @FXML private Label lblIdSolicitud;
    @FXML private Label lblTipoSolicitud;
    @FXML private Label lblEmpleado;
    @FXML private Label lblAdmin;
    @FXML private Label lblComentario;
    @FXML private Label lblFechaRegistro;
    @FXML private Label lblEstado;

    public void initData(RegistroAusencia r) {
        lblId.setText(String.valueOf(r.getId()));
        lblIdSolicitud.setText(String.valueOf(r.getSolicitud().getId()));
        lblTipoSolicitud.setText(r.getTipoSolicitud().getNombre());
        lblEmpleado.setText(r.getEmpleadoSolicitud().getNombre() + " " +
                r.getEmpleadoSolicitud().getApellidos());
        lblAdmin.setText(r.getAdmin().getNombre() + " " +
                r.getAdmin().getApellidos());
        lblComentario.setText(r.getComentarioAdmin() != null ?
                r.getComentarioAdmin() : "—");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblFechaRegistro.setText(r.getFechaRegistro() != null ?
                r.getFechaRegistro().format(fmt) : "—");
        lblEstado.setText(r.getSolicitud().getEstado());
    }

    @FXML
    private void volver() {
        ((Stage) lblId.getScene().getWindow()).close();
    }
}