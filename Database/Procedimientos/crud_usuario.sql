USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_usuario//
CREATE PROCEDURE sp_insertar_usuario(
  IN p_primer_nombre VARCHAR(30),
  IN p_segundo_nombre VARCHAR(30),
  IN p_primer_apellido VARCHAR(30),
  IN p_segundo_apellido VARCHAR(30),
  IN p_correo VARCHAR(100),
  IN p_salario_mensual DECIMAL(12,2),
  IN p_estado BOOL,
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO usuario (
    id_usuario,
    primer_nombre,
    segundo_nombre,
    primer_apellido,
    segundo_apellido,
    correo,
    fecha_registro,
    salario_mensual,
    estado,
    creado_por,
    modificado_por
  )
  VALUES (
    CONCAT('USR_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_primer_nombre,
    p_segundo_nombre,
    p_primer_apellido,
    p_segundo_apellido,
    p_correo,
    NOW(),
    p_salario_mensual,
    p_estado,
    p_creado_por,
    p_creado_por
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_usuario//
CREATE PROCEDURE sp_actualizar_usuario(
  IN p_id_usuario VARCHAR(30),
  IN p_primer_nombre VARCHAR(30),
  IN p_segundo_nombre VARCHAR(30),
  IN p_primer_apellido VARCHAR(30),
  IN p_segundo_apellido VARCHAR(30),
  IN p_correo VARCHAR(100),
  IN p_salario_mensual DECIMAL(12,2),
  IN p_estado BOOL,
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE usuario
  SET primer_nombre = p_primer_nombre,
      segundo_nombre = p_segundo_nombre,
      primer_apellido = p_primer_apellido,
      segundo_apellido = p_segundo_apellido,
      correo = p_correo,
      salario_mensual = p_salario_mensual,
      estado = p_estado,
      modificado_por = p_modificado_por
  WHERE id_usuario = p_id_usuario;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_usuario//
CREATE PROCEDURE sp_eliminar_usuario(IN p_id_usuario VARCHAR(30))
BEGIN
  IF EXISTS (SELECT 1 FROM presupuesto WHERE id_usuario = p_id_usuario) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: el usuario tiene presupuestos asociados';
  END IF;

  IF EXISTS (SELECT 1 FROM categoria WHERE id_usuario = p_id_usuario) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: el usuario tiene categorias asociadas';
  END IF;

  IF EXISTS (SELECT 1 FROM obligacionfija WHERE id_usuario = p_id_usuario) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: el usuario tiene obligaciones asociadas';
  END IF;

  IF EXISTS (SELECT 1 FROM transaccion WHERE id_usuario = p_id_usuario) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: el usuario tiene transacciones asociadas';
  END IF;

  DELETE FROM usuario
  WHERE id_usuario = p_id_usuario;
END//

DROP PROCEDURE IF EXISTS sp_consultar_usuario//
CREATE PROCEDURE sp_consultar_usuario(IN p_id_usuario VARCHAR(30))
BEGIN
  SELECT
    id_usuario,
    primer_nombre,
    segundo_nombre,
    primer_apellido,
    segundo_apellido,
    correo,
    fecha_registro,
    salario_mensual,
    estado,
    creado_por,
    modificado_por,
    creado_en,
    modificado_en
  FROM usuario
  WHERE id_usuario = p_id_usuario;
END//

DROP PROCEDURE IF EXISTS sp_listar_usuarios//
CREATE PROCEDURE sp_listar_usuarios()
BEGIN
  SELECT
    id_usuario,
    primer_nombre,
    segundo_nombre,
    primer_apellido,
    segundo_apellido,
    correo,
    fecha_registro,
    salario_mensual,
    estado,
    creado_por,
    modificado_por,
    creado_en,
    modificado_en
  FROM usuario
  ORDER BY primer_nombre, primer_apellido;
END//

DELIMITER ;