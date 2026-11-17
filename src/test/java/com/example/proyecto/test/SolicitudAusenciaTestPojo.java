package com.example.proyecto.test;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.TipoAusencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudAusenciaTestPojo {

    private SolicitudAusencia solicitud;
    private Empleado empleado;
    private TipoAusencia tipo;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setNombre("Ana");
        empleado.setApellidos("Torres Díaz");
        empleado.setDni("34567890C");

        tipo = new TipoAusencia(1L, "Vacaciones");

        solicitud = new SolicitudAusencia(
                1L,
                empleado,
                tipo,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                11,
                "Vacaciones de verano",
                "PENDIENTE",
                null,
                LocalDate.now()
        );
    }

    @Test
    void getEmpleado() {
        assertEquals("34567890C", solicitud.getEmpleado().getDni());
    }

    @Test
    void setEmpleado() {
        Empleado nuevo = new Empleado();
        nuevo.setDni("12345678A");
        solicitud.setEmpleado(nuevo);
        assertEquals("12345678A", solicitud.getEmpleado().getDni());
    }

    @Test
    void getTipoAusencia() {
        assertEquals("Vacaciones", solicitud.getTipoAusencia().getNombre());
    }

    @Test
    void setTipoAusencia() {
        TipoAusencia nuevo = new TipoAusencia(2L, "Enfermedad");
        solicitud.setTipoAusencia(nuevo);
        assertEquals("Enfermedad", solicitud.getTipoAusencia().getNombre());
    }

    @Test
    void getFechaInicio() {
        assertEquals(LocalDate.of(2026, 7, 1), solicitud.getFechaInicio());
    }

    @Test
    void setFechaInicio() {
        solicitud.setFechaInicio(LocalDate.of(2026, 8, 1));
        assertEquals(LocalDate.of(2026, 8, 1), solicitud.getFechaInicio());
    }

    @Test
    void getFechaFin() {
        assertEquals(LocalDate.of(2026, 7, 15), solicitud.getFechaFin());
    }

    @Test
    void setFechaFin() {
        solicitud.setFechaFin(LocalDate.of(2026, 8, 15));
        assertEquals(LocalDate.of(2026, 8, 15), solicitud.getFechaFin());
    }

    @Test
    void getDiasSolicitados() {
        assertEquals(11, solicitud.getDiasSolicitados());
    }

    @Test
    void setDiasSolicitados() {
        solicitud.setDiasSolicitados(5);
        assertEquals(5, solicitud.getDiasSolicitados());
    }

    @Test
    void getMotivo() {
        assertEquals("Vacaciones de verano", solicitud.getMotivo());
    }

    @Test
    void setMotivo() {
        solicitud.setMotivo("Descanso");
        assertEquals("Descanso", solicitud.getMotivo());
    }

    @Test
    void getEstado() {
        assertEquals("PENDIENTE", solicitud.getEstado());
    }

    @Test
    void setEstado() {
        solicitud.setEstado("APROBADA");
        assertEquals("APROBADA", solicitud.getEstado());
    }

    @Test
    void getComentarioAdmin() {
        assertNull(solicitud.getComentarioAdmin());
    }

    @Test
    void setComentarioAdmin() {
        solicitud.setComentarioAdmin("Aprobado sin incidencias");
        assertEquals("Aprobado sin incidencias", solicitud.getComentarioAdmin());
    }

    @Test
    void getFechaSolicitud() {
        assertNotNull(solicitud.getFechaSolicitud());
    }

    @Test
    void setFechaSolicitud() {
        solicitud.setFechaSolicitud(LocalDate.of(2026, 6, 1));
        assertEquals(LocalDate.of(2026, 6, 1), solicitud.getFechaSolicitud());
    }

    @Test
    void testEquals() {
        SolicitudAusencia otra = new SolicitudAusencia();
        otra.setId(1L);
        assertEquals(solicitud, otra);
    }

    @Test
    void testNotEquals() {
        SolicitudAusencia otra = new SolicitudAusencia();
        otra.setId(99L);
        assertNotEquals(solicitud, otra);
    }

    @Test
    void testToString() {
        String resultado = solicitud.toString();
        assertNotNull(resultado);
        assertTrue(resultado.contains("PENDIENTE"));
        assertTrue(resultado.contains("Vacaciones"));
    }
}