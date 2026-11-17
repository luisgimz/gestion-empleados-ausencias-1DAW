package com.example.proyecto.database;


import com.example.proyecto.model.*;
import java.sql.*;
import java.util.ArrayList;

public class PuestoTrabajoDAO implements InterfazDAO<PuestoTrabajo> {

    private final Connection con;

    public PuestoTrabajoDAO() throws SQLException {
        this.con = ConexionBD.getInstance().getConexion();
    }

    @Override
    public ArrayList<PuestoTrabajo> obtenerTodos() {

        ArrayList<PuestoTrabajo> lista = new ArrayList<>();

        String sql = "SELECT p.id, p.nombre, p.descripcion_funciones, p.jornada, p.modalidad, p.activo, " +
                "s.id AS id_sede, s.empresa_id AS sede_empresa_id, s.tipo, s.email_contacto, s.calle, s.numero, s.cp, s.ciudad, s.provincia, " +
                "d.id AS id_departamento, d.empresa_id AS dep_empresa_id, d.codigo AS dep_codigo, d.nombre AS dep_nombre, d.descripcion AS dep_descripcion, " +
                "c.id AS id_categoria, c.empresa_id AS cat_empresa_id, c.convenio, c.grupo_profesional, c.nivel, c.descripcion AS cat_descripcion " +
                "FROM puesto_trabajo p " +
                "INNER JOIN sede s ON p.sede_id = s.id " +
                "INNER JOIN departamento d ON p.departamento_id = d.id " +
                "INNER JOIN categoria_laboral c ON p.categoria_referencia = c.id ";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                PuestoTrabajo p = new PuestoTrabajo(
                        rs.getLong("id"),

                        new Sede(
                                rs.getLong("id_sede"),
                                rs.getLong("sede_empresa_id"),
                                rs.getString("tipo"),
                                rs.getString("email_contacto"),
                                rs.getString("calle"),
                                rs.getString("numero"),
                                rs.getString("cp"),
                                rs.getString("ciudad"),
                                rs.getString("provincia")
                        ),

                        new Departamento(
                                rs.getLong("id_departamento"),
                                rs.getLong("dep_empresa_id"),
                                rs.getString("dep_codigo"),
                                rs.getString("dep_nombre"),
                                rs.getString("dep_descripcion")
                        ),

                        new CategoriaLaboral(
                                rs.getLong("id_categoria"),
                                rs.getLong("cat_empresa_id"),
                                rs.getString("convenio"),
                                rs.getString("grupo_profesional"),
                                rs.getString("nivel"),
                                rs.getString("cat_descripcion")
                        ),

                        rs.getString("nombre"),
                        rs.getString("descripcion_funciones"),
                        rs.getString("jornada"),
                        rs.getString("modalidad"),
                        rs.getBoolean("activo")
                );

                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener puestos: " + e.getMessage());
        }

