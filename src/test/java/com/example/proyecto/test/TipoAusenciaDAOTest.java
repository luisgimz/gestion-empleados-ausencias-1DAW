package com.example.proyecto.test;

import com.example.proyecto.database.TipoAusenciaDAO;
import com.example.proyecto.model.TipoAusencia;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TipoAusenciaDAOTest {

    private static TipoAusenciaDAO dao;
    private static Long idTest;

    @BeforeAll
    static void setUp() throws SQLException {
        dao = new TipoAusenciaDAO();
    }

    @Test
    @Order(1)
    void obtenerTodos() {
        var lista = dao.obtenerTodos();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    @Order(2)
    void obtenerPorNombre_existente() {
        TipoAusencia t = dao.obtenerPorNombre("Vacaciones");
        assertNotNull(t);
        assertEquals("Vacaciones", t.getNombre());
        idTest = t.getId();
    }

    @Test
    @Order(3)
    void obtenerPorId() {
        TipoAusencia t = dao.obtenerPorId(idTest);
        assertNotNull(t);
        assertEquals(idTest, t.getId());
    }

    @Test
    @Order(4)
    void insertar() {
        TipoAusencia t = new TipoAusencia();
        t.setNombre("TIPO_TEST");

        boolean ok = dao.insertar(t);
        assertTrue(ok);
        TipoAusencia guardado = dao.obtenerPorNombre("TIPO_TEST");
        assertNotNull(guardado);
        idTest = guardado.getId();
    }

    @Test
    @Order(5)
    void actualizar() {
        TipoAusencia t = dao.obtenerPorId(idTest);
        t.setNombre("TIPO_TEST_MOD");
        assertTrue(dao.actualizar(t));
        TipoAusencia actualizado = dao.obtenerPorId(idTest);
        assertEquals("TIPO_TEST_MOD", actualizado.getNombre());
    }

    @Test
    @Order(6)
    void obtenerNombres() {
        var nombres = dao.obtenerNombres();
        assertTrue(nombres.contains("Vacaciones"));
        assertTrue(nombres.contains("TIPO_TEST_MOD"));
    }

    @Test
    @Order(7)
    void eliminar() {
        assertTrue(dao.eliminar(idTest));
        assertNull(dao.obtenerPorId(idTest));
    }

    @Test
    @Order(8)
    void obtenerPorId_noExiste() {
        assertNull(dao.obtenerPorId(999999L));
    }
}