USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_usuario//
CREATE PROCEDURE sp_insertar_usuario(
  IN p_nombre VARCHAR(30),
  IN p_apellido VARCHAR(30),
  IN p_email VARCHAR(100),
  IN p_salario_mensual DECIMAL(12,2),
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO usuario (
    id_usuario, primer_nombre, primer_apellido, correo, fecha_registro, salario_mensual, estado
  )
  VALUES (
    CONCAT('U_', LEFT(REPLACE(UUID(),'-',''), 24)),
    p_nombre, p_apellido, p_email, NOW(), p_salario_mensual, 1
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_usuario//
CREATE PROCEDURE sp_actualizar_usuario(
  IN p_id_usuario VARCHAR(30),
  IN p_nombre VARCHAR(30),
  IN p_apellido VARCHAR(30),
  IN p_salario_mensual DECIMAL(12,2),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE usuario
  SET primer_nombre = p_nombre,
      primer_apellido = p_apellido,
      salario_mensual = p_salario_mensual
  WHERE id_usuario = p_id_usuario;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_usuario//
CREATE PROCEDURE sp_eliminar_usuario(IN p_id_usuario VARCHAR(30))
BEGIN
  UPDATE usuario
  SET estado = 0
  WHERE id_usuario = p_id_usuario;
END//

DROP PROCEDURE IF EXISTS sp_consultar_usuario//
CREATE PROCEDURE sp_consultar_usuario(IN p_id_usuario VARCHAR(30))
BEGIN
  SELECT * FROM usuario WHERE id_usuario = p_id_usuario;
END//

DROP PROCEDURE IF EXISTS sp_listar_usuarios//
CREATE PROCEDURE sp_listar_usuarios()
BEGIN
  SELECT id_usuario, primer_nombre, primer_apellido, correo, salario_mensual, estado
  FROM usuario
  ORDER BY fecha_registro DESC;
END//

DELIMITER ;