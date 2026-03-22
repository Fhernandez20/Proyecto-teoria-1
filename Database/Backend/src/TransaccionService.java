package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransaccionService {

    public void listarTransaccionesUsuario(String idUsuario, Integer anio, Integer mes) {
        String sql = "{CALL sp_listar_transacciones_usuario(?, ?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idUsuario);

            if (anio == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, anio);
            }

            if (mes == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, mes);
            }

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                System.out.println("\n========== LISTA DE TRANSACCIONES ==========");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("id_transaccion"));
                    System.out.println("Presupuesto: " + rs.getString("id_presupuesto"));
                    System.out.println("Periodo imputado: " + rs.getInt("mes") + "/" + rs.getInt("anio"));
                    System.out.println("Tipo: " + rs.getString("tipo"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Monto: L. " + rs.getDouble("monto"));
                    System.out.println("Fecha real: " + rs.getString("fecha"));
                    System.out.println("Metodo pago: " + rs.getString("metodo_pago"));
                    System.out.println("Categoria: " + rs.getString("nombre_categoria"));
                    System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                    System.out.println("Obligacion asociada: " + rs.getString("nombre_obligacion"));
                    System.out.println("--------------------------------------------");
                }
            } else {
                System.out.println("No hay transacciones para mostrar.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar transacciones: " + e.getMessage());
        }
    }

    public void consultarTransaccion(String idTransaccion) {
        String sql = "{CALL sp_consultar_transaccion(?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idTransaccion);

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                if (rs.next()) {
                    System.out.println("\n========== TRANSACCION ==========");
                    System.out.println("ID: " + rs.getString("id_transaccion"));
                    System.out.println("ID Usuario: " + rs.getString("id_usuario"));
                    System.out.println("ID Presupuesto: " + rs.getString("id_presupuesto"));
                    System.out.println("Anio: " + rs.getInt("anio"));
                    System.out.println("Mes: " + rs.getInt("mes"));
                    System.out.println("ID Subcategoria: " + rs.getString("id_subcategoria"));
                    System.out.println("Tipo: " + rs.getString("tipo"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Monto: L. " + rs.getDouble("monto"));
                    System.out.println("Fecha: " + rs.getString("fecha"));
                    System.out.println("Metodo pago: " + rs.getString("metodo_pago"));
                    System.out.println("Factura: " + rs.getString("num_factura"));
                    System.out.println("Observaciones: " + rs.getString("observaciones"));
                    System.out.println("Fecha registro: " + rs.getString("fecha_registro"));
                    System.out.println("Categoria: " + rs.getString("nombre_categoria"));
                    System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                    System.out.println("ID Obligacion: " + rs.getString("id_obligacion"));
                    System.out.println("Nombre obligacion: " + rs.getString("nombre_obligacion"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Modificado por: " + rs.getString("modificado_por"));
                    System.out.println("Creado en: " + rs.getString("creado_en"));
                    System.out.println("Modificado en: " + rs.getString("modificado_en"));
                } else {
                    System.out.println("Transaccion no encontrada.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar transaccion: " + e.getMessage());
        }
    }
}