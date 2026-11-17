package com.example.proyecto.test;

import com.example.proyecto.service.UsuarioService;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioServiceTest {

    private static UsuarioService service;

    private static final String USER_REAL = "danny.rios";

    @BeforeAll
    static void setUp() throws SQLException {
        service = new UsuarioService();
    }

    @Test
    @Order(1)
    void obtenerTodos() {
        assertFalse(service.obtenerTodos().isEmpty());
    }

    @Test
    @Order(2)
    void obtenerPorId_invalido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.obtenerPorId(0));
    }

    @Test
    @Order(3)
    void obtenerPorUsername_invalido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.obtenerPorUsername(" "));
    }

    @Test
    @Order(4)
    void obtenerDepartamento_invalido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.obtenerDepartamento(null));
    }

    @Test
    @Order(5)
    void actualizarUsuario_null() {
        assertThrows(IllegalArgumentException.class,
                () -> service.actualizarUsuario(null));
    }

    @Test
    @Order(6)
    void eliminarUsuario_invalido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.eliminarUsuario(""));
    }

    @Test
    @Order(7)
    void actualizarActivoUsuario() {
        boolean ok = service.actualizarActivoUsuario(USER_REAL, true);
        assertTrue(ok);
    }

    @Test
    @Order(8)
    void reiniciarVacacionesTodos() {
        assertTrue(service.reiniciarVacacionesTodos());
    }

    @Test
    @Order(9)
    void obtenerTodosConEmpleados() {
        assertFalse(service.obtenerTodosConEmpleados().isEmpty());
    }

    @Test
    @Order(10)
    void cambiarPassword() {
        boolean ok = service.cambiarPassword(USER_REAL, "123456");
        assertTrue(ok);
    }
}