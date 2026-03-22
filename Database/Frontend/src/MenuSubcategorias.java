package menu;

import service.SubcategoriaService;
import util.InputHelper;

public class MenuSubcategorias {

    private final SubcategoriaService subcategoriaService = new SubcategoriaService();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n====== MODULO SUBCATEGORIAS ======");
            System.out.println("1. Insertar subcategoria");
            System.out.println("2. Listar subcategorias por categoria");
            System.out.println("3. Consultar subcategoria por ID");
            System.out.println("0. Volver");

            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> insertarSubcategoria();
                case 2 -> listarSubcategorias();
                case 3 -> consultarSubcategoria();
                case 0 -> System.out.println("Regresando al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarSubcategoria() {
        String idCategoria = InputHelper.leerTexto("ID de la categoria: ");
        String nombre = InputHelper.leerTexto("Nombre de la subcategoria: ");
        String descripcion = InputHelper.leerTexto("Descripcion: ");
        boolean activo = InputHelper.leerBooleanEstado("Indicador activo");
        boolean esDefecto = InputHelper.leerBooleanEstado("Es subcategoria por defecto");
        String creadoPor = InputHelper.leerTexto("Creado por: ");

        subcategoriaService.insertarSubcategoria(
                idCategoria,
                nombre,
                descripcion,
                activo,
                esDefecto,
                creadoPor
        );
    }

    private void listarSubcategorias() {
        String idCategoria = InputHelper.leerTexto("ID de la categoria: ");
        subcategoriaService.listarSubcategoriasPorCategoria(idCategoria);
    }

    private void consultarSubcategoria() {
        String idSubcategoria = InputHelper.leerTexto("ID de la subcategoria: ");
        subcategoriaService.consultarSubcategoria(idSubcategoria);
    }
}