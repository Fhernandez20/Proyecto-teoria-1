USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_categoria//
CREATE PROCEDURE sp_insertar_categoria(
  IN p_nombre VARCHAR(50),
  IN p_descripcion VARCHAR(150),
  IN p_tipo VARCHAR(10),
  IN p_id_usuario VARCHAR(30),
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO categoria (
    id_categoria, id_usuario, nombre_categoria, descripcion, tipo_categoria
  )
  VALUES (
    CONCAT('CAT_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_id_usuario, p_nombre, p_descripcion, p_tipo
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_categoria//
CREATE PROCEDURE sp_actualizar_categoria(
  IN p_id_categoria VARCHAR(30),
  IN p_nombre VARCHAR(50),
  IN p_descripcion VARCHAR(150),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE categoria
  SET nombre_categoria = p_nombre,
      descripcion = p_descripcion
  WHERE id_categoria = p_id_categoria;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_categoria//
CREATE PROCEDURE sp_eliminar_categoria(IN p_id_categoria VARCHAR(30))
BEGIN
  IF EXISTS (
    SELECT 1
    FROM subcategoria
    WHERE id_categoria = p_id_categoria
      AND indicador_activo = 1
      AND es_defecto = 0
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: la categoría tiene subcategorías activas adicionales';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM subcategoria s
    JOIN transaccion t ON t.id_subcategoria = s.id_subcategoria
    WHERE s.id_categoria = p_id_categoria
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: hay transacciones asociadas a esta categoría';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM subcategoria s
    JOIN presupuestodetalle pd ON pd.id_subcategoria = s.id_subcategoria
    WHERE s.id_categoria = p_id_categoria
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: hay detalles de presupuesto asociados a esta categoría';
  END IF;

  DELETE FROM subcategoria WHERE id_categoria = p_id_categoria;
  DELETE FROM categoria WHERE id_categoria = p_id_categoria;
END//

DROP PROCEDURE IF EXISTS sp_consultar_categoria//
CREATE PROCEDURE sp_consultar_categoria(IN p_id_categoria VARCHAR(30))
BEGIN
  SELECT * FROM categoria WHERE id_categoria = p_id_categoria;
END//

DROP PROCEDURE IF EXISTS sp_listar_categorias//
CREATE PROCEDURE sp_listar_categorias(
  IN p_id_usuario VARCHAR(30),
  IN p_tipo VARCHAR(10)
)
BEGIN
  SELECT *
  FROM categoria
  WHERE id_usuario = p_id_usuario
    AND (p_tipo IS NULL OR p_tipo = '' OR tipo_categoria = p_tipo)
  ORDER BY nombre_categoria;
END//

DELIMITER ;