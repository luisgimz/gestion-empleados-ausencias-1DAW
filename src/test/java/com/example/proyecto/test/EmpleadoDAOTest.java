package com.example.proyecto.test;

import com.example.proyecto.database.EmpleadoDAO;
import com.example.proyecto.model.CategoriaLaboral;
import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.PuestoTrabajo;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmpleadoDAOTest {

    private static EmpleadoDAO dao;
    private static final String DNI_TEST = "99999999Z";

    @BeforeAll
    static void setUp() throws SQLException {
        dao = new EmpleadoDAO();
    }

    @AfterAll
    static void tearDown() {
        dao.eliminarPorDni(DNI_TEST);
    }

    private Empleado crearEmpleadoTest() {
        PuestoTrabajo puesto = new PuestoTrabajo();
        puesto.setId(5L);

        CategoriaLaboral categoria = new CategoriaLaboral();
        categoria.setId(1L);

        return new Empleado(
                "Carlos",
                "Pérez López",
                DNI_TEST,
                puesto,
                categoria,
                "carlos.test@empresa.com",
                "carlos.test@gmail.com",
                "612345678",
                "Calle Falsa 123, Valencia",
                "28/12345678/01",
                LocalDate.of(1990, 5, 15),
                LocalDate.now(),
                null,
                "Activo"
        );
    }

    @Test
    @Order(1)
    void insertar() {
        assertTrue(dao.insertar(crearEmpleadoTest()));

        Empleado db = dao.obtenerPorDni(DNI_TEST);
        assertNotNull(db);
        assertEquals(DNI_TEST, db.getDni());
    }

    @Test
    @Order(2)
    void obtenerPorDni() {
        Empleado emp = dao.obtenerPorDni(DNI_TEST);
        assertNotNull(emp);
        assertEquals(DNI_TEST, emp.getDni());
    }

    @Test
    @Order(3)
    void actualizar() {
        Empleado emp = dao.obtenerPorDni(DNI_TEST);

        emp.setTelefono("699999999");
        emp.setEstadoLaboral("Teletrabajo");

        assertTrue(dao.actualizar(emp));

        Empleado actualizado = dao.obtenerPorDni(DNI_TEST);
        assertEquals("699999999", actualizado.getTelefono());
        assertEquals("Teletrabajo", actualizado.getEstadoLaboral());
    }

    @Test
    @Order(4)
    void obtenerDiasDisponibles() {
        int dias = dao.obtenerDiasDisponiblesPorDni(DNI_TEST);
        assertTrue(dias >= 0);
    }

    @Test
    @Order(5)
    void actualizarDiasVacaciones() {
        int original = dao.obtenerDiasDisponiblesPorDni(DNI_TEST);

        assertTrue(dao.actualizarDiasVacacionesDisponibles(DNI_TEST, original - 1));

        int actualizado = dao.obtenerDiasDisponiblesPorDni(DNI_TEST);
        assertEquals(original - 1, actualizado);

        dao.actualizarDiasVacacionesDisponibles(DNI_TEST, original);
    }

    @Test
    @Order(6)
    void obtenerTodos() {
        assertNotNull(dao.obtenerTodos());
        assertTrue(dao.obtenerTodos().size() > 0);
    }

    @Test
    @Order(7)
    void obtenerPorNombre() {
        Empleado emp = dao.obtenerPorNombre("Carlos");

        assertNotNull(emp);
        assertEquals("Carlos", emp.getNombre());
    }

    @Test
    @Order(8)
    void actualizarFoto() {
        byte[] foto = new byte[]{1, 2, 3};

        assertTrue(dao.actualizarFoto(DNI_TEST, foto));
    }

    @Test
    @Order(9)
    void actualizarPerfil() {

        assertTrue(dao.actualizarPerfil(
                DNI_TEST,
                "600111222",
                "Nueva dirección",
                "nuevo@mail.com"
        ));

        Empleado emp = dao.obtenerPorDni(DNI_TEST);

        assertEquals("600111222", emp.getTelefono());
        assertEquals("Nueva dirección", emp.getDireccion());
        assertEquals("nuevo@mail.com", emp.getEmailPersonal());
    }

    @Test
    @Order(10)
    void actualizarPuesto() {

        assertTrue(dao.actualizarPuesto(DNI_TEST, 1L));

        Empleado emp = dao.obtenerPorDni(DNI_TEST);

        assertNotNull(emp.getPuestoActual());
    }

    @Test
    @Order(11)
    void eliminar() {
        assertTrue(dao.eliminarPorDni(DNI_TEST));
        assertNull(dao.obtenerPorDni(DNI_TEST));
    }

    @Test
    @Order(12)
    void eliminarCompleto() {

        dao.insertar(crearEmpleadoTest());

        assertTrue(dao.eliminarCompleto(DNI_TEST));
        assertNull(dao.obtenerPorDni(DNI_TEST));
    }
}