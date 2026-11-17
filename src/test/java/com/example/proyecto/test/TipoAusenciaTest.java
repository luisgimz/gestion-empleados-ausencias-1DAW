package com.example.proyecto.test;

import com.example.proyecto.model.TipoAusencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoAusenciaTest {

    @Test
    void getId() {
        TipoAusencia t = new TipoAusencia(1L, "Vacaciones");
        assertEquals(1L, t.getId());
    }

    @Test
    void setId() {
        TipoAusencia t = new TipoAusencia();
        t.setId(2L);
        assertEquals(2L, t.getId());
    }

    @Test
    void getNombre() {
        TipoAusencia t = new TipoAusencia(1L, "Baja");
        assertEquals("Baja", t.getNombre());
    }

    @Test
    void setNombre() {
        TipoAusencia t = new TipoAusencia();
        t.setNombre("Vacaciones");
        assertEquals("Vacaciones", t.getNombre());
    }

    @Test
    void testToString() {
        TipoAusencia t = new TipoAusencia(1L, "Vacaciones");
        String result = t.toString();
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Vacaciones"));
    }

    @Test
    void testEquals() {
        TipoAusencia t1 = new TipoAusencia(1L, "Vacaciones");
        TipoAusencia t2 = new TipoAusencia(1L, "Baja"); // mismo ID
        assertEquals(t1, t2);
    }

    @Test
    void testHashCode() {
        TipoAusencia t1 = new TipoAusencia(1L, "Vacaciones");
        TipoAusencia t2 = new TipoAusencia(1L, "Baja");
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}