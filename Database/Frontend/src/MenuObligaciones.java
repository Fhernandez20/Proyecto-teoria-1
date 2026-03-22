package menu;

import service.ObligacionService;
import util.InputHelper;

public class MenuObligaciones {

    private final ObligacionService obligacionService = new ObligacionService();

    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n====== MODULO OBLIGACIONES ======");
            System.out.println("1. Insertar obligacion");
            System.out.println("2. Listar obligaciones por usuario");
            System.out.println("3. Consultar obligacion por ID");
            System.out.println("0. Volver");

            opcion = InputHelper.leerInt("Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> insertarObligacion();
                case 2 -> listarObligaciones();
                case 3 -> consultarObligacion();
                case 0 -> System.out.println("Regresando al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void insertarObligacion() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");
        String idSubcategoria = InputHelper.leerTexto("ID de la subcategoria: ");
        String nombre = InputHelper.leerTexto("Nombre de la obligacion: ");
        String descripcion = InputHelper.leerTexto("Descripcion: ");
        double monto = InputHelper.leerDouble("Monto fijo mensual: ");
        int diaVencimiento = InputHelper.leerInt("Dia de vencimiento (1-31): ");
        boolean vigente = leerSiNo("Vigente");
        String fechaInicio = InputHelper.leerFecha("Fecha inicio");
        String fechaFin = InputHelper.leerFecha("Fecha fin");
        String creadoPor = InputHelper.leerTexto("Creado por: ");

        obligacionService.insertarObligacion(
                idUsuario,
                idSubcategoria,
                nombre,
                descripcion,
                monto,
                diaVencimiento,
                vigente,
                fechaInicio,
                fechaFin,
                creadoPor
        );
    }

    private void listarObligaciones() {
        String idUsuario = InputHelper.leerTexto("ID del usuario: ");
        String filtro = InputHelper.leerTextoOpcional("Vigente (1=Si, 0=No) o Enter para todas: ");

        Boolean vigente = null;
        if (filtro != null) {
            filtro = filtro.trim();
            if (filtro.equals("1")) vigente = true;
            else if (filtro.equals("0")) vigente = false;
            else {
                System.out.println("Filtro invalido. Se listaran todas.");
                vigente = null;
            }
        }

        obligacionService.listarObligacionesUsuario(idUsuario, vigente);
    }

    private void consultarObligacion() {
        String idObligacion = InputHelper.leerTexto("ID de la obligacion: ");
        obligacionService.consultarObligacion(idObligacion);
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String valor = InputHelper.leerTexto(mensaje + " (1=Si, 0=No): ");
            if (valor.equals("1")) return true;
            if (valor.equals("0")) return false;
            System.out.println("Valor invalido. Usa 1 o 0.");
        }
    }
}