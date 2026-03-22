package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubcategoriaService {

    public void insertarSubcategoria(
            String idCategoria,
            String nombre,
            String descripcion,
            boolean indicadorActivo,
            boolean esDefecto,
            String creadoPor
    ) {
        String sql = "{CALL sp_insertar_subcategoria(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idCategoria);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setBoolean(4, indicadorActivo);
            stmt.setBoolean(5, esDefecto);
            stmt.setString(6, creadoPor);

            stmt.execute();
            System.out.println("Subcategoria insertada correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al insertar subcategoria: " + e.getMessage());
        }
    }

    public void listarSubcategoriasPorCategoria(String idCategoria) {
        String sql = "{CALL sp_listar_subcategorias_categoria(?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idCategoria);

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                System.out.println("\n======= LISTA DE SUBCATEGORIAS =======");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("id_subcategoria"));
                    System.out.println("Categoria ID: " + rs.getString("id_categoria"));
                    System.out.println("Nombre: " + rs.getString("nombre_subcategoria"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Activo: " + (rs.getBoolean("indicador_activo") ? "Si" : "No"));
                    System.out.println("Es defecto: " + (rs.getBoolean("es_defecto") ? "Si" : "No"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("-------------------------------------");
                }
            } else {
                System.out.println("No hay subcategorias para mostrar.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar subcategorias: " + e.getMessage());
        }
    }

    public void consultarSubcategoria(String idSubcategoria) {
        String sql = "{CALL sp_consultar_subcategoria(?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idSubcategoria);

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                if (rs.next()) {
                    System.out.println("\n========== SUBCATEGORIA ==========");
                    System.out.println("ID: " + rs.getString("id_subcategoria"));
                    System.out.println("Categoria ID: " + rs.getString("id_categoria"));
                    System.out.println("Nombre: " + rs.getString("nombre_subcategoria"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Activo: " + (rs.getBoolean("indicador_activo") ? "Si" : "No"));
                    System.out.println("Es defecto: " + (rs.getBoolean("es_defecto") ? "Si" : "No"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Modificado por: " + rs.getString("modificado_por"));
                    System.out.println("Creado en: " + rs.getString("creado_en"));
                    System.out.println("Modificado en: " + rs.getString("modificado_en"));
                } else {
                    System.out.println("Subcategoria no encontrada.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar subcategoria: " + e.getMessage());
        }
    }
}