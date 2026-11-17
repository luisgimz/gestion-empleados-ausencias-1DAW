package com.example.proyecto.test;

import com.example.proyecto.database.RegistroAusenciaDAO;
import com.example.proyecto.model.*;
import com.example.proyecto.service.RegistroAusenciaService;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RegistroAusenciaServiceTest {

    private static RegistroAusenciaService service;

    @BeforeAll
    static void setUp() throws SQLException {
        service = new RegistroAusenciaService();
    }

    @AfterAll
    static void tearDown() {
        // Limpieza por motivo de test si existiera en BD
        RegistroAusenciaDAO dao;
        try {
            dao = new RegistroAusenciaDAO();
            ArrayList<RegistroAusencia> lista = dao.obtenerTodos();

            for (RegistroAusencia r : lista) {
                if (r.getSolicitud() != null &&
                        "TEST_JUNIT".equals(r.getSolicitud().getMotivo())) {
                    dao.eliminar(r.getId());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void obtenerTodos() {
        ArrayList<RegistroAusencia> lista = service.obtenerTodos();
        assertNotNull(lista);
    }

    @Test
    void obtenerPorId() {
        ArrayList<RegistroAusencia> lista = service.obtenerTodos();

        if (!lista.isEmpty()) {
            RegistroAusencia r = service.obtenerPorId(lista.get(0).getId());
            assertNotNull(r);
            assertNotNull(r.getEmpleadoSolicitud());
        }
    }

    @Test
    void obtenerPorSolicitud() {
        ArrayList<RegistroAusencia> lista = service.obtenerTodos();

        if (!lista.isEmpty()) {
            long solId = lista.get(0).getSolicitud().getId();

            ArrayList<RegistroAusencia> filtrados =
                    service.obtenerPorSolicitud(solId);

            assertNotNull(filtrados);
        }
    }

    @Test
    void insertarRegistro() {

        Empleado emp = new Empleado();
        emp.setDni("34567890C");

        Empleado admin = new Empleado();
        admin.setDni("12345678A");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia sol = new SolicitudAusencia();
        sol.setId(1L);

        RegistroAusencia r = new RegistroAusencia(
                0L,
                emp,
                admin,
                sol,
                tipo,
                LocalDateTime.now(),
                "TEST_JUNIT"
        );

        assertTrue(service.insertarRegistro(r));
    }

    @Test
    void insertarRegistro_nulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.insertarRegistro(null);
        });
    }

    @Test
    void validar_empleado_obligatorio() {

        RegistroAusencia r = new RegistroAusencia(
                0L,
                null,
                new Empleado(),
                new SolicitudAusencia(),
                new TipoAusencia(1L, "Vacaciones"),
                LocalDateTime.now(),
                "TEST_JUNIT"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            service.insertarRegistro(r);
        });
    }

    @Test
    void validar_solicitud_invalida() {

        Empleado emp = new Empleado();
        emp.setDni("34567890C");

        RegistroAusencia r = new RegistroAusencia(
                0L,
                emp,
                emp,
                null,
                new TipoAusencia(1L, "Vacaciones"),
                LocalDateTime.now(),
                "TEST_JUNIT"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            service.insertarRegistro(r);
        });
    }
}