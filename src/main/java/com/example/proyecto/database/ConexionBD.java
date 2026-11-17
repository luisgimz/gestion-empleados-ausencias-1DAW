package com.example.proyecto.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static ConexionBD instancia;

    private Connection conexion;

    /*
    URL para conectar con la BBDD de AWS

    user: betauser
    password: BetaUser123!

    private static final String URL = "jdbc:mysql://100.54.147.117:3306/BD_grupo_beta";
     */
    private static final String URL = "jdbc:mysql://localhost:3306/BD_grupo_beta";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root";

    private ConexionBD() {}

    public static ConexionBD getInstance() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Devuelve una única conexión reutilizable.
     * Si no existe o está cerrada, la crea.
     */
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        }
        return conexion;
    }

    /**
     * Cierra la única conexión al final del programa
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println(" Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}