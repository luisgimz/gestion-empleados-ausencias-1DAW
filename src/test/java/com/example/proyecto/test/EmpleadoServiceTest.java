package com.example.proyecto.test;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.PuestoTrabajo;
import com.example.proyecto.service.EmpleadoService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmpleadoServiceTest {

    private static EmpleadoService service;

    private static final String DNI_TEST = "99999999Z";

    @BeforeAll
    static void setUp() throws Exception {
        service = new EmpleadoService();
    }

    @AfterAll
    static void tearDown() {
        service.eliminarEmpleadoCompleto(DNI_TEST);
    }

    private PuestoTrabajo puestoNormal() {
        PuestoTrabajo p = new PuestoTrabajo();
        p.setId(5L); // Programador Junior en BD
        return p;
    }


    @Test
    @Order(1)
    void insertarEmpleado_ok() {

        boolean ok = service.insertarEmpleado(
                DNI_TEST,
                "Test",
                "Empleado Uno",
                "test@empresa.com",
                "test@gmail.com",
                "612345678",
                "Valencia",
                "123456789012",
                LocalDate.of(1995, 1, 1),
                null,
                null,
                "ACTIVO",
                "USER",
                puestoNormal()
        );

        assertTrue(ok);

        Empleado emp = service.obtenerPorDni(DNI_TEST);
        assertNotNull(emp);
    }

    @Test
    @Order(2)
    void insertarEmpleado_dniDuplicado() {

        assertThrows(RuntimeException.class, () ->
                service.insertarEmpleado(
                        DNI_TEST,
                        "Otro",
                        "Empleado",
                        "otro@empresa.com",
                        null,
                        "612345678",
                        null,
                        null,
                        LocalDate.of(1995, 1, 1),
                        null,
                        null,
                        "ACTIVO",
                        "USER",
                        puestoNormal()
                )
        );
    }

    @Test
    @Order(3)
    void insertarEmpleado_dniInvalido() {

        assertThrows(IllegalArgumentException.class, () ->
                service.insertarEmpleado(
                        "1234",
                        "Juan",
                        "Perez",
                        "juan@empresa.com",
                        null,
                        "612345678",
                        null,
                        null,
                        LocalDate.of(1995, 1, 1),
                        null,
                        null,
                        "ACTIVO",
                        "USER",
                        puestoNormal()
                )
        );
    }

    @Test
    @Order(4)
    void insertarEmpleado_menorEdad() {

        assertThrows(IllegalArgumentException.class, () ->
                service.insertarEmpleado(
                        "88888888X",
                        "Pepe",
                        "Lopez",
                        "pepe@empresa.com",
                        null,
                        "612345678",
                        null,
                        null,
                        LocalDate.now().minusYears(10),
                        null,
                        null,
                        "ACTIVO",
                        "USER",
                        puestoNormal()
                )
        );
    }

    @Test
    @Order(5)
    void obtenerPorDni_ok() {

        Empleado emp = service.obtenerPorDni(DNI_TEST);

        assertNotNull(emp);
        assertEquals(DNI_TEST, emp.getDni());
    }

    @Test
    @Order(6)
    void actualizarEmpleado_ok() {

        Empleado emp = service.obtenerPorDni(DNI_TEST);

        emp.setTelefono("699999999");

        assertTrue(service.actualizarEmpleado(emp));
    }

    @Test
    @Order(7)
    void vacaciones_ok() {

        int dias = service.obtenerDiasVacaciones(DNI_TEST);

        service.actualizarVacacionesAprobadas(DNI_TEST, 1);

        int nuevo = service.obtenerDiasVacaciones(DNI_TEST);

        assertEquals(dias - 1, nuevo);
    }

    @Test
    @Order(8)
    void vacaciones_insuficientes() {

        assertThrows(IllegalArgumentException.class,
                () -> service.actualizarVacacionesAprobadas(DNI_TEST, 999));
    }

    @Test
    @Order(9)
    void actualizarPerfil_ok() {

        assertTrue(service.actualizarPerfil(
                DNI_TEST,
                "611223344",
                "Nueva dirección",
                "mail@ok.com"
        ));
    }

    @Test
    @Order(10)
    void actualizarFoto_ok() {

        assertTrue(service.actualizarFoto(
                DNI_TEST,
                new byte[]{1, 2, 3}
        ));
    }

    @Test
    @Order(11)
    void eliminarEmpleado() {

        assertTrue(service.eliminarEmpleadoCompleto(DNI_TEST));

        assertNull(service.obtenerPorDni(DNI_TEST));
    }
}