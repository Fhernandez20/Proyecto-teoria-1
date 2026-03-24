package menu;

import util.InputHelper;

public class MenuPrincipal {

    private final MenuUsuarios      menuUsuarios      = new MenuUsuarios();
    private final MenuCategorias    menuCategorias    = new MenuCategorias();
    private final MenuSubcategorias menuSubcategorias = new MenuSubcategorias();
    private final MenuPresupuestos  menuPresupuestos  = new MenuPresupuestos();
    private final MenuObligaciones  menuObligaciones  = new MenuObligaciones();
    private final MenuTransacciones menuTransacciones = new MenuTransacciones();
    private final MenuReportes      menuReportes      = new MenuReportes();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║       SISTEMA DE PRESUPUESTO PERSONAL        ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║                                              ║");
            System.out.println("  ║   GESTION                                    ║");
            System.out.println("  ║   1. Usuarios                                ║");
            System.out.println("  ║   2. Categorias                              ║");
            System.out.println("  ║   3. Subcategorias                           ║");
            System.out.println("  ║   4. Presupuestos                            ║");
            System.out.println("  ║   5. Obligaciones Fijas                      ║");
            System.out.println("  ║   6. Transacciones                           ║");
            System.out.println("  ║                                              ║");
            System.out.println("  ║   ANALISIS                                   ║");
            System.out.println("  ║   7. Reportes y Consultas                    ║");
            System.out.println("  ║                                              ║");
            System.out.println("  ║   0. Salir                                   ║");
            System.out.println("  ║                                              ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");

            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> menuUsuarios.mostrar();
                case 2 -> menuCategorias.mostrar();
                case 3 -> menuSubcategorias.mostrar();
                case 4 -> menuPresupuestos.mostrar();
                case 5 -> menuObligaciones.mostrar();
                case 6 -> menuTransacciones.mostrar();
                case 7 -> menuReportes.mostrar();
                case 0 -> {
                    System.out.println("\n  Hasta luego. Cerrando el sistema...\n");
                }
                default -> System.out.println("  Opcion invalida. Intenta de nuevo.");
            }
        } while (opcion != 0);
    }
}
