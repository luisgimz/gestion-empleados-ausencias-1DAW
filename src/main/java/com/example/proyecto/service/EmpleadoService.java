package com.example.proyecto.service;

import com.example.proyecto.database.EmpleadoDAO;
import com.example.proyecto.database.UsuarioDAO;
import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.PuestoTrabajo;
import com.example.proyecto.model.Rol;
import com.example.proyecto.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmpleadoService {

    private final EmpleadoDAO empleadoDAO;
    private final UsuarioDAO usuarioDAO;

    public EmpleadoService() throws SQLException {
        empleadoDAO = new EmpleadoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    public ArrayList<Empleado> obtenerTodos() {
        return empleadoDAO.obtenerTodos();
    }

    public Empleado obtenerPorId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        return empleadoDAO.obtenerPorId(id);
    }

    public Empleado obtenerPorDni(String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI obligatorio");
        }

        return empleadoDAO.obtenerPorDni(dni);
    }

    public boolean insertarEmpleado(
            String dni,
            String nombre,
            String apellidos,
            String emailCorporativo,
            String emailPersonal,
            String telefono,
            String direccion,
            String numeroSs,
            LocalDate fechaNacimiento,
            LocalDate fechaAlta,
            LocalDate fechaBaja,
            String estado,
            String rolSeleccionado,
            PuestoTrabajo puestoSeleccionado
    ) {

        if (dni == null || dni.isBlank()
                || nombre == null || nombre.isBlank()
                || apellidos == null || apellidos.isBlank()
                || emailCorporativo == null || emailCorporativo.isBlank()
                || estado == null || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "DNI, nombre, apellidos, email y estado son obligatorios."
            );
        }

        dni = dni.trim().toUpperCase();

        if (!dni.matches("\\d{8}[A-Z]")) {
            throw new IllegalArgumentException(
                    "Formato de DNI inválido."
            );
        }

        if (puestoSeleccionado == null) {
            throw new IllegalArgumentException("El puesto de trabajo es obligatorio.");
        }

        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }

        if (telefono != null
                && !telefono.isBlank()
                && !telefono.matches("\\d{9}")) {

            throw new IllegalArgumentException(
                    "El teléfono debe tener 9 dígitos."
            );
        }

        if (!emailCorporativo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new IllegalArgumentException(
                    "Email corporativo inválido."
            );
        }

        if (emailPersonal != null
                && !emailPersonal.isBlank()
                && !emailPersonal.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new IllegalArgumentException(
                    "Email personal inválido."
            );
        }

        if (fechaNacimiento != null) {

            int edad =
                    java.time.Period.between(
                            fechaNacimiento,
                            LocalDate.now()
                    ).getYears();

            if (edad < 16) {

                throw new IllegalArgumentException(
                        "El empleado debe ser mayor de 16 años."
                );
            }
        }

        if (numeroSs != null
                && !numeroSs.isBlank()
                && !numeroSs.matches("\\d{12}")) {

            throw new IllegalArgumentException(
                    "El número SS debe tener 12 dígitos."
            );
        }

        Empleado existente = empleadoDAO.obtenerPorDni(dni);

        if (existente != null) {
            throw new RuntimeException(
                    "Ya existe un empleado con ese DNI"
            );
        }

        if (puestoSeleccionado != null && puestoSeleccionado.getId() > 0) {
            if (puestoUnicoOcupado(puestoSeleccionado.getId(), null)) {
                String nombrePuesto = puestoSeleccionado.getId() == 1 ? "CEO" : "CTO";
                throw new IllegalArgumentException(
                        "Ya existe un empleado con el puesto de " + nombrePuesto +
                                ". Debes reasignarlo antes de continuar."
                );
            }
        }

        Empleado nuevo = new Empleado(
                nombre.trim(),
                apellidos.trim(),
                dni,
                puestoSeleccionado,
                null,
                emailCorporativo.trim(),
                emailPersonal != null
                        ? emailPersonal.trim()
                        : "",
                telefono != null
                        ? telefono.trim()
                        : "",
                direccion != null
                        ? direccion.trim()
                        : "",
                numeroSs != null
                        ? numeroSs.trim()
                        : "",
                fechaNacimiento,
                fechaAlta != null
                        ? fechaAlta
                        : LocalDate.now(),
                fechaBaja,
                estado
        );

        boolean empleadoOk = empleadoDAO.insertar(nuevo);

        if (!empleadoOk) {
            return false;
        }

        String username =
                nuevo.getNombre().toLowerCase() + "." +
                        nuevo.getApellidos()
                                .split(" ")[0]
                                .toLowerCase();

        Rol rol = new Rol();

        if ("ADMIN".equals(rolSeleccionado)) {
            rol.setId(1);
        } else {
            rol.setId(2);
        }

        Usuario usuario = new Usuario();

        usuario.setEmpleado(nuevo);
        usuario.setUsername(username);
        usuario.setPasswordHash("123456");
        usuario.setActivo(true);
        usuario.setRol(rol);

        return usuarioDAO.insertar(usuario);
    }

    public boolean actualizarEmpleado(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "Empleado nulo"
            );
        }

        if (empleado.getNombre() == null
                || empleado.getNombre().isBlank()
                || empleado.getApellidos() == null
                || empleado.getApellidos().isBlank()
                || empleado.getEmailCorporativo() == null
                || empleado.getEmailCorporativo().isBlank()
                || empleado.getEstadoLaboral() == null
                || empleado.getEstadoLaboral().isBlank()) {

            throw new IllegalArgumentException(
                    "Nombre, apellidos, email y estado son obligatorios."
            );
        }

        if (!empleado.getEmailCorporativo()
                .matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new IllegalArgumentException(
                    "Email corporativo inválido."
            );
        }

        if (empleado.getEmailPersonal() != null
                && !empleado.getEmailPersonal().isBlank()
                && !empleado.getEmailPersonal()
                .matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new IllegalArgumentException(
                    "Email personal inválido."
            );
        }

        if (empleado.getTelefono() != null
                && !empleado.getTelefono().isBlank()
                && !empleado.getTelefono().matches("\\d{9}")) {

            throw new IllegalArgumentException(
                    "El teléfono debe tener 9 dígitos."
            );
        }

        if (empleado.getNumeroSs() != null
                && !empleado.getNumeroSs().isBlank()
                && !empleado.getNumeroSs().matches("\\d{12}")) {

            throw new IllegalArgumentException(
                    "El número SS debe tener 12 dígitos."
            );
        }

        if (empleado.getFechaNacimiento() != null) {

            int edad =
                    java.time.Period.between(
                            empleado.getFechaNacimiento(),
                            LocalDate.now()
                    ).getYears();

            if (edad < 16) {

                throw new IllegalArgumentException(
                        "El empleado debe ser mayor de 16 años."
                );
            }
        }

        empleado.setNombre(
                empleado.getNombre().trim()
        );

        empleado.setApellidos(
                empleado.getApellidos().trim()
        );

        empleado.setEmailCorporativo(
                empleado.getEmailCorporativo().trim()
        );

        empleado.setEmailPersonal(
                empleado.getEmailPersonal() != null
                        ? empleado.getEmailPersonal().trim()
                        : ""
        );

        empleado.setTelefono(
                empleado.getTelefono() != null
                        ? empleado.getTelefono().trim()
                        : ""
        );

        empleado.setDireccion(
                empleado.getDireccion() != null
                        ? empleado.getDireccion().trim()
                        : ""
        );

        empleado.setNumeroSs(
                empleado.getNumeroSs() != null
                        ? empleado.getNumeroSs().trim()
                        : ""
        );

        return empleadoDAO.actualizar(empleado);
    }


    public int obtenerDiasVacaciones(String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI obligatorio");
        }

        return empleadoDAO.obtenerDiasDisponiblesPorDni(dni);
    }

    public void actualizarVacacionesAprobadas(String dni, int diasSolicitados) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI obligatorio");
        }

        if (diasSolicitados <= 0) {
            throw new IllegalArgumentException("Días inválidos");
        }

        Empleado emp = empleadoDAO.obtenerPorDni(dni);

        if (emp == null) {
            throw new IllegalArgumentException("Empleado no encontrado");
        }

        int diasRestantes =
                emp.getDiasVacacionesDisponibles() - diasSolicitados;

        if (diasRestantes < 0) {
            throw new IllegalArgumentException("No hay días suficientes");
        }

        empleadoDAO.actualizarDiasVacacionesDisponibles(dni, diasRestantes);
    }

    public boolean actualizarFoto(String dni, byte[] foto) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI obligatorio");
        }

        if (foto == null || foto.length == 0) {
            throw new IllegalArgumentException("Foto obligatoria");
        }

        return empleadoDAO.actualizarFoto(dni.trim(), foto);
    }

    public boolean actualizarPerfil(String dni, String telefono, String direccion, String emailPersonal) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        if (telefono != null && !telefono.isBlank() && !telefono.matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener 9 dígitos");
        }

        if (emailPersonal != null
                && !emailPersonal.isBlank()
                && !emailPersonal.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new IllegalArgumentException("El email personal no es válido");
        }

        return empleadoDAO.actualizarPerfil(
                dni.trim(),
                telefono,
                direccion,
                emailPersonal
        );
    }

    public boolean eliminarEmpleadoCompleto(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI obligatorio");
        }
        return empleadoDAO.eliminarCompleto(dni.trim());
    }

    public boolean actualizarPuesto(String dni, long puestoId) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI obligatorio");
        }

        if (puestoId == 1 || puestoId == 2) {
            ArrayList<Empleado> todos = empleadoDAO.obtenerTodos();
            boolean puestoOcupado = todos.stream()
                    .anyMatch(e ->
                            e.getPuestoActual() != null &&
                                    e.getPuestoActual().getId() == puestoId &&
                                    !e.getDni().equalsIgnoreCase(dni)
                    );

            if (puestoOcupado) {
                String nombrePuesto = puestoId == 1 ? "CEO" : "CTO";
                throw new IllegalArgumentException(
                        "Ya existe un empleado con el puesto de " + nombrePuesto +
                                ". Debes reasignarlo antes de continuar."
                );
            }
        }

        return empleadoDAO.actualizarPuesto(dni, puestoId);
    }

    public boolean puestoUnicoOcupado(long puestoId, String dniExcluir) {
        if (puestoId == 1 || puestoId == 2) {
            ArrayList<Empleado> todos = empleadoDAO.obtenerTodos();
            return todos.stream()
                    .anyMatch(e ->
                            e.getPuestoActual() != null &&
                                    e.getPuestoActual().getId() == puestoId &&
                                    (dniExcluir == null || !e.getDni().equalsIgnoreCase(dniExcluir))
                    );
        }
        return false;
    }
}
