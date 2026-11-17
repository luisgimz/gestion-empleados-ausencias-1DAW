package com.example.proyecto.database;

import com.example.proyecto.model.TipoAusencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TipoAusenciaDAO implements InterfazDAO<TipoAusencia> {

    private final Connection con;

    public TipoAusenciaDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();;
    }

    @Override
    public ArrayList<TipoAusencia> obtenerTodos() {

        ArrayList<TipoAusencia> lista = new ArrayList<>();

        String sql = "SELECT id, nombre FROM tipo_ausencia";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                TipoAusencia t = new TipoAusencia();

                t.setId(rs.getLong("id"));
                t.setNombre(rs.getString("nombre"));

                lista.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de ausencia: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public TipoAusencia obtenerPorId(long id) {

        TipoAusencia t = null;

        String sql = "SELECT id, nombre FROM tipo_ausencia WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    t = new TipoAusencia();
                    t.setId(rs.getLong("id"));
                    t.setNombre(rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de ausencia con id " + id + ": " + e.getMessage());
        }

        return t;
    }

    @Override
    public TipoAusencia obtenerPorNombre(String nombre) {

        TipoAusencia t = null;

        String sql = "SELECT id, nombre FROM tipo_ausencia WHERE nombre = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    t = new TipoAusencia();
                    t.setId(rs.getLong("id"));
                    t.setNombre(rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de ausencia con nombre " + nombre + ": " + e.getMessage());
        }

        return t;
    }

    @Override
    public boolean insertar(TipoAusencia item) {

        String sql = "INSERT INTO tipo_ausencia (nombre) VALUES (?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, item.getNombre());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar tipo de ausencia: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(TipoAusencia item) {

        String sql = "UPDATE tipo_ausencia SET nombre = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, item.getNombre());
            pstmt.setLong(2, item.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar tipo de ausencia: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {

        String sql = "DELETE FROM tipo_ausencia WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar tipo de ausencia: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String> obtenerNombres() {
        ArrayList<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre FROM tipo_ausencia";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de tipos de ausencia: " + e.getMessage());
        }
        return nombres;
    }
}