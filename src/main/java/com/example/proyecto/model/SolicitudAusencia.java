package com.example.proyecto.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class SolicitudAusencia extends BaseEntity implements Serializable {

    private Empleado empleado;
    private TipoAusencia tipoAusencia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int diasSolicitados;
    private String motivo;
    private String estado;
    private String comentarioAdmin;
    private LocalDate fechaSolicitud;

    public SolicitudAusencia() { }

    public SolicitudAusencia(long id,
                             Empleado empleado,
                             TipoAusencia tipoAusencia,
                             LocalDate fechaInicio,
                             LocalDate fechaFin,
                             int diasSolicitados,
                             String motivo,
                             String estado,
                             String comentarioAdmin,
                             LocalDate fechaSolicitud) {
        super(id);
        this.empleado = empleado;
        this.tipoAusencia = tipoAusencia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasSolicitados = diasSolicitados;
        this.motivo = motivo;
        this.estado = estado;
        this.comentarioAdmin = comentarioAdmin;
        this.fechaSolicitud = fechaSolicitud;
    }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public TipoAusencia getTipoAusencia() { return tipoAusencia; }
    public void setTipoAusencia(TipoAusencia tipoAusencia) { this.tipoAusencia = tipoAusencia; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(int diasSolicitados) { this.diasSolicitados = diasSolicitados; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComentarioAdmin() { return comentarioAdmin; }
    public void setComentarioAdmin(String comentarioAdmin) { this.comentarioAdmin = comentarioAdmin; }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitudAusencia)) return false;
        SolicitudAusencia that = (SolicitudAusencia) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "SolicitudAusencia{" +
                "id=" + id +
                ", empleado=" + (empleado != null ? empleado.getNombre() + " " + empleado.getApellidos() : "null") +
                ", tipoAusencia=" + (tipoAusencia != null ? tipoAusencia.getNombre() : "null") +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", diasSolicitados=" + diasSolicitados +
                ", estado='" + estado + '\'' +
                ", motivo='" + motivo + '\'' +
                ", comentarioAdmin='" + comentarioAdmin + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                '}';
    }
}