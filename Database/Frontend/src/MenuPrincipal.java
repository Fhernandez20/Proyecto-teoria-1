package menu;

import util.InputHelper;

public class MenuPrincipal {

    private final MenuUsuarios menuUsuarios = new MenuUsuarios();
    private final MenuCategorias menuCategorias = new MenuCategorias();
    private final MenuSubcategorias menuSubcategorias = new MenuSubcategorias();
    private final MenuPresupuestos menuPresupuestos = new MenuPresupuestos();
    private final MenuObligaciones menuObligaciones = new MenuObligaciones();
    private final MenuTransacciones menuTransacciones = new MenuTransacciones();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n===== SISTEMA DE PRESUPUESTO PERSONAL =====");
            System.out.println("1. Gestionar usuarios");
            System.out.println("2. Gestionar categorias");
            System.out.println("3. Gestionar subcategorias");
            System.out.println("4. Gestionar presupuestos");
            System.out.println("5. Gestionar obligaciones");
            System.out.println("6. Gestionar transacciones");
            System.out.println("7. Consultas y reportes");
            System.out.println("0. Salir");

            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> menuUsuarios.mostrar();
                case 2 -> menuCategorias.mostrar();
                case 3 -> menuSubcategorias.mostrar();
                case 4 -> menuPresupuestos.mostrar();
                case 5 -> menuObligaciones.mostrar();
                case 6 -> menuTransacciones.mostrar();
                case 7 -> System.out.println("Modulo aun no implementado.");
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }
}