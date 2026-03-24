package menu;

import db.ConexionDB;
import service.ReporteService;
import util.InputHelper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuReportes {

    private final ReporteService reporteService = new ReporteService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║         MODULO DE REPORTES                   ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Balance Mensual (Ingresos/Gastos/Ahorro) ║");
            System.out.println("  ║  2. Distribucion de Gastos por Categoria     ║");
            System.out.println("  ║  3. Cumplimiento de Presupuesto              ║");
            System.out.println("  ║  4. Tendencia de Gastos (multi-mes)          ║");
            System.out.println("  ║  5. Estado de Obligaciones Fijas             ║");
            System.out.println("  ║  6. Progreso de Metas de Ahorro              ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver al menu principal                 ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");

            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> ejecutarReporte(1);
                case 2 -> ejecutarReporte(2);
                case 3 -> ejecutarReporte(3);
                case 4 -> ejecutarReporte(4);
                case 5 -> ejecutarReporte(5);
                case 6 -> ejecutarReporte(6);
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    // Punto de entrada unificado — pide ID usuario, busca su presupuesto activo
    private void ejecutarReporte(int numero) {
        System.out.println("\n  ─────────────────────────────────────────────");
        String idUsuario = InputHelper.leerTexto("  ID del usuario: ").trim();
        if (idUsuario.isEmpty()) { System.out.println("  ID invalido."); return; }

        // Buscar presupuesto activo del usuario
        String idPresupuesto = buscarPresupuestoActivo(idUsuario);
        if (idPresupuesto == null) {
            System.out.println("  El usuario no tiene un presupuesto activo.");
            System.out.println("  Puedes ingresar el ID manualmente:");
            idPresupuesto = InputHelper.leerTexto("  ID del presupuesto: ").trim();
            if (idPresupuesto.isEmpty()) return;
        } else {
            System.out.println("  Presupuesto activo encontrado: " + idPresupuesto);
        }

        switch (numero) {
            case 1, 2, 3, 5, 6 -> {
                int anio = InputHelper.leerInt("  Anio (ej. 2026): ");
                int mes  = InputHelper.leerInt("  Mes  (1-12): ");
                if (mes < 1 || mes > 12) { System.out.println("  Mes invalido."); return; }
                System.out.println("\n  Generando reporte...");
                switch (numero) {
                    case 1 -> reporteService.reporte1BalanceMensual(idUsuario, idPresupuesto, anio, mes);
                    case 2 -> reporteService.reporte2GastosPorCategoria(idUsuario, idPresupuesto, anio, mes);
                    case 3 -> reporteService.reporte3Cumplimiento(idUsuario, idPresupuesto, anio, mes);
                    case 5 -> reporteService.reporte5Obligaciones(idUsuario, idPresupuesto, anio, mes);
                    case 6 -> reporteService.reporte6Ahorros(idUsuario, idPresupuesto, anio, mes);
                }
            }
            case 4 -> {
                System.out.println("  Periodo DESDE:");
                int anioDesde = InputHelper.leerInt("  Anio desde: ");
                int mesDesde  = InputHelper.leerInt("  Mes desde (1-12): ");
                System.out.println("  Periodo HASTA:");
                int anioHasta = InputHelper.leerInt("  Anio hasta: ");
                int mesHasta  = InputHelper.leerInt("  Mes hasta (1-12): ");
                if (mesDesde < 1 || mesDesde > 12 || mesHasta < 1 || mesHasta > 12) {
                    System.out.println("  Mes invalido."); return;
                }
                System.out.println("\n  Generando reporte...");
                reporteService.reporte4Tendencia(idUsuario, idPresupuesto,
                        anioDesde, mesDesde, anioHasta, mesHasta);
            }
        }
    }

    // Busca el presupuesto activo del usuario en la BD
    private String buscarPresupuestoActivo(String idUsuario) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) return null;
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_presupuestos_usuario(?,?)}")) {
            stmt.setString(1, idUsuario);
            stmt.setString(2, "activo");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("id_presupuesto");
        } catch (SQLException e) {
            // silencioso — el menú maneja el null
        } finally {
            try { conn.close(); } catch (SQLException ignored) {}
        }
        return null;
    }
}
