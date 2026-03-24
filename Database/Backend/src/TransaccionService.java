package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TransaccionService {

    public void insertarTransaccion(
            String idUsuario, String idPresupuesto, int anio, int mes,
            String idSubcategoria, String idObligacion, String tipo,
            String descripcion, double monto, String fecha,
            String metodoPago, String numFactura, String observaciones,
            String creadoPor) {

        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall(
                "{CALL sp_insertar_transaccion(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {
            stmt.setString(1, idUsuario);
            stmt.setString(2, idPresupuesto);
            stmt.setInt(3, anio);
            stmt.setInt(4, mes);
            stmt.setString(5, idSubcategoria);
            stmt.setString(6, tipo);
            stmt.setString(7, descripcion);
            stmt.setDouble(8, monto);
            stmt.setString(9, fecha);
            stmt.setString(10, metodoPago);
            if (numFactura == null || numFactura.isBlank()) stmt.setNull(11, Types.VARCHAR);
            else stmt.setString(11, numFactura);
            if (observaciones == null || observaciones.isBlank()) stmt.setNull(12, Types.VARCHAR);
            else stmt.setString(12, observaciones);
            if (idObligacion == null || idObligacion.isBlank()) stmt.setNull(13, Types.VARCHAR);
            else stmt.setString(13, idObligacion);
            stmt.setString(14, creadoPor);
            stmt.execute();
            System.out.println("Transaccion registrada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al insertar transaccion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void actualizarTransaccion(
            String idTransaccion, int anio, int mes, String descripcion,
            double monto, String fecha, String metodoPago,
            String numFactura, String observaciones, String modificadoPor) {

        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall(
                "{CALL sp_actualizar_transaccion(?,?,?,?,?,?,?,?,?,?)}")) {
            stmt.setString(1, idTransaccion);
            stmt.setInt(2, anio);
            stmt.setInt(3, mes);
            stmt.setString(4, descripcion);
            stmt.setDouble(5, monto);
            stmt.setString(6, fecha);
            stmt.setString(7, metodoPago);
            if (numFactura == null || numFactura.isBlank()) stmt.setNull(8, Types.VARCHAR);
            else stmt.setString(8, numFactura);
            if (observaciones == null || observaciones.isBlank()) stmt.setNull(9, Types.VARCHAR);
            else stmt.setString(9, observaciones);
            stmt.setString(10, modificadoPor);
            stmt.execute();
            System.out.println("Transaccion actualizada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar transaccion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void eliminarTransaccion(String idTransaccion) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_eliminar_transaccion(?)}")) {
            stmt.setString(1, idTransaccion);
            stmt.execute();
            System.out.println("Transaccion eliminada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar transaccion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void listarTransaccionesUsuario(String idUsuario, Integer anio, Integer mes) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_transacciones_usuario(?,?,?)}")) {
            stmt.setString(1, idUsuario);
            if (anio == null) stmt.setNull(2, Types.INTEGER); else stmt.setInt(2, anio);
            if (mes == null)  stmt.setNull(3, Types.INTEGER); else stmt.setInt(3, mes);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== LISTA DE TRANSACCIONES ==========");
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                System.out.println("ID          : " + rs.getString("id_transaccion"));
                System.out.println("Periodo     : " + rs.getInt("mes") + "/" + rs.getInt("anio"));
                System.out.println("Tipo        : " + rs.getString("tipo"));
                System.out.println("Descripcion : " + rs.getString("descripcion"));
                System.out.println("Monto       : L. " + rs.getDouble("monto"));
                System.out.println("Fecha real  : " + rs.getString("fecha"));
                System.out.println("Metodo pago : " + rs.getString("metodo_pago"));
                System.out.println("Categoria   : " + rs.getString("nombre_categoria"));
                System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                System.out.println("Obligacion  : " + rs.getString("nombre_obligacion"));
                System.out.println("--------------------------------------------");
            }
            if (!hay) System.out.println("No hay transacciones para mostrar.");
        } catch (SQLException e) {
            System.out.println("Error al listar transacciones: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    public void consultarTransaccion(String idTransaccion) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_consultar_transaccion(?)}")) {
            stmt.setString(1, idTransaccion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== TRANSACCION ==========");
                System.out.println("ID           : " + rs.getString("id_transaccion"));
                System.out.println("Presupuesto  : " + rs.getString("id_presupuesto"));
                System.out.println("Periodo      : " + rs.getInt("mes") + "/" + rs.getInt("anio"));
                System.out.println("Tipo         : " + rs.getString("tipo"));
                System.out.println("Descripcion  : " + rs.getString("descripcion"));
                System.out.println("Monto        : L. " + rs.getDouble("monto"));
                System.out.println("Fecha real   : " + rs.getString("fecha"));
                System.out.println("Metodo pago  : " + rs.getString("metodo_pago"));
                System.out.println("Factura      : " + rs.getString("num_factura"));
                System.out.println("Observaciones: " + rs.getString("observaciones"));
                System.out.println("Categoria    : " + rs.getString("nombre_categoria"));
                System.out.println("Subcategoria : " + rs.getString("nombre_subcategoria"));
                System.out.println("Obligacion   : " + rs.getString("nombre_obligacion"));
                System.out.println("Creado por   : " + rs.getString("creado_por"));
            } else {
                System.out.println("Transaccion no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar transaccion: " + e.getMessage());
        } finally { cerrar(conn); }
    }

    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
