package com.example.proyecto.service;

import com.example.proyecto.database.*;
import com.example.proyecto.model.PuestoTrabajo;

import java.sql.SQLException;
import java.util.ArrayList;

public class PuestoTrabajoService {

    private final PuestoTrabajoDAO puestoTrabajoDAO;

    public PuestoTrabajoService() throws SQLException {
        puestoTrabajoDAO = new PuestoTrabajoDAO();
    }

    public ArrayList<PuestoTrabajo> obtenerTodos() {
        return puestoTrabajoDAO.obtenerTodos();
    }

    public PuestoTrabajo obtenerPorId(long id) {
        return puestoTrabajoDAO.obtenerPorId(id);
    }
}