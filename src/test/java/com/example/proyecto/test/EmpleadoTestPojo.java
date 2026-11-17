package com.example.proyecto.test;

import com.example.proyecto.model.CategoriaLaboral;
import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.PuestoTrabajo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoTestPojo {

    private Empleado emp;

    @BeforeEach
    void setUp() {
        emp = new Empleado(
                "Carlos", "Pérez López", "99999999Z",
                null, null,
                "carlos@empresa.com",
                "carlos@gmail.com",
                "612345678",
                "Calle Falsa 123, Valencia",
                "28/12345678/01",
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2023, 1, 10),
                null,
                "Activo"
        );
    }

    @Test
    void getSetPuestoActual() {
        PuestoTrabajo puesto = new PuestoTrabajo();
        puesto.setId(1L);
        emp.setPuestoActual(puesto);
        assertEquals(1L, emp.getPuestoActual().getId());
    }

    @Test
    void getSetCategoriaReal() {
        CategoriaLaboral categoria = new CategoriaLaboral();
        categoria.setId(1L);
        emp.setCategoriaReal(categoria);
        assertEquals(1L, emp.getCategoriaReal().getId());
    }

    @Test
    void getSetEmailCorporativo() {
        emp.setEmailCorporativo("nuevo@empresa.com");
        assertEquals("nuevo@empresa.com", emp.getEmailCorporativo());
    }

    @Test
    void getSetEmailPersonal() {
        emp.setEmailPersonal("nuevo@gmail.com");
        assertEquals("nuevo@gmail.com", emp.getEmailPersonal());
    }

    @Test
    void getSetTelefono() {
        emp.setTelefono("699999999");
        assertEquals("699999999", emp.getTelefono());
    }

    @Test
    void getSetDireccion() {
        emp.setDireccion("Avenida del Puerto 7");
        assertEquals("Avenida del Puerto 7", emp.getDireccion());
    }

    @Test
    void getSetNumeroSs() {
        emp.setNumeroSs("28/99999999/99");
        assertEquals("28/99999999/99", emp.getNumeroSs());
    }

    @Test
    void getSetFechaNacimiento() {
        emp.setFechaNacimiento(LocalDate.of(1991, 3, 20));
        assertEquals(LocalDate.of(1991, 3, 20), emp.getFechaNacimiento());
    }

    @Test
    void getSetFechaAlta() {
        emp.setFechaAlta(LocalDate.of(2024, 6, 1));
        assertEquals(LocalDate.of(2024, 6, 1), emp.getFechaAlta());
    }

    @Test
    void getSetFechaBaja() {
        emp.setFechaBaja(LocalDate.of(2025, 12, 31));
        assertEquals(LocalDate.of(2025, 12, 31), emp.getFechaBaja());
    }

    @Test
    void getSetEstadoLaboral() {
        emp.setEstadoLaboral("Teletrabajo");
        assertEquals("Teletrabajo", emp.getEstadoLaboral());
    }

    @Test
    void getSetDiasVacacionesDisponibles() {
        emp.setDiasVacacionesDisponibles(10);
        assertEquals(10, emp.getDiasVacacionesDisponibles());
    }

    @Test
    void getSetFoto() {
        byte[] foto = new byte[]{1, 2, 3};
        emp.setFoto(foto);
        assertArrayEquals(foto, emp.getFoto());
    }

    @Test
    void testToString() {
        String result = emp.toString();
        assertNotNull(result);
        assertTrue(result.contains("99999999Z"));
        assertTrue(result.contains("Carlos"));
    }
}