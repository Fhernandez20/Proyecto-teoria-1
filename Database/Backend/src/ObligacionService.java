package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObligacionService {
    public void insertarObligacion(
            String idUsuario, String idSubcategoria, String nombre,
            String descripcion, double monto, int diaVencimiento,
            boolean vigente, String fechaInicio, String fechaFin, String creadoPor) {

        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_insertar_obligacion(?,?,?,?,?,?,?,?,?,?)}")) {
            stmt.setString(1, idUsuario);
            stmt.setString(2, idSubcategoria);
            stmt.setString(3, nombre);
            stmt.setString(4, descripcion);
            stmt.setDouble(5, monto);
            stmt.setInt(6, diaVencimiento);
            stmt.setBoolean(7, vigente);
            stmt.setString(8, fechaInicio);
            if (fechaFin == null || fechaFin.isBlank()) stmt.setNull(9, java.sql.Types.DATE);
            else stmt.setString(9, fechaFin);
            stmt.setString(10, creadoPor);
            stmt.execute();
            System.out.println("Obligacion insertada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al insertar obligacion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void actualizarObligacion(
            String idObligacion, String nombre, String descripcion,
            double monto, int diaVencimiento, String fechaFin,
            boolean activo, String modificadoPor) {

        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_actualizar_obligacion(?,?,?,?,?,?,?,?)}")) {
            stmt.setString(1, idObligacion);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setDouble(4, monto);
            stmt.setInt(5, diaVencimiento);
            if (fechaFin == null || fechaFin.isBlank()) stmt.setNull(6, java.sql.Types.DATE);
            else stmt.setString(6, fechaFin);
            stmt.setBoolean(7, activo);
            stmt.setString(8, modificadoPor);
            stmt.execute();
            System.out.println("Obligacion actualizada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar obligacion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void eliminarObligacion(String idObligacion) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_eliminar_obligacion(?)}")) {
            stmt.setString(1, idObligacion);
            stmt.execute();
            System.out.println("Obligacion desactivada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar obligacion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void listarObligacionesUsuario(String idUsuario, Boolean vigente) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_obligaciones_usuario(?,?)}")) {
            stmt.setString(1, idUsuario);
            if (vigente == null) stmt.setNull(2, java.sql.Types.BOOLEAN);
            else stmt.setBoolean(2, vigente);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== LISTA DE OBLIGACIONES ==========");
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                System.out.println("ID          : " + rs.getString("id_obligacion"));
                System.out.println("Nombre      : " + rs.getString("nombre"));
                System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                System.out.println("Monto       : L. " + rs.getDouble("monto_fijo_mensual"));
                System.out.println("Vencimiento : dia " + rs.getInt("dia_vencimiento"));
                System.out.println("Vigente     : " + (rs.getBoolean("vigente") ? "Si" : "No"));
                System.out.println("--------------------------------------------");
            }
            if (!hay) System.out.println("No hay obligaciones para mostrar.");
        } catch (SQLException e) {
            System.out.println("Error al listar obligaciones: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void consultarObligacion(String idObligacion) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_consultar_obligacion(?)}")) {
            stmt.setString(1, idObligacion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== OBLIGACION ==========");
                System.out.println("ID          : " + rs.getString("id_obligacion"));
                System.out.println("Nombre      : " + rs.getString("nombre"));
                System.out.println("Descripcion : " + rs.getString("descripcion"));
                System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                System.out.println("Categoria   : " + rs.getString("nombre_categoria"));
                System.out.println("Monto       : L. " + rs.getDouble("monto_fijo_mensual"));
                System.out.println("Vencimiento : dia " + rs.getInt("dia_vencimiento"));
                System.out.println("Fecha inicio: " + rs.getString("fecha_inicio"));
                System.out.println("Fecha fin   : " + rs.getString("fecha_fin"));
                System.out.println("Vigente     : " + (rs.getBoolean("vigente") ? "Si" : "No"));
                System.out.println("Creado por  : " + rs.getString("creado_por"));
            } else {
                System.out.println("Obligacion no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar obligacion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
