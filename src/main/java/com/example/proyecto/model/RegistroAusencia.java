package com.example.proyecto.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class RegistroAusencia {

    private Long id;
    private Empleado empleadoSolicitud;
    private Empleado admin;
    private SolicitudAusencia solicitud;
    private TipoAusencia tipoSolicitud;
    private LocalDateTime fechaRegistro;
    private String comentarioAdmin;

    public RegistroAusencia() {};

    public RegistroAusencia(Long id, Empleado empleadoSolicitud, Empleado admin, SolicitudAusencia solicitud, TipoAusencia tipoSolicitud, LocalDateTime fechaRegistro, String comentarioAdmin) {
        this.id = id;
        this.empleadoSolicitud = empleadoSolicitud;
        this.admin = admin;
        this.solicitud = solicitud;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaRegistro = fechaRegistro;
        this.comentarioAdmin = comentarioAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleadoSolicitud() {
        return empleadoSolicitud;
    }

    public void setEmpleadoSolicitud(Empleado empleadoSolicitud) {
        this.empleadoSolicitud = empleadoSolicitud;
    }

    public Empleado getAdmin() {
        return admin;
    }

    public void setAdmin(Empleado admin) {
        this.admin = admin;
    }

    public SolicitudAusencia getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudAusencia solicitud) {
        this.solicitud = solicitud;
    }

    public TipoAusencia getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(TipoAusencia tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }

    @Override
    public String toString() {
        return "RegistroAusencia{" +
                "id=" + id +
                ", empleadoSolicitud=" + empleadoSolicitud +
                ", admin=" + admin +
                ", solicitud=" + solicitud +
                ", tipoSolicitud=" + tipoSolicitud +
                ", fechaRegistro=" + fechaRegistro +
                ", comentarioAdmin='" + comentarioAdmin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroAusencia that = (RegistroAusencia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
