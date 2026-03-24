package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubcategoriaService {

    public void insertarSubcategoria(
            String idCategoria, String nombre, String descripcion,
            boolean indicadorActivo, boolean esDefecto, String creadoPor) {

        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_insertar_subcategoria(?,?,?,?,?,?)}")) {
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
        } finally { cerrar(conn); }
    }

    public void actualizarSubcategoria(
            String idSubcategoria, String nombre, String descripcion,
            boolean indicadorActivo, String modificadoPor) {

        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_actualizar_subcategoria(?,?,?,?,?)}")) {
            stmt.setString(1, idSubcategoria);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setBoolean(4, indicadorActivo);
            stmt.setString(5, modificadoPor);
            stmt.execute();
            System.out.println("Subcategoria actualizada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar subcategoria: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void eliminarSubcategoria(String idSubcategoria) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_eliminar_subcategoria(?)}")) {
            stmt.setString(1, idSubcategoria);
            stmt.execute();
            System.out.println("Subcategoria eliminada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar subcategoria: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void listarSubcategoriasPorCategoria(String idCategoria) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_subcategorias_categoria(?)}")) {
            stmt.setString(1, idCategoria);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== SUBCATEGORIAS ==========");
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                System.out.println("ID         : " + rs.getString("id_subcategoria"));
                System.out.println("Nombre     : " + rs.getString("nombre_subcategoria"));
                System.out.println("Descripcion: " + rs.getString("descripcion"));
                System.out.println("Activa     : " + (rs.getBoolean("indicador_activo") ? "Si" : "No"));
                System.out.println("Por defecto: " + (rs.getBoolean("es_defecto") ? "Si" : "No"));
                System.out.println("------------------------------------");
            }
            if (!hay) System.out.println("No hay subcategorias para esta categoria.");
        } catch (SQLException e) {
            System.out.println("Error al listar subcategorias: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void consultarSubcategoria(String idSubcategoria) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_consultar_subcategoria(?)}")) {
            stmt.setString(1, idSubcategoria);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== SUBCATEGORIA ==========");
                System.out.println("ID          : " + rs.getString("id_subcategoria"));
                System.out.println("ID Categoria: " + rs.getString("id_categoria"));
                System.out.println("Nombre      : " + rs.getString("nombre_subcategoria"));
                System.out.println("Descripcion : " + rs.getString("descripcion"));
                System.out.println("Activa      : " + (rs.getBoolean("indicador_activo") ? "Si" : "No"));
                System.out.println("Por defecto : " + (rs.getBoolean("es_defecto") ? "Si" : "No"));
                System.out.println("Creado por  : " + rs.getString("creado_por"));
                System.out.println("Creado en   : " + rs.getString("creado_en"));
            } else {
                System.out.println("Subcategoria no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar subcategoria: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
