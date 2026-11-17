package com.example.proyecto.test;

import com.example.proyecto.database.EmpleadoDAO;
import com.example.proyecto.database.SolicitudAusenciaDAO;
import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.TipoAusencia;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudAusenciaDAOTest {

    private static SolicitudAusenciaDAO dao;


    @BeforeAll
    static void setUp() throws SQLException {
        dao = new SolicitudAusenciaDAO();
    }

    @AfterAll
    static void tearDown() {
        ArrayList<SolicitudAusencia> lista = dao.obtenerPorDni("34567890C");
        for (SolicitudAusencia s : lista) {
            if ("Solicitud de prueba JUnit".equals(s.getMotivo())) {
                dao.eliminar(s.getId());
            }
        }
    }

    @Test
    void obtenerTodos(){
        ArrayList<SolicitudAusencia> test = dao.obtenerTodos();
        assertNotNull(test);
        assertFalse(test.isEmpty());
    }

    @Test
    void obtenerPorId(){
        SolicitudAusencia test = dao.obtenerPorId(1);
        assertNotNull(test);
        assertNotNull(test.getEmpleado());
    }

    @Test
    void obtenerPorNombre(){
        SolicitudAusencia test = dao.obtenerPorNombre("Laura");
        assertNotNull(test);
        assertNotNull(test.getEmpleado().getNombre());
    }

    @Test
    void obtenerPorDni(){
        ArrayList<SolicitudAusencia> test = dao.obtenerPorDni("34567890C");
        assertNotNull(test);
        assertFalse(test.isEmpty());
    }

    @Test
    void obtenerPorEstado(){
      ArrayList<SolicitudAusencia> test = dao.obtenerPorEstado("PENDIENTE");
      assertNotNull(test);
        for (SolicitudAusencia s : test) {
            assertEquals("PENDIENTE", s.getEstado());
        }
    }

    @Test
    void insertar() {
        Empleado emp = new Empleado();
        emp.setDni("34567890C");
        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia nueva = new SolicitudAusencia(
                0L, emp, tipo,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5),
                0, "Solicitud de prueba JUnit",
                "PENDIENTE", null, LocalDate.now()
        );
        assertTrue(dao.insertar(nueva));

        ArrayList<SolicitudAusencia> lista = dao.obtenerPorDni("34567890C");
        lista.stream()
                .filter(s -> "Solicitud de prueba JUnit".equals(s.getMotivo()))
                .forEach(s -> dao.eliminar(s.getId()));
    }

    @Test
    void actualizar() {
        Empleado emp = new Empleado();
        emp.setDni("34567890C");
        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");
        SolicitudAusencia nueva = new SolicitudAusencia(
                0L, emp, tipo,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5),
                0, "Solicitud de prueba JUnit",
                "PENDIENTE", null, LocalDate.now()
        );
        dao.insertar(nueva);

        SolicitudAusencia s = dao.obtenerPorDni("34567890C").stream()
                .filter(sol -> "Solicitud de prueba JUnit".equals(sol.getMotivo()))
                .findFirst().orElse(null);
        assertNotNull(s);

        s.setComentarioAdmin("Actualizado en test");
        s.setEstado("APROBADA");
        assertTrue(dao.actualizar(s));

        SolicitudAusencia actualizada = dao.obtenerPorId(s.getId());
        assertEquals("APROBADA", actualizada.getEstado());

        dao.eliminar(s.getId());
    }

    @Test
    void eliminar() {
        Empleado emp = new Empleado();
        emp.setDni("34567890C");
        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");
        SolicitudAusencia nueva = new SolicitudAusencia(
                0L, emp, tipo,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5),
                0, "Solicitud de prueba JUnit",
                "PENDIENTE", null, LocalDate.now()
        );
        dao.insertar(nueva);

        SolicitudAusencia s = dao.obtenerPorDni("34567890C").stream()
                .filter(sol -> "Solicitud de prueba JUnit".equals(sol.getMotivo()))
                .findFirst().orElse(null);
        assertNotNull(s);

        assertTrue(dao.eliminar(s.getId()));
        assertNull(dao.obtenerPorId(s.getId()));
    }
}
