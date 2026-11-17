package com.example.proyecto.service;

import com.example.proyecto.database.EmpleadoDAO;
import com.example.proyecto.database.RegistroAusenciaDAO;
import com.example.proyecto.database.SolicitudAusenciaDAO;
import com.example.proyecto.database.TipoAusenciaDAO;
import com.example.proyecto.model.RegistroAusencia;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.TipoAusencia;
import com.example.proyecto.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAusenciaService {

    private final SolicitudAusenciaDAO solicitudDAO;
    private final RegistroAusenciaDAO  registroDAO;
    private final TipoAusenciaDAO      tipoDAO;
    private final EmpleadoDAO          empleadoDAO;

    public SolicitudAusenciaService() throws SQLException {
        this.solicitudDAO = new SolicitudAusenciaDAO();
        this.registroDAO  = new RegistroAusenciaDAO();
        this.tipoDAO      = new TipoAusenciaDAO();
        this.empleadoDAO  = new EmpleadoDAO();
    }

    public List<String> obtenerNombresTipos() {
        return tipoDAO.obtenerNombres();
    }

    public TipoAusencia obtenerTipoPorNombre(String nombre) {
        return tipoDAO.obtenerPorNombre(nombre);
    }

    public boolean registrarSolicitud(SolicitudAusencia solicitud) {

        if (solicitud.getFechaInicio() == null || solicitud.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        if (solicitud.getFechaFin().isBefore(solicitud.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio.");
        }

        if (solicitudDAO.existeSolapamiento(
                solicitud.getEmpleado().getDni(),
                solicitud.getFechaInicio(),
                solicitud.getFechaFin(),
                0L)) {
            throw new IllegalStateException(
                    "Ya tienes una solicitud aprobada o pendiente en esas fechas.");
        }

        if ("Vacaciones".equalsIgnoreCase(solicitud.getTipoAusencia().getNombre())) {
            String dni           = solicitud.getEmpleado().getDni();
            int    disponibles   = empleadoDAO.obtenerDiasDisponiblesPorDni(dni);
            int    comprometidos = solicitudDAO.obtenerDiasVacacionesPedidosPorDni(dni);
            int    reales        = disponibles - comprometidos;

            if (solicitud.getDiasSolicitados() > reales) {
                throw new IllegalStateException(
                        "Días insuficientes. Tienes " + reales
                                + " días disponibles y estás solicitando "
                                + solicitud.getDiasSolicitados() + ".");
            }
        }

        return solicitudDAO.insertar(solicitud);
    }

    public String obtenerMensajeConfirmacion(String dni,
                                             String tipoNombre,
                                             LocalDate desde,
                                             LocalDate hasta) {

        if (solicitudDAO.existeSolapamiento(dni, desde, hasta, 0L)) {
            throw new IllegalStateException(
                    "Ya tienes una solicitud aprobada o pendiente en esas fechas.");
        }

        if ("Vacaciones".equalsIgnoreCase(tipoNombre)) {
            long diasSolicitados = ChronoUnit.DAYS.between(desde, hasta) + 1;
            int  disponibles     = empleadoDAO.obtenerDiasDisponiblesPorDni(dni);
            int  comprometidos   = solicitudDAO.obtenerDiasVacacionesPedidosPorDni(dni);
            int  reales          = disponibles - comprometidos;

            if (diasSolicitados > reales) {
                throw new IllegalStateException(
                        "Días insuficientes. Tienes " + reales
                                + " días disponibles y estás solicitando " + diasSolicitados + ".");
            }

            return construirMensajeVacaciones(dni, reales - diasSolicitados);
        }

        return "";
    }

    private String construirMensajeVacaciones(String dni, long diasRestantes) {
        ArrayList<SolicitudAusencia> pendientes = solicitudDAO.obtenerPendientesPorDni(dni);
        ArrayList<SolicitudAusencia> pendientesVacaciones = new ArrayList<>();
        for (SolicitudAusencia p : pendientes) {
            if ("Vacaciones".equalsIgnoreCase(p.getTipoAusencia().getNombre())) {
                pendientesVacaciones.add(p);
            }
        }

        boolean hayPendientes = !pendientesVacaciones.isEmpty();
        StringBuilder sb = new StringBuilder();

        if (diasRestantes == 0) {
            if (hayPendientes) {
                sb.append("Si se aprueba esta y otras solicitudes pendientes, "
                        + "habrás agotado todos tus días disponibles.");
            } else {
                sb.append("Si se aprueba esta solicitud, "
                        + "habrás agotado todos tus días disponibles.");
            }
        } else {
            if (hayPendientes) {
                sb.append("Te quedarían ").append(diasRestantes)
                        .append(" días disponibles si se aprueba la solicitud y otras pendientes.");
            } else {
                sb.append("Te quedarían ").append(diasRestantes)
                        .append(" días disponibles si se aprueba la solicitud.");
            }
        }

        if (hayPendientes) {
            sb.append("\n\nSolicitudes de vacaciones pendientes:");
            for (SolicitudAusencia p : pendientesVacaciones) {
                sb.append("\n· ID ").append(p.getId())
                        .append(" — ").append(p.getDiasSolicitados()).append(" días");
            }
        }
        return sb.toString();
    }

    public boolean aprobarSolicitud(SolicitudAusencia solicitud,
                                    Usuario admin,
                                    String comentario) {

        boolean okEstado = solicitudDAO.cambiarEstado(
                solicitud.getId(), "APROBADA", comentario);
        if (!okEstado) return false;

        if ("Vacaciones".equalsIgnoreCase(solicitud.getTipoAusencia().getNombre())) {
            String dni        = solicitud.getEmpleado().getDni();
            int    disponibles = empleadoDAO.obtenerDiasDisponiblesPorDni(dni);
            empleadoDAO.actualizarDiasVacacionesDisponibles(
                    dni,
                    disponibles - solicitud.getDiasSolicitados()
            );
        }

        return registroDAO.insertar(new RegistroAusencia(
                null,
                solicitud.getEmpleado(),
                admin.getEmpleado(),
                solicitud,
                solicitud.getTipoAusencia(),
                LocalDateTime.now(),
                comentario
        ));
    }

    public boolean rechazarSolicitud(SolicitudAusencia solicitud,
                                     Usuario admin,
                                     String comentario) {

        if (comentario == null || comentario.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio.");
        }

        boolean okEstado = solicitudDAO.cambiarEstado(
                solicitud.getId(), "RECHAZADA", comentario);
        if (!okEstado) return false;

        return registroDAO.insertar(new RegistroAusencia(
                null,
                solicitud.getEmpleado(),
                admin.getEmpleado(),
                solicitud,
                solicitud.getTipoAusencia(),
                LocalDateTime.now(),
                comentario
        ));
    }

    public boolean cancelarSolicitud(SolicitudAusencia solicitud) {
        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar solicitudes en estado PENDIENTE.");
        }
        return solicitudDAO.eliminar(solicitud.getId());
    }

    public ArrayList<SolicitudAusencia> obtenerPendientes() {
        return solicitudDAO.obtenerPorEstado("PENDIENTE");
    }

    public ArrayList<SolicitudAusencia> obtenerPorDni(String dni) {
        return solicitudDAO.obtenerPorDni(dni);
    }

    public int obtenerDiasDisponibles(String dni) {
        int total         = empleadoDAO.obtenerDiasDisponiblesPorDni(dni);
        int comprometidos = solicitudDAO.obtenerDiasPedidosPorDni(dni);
        return total - comprometidos;
    }
}