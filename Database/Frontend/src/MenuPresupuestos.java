package menu;

import service.PresupuestoService;
import util.InputHelper;

public class MenuPresupuestos {
    private final PresupuestoService presupuestoService = new PresupuestoService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║            GESTION DE PRESUPUESTOS           ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Crear presupuesto completo               ║");
            System.out.println("  ║  2. Listar presupuestos por usuario          ║");
            System.out.println("  ║  3. Consultar presupuesto por ID             ║");
            System.out.println("  ║  4. Actualizar presupuesto                   ║");
            System.out.println("  ║  5. Cerrar presupuesto                       ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver                                   ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");
            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");
            switch (opcion) {
                case 1 -> crearPresupuesto();
                case 2 -> listarPresupuestos();
                case 3 -> { String id = InputHelper.leerTexto("  ID del presupuesto: ");
                             presupuestoService.consultarPresupuesto(id); }
                case 4 -> actualizarPresupuesto();
                case 5 -> { String id  = InputHelper.leerTexto("  ID del presupuesto a cerrar: ");
                             String mod = InputHelper.leerTexto("  Modificado por: ");
                             presupuestoService.cerrarPresupuesto(id, mod); }
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void crearPresupuesto() {
        System.out.println("\n  -- Nuevo Presupuesto --");
        System.out.println("  NOTA: El JSON debe tener el formato:");
        System.out.println("  [{\"id_subcategoria\":\"SUB_xxx\",\"monto_mensual\":1000}]");
        String idUsuario = InputHelper.leerTexto("  ID del usuario: ");
        String nombre    = InputHelper.leerTexto("  Nombre descriptivo: ");
        String desc      = InputHelper.leerTexto("  Descripcion: ");
        String inicio    = InputHelper.leerFecha("  Fecha de inicio");
        String fin       = InputHelper.leerFecha("  Fecha de fin");
        String json      = InputHelper.leerTexto("  JSON subcategorias: ");
        String creadoPor = InputHelper.leerTexto("  Creado por: ");
        presupuestoService.crearPresupuestoCompleto(idUsuario, nombre, desc, inicio, fin, json, creadoPor);
    }

    private void listarPresupuestos() {
        String idUsuario = InputHelper.leerTexto("  ID del usuario: ");
        System.out.println("  Estado: 1=activo  2=cerrado  3=todos");
        int op = InputHelper.leerInt("  Selecciona: ");
        String estado = switch (op) {
            case 1 -> "activo"; case 2 -> "cerrado"; default -> null;
        };
        presupuestoService.listarPresupuestosUsuario(idUsuario, estado);
    }

    private void actualizarPresupuesto() {
        System.out.println("\n  -- Actualizar Presupuesto --");
        System.out.println("  NOTA: Febrero tiene 28 dias en 2026. Usa 2026-02-28 como fecha fin.");
        String id    = InputHelper.leerTexto("  ID del presupuesto: ");
        String nom   = InputHelper.leerTexto("  Nuevo nombre: ");
        String desc  = InputHelper.leerTexto("  Nueva descripcion: ");
        String ini   = InputHelper.leerFecha("  Nueva fecha de inicio");
        String fin   = InputHelper.leerFecha("  Nueva fecha de fin");
        String mod   = InputHelper.leerTexto("  Modificado por: ");
        presupuestoService.actualizarPresupuesto(id, nom, desc, ini, fin, mod);
    }
}
