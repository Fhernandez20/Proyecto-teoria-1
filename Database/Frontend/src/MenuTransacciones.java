package menu;

import service.TransaccionService;
import util.InputHelper;

public class MenuTransacciones {
    private final TransaccionService transaccionService = new TransaccionService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║            GESTION DE TRANSACCIONES          ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Registrar transaccion                    ║");
            System.out.println("  ║  2. Listar transacciones por usuario         ║");
            System.out.println("  ║  3. Consultar transaccion por ID             ║");
            System.out.println("  ║  4. Actualizar transaccion                   ║");
            System.out.println("  ║  5. Eliminar transaccion                     ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver                                   ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");
            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");
            switch (opcion) {
                case 1 -> insertarTransaccion();
                case 2 -> listarTransacciones();
                case 3 -> { String id = InputHelper.leerTexto("  ID de la transaccion: ");
                             transaccionService.consultarTransaccion(id); }
                case 4 -> actualizarTransaccion();
                case 5 -> { String id = InputHelper.leerTexto("  ID de la transaccion a eliminar: ");
                             transaccionService.eliminarTransaccion(id); }
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarTransaccion() {
        System.out.println("\n  -- Nueva Transaccion --");
        String idUsuario  = InputHelper.leerTexto("  ID del usuario: ");
        String idPres     = InputHelper.leerTexto("  ID del presupuesto: ");
        int anio          = InputHelper.leerInt("  Anio de imputacion (ej. 2026): ");
        int mes           = InputHelper.leerInt("  Mes de imputacion (1-12): ");
        String idSub      = InputHelper.leerTexto("  ID de la subcategoria: ");
        String idObli     = InputHelper.leerTextoOpcional("  ID obligacion (Enter para omitir): ");
        String tipo       = elegirTipo();
        String desc       = InputHelper.leerTexto("  Descripcion: ");
        double monto      = InputHelper.leerDouble("  Monto (L.): ");
        String fecha      = InputHelper.leerFecha("  Fecha real de la transaccion");
        String metodo     = elegirMetodo();
        String factura    = InputHelper.leerTextoOpcional("  Num. factura (Enter para omitir): ");
        String obs        = InputHelper.leerTextoOpcional("  Observaciones (Enter para omitir): ");
        String creadoPor  = InputHelper.leerTexto("  Creado por: ");
        transaccionService.insertarTransaccion(idUsuario, idPres, anio, mes, idSub,
                idObli, tipo, desc, monto, fecha, metodo, factura, obs, creadoPor);
    }

    private void listarTransacciones() {
        String idUsuario = InputHelper.leerTexto("  ID del usuario: ");
        String anioStr   = InputHelper.leerTextoOpcional("  Anio (Enter para todos): ");
        String mesStr    = InputHelper.leerTextoOpcional("  Mes  (Enter para todos): ");
        Integer anio = null, mes = null;
        try { if (anioStr != null) anio = Integer.parseInt(anioStr); } catch (NumberFormatException ignored) {}
        try { if (mesStr  != null) mes  = Integer.parseInt(mesStr);  } catch (NumberFormatException ignored) {}
        transaccionService.listarTransaccionesUsuario(idUsuario, anio, mes);
    }

    private void actualizarTransaccion() {
        System.out.println("\n  -- Actualizar Transaccion --");
        String id    = InputHelper.leerTexto("  ID de la transaccion: ");
        int anio     = InputHelper.leerInt("  Nuevo anio de imputacion: ");
        int mes      = InputHelper.leerInt("  Nuevo mes de imputacion (1-12): ");
        String desc  = InputHelper.leerTexto("  Nueva descripcion: ");
        double monto = InputHelper.leerDouble("  Nuevo monto (L.): ");
        String fecha = InputHelper.leerFecha("  Nueva fecha real");
        String met   = elegirMetodo();
        String fac   = InputHelper.leerTextoOpcional("  Num. factura (Enter para omitir): ");
        String obs   = InputHelper.leerTextoOpcional("  Observaciones (Enter para omitir): ");
        String mod   = InputHelper.leerTexto("  Modificado por: ");
        transaccionService.actualizarTransaccion(id, anio, mes, desc, monto, fecha, met, fac, obs, mod);
    }

    private String elegirTipo() {
        while (true) {
            System.out.println("  Tipo: 1=ingreso  2=gasto  3=ahorro");
            int op = InputHelper.leerInt("  Selecciona: ");
            switch (op) { case 1: return "ingreso"; case 2: return "gasto"; case 3: return "ahorro";
                          default: System.out.println("  Opcion invalida."); }
        }
    }

    private String elegirMetodo() {
        while (true) {
            System.out.println("  Metodo: 1=efectivo  2=tarjeta_debito  3=tarjeta_credito  4=transferencia");
            int op = InputHelper.leerInt("  Selecciona: ");
            switch (op) { case 1: return "efectivo"; case 2: return "tarjeta_debito";
                          case 3: return "tarjeta_credito"; case 4: return "transferencia";
                          default: System.out.println("  Opcion invalida."); }
        }
    }
}
