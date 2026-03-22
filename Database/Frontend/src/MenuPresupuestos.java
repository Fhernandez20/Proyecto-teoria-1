package menu;

import service.PresupuestoService;
import util.InputHelper;

public class MenuPresupuestos {

    private final PresupuestoService presupuestoService = new PresupuestoService();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n====== MODULO PRESUPUESTOS ======");
            System.out.println("1. Listar presupuestos por usuario");
            System.out.println("2. Consultar presupuesto por ID");
            System.out.println("3. Crear presupuesto completo");
            System.out.println("0. Volver");

            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> listarPresupuestos();
                case 2 -> consultarPresupuesto();
                case 3 -> crearPresupuestoCompleto();
                case 0 -> System.out.println("Regresando al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void listarPresupuestos() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");
        String estado = InputHelper.leerTextoOpcional("Estado (activo/cerrado/borrador) o Enter para todos: ");

        if (estado != null) {
            estado = estado.trim().toLowerCase();
            if (!estado.equals("activo") && !estado.equals("cerrado") && !estado.equals("borrador")) {
                System.out.println("Estado invalido. Se listaran todos los presupuestos.");
                estado = null;
            }
        }

        presupuestoService.listarPresupuestosUsuario(idUsuario, estado);
    }

    private void consultarPresupuesto() {
        String idPresupuesto = InputHelper.leerTexto("ID del presupuesto: ");
        presupuestoService.consultarPresupuesto(idPresupuesto);
    }

    private void crearPresupuestoCompleto() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");
        String nombre = InputHelper.leerTexto("Nombre del presupuesto: ");
        String descripcion = InputHelper.leerTexto("Descripcion: ");
        String periodoInicio = InputHelper.leerFecha("Periodo inicio");
        String periodoFin = InputHelper.leerFecha("Periodo fin");
        String creadoPor = InputHelper.leerTexto("Creado por: ");

        int cantidad = InputHelper.leerInt("Cuantas subcategorias deseas agregar al presupuesto: ");

        if (cantidad <= 0) {
            System.out.println("Debes ingresar al menos una subcategoria.");
            return;
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < cantidad; i++) {
            System.out.println("\nSubcategoria #" + (i + 1));
            String idSubcategoria = InputHelper.leerTexto("ID subcategoria: ");
            double montoMensual = InputHelper.leerDouble("Monto mensual: ");

            json.append("{")
                .append("\"id_subcategoria\":\"").append(idSubcategoria).append("\",")
                .append("\"monto_mensual\":").append(montoMensual)
                .append("}");

            if (i < cantidad - 1) {
                json.append(",");
            }
        }
        json.append("]");

        presupuestoService.crearPresupuestoCompleto(
                idUsuario,
                nombre,
                descripcion,
                periodoInicio,
                periodoFin,
                json.toString(),
                creadoPor
        );
    }
}