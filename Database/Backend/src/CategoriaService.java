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
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_insertar_categoria(?,?,?,?,?)}")) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setString(3, tipo);
            stmt.setString(4, idUsuario);
            stmt.setString(5, creadoPor);
            stmt.execute();
            System.out.println("Categoria insertada correctamente.");
            System.out.println("Nota: el trigger creo la subcategoria 'General' automaticamente.");
        } catch (SQLException e) {
            System.out.println("Error al insertar categoria: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void actualizarCategoria(
            String idCategoria,
            String nombre,
            String descripcion,
            String modificadoPor
    ) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_actualizar_categoria(?,?,?,?)}")) {
            stmt.setString(1, idCategoria);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setString(4, modificadoPor);
            stmt.execute();
            System.out.println("Categoria actualizada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar categoria: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void eliminarCategoria(String idCategoria) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_eliminar_categoria(?)}")) {
            stmt.setString(1, idCategoria);
            stmt.execute();
            System.out.println("Categoria eliminada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoria: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void listarCategorias(String idUsuario, String tipo) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_categorias(?,?)}")) {
            stmt.setString(1, idUsuario);
            if (tipo == null || tipo.isBlank()) stmt.setNull(2, java.sql.Types.VARCHAR);
            else stmt.setString(2, tipo);

            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== LISTA DE CATEGORIAS ==========");
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                System.out.println("ID     : " + rs.getString("id_categoria"));
                System.out.println("Nombre : " + rs.getString("nombre_categoria"));
                System.out.println("Tipo   : " + rs.getString("tipo_categoria"));
                System.out.println("Usuario: " + rs.getString("primer_nombre") + " " + rs.getString("primer_apellido"));
                System.out.println("----------------------------------------");
            }
            if (!hay) System.out.println("No hay categorias para mostrar.");
        } catch (SQLException e) {
            System.out.println("Error al listar categorias: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void consultarCategoria(String idCategoria) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_consultar_categoria(?)}")) {
            stmt.setString(1, idCategoria);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== CATEGORIA ==========");
                System.out.println("ID          : " + rs.getString("id_categoria"));
                System.out.println("Nombre      : " + rs.getString("nombre_categoria"));
                System.out.println("Descripcion : " + rs.getString("descripcion"));
                System.out.println("Tipo        : " + rs.getString("tipo_categoria"));
                System.out.println("Usuario     : " + rs.getString("primer_nombre") + " " + rs.getString("primer_apellido"));
                System.out.println("Creado por  : " + rs.getString("creado_por"));
                System.out.println("Creado en   : " + rs.getString("creado_en"));
            } else {
                System.out.println("Categoria no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar categoria: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
