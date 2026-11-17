package com.example.proyecto.model;

import java.io.Serializable;
import java.util.Objects;

public class Rol extends BaseEntity implements Serializable {
    private String nombre;

    public Rol() {
    }

    public Rol(long id, String nombre) {
        super(id);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "nombre='" + nombre + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rol)) return false;
        Rol rol = (Rol) o;
        return id == rol.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
