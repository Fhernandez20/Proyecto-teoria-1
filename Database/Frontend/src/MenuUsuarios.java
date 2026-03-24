package menu;

import service.UsuarioService;
import util.InputHelper;

public class MenuUsuarios {

    private final UsuarioService usuarioService = new UsuarioService();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n  ╔══════════════════════════════════════════════╗");
            System.out.println("  ║              GESTION DE USUARIOS             ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  1. Insertar usuario                         ║");
            System.out.println("  ║  2. Listar todos los usuarios                ║");
            System.out.println("  ║  3. Consultar usuario por ID                 ║");
            System.out.println("  ║  4. Actualizar usuario                       ║");
            System.out.println("  ║  5. Eliminar usuario                         ║");
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  0. Volver                                   ║");
            System.out.println("  ╚══════════════════════════════════════════════╝");

            opcion = InputHelper.leerInt("\n  Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> insertarUsuario();
                case 2 -> usuarioService.listarUsuarios();
                case 3 -> consultarUsuario();
                case 4 -> actualizarUsuario();
                case 5 -> eliminarUsuario();
                case 0 -> System.out.println("  Regresando...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarUsuario() {
        System.out.println("\n  -- Nuevo Usuario --");
        String primerNombre    = InputHelper.leerTexto("  Primer nombre: ");
        String segundoNombre   = InputHelper.leerTextoOpcional("  Segundo nombre (Enter para omitir): ");
        String primerApellido  = InputHelper.leerTexto("  Primer apellido: ");
        String segundoApellido = InputHelper.leerTextoOpcional("  Segundo apellido (Enter para omitir): ");
        String correo          = InputHelper.leerTexto("  Correo electronico: ");
        double salario         = InputHelper.leerDouble("  Salario mensual (L.): ");
        boolean estado         = InputHelper.leerBooleanEstado("  Estado");
        String creadoPor       = InputHelper.leerTexto("  Creado por: ");
        usuarioService.insertarUsuario(primerNombre, segundoNombre, primerApellido,
                segundoApellido, correo, salario, estado, creadoPor);
    }

    private void consultarUsuario() {
        String id = InputHelper.leerTexto("  ID del usuario: ");
        usuarioService.consultarUsuario(id);
    }

    private void actualizarUsuario() {
        System.out.println("\n  -- Actualizar Usuario --");
        String id              = InputHelper.leerTexto("  ID del usuario: ");
        String primerNombre    = InputHelper.leerTexto("  Primer nombre: ");
        String segundoNombre   = InputHelper.leerTextoOpcional("  Segundo nombre (Enter para omitir): ");
        String primerApellido  = InputHelper.leerTexto("  Primer apellido: ");
        String segundoApellido = InputHelper.leerTextoOpcional("  Segundo apellido (Enter para omitir): ");
        String correo          = InputHelper.leerTexto("  Correo electronico: ");
        double salario         = InputHelper.leerDouble("  Salario mensual (L.): ");
        boolean estado         = InputHelper.leerBooleanEstado("  Estado");
        String modificadoPor   = InputHelper.leerTexto("  Modificado por: ");
        usuarioService.actualizarUsuario(id, primerNombre, segundoNombre, primerApellido,
                segundoApellido, correo, salario, estado, modificadoPor);
    }

    private void eliminarUsuario() {
        String id = InputHelper.leerTexto("  ID del usuario a eliminar: ");
        usuarioService.eliminarUsuario(id);
    }
}
