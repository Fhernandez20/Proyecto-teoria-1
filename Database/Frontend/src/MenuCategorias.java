package menu;

import service.CategoriaService;
import util.InputHelper;

public class MenuCategorias {
    private final CategoriaService categoriaService = new CategoriaService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║            GESTION DE CATEGORIAS             ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Insertar categoria                       ║");
            System.out.println("  ║  2. Listar categorias                        ║");
            System.out.println("  ║  3. Consultar categoria por ID               ║");
            System.out.println("  ║  4. Actualizar categoria                     ║");
            System.out.println("  ║  5. Eliminar categoria                       ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver                                   ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");
            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");
            switch (opcion) {
                case 1 -> insertarCategoria();
                case 2 -> listarCategorias();
                case 3 -> { String id = InputHelper.leerTexto("  ID de la categoria: ");
                             categoriaService.consultarCategoria(id); }
                case 4 -> actualizarCategoria();
                case 5 -> { String id = InputHelper.leerTexto("  ID de la categoria a eliminar: ");
                             categoriaService.eliminarCategoria(id); }
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarCategoria() {
        System.out.println("\n  -- Nueva Categoria --");
        String nombre    = InputHelper.leerTexto("  Nombre: ");
        String desc      = InputHelper.leerTexto("  Descripcion: ");
        String tipo      = elegirTipo();
        String idUsuario = InputHelper.leerTexto("  ID del usuario propietario: ");
        String creadoPor = InputHelper.leerTexto("  Creado por: ");
        categoriaService.insertarCategoria(nombre, desc, tipo, idUsuario, creadoPor);
    }

    private void listarCategorias() {
        String idUsuario = InputHelper.leerTexto("  ID del usuario: ");
        System.out.println("  Tipo: 1=ingreso  2=gasto  3=ahorro  4=todos");
        int op = InputHelper.leerInt("  Selecciona: ");
        String tipo = switch (op) {
            case 1 -> "ingreso"; case 2 -> "gasto"; case 3 -> "ahorro"; default -> null;
        };
        categoriaService.listarCategorias(idUsuario, tipo);
    }

    private void actualizarCategoria() {
        System.out.println("\n  -- Actualizar Categoria --");
        String id   = InputHelper.leerTexto("  ID de la categoria: ");
        String nom  = InputHelper.leerTexto("  Nuevo nombre: ");
        String desc = InputHelper.leerTexto("  Nueva descripcion: ");
        String mod  = InputHelper.leerTexto("  Modificado por: ");
        categoriaService.actualizarCategoria(id, nom, desc, mod);
    }

    private String elegirTipo() {
        while (true) {
            System.out.println("  Tipo: 1=ingreso  2=gasto  3=ahorro");
            int op = InputHelper.leerInt("  Selecciona: ");
            switch (op) {
                case 1: return "ingreso";
                case 2: return "gasto";
                case 3: return "ahorro";
                default: System.out.println("  Opcion invalida.");
            }
        }
    }
}
