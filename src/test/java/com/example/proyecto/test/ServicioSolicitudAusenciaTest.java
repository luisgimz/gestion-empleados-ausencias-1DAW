package com.example.proyecto.test;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.TipoAusencia;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.SolicitudAusenciaService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudAusenciaServiceTest {

    private static SolicitudAusenciaService service;

    @BeforeAll
    static void setUp() throws SQLException {
        service = new SolicitudAusenciaService();
    }

    @Test
    void registrarSolicitud_conDatasValidas_exitoso() {
        Empleado emp = new Empleado();
        emp.setDni("12345678A");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia solicitud = new SolicitudAusencia(
                0L, emp, tipo,
                LocalDate.of(2026, 2, 15),
                LocalDate.of(2026, 2, 25),
                0, "Test servicio JUnit " + System.currentTimeMillis(),
                "PENDIENTE", null, LocalDate.now()
        );

        boolean resultado = service.registrarSolicitud(solicitud);
        assertTrue(resultado);

        try {
            service.cancelarSolicitud(solicitud);
        } catch (Exception e) {
        }
    }

    @Test
    void registrarSolicitud_conFechasInvalidas_lanzaExcepcion() {
        Empleado emp = new Empleado();
        emp.setDni("11223344F");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia solicitud = new SolicitudAusencia(
                0L, emp, tipo,
                LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 10, 1),
                0, "Test", "PENDIENTE", null, LocalDate.now()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            service.registrarSolicitud(solicitud);
        });
    }

    @Test
    void registrarSolicitud_conFechaNula_lanzaExcepcion() {
        Empleado emp = new Empleado();
        emp.setDni("34567890C");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia solicitud = new SolicitudAusencia(
                0L, emp, tipo,
                null,
                LocalDate.of(2026, 10, 10),
                0, "Test", "PENDIENTE", null, LocalDate.now()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            service.registrarSolicitud(solicitud);
        });
    }

    @Test
    void obtenerPendientes() {
        var pendientes = service.obtenerPendientes();
        assertNotNull(pendientes);
        for (SolicitudAusencia s : pendientes) {
            assertEquals("PENDIENTE", s.getEstado());
        }
    }

    @Test
    void obtenerPorDni() {
        var solicitudes = service.obtenerPorDni("34567890C");
        assertNotNull(solicitudes);
        for (SolicitudAusencia s : solicitudes) {
            assertEquals("34567890C", s.getEmpleado().getDni());
        }
    }

    @Test
    void obtenerDiasDisponibles() {
        int dias = service.obtenerDiasDisponibles("12345678A");
        assertTrue(dias >= 0);
    }
}