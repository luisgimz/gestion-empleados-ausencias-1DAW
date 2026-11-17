package com.example.proyecto.controller;

import com.example.proyecto.model.Usuario;

public class SesionManager {
    private static Usuario usuario = null;

    public static void login(Usuario usuario) {
        SesionManager.usuario = usuario;
    }

    public static void logout() {
        SesionManager.usuario = null;
    }

    public static Usuario getUsuario() {
        return SesionManager.usuario;
    }

    public static boolean haySesion() {
        return usuario != null;
    }
}