package com.example.proyecto.database;

import com.example.proyecto.model.*;
import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO implements InterfazDAO<Usuario> {

    private final Connection con;

    public UsuarioDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();
    }

    private static final String SQL_SELECT =
            "SELECT u.id, u.username, u.password_hash, u.activo, " +
                    "e.dni, e.nombre, e.apellidos, e.email_corporativo, " +
                    "e.email_personal, e.telefono, e.direccion, e.numero_ss, " +
                    "e.fecha_nacimiento, e.fecha_alta, e.fecha_baja, e.estado_laboral, " +
                    "e.dias_vacaciones_disponibles, " +
                    "pt.id AS pt_id, " +
                    "pt.nombre AS pt_nombre, " +
                    "d.id AS dep_id, " +
                    "d.nombre AS dep_nombre, " +
                    "r.nombre AS r_nombre " +
                    "FROM usuario u " +
                    "JOIN empleado e ON u.empleado_id = e.id " +
                    "LEFT JOIN puesto_trabajo pt ON e.puesto_actual = pt.id " +
                    "LEFT JOIN departamento d ON pt.departamento_id = d.id " +
                    "LEFT JOIN usuario_rol ur ON u.id = ur.usuario_id " +
                    "LEFT JOIN rol r ON ur.rol_id = r.id";

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setActivo(rs.getBoolean("activo"));

        Rol rol = new Rol();
        rol.setNombre(rs.getString("r_nombre"));
        u.setRol(rol);

        Empleado emp = new Empleado();
        emp.setDni(rs.getString("dni"));
        emp.setNombre(rs.getString("nombre"));
        emp.setApellidos(rs.getString("apellidos"));
        emp.setEmailCorporativo(rs.getString("email_corporativo"));
        emp.setEmailPersonal(rs.getString("email_personal"));
        emp.setTelefono(rs.getString("telefono"));
        emp.setDireccion(rs.getString("direccion"));
        emp.setNumeroSs(rs.getString("numero_ss"));
        emp.setFechaNacimiento(rs.getDate("fecha_nacimiento") != null
                ? rs.getDate("fecha_nacimiento").toLocalDate() : null);
        emp.setFechaAlta(rs.getDate("fecha_alta") != null
                ? rs.getDate("fecha_alta").toLocalDate() : null);
        emp.setFechaBaja(rs.getDate("fecha_baja") != null
                ? rs.getDate("fecha_baja").toLocalDate() : null);
        emp.setEstadoLaboral(rs.getString("estado_laboral"));
        emp.setDiasVacacionesDisponibles(rs.getInt("dias_vacaciones_disponibles"));
        long puestoId = rs.getLong("pt_id");

        if (!rs.wasNull()) {

            Departamento dep = new Departamento();
            dep.setId(rs.getLong("dep_id"));
            dep.setNombre(rs.getString("dep_nombre"));

            PuestoTrabajo pt = new PuestoTrabajo();
            pt.setId(puestoId);
            pt.setNombre(rs.getString("pt_nombre"));
            pt.setDepartamento(dep);

            emp.setPuestoActual(pt);
        } else {
            emp.setPuestoActual(null);
        }

        u.setEmpleado(emp);

        return u;
    }

    @Override
    public ArrayList<Usuario> obtenerTodos() {
        ArrayList<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Usuario obtenerPorId(long id) {
        String sql = SQL_SELECT + " WHERE u.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Usuario obtenerPorNombre(String username) {
        String sql = SQL_SELECT + " WHERE u.username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por username: " + e.getMessage());
        }
        return null;
    }

    public String obtenerDepartamentoPorUsername(String username) {
        String sql = "SELECT d.nombre " +
                "FROM usuario u " +
                "JOIN empleado e ON u.empleado_id = e.id " +
                "LEFT JOIN puesto_trabajo pt ON e.puesto_actual = pt.id " +
                "LEFT JOIN departamento d ON pt.departamento_id = d.id " +
                "WHERE u.username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener departamento: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertar(Usuario u) {

        String sqlUsuario =
                "INSERT INTO usuario " +
                        "(empleado_id, username, password_hash, activo) " +
                        "SELECT e.id, ?, ?, ? " +
                        "FROM empleado e WHERE e.dni = ?";

        String sqlRol =
                "INSERT INTO usuario_rol (usuario_id, rol_id) " +
                        "SELECT u.id, ? " +
                        "FROM usuario u " +
                        "WHERE u.username = ?";

        try {

            // INSERTAR USUARIO
            try (PreparedStatement ps =
                         con.prepareStatement(sqlUsuario)) {

                ps.setString(1, u.getUsername());
                ps.setString(2, u.getPasswordHash());
                ps.setBoolean(3, u.isActivo());
                ps.setString(4, u.getEmpleado().getDni());

                ps.executeUpdate();
            }

            // INSERTAR ROL
            try (PreparedStatement psRol =
                         con.prepareStatement(sqlRol)) {

                psRol.setLong(1, u.getRol().getId());
                psRol.setString(2, u.getUsername());

                psRol.executeUpdate();
            }

            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Error al insertar usuario: " + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET username=?, password_hash=?, activo=? WHERE username=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setBoolean(3, u.isActivo());
            ps.setString(4, u.getUsername());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {
        String sql = "DELETE FROM usuario WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(id));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPorUsername(String username) {
        String sql = "DELETE FROM usuario WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarActivoUsuario(String username, boolean activo) {

        String sql = "UPDATE usuario SET activo = ? WHERE username = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);   // 👈 BOOLEAN REAL
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar activo: " + e.getMessage());
            return false;
        }
    }

    public boolean reiniciarVacacionesTodos() {
        String sql = "UPDATE empleado SET dias_vacaciones_disponibles = 22 WHERE estado_laboral = 'Activo'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al reiniciar vacaciones: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Usuario> obtenerTodosConEmpleados() {
        String sql = "SELECT e.id AS e_id, e.dni, e.nombre, e.apellidos, e.estado_laboral, " +
                "e.dias_vacaciones_disponibles, e.email_corporativo, e.email_personal, " +
                "e.telefono, e.direccion, e.numero_ss, e.fecha_nacimiento, e.fecha_alta, e.fecha_baja, " +
                "u.id AS u_id, u.username, u.activo " +
                "FROM empleado e " +
                "LEFT JOIN usuario u ON u.empleado_id = e.id";
        ArrayList<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("u_id"));
                u.setUsername(rs.getString("username") != null ? rs.getString("username") : "Sin cuenta");
                u.setActivo(rs.getBoolean("activo"));

                Empleado emp = new Empleado();
                emp.setDni(rs.getString("dni"));
                emp.setNombre(rs.getString("nombre"));
                emp.setApellidos(rs.getString("apellidos"));
                emp.setEstadoLaboral(rs.getString("estado_laboral"));
                emp.setEmailCorporativo(rs.getString("email_corporativo"));
                emp.setDiasVacacionesDisponibles(rs.getInt("dias_vacaciones_disponibles"));
                u.setEmpleado(emp);

                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodosConEmpleados: " + e.getMessage());
        }
        return lista;
    }

    public boolean cambiarPassword(String username, String passwordHash) {
        String sql = "UPDATE usuario SET password_hash = ? WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }
}