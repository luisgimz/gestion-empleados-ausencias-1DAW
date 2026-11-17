package com.example.proyecto.test;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.RegistroAusencia;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.TipoAusencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegistroAusenciaTestPojo {

    private RegistroAusencia registro;
    private Empleado empleado;
    private Empleado admin;
    private SolicitudAusencia solicitud;
    private TipoAusencia tipo;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setNombre("Ana");
        empleado.setApellidos("Torres Díaz");
        empleado.setDni("34567890C");

        admin = new Empleado();
        admin.setNombre("Laura");
        admin.setApellidos("Martínez Ruiz");
        admin.setDni("12345678A");

        tipo = new TipoAusencia(1L, "Vacaciones");

        solicitud = new SolicitudAusencia();
        solicitud.setId(1L);
        solicitud.setEmpleado(empleado);
        solicitud.setTipoAusencia(tipo);
        solicitud.setFechaInicio(LocalDate.of(2026, 7, 1));
        solicitud.setFechaFin(LocalDate.of(2026, 7, 15));
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(LocalDate.now());

        registro = new RegistroAusencia(
                1L,
                empleado,
                admin,
                solicitud,
                tipo,
                LocalDateTime.of(2026, 5, 11, 9, 15),
                "Aprobado sin incidencias"
        );
    }

    @Test
    void getEmpleadoSolicitud() {
        assertEquals("34567890C", registro.getEmpleadoSolicitud().getDni());
    }

    @Test
    void setEmpleadoSolicitud() {
        Empleado nuevo = new Empleado();
        nuevo.setDni("99999999Z");
        registro.setEmpleadoSolicitud(nuevo);
        assertEquals("99999999Z", registro.getEmpleadoSolicitud().getDni());
    }

    @Test
    void getAdmin() {
        assertEquals("12345678A", registro.getAdmin().getDni());
    }

    @Test
    void setAdmin() {
        Empleado nuevoAdmin = new Empleado();
        nuevoAdmin.setDni("11111111B");
        registro.setAdmin(nuevoAdmin);
        assertEquals("11111111B", registro.getAdmin().getDni());
    }

    @Test
    void getSolicitud() {
        assertEquals(1L, registro.getSolicitud().getId());
    }

    @Test
    void setSolicitud() {
        SolicitudAusencia nueva = new SolicitudAusencia();
        nueva.setId(99L);
        registro.setSolicitud(nueva);
        assertEquals(99L, registro.getSolicitud().getId());
    }

    @Test
    void getTipoSolicitud() {
        assertEquals("Vacaciones", registro.getTipoSolicitud().getNombre());
    }

    @Test
    void setTipoSolicitud() {
        TipoAusencia nuevo = new TipoAusencia(2L, "Enfermedad");
        registro.setTipoSolicitud(nuevo);
        assertEquals("Enfermedad", registro.getTipoSolicitud().getNombre());
    }

    @Test
    void getFechaRegistro() {
        assertEquals(LocalDateTime.of(2026, 5, 11, 9, 15), registro.getFechaRegistro());
    }

    @Test
    void setFechaRegistro() {
        LocalDateTime nueva = LocalDateTime.of(2026, 6, 1, 10, 0);
        registro.setFechaRegistro(nueva);
        assertEquals(nueva, registro.getFechaRegistro());
    }

    @Test
    void getComentarioAdmin() {
        assertEquals("Aprobado sin incidencias", registro.getComentarioAdmin());
    }

    @Test
    void setComentarioAdmin() {
        registro.setComentarioAdmin("Rechazado por falta de cobertura");
        assertEquals("Rechazado por falta de cobertura", registro.getComentarioAdmin());
    }

    @Test
    void testEquals() {
        RegistroAusencia otro = new RegistroAusencia();
        otro.setId(1L);
        assertEquals(registro, otro);
    }

    @Test
    void testNotEquals() {
        RegistroAusencia otro = new RegistroAusencia();
        otro.setId(99L);
        assertNotEquals(registro, otro);
    }

    @Test
    void testToString() {
        String resultado = registro.toString();
        assertNotNull(resultado);
        assertTrue(resultado.contains("Aprobado sin incidencias"));
        assertTrue(resultado.contains("Vacaciones"));
    }
}