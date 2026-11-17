package com.example.proyecto.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Empleado extends Persona implements Serializable {
    private PuestoTrabajo puestoActual;
    private CategoriaLaboral categoriaReal;
    private String emailCorporativo;
    private String           emailPersonal;
    private String           telefono;
    private String           direccion;
    private String           numeroSs;
    private LocalDate        fechaNacimiento;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private String estadoLaboral;
    private int diasVacacionesDisponibles;
    private byte[] foto;

    public Empleado() { }


    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Empleado(String nombre, String apellidos, String dni,
                    PuestoTrabajo puestoActual, CategoriaLaboral categoriaReal,
                    String emailCorporativo, String emailPersonal,
                    String telefono, String direccion, String numeroSs,
                    LocalDate fechaNacimiento,
                    LocalDate fechaAlta, LocalDate fechaBaja,
                    String estadoLaboral) {
        super(nombre, apellidos, dni);
        this.puestoActual     = puestoActual;
        this.categoriaReal    = categoriaReal;
        this.emailCorporativo = emailCorporativo;
        this.emailPersonal    = emailPersonal;
        this.telefono         = telefono;
        this.direccion        = direccion;
        this.numeroSs         = numeroSs;
        this.fechaNacimiento  = fechaNacimiento;
        this.fechaAlta        = fechaAlta;
        this.fechaBaja        = fechaBaja;
        this.estadoLaboral    = estadoLaboral;
        this.diasVacacionesDisponibles = 22;
    }


    public PuestoTrabajo getPuestoActual() { return puestoActual; }
    public void setPuestoActual(PuestoTrabajo puestoActual) { this.puestoActual = puestoActual; }

    public CategoriaLaboral getCategoriaReal() { return categoriaReal; }
    public void setCategoriaReal(CategoriaLaboral categoriaReal) { this.categoriaReal = categoriaReal; }

    public String getEmailCorporativo() { return emailCorporativo; }
    public void setEmailCorporativo(String emailCorporativo) { this.emailCorporativo = emailCorporativo; }

    public String getEmailPersonal() {
        return emailPersonal;
    }

    public void setEmailPersonal(String emailPersonal) {
        this.emailPersonal = emailPersonal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumeroSs() {
        return numeroSs;
    }

    public void setNumeroSs(String numeroSs) {
        this.numeroSs = numeroSs;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }

    public String getEstadoLaboral() { return estadoLaboral; }
    public void setEstadoLaboral(String estadoLaboral) { this.estadoLaboral = estadoLaboral; }

    public int getDiasVacacionesDisponibles() { return diasVacacionesDisponibles; }
    public void setDiasVacacionesDisponibles(int d) { this.diasVacacionesDisponibles = d; }


    @Override
    public String toString() {
        return "Empleado{" +
                "puestoActual=" + puestoActual +
                ", categoriaReal=" + categoriaReal +
                ", emailCorporativo='" + emailCorporativo + '\'' +
                ", emailPersonal='" + emailPersonal + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", numeroSs='" + numeroSs + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", fechaAlta=" + fechaAlta +
                ", fechaBaja=" + fechaBaja +
                ", estadoLaboral='" + estadoLaboral + '\'' +
                ", diasVacacionesDisponibles=" + diasVacacionesDisponibles +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", dni='" + dni + '\'' +
                '}';
    }
}