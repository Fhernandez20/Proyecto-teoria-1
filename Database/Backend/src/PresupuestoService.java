package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PresupuestoService {

    public void crearPresupuestoCompleto(
            String idUsuario,
            String nombre,
            String descripcion,
            String periodoInicio,
            String periodoFin,
            String listaSubcategoriasJson,
            String creadoPor
    ) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_crear_presupuesto_completo(?,?,?,?,?,?,?)}")) {
            stmt.setString(1, idUsuario);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setString(4, periodoInicio);
            stmt.setString(5, periodoFin);
            stmt.setString(6, listaSubcategoriasJson);
            stmt.setString(7, creadoPor);
            stmt.execute();
            System.out.println("Presupuesto creado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear presupuesto: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void actualizarPresupuesto(
            String idPresupuesto,
            String nombre,
            String descripcion,
            String periodoInicio,
            String periodoFin,
            String modificadoPor
    ) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_actualizar_presupuesto(?,?,?,?,?,?)}")) {
            stmt.setString(1, idPresupuesto);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setString(4, periodoInicio);
            stmt.setString(5, periodoFin);
            stmt.setString(6, modificadoPor);
            stmt.execute();
            System.out.println("Presupuesto actualizado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar presupuesto: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void cerrarPresupuesto(String idPresupuesto, String modificadoPor) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_cerrar_presupuesto(?,?)}")) {
            stmt.setString(1, idPresupuesto);
            stmt.setString(2, modificadoPor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== RESUMEN DE CIERRE ==========");
                System.out.println("ID             : " + rs.getString("id_presupuesto"));
                System.out.println("Nombre         : " + rs.getString("nombre_descriptivo"));
                System.out.println("Estado         : " + rs.getString("estado"));
                System.out.println("Total ingresos : L. " + rs.getDouble("total_ingresos"));
                System.out.println("Total gastos   : L. " + rs.getDouble("total_gastos"));
                System.out.println("Total ahorro   : L. " + rs.getDouble("total_ahorro"));
                System.out.println("Total ejecutado: L. " + rs.getDouble("total_ejecutado"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar presupuesto: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void listarPresupuestosUsuario(String idUsuario, String estado) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_presupuestos_usuario(?,?)}")) {
            stmt.setString(1, idUsuario);
            if (estado == null || estado.isBlank()) stmt.setNull(2, Types.VARCHAR);
            else stmt.setString(2, estado);

            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== LISTA DE PRESUPUESTOS ==========");
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                System.out.println("ID      : " + rs.getString("id_presupuesto"));
                System.out.println("Nombre  : " + rs.getString("nombre_descriptivo"));
                System.out.println("Periodo : "
                        + rs.getInt("init_month") + "/" + rs.getInt("init_year")
                        + " - "
                        + rs.getInt("end_month") + "/" + rs.getInt("end_year"));
                System.out.println("Estado  : " + rs.getString("estado"));
                System.out.println("Ingresos: L. " + rs.getDouble("total_ingresos"));
                System.out.println("Gastos  : L. " + rs.getDouble("total_gastos"));
                System.out.println("Ahorro  : L. " + rs.getDouble("total_ahorro"));
                System.out.println("------------------------------------------");
            }
            if (!hay) System.out.println("No hay presupuestos para mostrar.");
        } catch (SQLException e) {
            System.out.println("Error al listar presupuestos: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void consultarPresupuesto(String idPresupuesto) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_consultar_presupuesto(?)}")) {
            stmt.setString(1, idPresupuesto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== PRESUPUESTO ==========");
                System.out.println("ID             : " + rs.getString("id_presupuesto"));
                System.out.println("Nombre         : " + rs.getString("nombre_descriptivo"));
                System.out.println("Periodo inicio : " + rs.getInt("init_month") + "/" + rs.getInt("init_year"));
                System.out.println("Periodo fin    : " + rs.getInt("end_month") + "/" + rs.getInt("end_year"));
                System.out.println("Estado         : " + rs.getString("estado"));
                System.out.println("Total ingresos : L. " + rs.getDouble("total_ingresos"));
                System.out.println("Total gastos   : L. " + rs.getDouble("total_gastos"));
                System.out.println("Total ahorro   : L. " + rs.getDouble("total_ahorro"));
                System.out.println("Fecha creacion : " + rs.getString("fecha_creacion"));
                System.out.println("Creado por     : " + rs.getString("creado_por"));
            } else {
                System.out.println("Presupuesto no encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar presupuesto: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
