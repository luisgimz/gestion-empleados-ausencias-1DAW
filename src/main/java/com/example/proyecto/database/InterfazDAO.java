package com.example.proyecto.database;

import java.util.ArrayList;

public interface InterfazDAO<T> {
    ArrayList<T> obtenerTodos();

    T obtenerPorId(long id);

    T obtenerPorNombre(String nombre);

    boolean insertar(T item);

    boolean actualizar(T item);

    boolean eliminar(long id);
}
