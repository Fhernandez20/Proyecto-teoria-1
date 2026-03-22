package menu;

import service.TransaccionService;
import util.InputHelper;

public class MenuTransacciones {

    private final TransaccionService transaccionService = new TransaccionService();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n====== MODULO TRANSACCIONES ======");
            System.out.println("1. Listar transacciones por usuario");
            System.out.println("2. Consultar transaccion por ID");
            System.out.println("0. Volver");

            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> listarTransacciones();
                case 2 -> consultarTransaccion();
                case 0 -> System.out.println("Regresando al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void listarTransacciones() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");

        String anioTexto = InputHelper.leerTextoOpcional("Anio o Enter para todos: ");
        String mesTexto = InputHelper.leerTextoOpcional("Mes o Enter para todos: ");

        Integer anio = null;
        Integer mes = null;

        try {
            if (anioTexto != null && !anioTexto.isBlank()) {
                anio = Integer.parseInt(anioTexto);
            }
        } catch (NumberFormatException e) {
            System.out.println("Anio invalido. Se listaran todos los anios.");
            anio = null;
        }

        try {
            if (mesTexto != null && !mesTexto.isBlank()) {
                mes = Integer.parseInt(mesTexto);
            }
        } catch (NumberFormatException e) {
            System.out.println("Mes invalido. Se listaran todos los meses.");
            mes = null;
        }

        transaccionService.listarTransaccionesUsuario(idUsuario, anio, mes);
    }

    private void consultarTransaccion() {
        String idTransaccion = InputHelper.leerTexto("ID de la transaccion: ");
        transaccionService.consultarTransaccion(idTransaccion);
    }
}