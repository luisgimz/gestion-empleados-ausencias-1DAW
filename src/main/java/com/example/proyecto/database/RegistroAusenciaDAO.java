package com.example.proyecto.database;

import com.example.proyecto.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RegistroAusenciaDAO implements InterfazDAO<RegistroAusencia> {

    private final Connection con;

    public RegistroAusenciaDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();
    }

    private static final String SQL_SELECT =
            "SELECT ra.id, ra.fecha_registro, ra.comentario_admin, " +
                    "es.dni AS emp_dni, es.nombre AS emp_nombre, es.apellidos AS emp_apellidos, " +
                    "es.email_corporativo AS emp_email, es.estado_laboral AS emp_estado, " +
                    "es.fecha_alta AS emp_alta, es.fecha_baja AS emp_baja, " +
                    "ad.dni AS ad_dni, ad.nombre AS ad_nombre, ad.apellidos AS ad_apellidos, " +
                    "ad.email_corporativo AS ad_email, ad.estado_laboral AS ad_estado, " +
                    "ad.fecha_alta AS ad_alta, ad.fecha_baja AS ad_baja, " +
                    "sa.id AS sol_id, sa.fecha_inicio, sa.fecha_fin, sa.dias_solicitados, " +
                    "sa.motivo, sa.estado, sa.comentario_admin AS sol_comentario, sa.fecha_solicitud, " +
                    "ta.id AS ta_id, ta.nombre AS ta_nombre, " +
                    "dep.nombre AS dep_nombre " +
                    "FROM registro_ausencia ra " +
                    "JOIN solicitud_ausencia sa  ON ra.id_solicitud          = sa.id " +
                    "JOIN empleado es            ON ra.id_empleado_solicitud = es.id " +
                    "LEFT JOIN usuario u         ON ra.id_admin              = u.id " +
                    "LEFT JOIN empleado ad       ON u.empleado_id            = ad.id " +
                    "JOIN tipo_ausencia ta       ON ra.tipo_solicitud        = ta.id " +
                    "JOIN puesto_trabajo pt      ON es.puesto_actual         = pt.id " +
                    "JOIN departamento dep       ON pt.departamento_id       = dep.id";

    private RegistroAusencia mapear(ResultSet rs) throws SQLException {

        Empleado emp = new Empleado();
        emp.setDni(rs.getString("emp_dni"));
        emp.setNombre(rs.getString("emp_nombre"));
        emp.setApellidos(rs.getString("emp_apellidos"));
        emp.setEmailCorporativo(rs.getString("emp_email"));
        emp.setEstadoLaboral(rs.getString("emp_estado"));
        emp.setFechaAlta(rs.getDate("emp_alta") != null ? rs.getDate("emp_alta").toLocalDate() : null);
        emp.setFechaBaja(rs.getDate("emp_baja") != null ? rs.getDate("emp_baja").toLocalDate() : null);

        Empleado admin = null;
        if (rs.getString("ad_nombre") != null) {
            admin = new Empleado();
            admin.setDni(rs.getString("ad_dni"));
            admin.setNombre(rs.getString("ad_nombre"));
            admin.setApellidos(rs.getString("ad_apellidos"));
            admin.setEmailCorporativo(rs.getString("ad_email"));
            admin.setEstadoLaboral(rs.getString("ad_estado"));
            admin.setFechaAlta(rs.getDate("ad_alta") != null ? rs.getDate("ad_alta").toLocalDate() : null);
            admin.setFechaBaja(rs.getDate("ad_baja") != null ? rs.getDate("ad_baja").toLocalDate() : null);
        }

        TipoAusencia tipo = new TipoAusencia(
                rs.getLong("ta_id"),
                rs.getString("ta_nombre")
        );

        SolicitudAusencia sol = new SolicitudAusencia(
                rs.getLong("sol_id"),
                emp,
                tipo,
                rs.getDate("fecha_inicio") != null
                        ? rs.getDate("fecha_inicio").toLocalDate() : null,
                rs.getDate("fecha_fin") != null
                        ? rs.getDate("fecha_fin").toLocalDate() : null,
                rs.getInt("dias_solicitados"),
                rs.getString("motivo"),
                rs.getString("estado"),
                rs.getString("sol_comentario"),
                rs.getDate("fecha_solicitud") != null
                        ? rs.getDate("fecha_solicitud").toLocalDate() : null
        );

        return new RegistroAusencia(
                rs.getLong("id"),
                emp,
                admin,
                sol,
                tipo,
                rs.getTimestamp("fecha_registro") != null
                        ? rs.getTimestamp("fecha_registro").toLocalDateTime() : null,
                rs.getString("comentario_admin")
        );
    }

    @Override
    public ArrayList<RegistroAusencia> obtenerTodos() {
        ArrayList<RegistroAusencia> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al obtener registros: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public RegistroAusencia obtenerPorId(long id) {
        String sql = SQL_SELECT + " WHERE ra.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener registro por id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public RegistroAusencia obtenerPorNombre(String nombre) {
        String sql = SQL_SELECT + " WHERE es.nombre = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener registro por nombre: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertar(RegistroAusencia r) {
        String sql = "INSERT INTO registro_ausencia " +
                "(id_empleado_solicitud, id_admin, id_solicitud, tipo_solicitud, fecha_registro, comentario_admin) " +
                "SELECT e.id, a.id, ?, ?, ?, ? " +
                "FROM empleado e, empleado a " +
                "WHERE e.dni = ? AND a.dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, r.getSolicitud().getId());
            ps.setLong(2, r.getTipoSolicitud().getId());
            ps.setTimestamp(3, Timestamp.valueOf(r.getFechaRegistro() != null ? r.getFechaRegistro() : LocalDateTime.now()));
            ps.setString(4, r.getComentarioAdmin());
            ps.setString(5, r.getEmpleadoSolicitud().getDni());
            ps.setString(6, r.getAdmin().getDni());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar registro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(RegistroAusencia r) {
        String sql = "UPDATE registro_ausencia SET " +
                "id_empleado_solicitud = (SELECT id FROM empleado WHERE dni = ?), " +
                "id_admin = (SELECT id FROM empleado WHERE dni = ?), " +
                "id_solicitud = ?, tipo_solicitud = ?, fecha_registro = ?, comentario_admin = ? " +
                "WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getEmpleadoSolicitud().getDni());
            ps.setString(2, r.getAdmin().getDni());
            ps.setLong(3, r.getSolicitud().getId());
            ps.setLong(4, r.getTipoSolicitud().getId());
            ps.setTimestamp(5, Timestamp.valueOf(r.getFechaRegistro() != null
                    ? r.getFechaRegistro() : LocalDateTime.now()));
            ps.setString(6, r.getComentarioAdmin());
            ps.setLong(7, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar registro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {
        String sql = "DELETE FROM registro_ausencia WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar registro: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<RegistroAusencia> obtenerPorSolicitudId(long solicitudId) {
        ArrayList<RegistroAusencia> lista = new ArrayList<>();
        String sql = SQL_SELECT + " WHERE ra.id_solicitud = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, solicitudId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener registros por solicitud: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<RegistroAusencia> obtenerAusentesProximos7Dias() {

        ArrayList<RegistroAusencia> lista = new ArrayList<>();

        String sql =
                "SELECT DISTINCT sa.id AS sol_id, sa.fecha_inicio, sa.fecha_fin, sa.dias_solicitados, " +
                        "sa.motivo, sa.estado, sa.comentario_admin AS sol_comentario, sa.fecha_solicitud, " +
                        "es.dni AS emp_dni, es.nombre AS emp_nombre, es.apellidos AS emp_apellidos, " +
                        "es.email_corporativo AS emp_email, es.estado_laboral AS emp_estado, " +
                        "es.fecha_alta AS emp_alta, es.fecha_baja AS emp_baja, " +
                        "ta.id AS ta_id, ta.nombre AS ta_nombre, " +
                        "dep.id AS dep_id, dep.nombre AS dep_nombre " +
                        "FROM solicitud_ausencia sa " +
                        "JOIN empleado es ON sa.empleado_id = es.id " +
                        "JOIN tipo_ausencia ta ON sa.tipo_ausencia_id = ta.id " +
                        "JOIN puesto_trabajo pt ON es.puesto_actual = pt.id " +
                        "JOIN departamento dep ON pt.departamento_id = dep.id " +
                        "WHERE sa.estado = 'APROBADA' " +
                        "AND sa.fecha_inicio <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) " +
                        "AND sa.fecha_fin >= CURRENT_DATE";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Empleado emp = new Empleado();

                emp.setDni(rs.getString("emp_dni"));
                emp.setNombre(rs.getString("emp_nombre"));
                emp.setApellidos(rs.getString("emp_apellidos"));
                emp.setEmailCorporativo(rs.getString("emp_email"));
                emp.setEstadoLaboral(rs.getString("emp_estado"));

                emp.setFechaAlta(
                        rs.getDate("emp_alta") != null
                                ? rs.getDate("emp_alta").toLocalDate()
                                : null
                );

                emp.setFechaBaja(
                        rs.getDate("emp_baja") != null
                                ? rs.getDate("emp_baja").toLocalDate()
                                : null
                );

                Departamento dep = new Departamento();

                dep.setId(rs.getLong("dep_id"));
                dep.setNombre(rs.getString("dep_nombre"));

                PuestoTrabajo puesto = new PuestoTrabajo();

                puesto.setDepartamento(dep);

                emp.setPuestoActual(puesto);

                TipoAusencia tipo = new TipoAusencia(
                        rs.getLong("ta_id"),
                        rs.getString("ta_nombre")
                );

                SolicitudAusencia sol = new SolicitudAusencia(
                        rs.getLong("sol_id"),
                        emp,
                        tipo,
                        rs.getDate("fecha_inicio") != null
                                ? rs.getDate("fecha_inicio").toLocalDate()
                                : null,
                        rs.getDate("fecha_fin") != null
                                ? rs.getDate("fecha_fin").toLocalDate()
                                : null,
                        rs.getInt("dias_solicitados"),
                        rs.getString("motivo"),
                        rs.getString("estado"),
                        rs.getString("sol_comentario"),
                        rs.getDate("fecha_solicitud") != null
                                ? rs.getDate("fecha_solicitud").toLocalDate()
                                : null
                );

                lista.add(
                        new RegistroAusencia(
                                0L,
                                emp,
                                null,
                                sol,
                                tipo,
                                null,
                                null
                        )
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ausentes hoy: " + e.getMessage());
        }

        return lista;
    }
}

