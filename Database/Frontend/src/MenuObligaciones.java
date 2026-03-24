package menu;

import service.ObligacionService;
import util.InputHelper;

public class MenuObligaciones {
    private final ObligacionService obligacionService = new ObligacionService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║          GESTION DE OBLIGACIONES FIJAS       ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Insertar obligacion fija                 ║");
            System.out.println("  ║  2. Listar obligaciones por usuario          ║");
            System.out.println("  ║  3. Consultar obligacion por ID              ║");
            System.out.println("  ║  4. Actualizar obligacion                    ║");
            System.out.println("  ║  5. Desactivar obligacion                    ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver                                   ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");
            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");
            switch (opcion) {
                case 1 -> insertarObligacion();
                case 2 -> listarObligaciones();
                case 3 -> { String id = InputHelper.leerTexto("  ID de la obligacion: ");
                             obligacionService.consultarObligacion(id); }
                case 4 -> actualizarObligacion();
                case 5 -> { String id = InputHelper.leerTexto("  ID de la obligacion a desactivar: ");
                             obligacionService.eliminarObligacion(id); }
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarObligacion() {
        System.out.println("\n  -- Nueva Obligacion Fija --");
        String idUsuario  = InputHelper.leerTexto("  ID del usuario: ");
        String idSub      = InputHelper.leerTexto("  ID de la subcategoria: ");
        String nombre     = InputHelper.leerTexto("  Nombre: ");
        String desc       = InputHelper.leerTexto("  Descripcion: ");
        double monto      = InputHelper.leerDouble("  Monto mensual (L.): ");
        int diaVenc       = InputHelper.leerInt("  Dia de vencimiento (1-31): ");
        boolean vigente   = InputHelper.leerBooleanEstado("  Vigente");
        String fechaIni   = InputHelper.leerFecha("  Fecha de inicio");
        String fechaFin   = InputHelper.leerTextoOpcional("  Fecha de fin (YYYY-MM-DD o Enter = indefinida): ");
        String creadoPor  = InputHelper.leerTexto("  Creado por: ");
        obligacionService.insertarObligacion(idUsuario, idSub, nombre, desc, monto,
                diaVenc, vigente, fechaIni, fechaFin, creadoPor);
    }

    private void listarObligaciones() {
        String idUsuario = InputHelper.leerTexto("  ID del usuario: ");
        System.out.println("  Filtrar: 1=Vigentes  2=Inactivas  3=Todas");
        int op = InputHelper.leerInt("  Selecciona: ");
        Boolean vigente = op == 1 ? true : op == 2 ? false : null;
        obligacionService.listarObligacionesUsuario(idUsuario, vigente);
    }

    private void actualizarObligacion() {
        System.out.println("\n  -- Actualizar Obligacion --");
        String id    = InputHelper.leerTexto("  ID de la obligacion: ");
        String nom   = InputHelper.leerTexto("  Nuevo nombre: ");
        String desc  = InputHelper.leerTexto("  Nueva descripcion: ");
        double monto = InputHelper.leerDouble("  Nuevo monto mensual (L.): ");
        int dia      = InputHelper.leerInt("  Nuevo dia de vencimiento: ");
        String fin   = InputHelper.leerTextoOpcional("  Nueva fecha de fin (YYYY-MM-DD o Enter): ");
        boolean act  = InputHelper.leerBooleanEstado("  Estado");
        String mod   = InputHelper.leerTexto("  Modificado por: ");
        obligacionService.actualizarObligacion(id, nom, desc, monto, dia, fin, act, mod);
    }
}
