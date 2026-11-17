package com.example.proyecto.test;
import com.example.proyecto.database.EmpleadoDAO;
import com.example.proyecto.database.SolicitudAusenciaDAO;
import com.example.proyecto.database.TipoAusenciaDAO;
import com.example.proyecto.model.*;
import com.example.proyecto.database.RegistroAusenciaDAO;
import com.example.proyecto.model.RegistroAusencia;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestBasico {
    public static void main(String[] args) throws SQLException {

        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        TipoAusenciaDAO tipoAusenciaDAO = new TipoAusenciaDAO();
        SolicitudAusenciaDAO solicitudDAO = new SolicitudAusenciaDAO();
        RegistroAusenciaDAO registroDAO = new RegistroAusenciaDAO();

        /*Test empleado  */
        // testEmpleadoPojo();
        // testEmpleadoDao(empleadoDAO);

        /*Test tipo ausencia*/
        // testTipoAusenciaPojo();
        // testTipoAusencia(tipoAusenciaDAO);

        /*Test Solicitud ausencia*/
        /*    testSolicitudAusencia(solicitudDAO);*/

        /*Test Registro ausencia
        testRegistroAusencia(registroDAO); */

//        System.out.println("\n=== TEST SOLAPAMIENTO ===");
//        SolicitudAusenciaDAO dao = new SolicitudAusenciaDAO();
//        boolean solapa = dao.existeSolapamiento("12345678A",
//                LocalDate.of(2026, 6, 11),
//                LocalDate.of(2026, 6, 18),
//                0L);
//        System.out.println("Solapamiento detectado (esperado true): " + solapa);
//
//        boolean noSolapa = dao.existeSolapamiento("34567890C",
//                LocalDate.of(2026, 8, 20),
//                LocalDate.of(2026, 8, 25),
//                0L);
//        System.out.println("Sin solapamiento (esperado true): " + !noSolapa);

    }

    private static void testEmpleadoDao(EmpleadoDAO dao) {

        System.out.println("=== OBTENER TODOS ===");
        ArrayList<Empleado> todos = dao.obtenerTodos();
        todos.forEach(System.out::println);

        System.out.println("\n=== OBTENER POR ID '1' ===");
        System.out.println(dao.obtenerPorId(1));

        System.out.println("\n=== OBTENER POR DNI ===");
        String dni = todos.get(0).getDni();
        System.out.println(dao.obtenerPorDni(dni));

        System.out.println("\n=== OBTENER POR NOMBRE ===");
        String nombre = todos.get(0).getNombre();
        System.out.println(dao.obtenerPorNombre(nombre));

        System.out.println("\n=== INSERTAR ===");
        PuestoTrabajo puesto = new PuestoTrabajo();
        puesto.setId(5L); // Programador Junior
        CategoriaLaboral categoria = new CategoriaLaboral();
        categoria.setId(1L); // Programador Junior - Nivel I

        Empleado nuevo = new Empleado(
                "Carlos", "Pérez López", "99999999Z",
                puesto, categoria,
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
        System.out.println(dao.insertar(nuevo));

        System.out.println("\n=== ACTUALIZAR ===");
        Empleado aActualizar = dao.obtenerPorDni("99999999Z");
        aActualizar.setTelefono("699999999");
        aActualizar.setEstadoLaboral("Teletrabajo");
        System.out.println(dao.actualizar(aActualizar));
        System.out.println(dao.obtenerPorDni("99999999Z"));

        System.out.println("\n=== ELIMINAR ===");
        System.out.println(dao.eliminarPorDni("99999999Z"));

        System.out.println("\n=== LISTA FINAL ===");
        dao.obtenerTodos().forEach(System.out::println);
    }


    private static void testEmpleadoPojo() {

            Empleado emp = new Empleado(
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

            System.out.println(emp);
            System.out.println(emp.getTelefono());
            System.out.println(emp.getEmailPersonal());

            emp.setDireccion("Avenida del Puerto 7");
            emp.setEstadoLaboral("Teletrabajo");
            System.out.println(emp);

            Empleado empClon = new Empleado();
            empClon.setDni("99999999Z");
            System.out.println(emp.equals(empClon));
    }

    private static void testTipoAusenciaPojo() {
        System.out.println("=== TEST POJO TipoAusencia ===");

        TipoAusencia t1 = new TipoAusencia(1L, "Vacaciones");
        TipoAusencia t2 = new TipoAusencia(1L, "Otro nombre");
        TipoAusencia t3 = new TipoAusencia(2L, "Baja médica");

        System.out.println("toString:  " + t1);
        System.out.println("getId:     " + (t1.getId() == 1L ? "OK" : "FALLO"));
        System.out.println("getNombre: " + (t1.getNombre().equals("Vacaciones") ? "OK" : "FALLO"));
        System.out.println("equals mismo id:    " + (t1.equals(t2) ? "OK" : "FALLO"));
        System.out.println("equals distinto id: " + (!t1.equals(t3) ? "OK" : "FALLO"));

        System.out.println("=== FIN TEST POJO ===\n");
    }

    private static void testSolicitudAusencia(SolicitudAusenciaDAO dao) {

        System.out.println("=== OBTENER TODOS ===");
        ArrayList<SolicitudAusencia> lista = dao.obtenerTodos();
        for (SolicitudAusencia s : lista) {
            System.out.println(s);
        }

        System.out.println("\n=== OBTENER POR ID '1' ===");
        SolicitudAusencia porId = dao.obtenerPorId(1);
        System.out.println(porId != null ? porId : "No encontrado");

        System.out.println("\n=== OBTENER POR NOMBRE 'Ana' ===");
        SolicitudAusencia porNombre = dao.obtenerPorNombre("Ana");
        System.out.println(porNombre != null ? porNombre : "No encontrado");

        System.out.println("\n=== OBTENER POR DNI '34567890C' ===");
        ArrayList<SolicitudAusencia> porDni = dao.obtenerPorDni("34567890C");
        porDni.forEach(System.out::println);

        System.out.println("\n=== OBTENER POR ESTADO 'PENDIENTE' ===");
        ArrayList<SolicitudAusencia> pendientes = dao.obtenerPorEstado("PENDIENTE");
        pendientes.forEach(System.out::println);

        System.out.println("\n=== INSERTAR NUEVA SOLICITUD ===");
        Empleado emp = new Empleado();
        emp.setDni("34567890C");
        emp.setNombre("Ana");
        emp.setApellidos("Torres Díaz");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia nueva = new SolicitudAusencia(
                0L, emp, tipo,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 5),
                0,
                "Vacaciones de prueba",
                "PENDIENTE",
                null,
                LocalDate.now()
        );
        boolean insertado = dao.insertar(nueva);
        System.out.println(insertado ? "Insertado correctamente" : "Error al insertar");

        System.out.println("\n=== LISTA TRAS INSERTAR ===");
        dao.obtenerTodos().forEach(System.out::println);

        System.out.println("\n=== ACTUALIZAR ESTADO DE SOLICITUD ID '2' ===");
        SolicitudAusencia aActualizar = dao.obtenerPorId(2);
        if (aActualizar != null) {
            aActualizar.setEstado("APROBADA");
            aActualizar.setComentarioAdmin("Aprobado en prueba");
            boolean actualizado = dao.actualizar(aActualizar);
            System.out.println(actualizado ? "Actualizado correctamente" : "Error al actualizar");
            System.out.println("Resultado: " + dao.obtenerPorId(2));
        }

        System.out.println("\n=== ELIMINAR SOLICITUD RECIÉN INSERTADA ===");
        ArrayList<SolicitudAusencia> listaFinal = dao.obtenerTodos();
        SolicitudAusencia ultima = listaFinal.get(listaFinal.size() - 1);
        boolean eliminado = dao.eliminar(ultima.getId());
        System.out.println(eliminado ? "Eliminado correctamente" : "Error al eliminar");

        System.out.println("\n=== LISTA FINAL TRAS ELIMINAR ===");
        dao.obtenerTodos().forEach(System.out::println);
    }

    private static void testTipoAusencia(TipoAusenciaDAO dao) {

        System.out.println("OBTENER TODOS");
        ArrayList<TipoAusencia> lista = dao.obtenerTodos();
        for (TipoAusencia t : lista) {
            System.out.println(t);
        }

        System.out.println("OBTENER POR ID '1'");
        TipoAusencia porId = dao.obtenerPorId(1);
        System.out.println(porId != null ? porId : "No encontrado");

        System.out.println("OBTENER POR NOMBRE 'Vacaciones'");
        TipoAusencia porNombre = dao.obtenerPorNombre("Vacaciones");
        System.out.println(porNombre != null ? porNombre : "No encontrado");

        System.out.println("INSERTAR 'Permiso de lactancia'");
        TipoAusencia nuevo = new TipoAusencia();
        nuevo.setNombre("Permiso de lactancia");
        boolean insertado = dao.insertar(nuevo);
        System.out.println(insertado ? "Insertado correctamente" : "Error al insertar");
        TipoAusencia recienInsertado = dao.obtenerPorNombre("Permiso de lactancia");

        System.out.println("LISTA TRAS INSERTAR");
        dao.obtenerTodos().forEach(System.out::println);

        System.out.println("ACTUALIZAR 'Permiso de lactancia' a 'Permiso de lactancia actualizado'");
        recienInsertado.setNombre("Permiso de lactancia actualizado");
        boolean actualizado = dao.actualizar(recienInsertado);
        System.out.println(actualizado ? "Actualizado correctamente" : "Error al actualizar");
        System.out.println("Resultado: " + dao.obtenerPorId(recienInsertado.getId()));

        System.out.println("ELIMINAR id=" + recienInsertado.getId());
        boolean eliminado = dao.eliminar(recienInsertado.getId());
        System.out.println(eliminado ? "Eliminado correctamente" : "Error al eliminar");

        System.out.println("LISTA FINAL TRAS ELIMINAR");
        dao.obtenerTodos().forEach(System.out::println);
    }

    private static void testRegistroAusencia(RegistroAusenciaDAO dao) {

        System.out.println("=== OBTENER TODOS ===");
        ArrayList<RegistroAusencia> lista = dao.obtenerTodos();
        lista.forEach(System.out::println);

        System.out.println("\n=== OBTENER POR ID '1' ===");
        RegistroAusencia porId = dao.obtenerPorId(1);
        System.out.println(porId != null ? porId : "No encontrado");

        System.out.println("\n=== OBTENER POR NOMBRE EMPLEADO 'Laura' ===");
        RegistroAusencia porNombre = dao.obtenerPorNombre("Laura");
        System.out.println(porNombre != null ? porNombre : "No encontrado");

        System.out.println("\n=== INSERTAR NUEVO REGISTRO ===");

        Empleado emp = new Empleado();
        emp.setDni("34567890C");
        emp.setNombre("Ana");
        emp.setApellidos("Torres Díaz");

        Empleado admin = new Empleado();
        admin.setDni("12345678A");
        admin.setNombre("Laura");
        admin.setApellidos("Martínez Ruiz");

        TipoAusencia tipo = new TipoAusencia(1L, "Vacaciones");

        SolicitudAusencia solicitud = new SolicitudAusencia();
        solicitud.setId(1L);

        RegistroAusencia nuevo = new RegistroAusencia(
                0L, // AUTO_INCREMENT
                emp,
                admin,
                solicitud,
                tipo,
                LocalDateTime.now(),
                "Registro de prueba"
        );

        boolean insertado = dao.insertar(nuevo);
        System.out.println(insertado ? "Insertado correctamente" : "Error al insertar");

        System.out.println("\n=== LISTA TRAS INSERTAR ===");
        dao.obtenerTodos().forEach(System.out::println);

        System.out.println("\n=== ACTUALIZAR REGISTRO ID '1' ===");
        RegistroAusencia aActualizar = dao.obtenerPorId(1);
        if (aActualizar != null) {
            aActualizar.setComentarioAdmin("Comentario actualizado");
            aActualizar.setFechaRegistro(LocalDateTime.now());
            boolean actualizado = dao.actualizar(aActualizar);
            System.out.println(actualizado ? "Actualizado correctamente" : "Error al actualizar");
            System.out.println("Resultado: " + dao.obtenerPorId(1));
        }

        System.out.println("\n=== ELIMINAR ÚLTIMO REGISTRO ===");
        ArrayList<RegistroAusencia> listaFinal = dao.obtenerTodos();
        RegistroAusencia ultimo = listaFinal.get(listaFinal.size() - 1);
        boolean eliminado = dao.eliminar(ultimo.getId());
        System.out.println(eliminado ? "Eliminado correctamente" : "Error al eliminar");

        System.out.println("\n=== LISTA FINAL TRAS ELIMINAR ===");
        dao.obtenerTodos().forEach(System.out::println);
    }

}
