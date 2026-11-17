package com.example.proyecto.service;

import com.example.proyecto.database.TipoAusenciaDAO;
import com.example.proyecto.model.TipoAusencia;

import java.sql.SQLException;
import java.util.ArrayList;

public class TipoAusenciaService {

    private final TipoAusenciaDAO tipoAusenciaDAO;

    public TipoAusenciaService() throws SQLException {
        this.tipoAusenciaDAO = new TipoAusenciaDAO();
    }

    public ArrayList<TipoAusencia> obtenerTodos() {
        return tipoAusenciaDAO.obtenerTodos();
    }

    public TipoAusencia obtenerPorId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        return tipoAusenciaDAO.obtenerPorId(id);
    }

    public TipoAusencia obtenerPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        return tipoAusenciaDAO.obtenerPorNombre(nombre);
    }

    public boolean insertar(TipoAusencia tipo) {
        if (tipo.getNombre() == null || tipo.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo es obligatorio.");
        }

        // check duplicate
        TipoAusencia existente = tipoAusenciaDAO.obtenerPorNombre(tipo.getNombre().trim());
        if (existente != null) {
            throw new IllegalStateException("Ya existe un tipo de ausencia con ese nombre.");
        }

        tipo.setNombre(tipo.getNombre().trim());
        return tipoAusenciaDAO.insertar(tipo);
    }

    public boolean actualizar(TipoAusencia tipo) {
        if (tipo.getNombre() == null || tipo.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo es obligatorio.");
        }
        tipo.setNombre(tipo.getNombre().trim());
        return tipoAusenciaDAO.actualizar(tipo);
    }

    public boolean eliminar(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        return tipoAusenciaDAO.eliminar(id);
    }

    public ArrayList<String> obtenerNombres() {
        return tipoAusenciaDAO.obtenerNombres();
    }
}