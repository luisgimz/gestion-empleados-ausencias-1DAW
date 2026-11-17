package com.example.proyecto.database;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.TipoAusencia;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SolicitudAusenciaDAO implements InterfazDAO<SolicitudAusencia> {

    private final Connection con;

    public SolicitudAusenciaDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();
    }

    private static final String SQL_SELECT =
            "SELECT sa.id, sa.fecha_inicio, sa.fecha_fin, sa.dias_solicitados, " +
                    "sa.motivo, sa.estado, sa.comentario_admin, sa.fecha_solicitud, " +
                    "e.dni, e.nombre, e.apellidos, " +
                    "e.email_corporativo, e.estado_laboral, " +
                    "e.fecha_alta, e.fecha_baja, " +
                    "ta.id AS ta_id, ta.nombre AS ta_nombre " +
                    "FROM solicitud_ausencia sa " +
                    "JOIN empleado e  ON sa.empleado_id      = e.id " +
                    "JOIN tipo_ausencia ta ON sa.tipo_ausencia_id = ta.id";

    private SolicitudAusencia mapear(ResultSet rs) throws SQLException {

        Empleado emp = new Empleado();
        emp.setDni(rs.getString("dni"));
        emp.setNombre(rs.getString("nombre"));
        emp.setApellidos(rs.getString("apellidos"));
        emp.setEmailCorporativo(rs.getString("email_corporativo"));
        emp.setEstadoLaboral(rs.getString("estado_laboral"));
        emp.setFechaAlta(rs.getDate("fecha_alta") != null
                ? rs.getDate("fecha_alta").toLocalDate() : null);
        emp.setFechaBaja(rs.getDate("fecha_baja") != null
                ? rs.getDate("fecha_baja").toLocalDate() : null);

        TipoAusencia tipo = new TipoAusencia(
                rs.getLong("ta_id"),
                rs.getString("ta_nombre")
        );

        return new SolicitudAusencia(
                rs.getLong("id"),
                emp,
                tipo,
                rs.getDate("fecha_inicio").toLocalDate(),
                rs.getDate("fecha_fin").toLocalDate(),
                rs.getInt("dias_solicitados"),
                rs.getString("motivo"),
                rs.getString("estado"),
                rs.getString("comentario_admin"),
                rs.getDate("fecha_solicitud").toLocalDate()
        );
    }

    @Override
    public ArrayList<SolicitudAusencia> obtenerTodos() {
        ArrayList<SolicitudAusencia> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public SolicitudAusencia obtenerPorId(long id) {
        String sql = SQL_SELECT + " WHERE sa.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitud por id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public SolicitudAusencia obtenerPorNombre(String nombre) {
        String sql = SQL_SELECT + " WHERE e.nombre = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitud por nombre: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertar(SolicitudAusencia s) {
        // dias_solicitados calculated automatically by trigger
        // empleado_id resolved via dni subquery
        String sql = "INSERT INTO solicitud_ausencia " +
                "(empleado_id, tipo_ausencia_id, fecha_inicio, fecha_fin, " +
                "motivo, estado, comentario_admin, fecha_solicitud) " +
                "SELECT e.id, ?, ?, ?, ?, ?, ?, ? FROM empleado e WHERE e.dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, s.getTipoAusencia().getId());
            ps.setObject(2, s.getFechaInicio());
            ps.setObject(3, s.getFechaFin());
            ps.setString(4, s.getMotivo());
            ps.setString(5, s.getEstado() != null ? s.getEstado() : "PENDIENTE");
            ps.setString(6, s.getComentarioAdmin());
            ps.setObject(7, s.getFechaSolicitud() != null
                    ? s.getFechaSolicitud() : java.time.LocalDate.now());
            ps.setString(8, s.getEmpleado().getDni());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar solicitud: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(SolicitudAusencia s) {
        String sql = "UPDATE solicitud_ausencia SET " +
                "tipo_ausencia_id = ?, fecha_inicio = ?, fecha_fin = ?, " +
                "motivo = ?, estado = ?, comentario_admin = ? " +
                "WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, s.getTipoAusencia().getId());
            ps.setObject(2, s.getFechaInicio());
            ps.setObject(3, s.getFechaFin());
            ps.setString(4, s.getMotivo());
            ps.setString(5, s.getEstado());
            ps.setString(6, s.getComentarioAdmin());
            ps.setLong(7, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar solicitud: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {
        String sqlRegistro = "DELETE FROM registro_ausencia WHERE id_solicitud = ?";
        String sqlSolicitud = "DELETE FROM solicitud_ausencia WHERE id = ?";
        try {
            try (PreparedStatement ps1 = con.prepareStatement(sqlRegistro)) {
                ps1.setLong(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(sqlSolicitud)) {
                ps2.setLong(1, id);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar solicitud: " + e.getMessage());
            return false;
        }
    }
    public ArrayList<SolicitudAusencia> obtenerPorDni(String dni) {
        ArrayList<SolicitudAusencia> lista = new ArrayList<>();
        String sql = SQL_SELECT + " WHERE e.dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes por dni: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<SolicitudAusencia> obtenerPorEstado(String estado) {
        ArrayList<SolicitudAusencia> lista = new ArrayList<>();
        String sql = SQL_SELECT + " WHERE sa.estado = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes por estado: " + e.getMessage());
        }
        return lista;
    }

    public int obtenerDiasPedidosPorDni(String dni) {
        int total = 0;
        String sql = "SELECT COALESCE(SUM(dias_solicitados), 0) FROM solicitud_ausencia sa " +
                "JOIN empleado e ON sa.empleado_id = e.id " +
                "WHERE e.dni = ? AND sa.estado = 'PENDIENTE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener días comprometidos: " + e.getMessage());
        }
        return total;
    }

    public boolean cambiarEstado(long id, String nuevoEstado, String comentarioAdmin) {
        String sql = "UPDATE solicitud_ausencia SET estado = ?, comentario_admin = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, comentarioAdmin);
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado solicitud: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<SolicitudAusencia> obtenerPendientesPorDni(String dni) {
        ArrayList<SolicitudAusencia> lista = new ArrayList<>();
        String sql = SQL_SELECT + " WHERE e.dni = ? AND sa.estado = 'PENDIENTE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pendientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean existeSolapamiento(String dni, LocalDate fechaInicio, LocalDate fechaFin, long idExcluir) {
        String sql = "SELECT COUNT(*) FROM solicitud_ausencia sa " +
                "JOIN empleado e ON sa.empleado_id = e.id " +
                "WHERE e.dni = ? " +
                "AND sa.estado != 'RECHAZADA' " +
                "AND sa.id != ? " +
                "AND sa.fecha_inicio <= ? " +
                "AND sa.fecha_fin >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setLong(2, idExcluir);
            ps.setObject(3, fechaFin);
            ps.setObject(4, fechaInicio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al comprobar solapamiento: " + e.getMessage());
        }
        return false;
    }

    public int obtenerDiasVacacionesPedidosPorDni(String dni) {
        int total = 0;
        String sql = "SELECT COALESCE(SUM(sa.dias_solicitados), 0) FROM solicitud_ausencia sa " +
                "JOIN empleado e ON sa.empleado_id = e.id " +
                "JOIN tipo_ausencia ta ON sa.tipo_ausencia_id = ta.id " +
                "WHERE e.dni = ? AND sa.estado = 'PENDIENTE' AND ta.nombre = 'Vacaciones'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener días de vacaciones comprometidos: " + e.getMessage());
        }
        return total;
    }
}