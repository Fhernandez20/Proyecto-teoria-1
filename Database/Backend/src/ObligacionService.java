package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObligacionService {

    public void insertarObligacion(
            String idUsuario,
            String idSubcategoria,
            String nombre,
            String descripcion,
            double montoFijoMensual,
            int diaVencimiento,
            boolean vigente,
            String fechaInicio,
            String fechaFin,
            String creadoPor
    ) {
        String sql = "{CALL sp_insertar_obligacion(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idUsuario);
            stmt.setString(2, idSubcategoria);
            stmt.setString(3, nombre);
            stmt.setString(4, descripcion);
            stmt.setDouble(5, montoFijoMensual);
            stmt.setInt(6, diaVencimiento);
            stmt.setBoolean(7, vigente);
            stmt.setString(8, fechaInicio);
            stmt.setString(9, fechaFin);
            stmt.setString(10, creadoPor);

            stmt.execute();
            System.out.println("Obligacion insertada correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al insertar obligacion: " + e.getMessage());
        }
    }

    public void listarObligacionesUsuario(String idUsuario, Boolean vigente) {
        String sql = "{CALL sp_listar_obligaciones_usuario(?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idUsuario);

            if (vigente == null) {
                stmt.setNull(2, java.sql.Types.BOOLEAN);
            } else {
                stmt.setBoolean(2, vigente);
            }

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                System.out.println("\n========== LISTA DE OBLIGACIONES ==========");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("id_obligacion"));
                    System.out.println("Nombre: " + rs.getString("nombre"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Monto fijo mensual: L. " + rs.getDouble("monto_fijo_mensual"));
                    System.out.println("Dia vencimiento: " + rs.getInt("dia_vencimiento"));
                    System.out.println("Vigente: " + (rs.getBoolean("vigente") ? "Si" : "No"));
                    System.out.println("Fecha inicio: " + rs.getString("fecha_inicio"));
                    System.out.println("Fecha fin: " + rs.getString("fecha_fin"));
                    System.out.println("Categoria: " + rs.getString("nombre_categoria"));
                    System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("------------------------------------------");
                }
            } else {
                System.out.println("No hay obligaciones para mostrar.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar obligaciones: " + e.getMessage());
        }
    }

    public void consultarObligacion(String idObligacion) {
        String sql = "{CALL sp_consultar_obligacion(?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idObligacion);

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                if (rs.next()) {
                    System.out.println("\n========== OBLIGACION ==========");
                    System.out.println("ID: " + rs.getString("id_obligacion"));
                    System.out.println("ID Usuario: " + rs.getString("id_usuario"));
                    System.out.println("ID Subcategoria: " + rs.getString("id_subcategoria"));
                    System.out.println("Nombre: " + rs.getString("nombre"));
                    System.out.println("Descripcion: " + rs.getString("descripcion"));
                    System.out.println("Monto fijo mensual: L. " + rs.getDouble("monto_fijo_mensual"));
                    System.out.println("Dia vencimiento: " + rs.getInt("dia_vencimiento"));
                    System.out.println("Vigente: " + (rs.getBoolean("vigente") ? "Si" : "No"));
                    System.out.println("Fecha inicio: " + rs.getString("fecha_inicio"));
                    System.out.println("Fecha fin: " + rs.getString("fecha_fin"));
                    System.out.println("Categoria: " + rs.getString("nombre_categoria"));
                    System.out.println("Subcategoria: " + rs.getString("nombre_subcategoria"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Modificado por: " + rs.getString("modificado_por"));
                    System.out.println("Creado en: " + rs.getString("creado_en"));
                    System.out.println("Modificado en: " + rs.getString("modificado_en"));
                } else {
                    System.out.println("Obligacion no encontrada.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar obligacion: " + e.getMessage());
        }
    }
}