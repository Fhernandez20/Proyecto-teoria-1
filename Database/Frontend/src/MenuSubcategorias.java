package menu;

import service.SubcategoriaService;
import util.InputHelper;

public class MenuSubcategorias {
    private final SubcategoriaService subcategoriaService = new SubcategoriaService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║           GESTION DE SUBCATEGORIAS           ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Insertar subcategoria                    ║");
            System.out.println("  ║  2. Listar subcategorias por categoria       ║");
            System.out.println("  ║  3. Consultar subcategoria por ID            ║");
            System.out.println("  ║  4. Actualizar subcategoria                  ║");
            System.out.println("  ║  5. Eliminar subcategoria                    ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver                                   ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");
            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");
            switch (opcion) {
                case 1 -> insertarSubcategoria();
                case 2 -> { String id = InputHelper.leerTexto("  ID de la categoria: ");
                             subcategoriaService.listarSubcategoriasPorCategoria(id); }
                case 3 -> { String id = InputHelper.leerTexto("  ID de la subcategoria: ");
                             subcategoriaService.consultarSubcategoria(id); }
                case 4 -> actualizarSubcategoria();
                case 5 -> { String id = InputHelper.leerTexto("  ID de la subcategoria a eliminar: ");
                             subcategoriaService.eliminarSubcategoria(id); }
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarSubcategoria() {
        System.out.println("\n  -- Nueva Subcategoria --");
        String idCat   = InputHelper.leerTexto("  ID de la categoria padre: ");
        String nombre  = InputHelper.leerTexto("  Nombre: ");
        String desc    = InputHelper.leerTexto("  Descripcion: ");
        boolean activa = InputHelper.leerBooleanEstado("  Activa");
        boolean defect = InputHelper.leerBooleanEstado("  Es por defecto");
        String creadoPor = InputHelper.leerTexto("  Creado por: ");
        subcategoriaService.insertarSubcategoria(idCat, nombre, desc, activa, defect, creadoPor);
    }

    private void actualizarSubcategoria() {
        System.out.println("\n  -- Actualizar Subcategoria --");
        String id    = InputHelper.leerTexto("  ID de la subcategoria: ");
        String nom   = InputHelper.leerTexto("  Nuevo nombre: ");
        String desc  = InputHelper.leerTexto("  Nueva descripcion: ");
        boolean act  = InputHelper.leerBooleanEstado("  Activa");
        String mod   = InputHelper.leerTexto("  Modificado por: ");
        subcategoriaService.actualizarSubcategoria(id, nom, desc, act, mod);
    }
}
