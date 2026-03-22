package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PresupuestoService {

    public void listarPresupuestosUsuario(String idUsuario, String estado) {
        String sql = "{CALL sp_listar_presupuestos_usuario(?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idUsuario);

            if (estado == null || estado.trim().isEmpty()) {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(2, estado);
            }

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                System.out.println("\n========== LISTA DE PRESUPUESTOS ==========");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("id_presupuesto"));
                    System.out.println("Nombre: " + rs.getString("nombre_descriptivo"));
                    System.out.println("Periodo: " 
                            + rs.getInt("init_month") + "/" + rs.getInt("init_year")
                            + " - "
                            + rs.getInt("end_month") + "/" + rs.getInt("end_year"));
                    System.out.println("Estado: " + rs.getString("estado"));
                    System.out.println("Total ingresos: L. " + rs.getDouble("total_ingresos"));
                    System.out.println("Total gastos: L. " + rs.getDouble("total_gastos"));
                    System.out.println("Total ahorro: L. " + rs.getDouble("total_ahorro"));
                    System.out.println("Fecha creacion: " + rs.getString("fecha_creacion"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("------------------------------------------");
                }
            } else {
                System.out.println("No hay presupuestos para mostrar.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar presupuestos: " + e.getMessage());
        }
    }

    public void consultarPresupuesto(String idPresupuesto) {
        String sql = "{CALL sp_consultar_presupuesto(?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idPresupuesto);

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                if (rs.next()) {
                    System.out.println("\n========== PRESUPUESTO ==========");
                    System.out.println("ID: " + rs.getString("id_presupuesto"));
                    System.out.println("ID Usuario: " + rs.getString("id_usuario"));
                    System.out.println("Nombre: " + rs.getString("nombre_descriptivo"));
                    System.out.println("Periodo inicio: " + rs.getInt("init_month") + "/" + rs.getInt("init_year"));
                    System.out.println("Periodo fin: " + rs.getInt("end_month") + "/" + rs.getInt("end_year"));
                    System.out.println("Total ingresos: L. " + rs.getDouble("total_ingresos"));
                    System.out.println("Total gastos: L. " + rs.getDouble("total_gastos"));
                    System.out.println("Total ahorro: L. " + rs.getDouble("total_ahorro"));
                    System.out.println("Fecha creacion: " + rs.getString("fecha_creacion"));
                    System.out.println("Estado: " + rs.getString("estado"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Modificado por: " + rs.getString("modificado_por"));
                    System.out.println("Modificado en: " + rs.getString("modificado_en"));
                } else {
                    System.out.println("Presupuesto no encontrado.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar presupuesto: " + e.getMessage());
        }
    }

    public void crearPresupuestoCompleto(
        String idUsuario,
        String nombre,
        String descripcion,
        String periodoInicio,
        String periodoFin,
        String listaSubcategoriasJson,
        String creadoPor
) {
    String sql = "{CALL sp_crear_presupuesto_completo(?, ?, ?, ?, ?, ?, ?)}";

    try (Connection conn = ConexionDB.conectar();
         CallableStatement stmt = conn.prepareCall(sql)) {

        if (conn == null) {
            System.out.println("No se pudo abrir la conexion.");
            return;
        }

        stmt.setString(1, idUsuario);
        stmt.setString(2, nombre);
        stmt.setString(3, descripcion);
        stmt.setString(4, periodoInicio);
        stmt.setString(5, periodoFin);
        stmt.setString(6, listaSubcategoriasJson);
        stmt.setString(7, creadoPor);

        stmt.execute();
        System.out.println("Presupuesto completo creado correctamente.");

    } catch (SQLException e) {
        System.out.println("Error al crear presupuesto completo: " + e.getMessage());
    }
}
}