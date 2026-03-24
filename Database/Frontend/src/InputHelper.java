package util;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public static String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        String valor = scanner.nextLine().trim();
        return valor.isEmpty() ? null : valor;
    }

    public static double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingresa un numero.");
            }
        }
    }

    public static int leerInt(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingresa un numero entero.");
            }
        }
    }

    public static boolean leerBooleanEstado(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (1=Activo, 0=Inactivo): ");
            String valor = scanner.nextLine().trim();
            if (valor.equals("1")) return true;
            if (valor.equals("0")) return false;
            System.out.println("Valor invalido. Usa 1 o 0.");
        }
    }
    public static String leerFecha(String mensaje) {
    while (true) {
        System.out.print(mensaje + " (YYYY-MM-DD): ");
        String fecha = scanner.nextLine().trim();

        if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return fecha;
        }

        System.out.println("Formato invalido. Usa YYYY-MM-DD.");
    }
}
}