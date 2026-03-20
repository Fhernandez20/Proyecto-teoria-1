USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_subcategoria//
CREATE PROCEDURE sp_insertar_subcategoria(
  IN p_id_categoria VARCHAR(30),
  IN p_nombre VARCHAR(50),
  IN p_descripcion VARCHAR(150),
  IN p_indicador_activo BOOL,
  IN p_es_defecto BOOL,
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO subcategoria (
    id_subcategoria,
    id_categoria,
    nombre_subcategoria,
    descripcion,
    indicador_activo,
    es_defecto,
    creado_por,
    modificado_por
  )
  VALUES (
    CONCAT('SUB_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_id_categoria,
    p_nombre,
    p_descripcion,
    p_indicador_activo,
    p_es_defecto,
    p_creado_por,
    p_creado_por
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_subcategoria//
CREATE PROCEDURE sp_actualizar_subcategoria(
  IN p_id_subcategoria VARCHAR(30),
  IN p_nombre VARCHAR(50),
  IN p_descripcion VARCHAR(150),
  IN p_indicador_activo BOOL,
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE subcategoria
  SET nombre_subcategoria = p_nombre,
      descripcion = p_descripcion,
      indicador_activo = p_indicador_activo,
      modificado_por = p_modificado_por
  WHERE id_subcategoria = p_id_subcategoria;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_subcategoria//
CREATE PROCEDURE sp_eliminar_subcategoria(IN p_id_subcategoria VARCHAR(30))
BEGIN
  IF EXISTS (
    SELECT 1
    FROM subcategoria
    WHERE id_subcategoria = p_id_subcategoria
      AND es_defecto = 1
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar la subcategoria por defecto';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM transaccion
    WHERE id_subcategoria = p_id_subcategoria
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: hay transacciones asociadas a esta subcategoria';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM presupuestodetalle
    WHERE id_subcategoria = p_id_subcategoria
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: hay detalles de presupuesto asociados a esta subcategoria';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM obligacionfija
    WHERE id_subcategoria = p_id_subcategoria
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: hay obligaciones fijas asociadas a esta subcategoria';
  END IF;

  DELETE FROM subcategoria
  WHERE id_subcategoria = p_id_subcategoria;
END//

DROP PROCEDURE IF EXISTS sp_consultar_subcategoria//
CREATE PROCEDURE sp_consultar_subcategoria(IN p_id_subcategoria VARCHAR(30))
BEGIN
  SELECT *
  FROM subcategoria
  WHERE id_subcategoria = p_id_subcategoria;
END//

DROP PROCEDURE IF EXISTS sp_listar_subcategorias_categoria//
CREATE PROCEDURE sp_listar_subcategorias_categoria(IN p_id_categoria VARCHAR(30))
BEGIN
  SELECT *
  FROM subcategoria
  WHERE id_categoria = p_id_categoria
  ORDER BY es_defecto DESC, nombre_subcategoria;
END//

DELIMITER ;