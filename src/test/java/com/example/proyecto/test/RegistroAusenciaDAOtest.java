package com.example.proyecto.test;

import com.example.proyecto.database.RegistroAusenciaDAO;
import com.example.proyecto.model.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RegistroAusenciaDAOTest {

    private static RegistroAusenciaDAO dao;
    private static final String DNI_TEST = "34567890C";

    @BeforeAll
    static void setUp() throws SQLException {
        dao = new RegistroAusenciaDAO();
    }

    @AfterAll
    static void tearDown() {
        ArrayList<RegistroAusencia> lista = dao.obtenerTodos();
        for (RegistroAusencia r : lista) {
            if ("Comentario de prueba JUnit".equals(r.getComentarioAdmin())) {
                dao.eliminar(r.getId());
            }
        }
    }

    private RegistroAusencia crearRegistroPrueba() {
        Empleado emp = new Empleado();
        emp.setDni(DNI_TEST);

        Empleado admin = new Empleado();
        admin.setDni("12345678A");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia sol = new SolicitudAusencia(
                1L, emp, tipo,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5),
                5, "Solicitud de prueba JUnit",
                "APROBADA", null, LocalDate.now()
        );

        return new RegistroAusencia(
                0L, emp, admin, sol, tipo,
                LocalDateTime.now(),
                "Comentario de prueba JUnit"
        );
    }

    @Test
    void obtenerTodos() {
        ArrayList<RegistroAusencia> lista = dao.obtenerTodos();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    void obtenerPorId() {
        RegistroAusencia r = dao.obtenerPorId(1);
        assertNotNull(r);
        assertNotNull(r.getEmpleadoSolicitud());
        assertNotNull(r.getSolicitud());
    }

    @Test
    void obtenerPorNombre() {
        RegistroAusencia r = dao.obtenerPorNombre("Danny");
        assertNotNull(r);
        assertNotNull(r.getEmpleadoSolicitud().getNombre());
    }

    @Test
    void obtenerPorSolicitudId() {
        ArrayList<RegistroAusencia> lista = dao.obtenerPorSolicitudId(1);
        assertNotNull(lista);
        for (RegistroAusencia r : lista) {
            assertEquals(1L, r.getSolicitud().getId());
        }
    }

    @Test
    void insertar() {
        RegistroAusencia nuevo = crearRegistroPrueba();
        assertTrue(dao.insertar(nuevo));

        dao.obtenerTodos().stream()
                .filter(r -> "Comentario de prueba JUnit.".equals(r.getComentarioAdmin()))
                .forEach(r -> dao.eliminar(r.getId()));
    }

    @Test
    void actualizar() {
        RegistroAusencia nuevo = crearRegistroPrueba();
        dao.insertar(nuevo);

        RegistroAusencia insertado = dao.obtenerTodos().stream()
                .filter(r -> "Comentario de prueba JUnit".equals(r.getComentarioAdmin()))
                .findFirst().orElse(null);
        assertNotNull(insertado);

        insertado.setComentarioAdmin("Comentario actualizado en test");
        assertTrue(dao.actualizar(insertado));

        RegistroAusencia actualizado = dao.obtenerPorId(insertado.getId());
        assertEquals("Comentario actualizado en test", actualizado.getComentarioAdmin());

        dao.eliminar(insertado.getId());
    }

    @Test
    void eliminar() {
        RegistroAusencia nuevo = crearRegistroPrueba();
        dao.insertar(nuevo);

        RegistroAusencia insertado = dao.obtenerTodos().stream()
                .filter(r -> "Comentario de prueba JUnit".equals(r.getComentarioAdmin()))
                .findFirst().orElse(null);
        assertNotNull(insertado);

        assertTrue(dao.eliminar(insertado.getId()));
        assertNull(dao.obtenerPorId(insertado.getId()));
    }

    @Test
    void obtenerAusentesProximos7Dias() {
        ArrayList<RegistroAusencia> lista = dao.obtenerAusentesProximos7Dias();
        assertNotNull(lista);
        for (RegistroAusencia r : lista) {
            assertNotNull(r.getEmpleadoSolicitud());
            assertNotNull(r.getSolicitud());
            assertEquals("APROBADA", r.getSolicitud().getEstado());
        }
    }
}