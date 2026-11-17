package com.example.proyecto.database;

import com.example.proyecto.model.*;
import java.sql.*;
import java.util.ArrayList;

public class EmpleadoDAO implements InterfazDAO<Empleado> {

    private final Connection con;

    public EmpleadoDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();
    }

    private static final String SQL_SELECT =
            "SELECT " +
                    "  e.id, e.dni, e.nombre, e.apellidos, e.email_corporativo, " +
                    "  e.email_personal, e.telefono, e.direccion, e.numero_ss, " +
                    "  e.fecha_nacimiento, e.fecha_alta, e.fecha_baja, e.estado_laboral, e.dias_vacaciones_disponibles, e.foto, " +
                    "  cr.id AS cr_id, cr.empresa_id AS cr_empresa_id, cr.convenio AS cr_convenio, " +
                    "  cr.grupo_profesional AS cr_grupo, cr.nivel AS cr_nivel, cr.descripcion AS cr_descripcion, " +
                    "  p.id AS p_id, p.sede_id AS p_sede_id, p.departamento_id AS p_departamento_id, " +
                    "  p.categoria_referencia AS p_categoria_referencia, p.nombre AS p_nombre, p.descripcion_funciones, p.jornada, p.modalidad, p.activo, " +
                    "  cref.id AS cref_id, cref.empresa_id AS cref_empresa_id, cref.convenio AS cref_convenio, " +
                    "  cref.grupo_profesional AS cref_grupo, cref.nivel AS cref_nivel, cref.descripcion AS cref_descripcion, " +
                    "  s.id AS s_id, s.empresa_id AS s_empresa_id, s.tipo AS s_tipo, s.email_contacto AS s_email_contacto, " +
                    "  s.calle AS s_calle, s.numero AS s_numero, s.cp AS s_cp, s.ciudad AS s_ciudad, s.provincia AS s_provincia, " +
                    "  d.id AS d_id, d.empresa_id AS d_empresa_id, d.codigo AS d_codigo, " +
                    "  d.nombre AS d_nombre, d.descripcion AS d_descripcion " +
                    "FROM empleado e " +
                    "LEFT JOIN categoria_laboral cr ON e.categoria_real = cr.id " +
                    "LEFT JOIN puesto_trabajo p ON e.puesto_actual = p.id " +
                    "LEFT JOIN sede s ON p.sede_id = s.id " +
                    "LEFT JOIN departamento d ON p.departamento_id = d.id " +
                    "LEFT JOIN categoria_laboral cref ON p.categoria_referencia = cref.id";


    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado(
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                new PuestoTrabajo(
                        rs.getLong("p_id"),
                        new Sede(
                                rs.getLong("s_id"),
                                rs.getLong("s_empresa_id"),
                                rs.getString("s_tipo"),
                                rs.getString("s_email_contacto"),
                                rs.getString("s_calle"),
                                rs.getString("s_numero"),
                                rs.getString("s_cp"),
                                rs.getString("s_ciudad"),
                                rs.getString("s_provincia")
                        ),
                        new Departamento(
                                rs.getLong("d_id"),
                                rs.getLong("d_empresa_id"),
                                rs.getString("d_codigo"),
                                rs.getString("d_nombre"),
                                rs.getString("d_descripcion")
                        ),
                        new CategoriaLaboral(
                                rs.getLong("cref_id"),
                                rs.getLong("cref_empresa_id"),
                                rs.getString("cref_convenio"),
                                rs.getString("cref_grupo"),
                                rs.getString("cref_nivel"),
                                rs.getString("cref_descripcion")
                        ),
                        rs.getString("p_nombre"),
                        rs.getString("descripcion_funciones"),
                        rs.getString("jornada"),
                        rs.getString("modalidad"),
                        rs.getBoolean("activo")
                ),
                new CategoriaLaboral(
                        rs.getLong("cr_id"),
                        rs.getLong("cr_empresa_id"),
                        rs.getString("cr_convenio"),
                        rs.getString("cr_grupo"),
                        rs.getString("cr_nivel"),
                        rs.getString("cr_descripcion")
                ),
                rs.getString("email_corporativo"),
                rs.getString("email_personal"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getString("numero_ss"),
                rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null,
                rs.getDate("fecha_alta")        != null ? rs.getDate("fecha_alta").toLocalDate()       : null,
                rs.getDate("fecha_baja")        != null ? rs.getDate("fecha_baja").toLocalDate()       : null,
                rs.getString("estado_laboral")
        );
        emp.setDiasVacacionesDisponibles(rs.getInt("dias_vacaciones_disponibles"));
        emp.setFoto(rs.getBytes("foto"));
        return emp;
    }

    @Override
    public ArrayList<Empleado> obtenerTodos() {
        ArrayList<Empleado> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Empleado obtenerPorId(long id) {
        String sql = SQL_SELECT + " WHERE e.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearEmpleado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorId: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Empleado obtenerPorNombre(String nombre) {
        String sql = SQL_SELECT + " WHERE e.nombre = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearEmpleado(rs);
            }
        } catch (SQLException ex) {
            System.err.println("Error obtenerPorNombre '" + nombre + "': " + ex.getMessage());
        }
        return null;
    }

    public Empleado obtenerPorDni(String dni) {
        String sql = SQL_SELECT + " WHERE e.dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearEmpleado(rs);
            }
        } catch (SQLException ex) {
            System.err.println("Error obtenerPorDni '" + dni + "': " + ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertar(Empleado emp) {
        String sql = "INSERT INTO empleado " +
                "(dni, nombre, apellidos, puesto_actual, categoria_real, " +
                " email_corporativo, email_personal, telefono, direccion, numero_ss, " +
                " fecha_nacimiento, fecha_alta, fecha_baja, estado_laboral) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getDni());
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getApellidos());

            if (emp.getPuestoActual() != null) {
                ps.setLong(4, emp.getPuestoActual().getId());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            if (emp.getCategoriaReal() != null) {
                ps.setLong(5, emp.getCategoriaReal().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.setString(6, emp.getEmailCorporativo());
            ps.setString(7, emp.getEmailPersonal());
            ps.setString(8, emp.getTelefono());
            ps.setString(9, emp.getDireccion());
            ps.setString(10, emp.getNumeroSs());
            ps.setObject(11, emp.getFechaNacimiento());
            ps.setObject(12, emp.getFechaAlta());
            ps.setObject(13, emp.getFechaBaja());
            ps.setString(14, emp.getEstadoLaboral());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Empleado emp) {
        String sql = "UPDATE empleado SET " +
                "  nombre            = ?, " +
                "  apellidos         = ?, " +
                "  email_corporativo = ?, " +
                "  email_personal    = ?, " +
                "  telefono          = ?, " +
                "  direccion         = ?, " +
                "  numero_ss         = ?, " +
                "  fecha_nacimiento  = ?, " +
                "  estado_laboral    = ? " +
                "WHERE dni = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1,  emp.getNombre());
            ps.setString(2,  emp.getApellidos());
            ps.setString(3,  emp.getEmailCorporativo());
            ps.setString(4,  emp.getEmailPersonal());
            ps.setString(5,  emp.getTelefono());
            ps.setString(6,  emp.getDireccion());
            ps.setString(7,  emp.getNumeroSs());
            ps.setObject(8,  emp.getFechaNacimiento());
            ps.setString(9,  emp.getEstadoLaboral());
            ps.setString(10, emp.getDni());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar empleado: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPorDni(String dni) {
        String sql = "DELETE FROM empleado WHERE dni = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminarPorDni '" + dni + "': " + e.getMessage());
            return false;
        }
    }

    public int obtenerDiasDisponiblesPorDni(String dni) {
        String sql = "SELECT dias_vacaciones_disponibles FROM empleado WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("dias_vacaciones_disponibles");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener días disponibles: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizarDiasVacacionesDisponibles(String dni, int diasNuevos) {
        String sql = "UPDATE empleado SET dias_vacaciones_disponibles = ? WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, diasNuevos);
            ps.setString(2, dni);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar días disponibles: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarFoto(String dni, byte[] foto) {
        String sql = "UPDATE empleado SET foto = ? WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBytes(1, foto);
            ps.setString(2, dni);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar foto: " + e.getMessage());
            return false;
        }
    }
    public boolean actualizarPerfil(String dni, String telefono, String direccion, String emailPersonal) {
        String sql = "UPDATE empleado SET telefono = ?, direccion = ?, email_personal = ? WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telefono);
            ps.setString(2, direccion);
            ps.setString(3, emailPersonal);
            ps.setString(4, dni);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCompleto(String dni) {
        try {

            long empleadoId;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id FROM empleado WHERE dni = ?")) {
                ps.setString(1, dni);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    empleadoId = rs.getLong("id");
                }
            }

            long usuarioId = -1;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id FROM usuario WHERE empleado_id = ?")) {
                ps.setLong(1, empleadoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) usuarioId = rs.getLong("id");
                }
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM registro_ausencia WHERE id_solicitud IN " +
                            "(SELECT id FROM solicitud_ausencia WHERE empleado_id = ?)")) {
                ps.setLong(1, empleadoId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM registro_ausencia WHERE id_empleado_solicitud = ?")) {
                ps.setLong(1, empleadoId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM solicitud_ausencia WHERE empleado_id = ?")) {
                ps.setLong(1, empleadoId);
                ps.executeUpdate();
            }

            if (usuarioId != -1) {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM usuario_rol WHERE usuario_id = ?")) {
                    ps.setLong(1, usuarioId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM usuario WHERE id = ?")) {
                    ps.setLong(1, usuarioId);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM empleado WHERE id = ?")) {
                ps.setLong(1, empleadoId);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar empleado completo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPuesto(String dni, long puestoId) {
        String sql = "UPDATE empleado SET puesto_actual = ? WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, puestoId);
            ps.setString(2, dni);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar puesto: " + e.getMessage());
            return false;
        }
    }
}