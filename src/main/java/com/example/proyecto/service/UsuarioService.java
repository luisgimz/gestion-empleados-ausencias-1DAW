package com.example.proyecto.service;

import com.example.proyecto.database.UsuarioDAO;
import com.example.proyecto.model.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() throws SQLException {
        this.usuarioDAO = new UsuarioDAO();
    }

    public ArrayList<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario obtenerPorId(long id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        return usuarioDAO.obtenerPorId(id);
    }

    public Usuario obtenerPorUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username obligatorio");
        }

        return usuarioDAO.obtenerPorNombre(username.trim());
    }

    public String obtenerDepartamento(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username obligatorio");
        }

        return usuarioDAO.obtenerDepartamentoPorUsername(username.trim());
    }

    public boolean actualizarUsuario(Usuario u) {

        if (u == null) {
            throw new IllegalArgumentException("Usuario nulo");
        }

        if (u.getUsername() == null || u.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username obligatorio");
        }

        if (u.getPasswordHash() == null || u.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Password obligatoria");
        }

        return usuarioDAO.actualizar(u);
    }

    public boolean eliminarUsuario(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username obligatorio");
        }

        return usuarioDAO.eliminarPorUsername(username.trim());
    }

    public boolean actualizarActivoUsuario(String username, boolean activo) {
        return usuarioDAO.actualizarActivoUsuario(username, activo);
    }

    public boolean reiniciarVacacionesTodos() {
        return usuarioDAO.reiniciarVacacionesTodos();
    }

    public ArrayList<Usuario> obtenerTodosConEmpleados() {
        return usuarioDAO.obtenerTodosConEmpleados();
    }

    public boolean cambiarPassword(String username, String nuevaPassword) {
        return usuarioDAO.cambiarPassword(username, nuevaPassword);
    }

}