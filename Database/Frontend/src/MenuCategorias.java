package menu;

import service.CategoriaService;
import util.InputHelper;

public class MenuCategorias {

    private final CategoriaService categoriaService = new CategoriaService();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n====== MODULO CATEGORIAS ======");
            System.out.println("1. Insertar categoria");
            System.out.println("2. Listar categorias");
            System.out.println("3. Consultar categoria por ID");
            System.out.println("0. Volver");

            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> insertarCategoria();
                case 2 -> listarCategorias();
                case 3 -> consultarCategoria();
                case 0 -> System.out.println("Regresando al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarCategoria() {
        String nombre = InputHelper.leerTexto("Nombre de la categoria: ");
        String descripcion = InputHelper.leerTexto("Descripcion: ");
        String tipo = leerTipoCategoria();
        String idUsuario = InputHelper.leerTexto("ID del usuario propietario: ");
        String creadoPor = InputHelper.leerTexto("Creado por: ");

        categoriaService.insertarCategoria(nombre, descripcion, tipo, idUsuario, creadoPor);
    }

    private void listarCategorias() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");
        String filtro = InputHelper.leerTextoOpcional("Tipo (ingreso/gasto/ahorro) o Enter para todas: ");

        if (filtro != null) {
            filtro = filtro.trim().toLowerCase();
            if (!filtro.equals("ingreso") && !filtro.equals("gasto") && !filtro.equals("ahorro")) {
                System.out.println("Tipo invalido. Se listaran todas las categorias.");
                filtro = null;
            }
        }

        categoriaService.listarCategorias(idUsuario, filtro);
    }

    private void consultarCategoria() {
        String idCategoria = InputHelper.leerTexto("ID de la categoria: ");
        categoriaService.consultarCategoria(idCategoria);
    }

    private String leerTipoCategoria() {
        while (true) {
            String tipo = InputHelper.leerTexto("Tipo (ingreso/gasto/ahorro): ").trim().toLowerCase();

            if (tipo.equals("ingreso") || tipo.equals("gasto") || tipo.equals("ahorro")) {
                return tipo;
            }

            System.out.println("Tipo invalido. Debe ser: ingreso, gasto o ahorro.");
        }
    }
}
