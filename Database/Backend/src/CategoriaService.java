package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaService {

    public void insertarCategoria(
            String nombre,
            String descripcion,
            String tipo,
            String idUsuario,
            String creadoPor
    ) {
        String sql = "{CALL sp_insertar_categoria(?, ?, ?, ?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setString(3, tipo);
            stmt.setString(4, idUsuario);
            stmt.setString(5, creadoPor);

            stmt.execute();
            System.out.println("Categoria insertada correctamente.");
            System.out.println("Nota: el trigger debio crear la subcategoria 'General' automaticamente.");

        } catch (SQLException e) {
            System.out.println("Error al insertar categoria: " + e.getMessage());
        }
    }

    public void listarCategorias(String idUsuario, String tipo) {
        String sql = "{CALL sp_listar_categorias(?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idUsuario);

            if (tipo == null || tipo.trim().isEmpty()) {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(2, tipo);
            }

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                System.out.println("\n========== LISTA DE CATEGORIAS ==========");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("id_categoria"));
                    System.out.println("Nombre: " + rs.getString("nombre_categoria"));
                    System.out.println("Tipo: " + rs.getString("tipo_categoria"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Fecha creacion: " + rs.getString("creado_en"));
                    System.out.println("Usuario: " + rs.getString("primer_nombre") + " " + rs.getString("primer_apellido"));
                    System.out.println("----------------------------------------");
                }
            } else {
                System.out.println("No hay categorias para mostrar.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar categorias: " + e.getMessage());
        }
    }

    public void consultarCategoria(String idCategoria) {
        String sql = "{CALL sp_consultar_categoria(?)}";

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

                if (rs.next()) {
                    System.out.println("\n========== CATEGORIA ==========");
                    System.out.println("ID: " + rs.getString("id_categoria"));
                    System.out.println("Nombre: " + rs.getString("nombre_categoria"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Tipo: " + rs.getString("tipo_categoria"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Modificado por: " + rs.getString("modificado_por"));
                    System.out.println("Creado en: " + rs.getString("creado_en"));
                    System.out.println("Modificado en: " + rs.getString("modificado_en"));
                    System.out.println("Usuario propietario: " + rs.getString("primer_nombre") + " " + rs.getString("primer_apellido"));
                } else {
                    System.out.println("Categoria no encontrada.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar categoria: " + e.getMessage());
        }
    }
}
