package service;

import db.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioService {

    public void insertarUsuario(
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            String correo,
            double salarioMensual,
            boolean estado,
            String creadoPor
    ) {
        String sql = "{CALL sp_insertar_usuario(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, primerNombre);
            stmt.setString(2, segundoNombre);
            stmt.setString(3, primerApellido);
            stmt.setString(4, segundoApellido);
            stmt.setString(5, correo);
            stmt.setDouble(6, salarioMensual);
            stmt.setBoolean(7, estado);
            stmt.setString(8, creadoPor);

            stmt.execute();
            System.out.println("Usuario insertado correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
        }
    }

    public void listarUsuarios() {
        String sql = "{CALL sp_listar_usuarios()}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                System.out.println("\n========== LISTA DE USUARIOS ==========");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("id_usuario"));
                    System.out.println("Nombre: "
                            + rs.getString("primer_nombre") + " "
                            + (rs.getString("segundo_nombre") == null ? "" : rs.getString("segundo_nombre")) + " "
                            + rs.getString("primer_apellido") + " "
                            + (rs.getString("segundo_apellido") == null ? "" : rs.getString("segundo_apellido")));
                    System.out.println("Correo: " + rs.getString("correo"));
                    System.out.println("Salario mensual: L. " + rs.getDouble("salario_mensual"));
                    System.out.println("Estado: " + (rs.getBoolean("estado") ? "Activo" : "Inactivo"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("--------------------------------------");
                }
            } else {
                System.out.println("No hay usuarios para mostrar.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
    }

    public void consultarUsuario(String idUsuario) {
        String sql = "{CALL sp_consultar_usuario(?)}";

        try (Connection conn = ConexionDB.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            if (conn == null) {
                System.out.println("No se pudo abrir la conexion.");
                return;
            }

            stmt.setString(1, idUsuario);

            boolean hayResultados = stmt.execute();

            if (hayResultados) {
                ResultSet rs = stmt.getResultSet();

                if (rs.next()) {
                    System.out.println("\n========== USUARIO ==========");
                    System.out.println("ID: " + rs.getString("id_usuario"));
                    System.out.println("Primer nombre: " + rs.getString("primer_nombre"));
                    System.out.println("Segundo nombre: " + rs.getString("segundo_nombre"));
                    System.out.println("Primer apellido: " + rs.getString("primer_apellido"));
                    System.out.println("Segundo apellido: " + rs.getString("segundo_apellido"));
                    System.out.println("Correo: " + rs.getString("correo"));
                    System.out.println("Fecha registro: " + rs.getString("fecha_registro"));
                    System.out.println("Salario mensual: L. " + rs.getDouble("salario_mensual"));
                    System.out.println("Estado: " + (rs.getBoolean("estado") ? "Activo" : "Inactivo"));
                    System.out.println("Creado por: " + rs.getString("creado_por"));
                    System.out.println("Modificado por: " + rs.getString("modificado_por"));
                } else {
                    System.out.println("Usuario no encontrado.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar usuario: " + e.getMessage());
        }
    }
}