        return lista;
    }


    @Override
    public PuestoTrabajo obtenerPorId(long id) {

        PuestoTrabajo p = null;

        String sql = "SELECT p.id, p.nombre, p.descripcion_funciones, p.jornada, p.modalidad, p.activo, " +
                "s.id AS id_sede, s.empresa_id AS sede_empresa_id, s.tipo, s.email_contacto, s.calle, s.numero, s.cp, s.ciudad, s.provincia, " +
                "d.id AS id_departamento, d.empresa_id AS dep_empresa_id, d.codigo AS dep_codigo, d.nombre AS dep_nombre, d.descripcion AS dep_descripcion, " +
                "c.id AS id_categoria, c.empresa_id AS cat_empresa_id, c.convenio, c.grupo_profesional, c.nivel, c.descripcion AS cat_descripcion " +
                "FROM puesto_trabajo p " +
                "INNER JOIN sede s ON p.sede_id = s.id " +
                "INNER JOIN departamento d ON p.departamento_id = d.id " +
                "INNER JOIN categoria_laboral c ON p.categoria_referencia = c.id " +
                "WHERE p.id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    p = new PuestoTrabajo(
                            rs.getLong("id"),

                            new Sede(
                                    rs.getLong("id_sede"),
                                    rs.getLong("sede_empresa_id"),
                                    rs.getString("tipo"),
                                    rs.getString("email_contacto"),
                                    rs.getString("calle"),
                                    rs.getString("numero"),
                                    rs.getString("cp"),
                                    rs.getString("ciudad"),
                                    rs.getString("provincia")
                            ),

                            new Departamento(
                                    rs.getLong("id_departamento"),
                                    rs.getLong("dep_empresa_id"),
                                    rs.getString("dep_codigo"),
                                    rs.getString("dep_nombre"),
                                    rs.getString("dep_descripcion")
                            ),

                            new CategoriaLaboral(
                                    rs.getLong("id_categoria"),
                                    rs.getLong("cat_empresa_id"),
                                    rs.getString("convenio"),
                                    rs.getString("grupo_profesional"),
                                    rs.getString("nivel"),
                                    rs.getString("cat_descripcion")
                            ),

                            rs.getString("nombre"),
                            rs.getString("descripcion_funciones"),
                            rs.getString("jornada"),
                            rs.getString("modalidad"),
                            rs.getBoolean("activo")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener puesto con id " + id + ": " + e.getMessage());
        }

        return p;
    }

    @Override
    public PuestoTrabajo obtenerPorNombre(String nombre) {

        PuestoTrabajo p = null;

        String sql = "SELECT p.id, p.nombre, p.descripcion_funciones, p.jornada, p.modalidad, p.activo, " +
                "s.id AS id_sede, s.empresa_id AS sede_empresa_id, s.tipo, s.email_contacto, s.calle, s.numero, s.cp, s.ciudad, s.provincia, " +
                "d.id AS id_departamento, d.empresa_id AS dep_empresa_id, d.codigo AS dep_codigo, d.nombre AS dep_nombre, d.descripcion AS dep_descripcion, " +
                "c.id AS id_categoria, c.empresa_id AS cat_empresa_id, c.convenio, c.grupo_profesional, c.nivel, c.descripcion AS cat_descripcion " +
                "FROM puesto_trabajo p " +
                "INNER JOIN sede s ON p.sede_id = s.id " +
                "INNER JOIN departamento d ON p.departamento_id = d.id " +
                "INNER JOIN categoria_laboral c ON p.categoria_referencia = c.id " +
                "WHERE p.nombre = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    p = new PuestoTrabajo(
                            rs.getLong("id"),

                            new Sede(
                                    rs.getLong("id_sede"),
                                    rs.getLong("sede_empresa_id"),
                                    rs.getString("tipo"),
                                    rs.getString("email_contacto"),
                                    rs.getString("calle"),
                                    rs.getString("numero"),
                                    rs.getString("cp"),
                                    rs.getString("ciudad"),
                                    rs.getString("provincia")
                            ),

                            new Departamento(
                                    rs.getLong("id_departamento"),
                                    rs.getLong("dep_empresa_id"),
                                    rs.getString("dep_codigo"),
                                    rs.getString("dep_nombre"),
                                    rs.getString("dep_descripcion")
                            ),

                            new CategoriaLaboral(
                                    rs.getLong("id_categoria"),
                                    rs.getLong("cat_empresa_id"),
                                    rs.getString("convenio"),
                                    rs.getString("grupo_profesional"),
                                    rs.getString("nivel"),
                                    rs.getString("cat_descripcion")
                            ),

                            rs.getString("nombre"),
                            rs.getString("descripcion_funciones"),
                            rs.getString("jornada"),
                            rs.getString("modalidad"),
                            rs.getBoolean("activo")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener puesto con nombre " + nombre + ": " + e.getMessage());
        }

        return p;
    }


    @Override
    public boolean insertar(PuestoTrabajo p) {

        // El id es AUTO_INCREMENT: no se incluye en el INSERT
        String sql = "INSERT INTO puesto_trabajo (sede_id, departamento_id, categoria_referencia, nombre, descripcion_funciones, jornada, modalidad, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, p.getSede().getId());
            pstmt.setLong(2, p.getDepartamento().getId());
            pstmt.setLong(3, p.getCategoriaReferencia().getId());
            pstmt.setString(4, p.getNombre());
            pstmt.setString(5, p.getDescripcionFunciones());
            pstmt.setString(6, p.getJornada());
            pstmt.setString(7, p.getModalidad());
            pstmt.setBoolean(8, p.isActivo());

            int filas = pstmt.executeUpdate();

            //  recuperar id generado y asignarlo al objeto usa lo de arriba de return_generated_keys
            if (filas > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setId(keys.getLong(1));
                    }
                }
            }

            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar puesto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(PuestoTrabajo p) {

        String sql = "UPDATE puesto_trabajo SET sede_id=?, departamento_id=?, categoria_referencia=?, nombre=?, descripcion_funciones=?, jornada=?, modalidad=?, activo=? WHERE id=?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, p.getSede().getId());
            pstmt.setLong(2, p.getDepartamento().getId());
            pstmt.setLong(3, p.getCategoriaReferencia().getId());
            pstmt.setString(4, p.getNombre());
            pstmt.setString(5, p.getDescripcionFunciones());
            pstmt.setString(6, p.getJornada());
            pstmt.setString(7, p.getModalidad());
            pstmt.setBoolean(8, p.isActivo());
            pstmt.setLong(9, p.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar puesto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(long id) {

        String sql = "DELETE FROM puesto_trabajo WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar puesto: " + e.getMessage());
            return false;
        }
    }
}