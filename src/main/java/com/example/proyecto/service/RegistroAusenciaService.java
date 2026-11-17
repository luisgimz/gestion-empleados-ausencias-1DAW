package com.example.proyecto.service;

import com.example.proyecto.database.RegistroAusenciaDAO;
import com.example.proyecto.model.RegistroAusencia;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RegistroAusenciaService {

    private final RegistroAusenciaDAO registroDAO;

    public RegistroAusenciaService() throws SQLException {
        this.registroDAO = new RegistroAusenciaDAO();
    }

    public ArrayList<RegistroAusencia> obtenerTodos() {
        return registroDAO.obtenerTodos();
    }

    public RegistroAusencia obtenerPorId(long id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return registroDAO.obtenerPorId(id);
    }

    public ArrayList<RegistroAusencia> obtenerPorSolicitud(long solicitudId) {

        if (solicitudId <= 0) {
            throw new IllegalArgumentException("ID de solicitud inválido");
        }
        return registroDAO.obtenerPorSolicitudId(solicitudId);
    }

    public ArrayList<RegistroAusencia> obtenerAusentesProximos7Dias() {
        return registroDAO.obtenerAusentesProximos7Dias();
    }

    public boolean insertarRegistro(RegistroAusencia registro) {

        if (registro == null) {
            throw new IllegalArgumentException("Registro nulo");
        }

        // Si no viene fecha, la ponemos
        if (registro.getFechaRegistro() == null) {
            registro.setFechaRegistro(LocalDateTime.now());
        }

        // Limpieza del comentario
        if (registro.getComentarioAdmin() != null) {
            registro.setComentarioAdmin(registro.getComentarioAdmin().trim());
        }

        validarRegistro(registro);

        return registroDAO.insertar(registro);
    }

    private void validarRegistro(RegistroAusencia registro) {

        if (registro == null) {
            throw new IllegalArgumentException("Registro nulo");
        }

        if (registro.getEmpleadoSolicitud() == null) {
            throw new IllegalArgumentException("Empleado obligatorio");
        }

        if (registro.getAdmin() == null) {
            throw new IllegalArgumentException("Administrador obligatorio");
        }

        if (registro.getSolicitud() == null) {
            throw new IllegalArgumentException("Solicitud obligatoria");
        }

        if (registro.getTipoSolicitud() == null) {
            throw new IllegalArgumentException("Tipo de ausencia obligatorio");
        }

        if (registro.getEmpleadoSolicitud().getDni() == null || registro.getEmpleadoSolicitud().getDni().isBlank()) {
            throw new IllegalArgumentException("DNI del empleado obligatorio");
        }

        if (registro.getAdmin().getDni() == null || registro.getAdmin().getDni().isBlank()) {
            throw new IllegalArgumentException("DNI del administrador obligatorio");
        }

        if (registro.getSolicitud().getId() <= 0) {
            throw new IllegalArgumentException("Solicitud inválida");
        }

        if (registro.getTipoSolicitud().getId() == null || registro.getTipoSolicitud().getId() <= 0) {
            throw new IllegalArgumentException("Tipo de ausencia inválido");
        }
    }

}