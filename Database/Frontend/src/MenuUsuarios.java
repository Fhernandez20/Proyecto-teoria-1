package menu;

import service.UsuarioService;
import util.InputHelper;

public class MenuUsuarios {

    private final UsuarioService usuarioService = new UsuarioService();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n====== MODULO USUARIOS ======");
            System.out.println("1. Insertar usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Consultar usuario por ID");
            System.out.println("0. Volver");
            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> insertarUsuario();
                case 2 -> usuarioService.listarUsuarios();
                case 3 -> consultarUsuario();
                case 0 -> System.out.println("Regresando al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarUsuario() {
        String primerNombre = InputHelper.leerTexto("Primer nombre: ");
        String segundoNombre = InputHelper.leerTextoOpcional("Segundo nombre (Enter si no tiene): ");
        String primerApellido = InputHelper.leerTexto("Primer apellido: ");
        String segundoApellido = InputHelper.leerTextoOpcional("Segundo apellido (Enter si no tiene): ");
        String correo = InputHelper.leerTexto("Correo: ");
        double salario = InputHelper.leerDouble("Salario mensual: ");
        boolean estado = InputHelper.leerBooleanEstado("Estado");
        String creadoPor = InputHelper.leerTexto("Creado por: ");

        usuarioService.insertarUsuario(
                primerNombre,
                segundoNombre,
                primerApellido,
                segundoApellido,
                correo,
                salario,
                estado,
                creadoPor
        );
    }

    private void consultarUsuario() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");
        usuarioService.consultarUsuario(idUsuario);
    }
}