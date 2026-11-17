package com.example.proyecto.database;



import com.example.proyecto.model.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RolDAO implements InterfazDAO<Rol> {

    private final Connection con;

    public RolDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();;
    }

    @Override
    public ArrayList<Rol> obtenerTodos() {

        ArrayList<Rol> lista = new ArrayList<>();

        String sql = "SELECT id, nombre FROM rol";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Rol r = new Rol();

                r.setId(rs.getLong("id"));
                r.setNombre(rs.getString("nombre"));

                lista.add(r);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener roles: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public Rol obtenerPorId(long id) {

        Rol r = null;

        String sql = "SELECT id, nombre FROM rol WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    r = new Rol();
                    r.setId(rs.getLong("id"));
                    r.setNombre(rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener rol con id " + id + ": " + e.getMessage());
        }

        return r;
    }

    @Override
    public Rol obtenerPorNombre(String nombre) {

        Rol r = null;

        String sql = "SELECT id, nombre FROM rol WHERE nombre = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    r = new Rol();

                    r.setId(rs.getLong("id"));
                    r.setNombre(rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener rol con nombre " + nombre + ": " + e.getMessage());
        }

        return r;
    }

    @Override
    public boolean insertar(Rol item) {

        String sql = "INSERT INTO rol(nombre) VALUES(?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, item.getNombre());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar rol: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Rol item) {

        String sql = "UPDATE rol SET nombre = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, item.getNombre());
            pstmt.setLong(2, item.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar rol: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {
        String sql = "delete from rol where id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setLong(1,id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error de eliminar rol: "+e.getMessage());
            return false;
        }
    }
}