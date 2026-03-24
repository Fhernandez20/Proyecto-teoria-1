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
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_insertar_usuario(?,?,?,?,?,?,?,?)}")) {
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
        } finally {
            cerrar(conn);
        }
    }

    public void actualizarUsuario(
            String idUsuario,
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            String correo,
            double salarioMensual,
            boolean estado,
            String modificadoPor
    ) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_actualizar_usuario(?,?,?,?,?,?,?,?,?)}")) {
            stmt.setString(1, idUsuario);
            stmt.setString(2, primerNombre);
            stmt.setString(3, segundoNombre);
            stmt.setString(4, primerApellido);
            stmt.setString(5, segundoApellido);
            stmt.setString(6, correo);
            stmt.setDouble(7, salarioMensual);
            stmt.setBoolean(8, estado);
            stmt.setString(9, modificadoPor);
            stmt.execute();
            System.out.println("Usuario actualizado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void eliminarUsuario(String idUsuario) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_eliminar_usuario(?)}")) {
            stmt.setString(1, idUsuario);
            stmt.execute();
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void listarUsuarios() {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_listar_usuarios()}")) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== LISTA DE USUARIOS ==========");
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                System.out.println("ID      : " + rs.getString("id_usuario"));
                System.out.println("Nombre  : "
                        + rs.getString("primer_nombre") + " "
                        + nullStr(rs.getString("segundo_nombre")) + " "
                        + rs.getString("primer_apellido") + " "
                        + nullStr(rs.getString("segundo_apellido")));
                System.out.println("Correo  : " + rs.getString("correo"));
                System.out.println("Salario : L. " + rs.getDouble("salario_mensual"));
                System.out.println("Estado  : " + (rs.getBoolean("estado") ? "Activo" : "Inactivo"));
                System.out.println("----------------------------------------");
            }
            if (!hay) System.out.println("No hay usuarios registrados.");
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    public void consultarUsuario(String idUsuario) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("No se pudo abrir la conexion."); return; }

        try (CallableStatement stmt = conn.prepareCall("{CALL sp_consultar_usuario(?)}")) {
            stmt.setString(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n========== USUARIO ==========");
                System.out.println("ID             : " + rs.getString("id_usuario"));
                System.out.println("Primer nombre  : " + rs.getString("primer_nombre"));
                System.out.println("Segundo nombre : " + nullStr(rs.getString("segundo_nombre")));
                System.out.println("Primer apellido: " + rs.getString("primer_apellido"));
                System.out.println("Segundo apell. : " + nullStr(rs.getString("segundo_apellido")));
                System.out.println("Correo         : " + rs.getString("correo"));
                System.out.println("Fecha registro : " + rs.getString("fecha_registro"));
                System.out.println("Salario        : L. " + rs.getDouble("salario_mensual"));
                System.out.println("Estado         : " + (rs.getBoolean("estado") ? "Activo" : "Inactivo"));
                System.out.println("Creado por     : " + rs.getString("creado_por"));
                System.out.println("Modificado por : " + rs.getString("modificado_por"));
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar usuario: " + e.getMessage());
        } finally {
            cerrar(conn);
        }
    }

    private String nullStr(String val) { return val == null ? "" : val; }
    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
