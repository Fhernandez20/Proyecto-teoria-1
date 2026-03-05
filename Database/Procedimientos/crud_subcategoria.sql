USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_subcategoria//
CREATE PROCEDURE sp_insertar_subcategoria(
  IN p_id_categoria VARCHAR(30),
  IN p_nombre VARCHAR(50),
  IN p_descripcion VARCHAR(150),
  IN p_es_defecto BOOL,
  IN p_creado_por VARCHAR(30)
)
BEGIN
  IF p_es_defecto = 1 THEN
    UPDATE subcategoria
    SET es_defecto = 0
    WHERE id_categoria = p_id_categoria;
  END IF;

  INSERT INTO subcategoria (
    id_subcategoria, id_categoria, nombre_subcategoria, descripcion, indicador_activo, es_defecto
  )
  VALUES (
    CONCAT('SUB_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_id_categoria, p_nombre, p_descripcion, 1, p_es_defecto
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_subcategoria//
CREATE PROCEDURE sp_actualizar_subcategoria(
  IN p_id_subcategoria VARCHAR(30),
  IN p_nombre VARCHAR(50),
  IN p_descripcion VARCHAR(150),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE subcategoria
  SET nombre_subcategoria = p_nombre,
      descripcion = p_descripcion
  WHERE id_subcategoria = p_id_subcategoria;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_subcategoria//
CREATE PROCEDURE sp_eliminar_subcategoria(IN p_id_subcategoria VARCHAR(30))
BEGIN
  IF EXISTS (SELECT 1 FROM presupuestodetalle WHERE id_subcategoria = p_id_subcategoria) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: subcategoría usada en PresupuestoDetalle';
  END IF;

  IF EXISTS (SELECT 1 FROM transaccion WHERE id_subcategoria = p_id_subcategoria) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: subcategoría usada en Transaccion';
  END IF;

  UPDATE subcategoria
  SET indicador_activo = 0,
      es_defecto = 0
  WHERE id_subcategoria = p_id_subcategoria;
END//

DROP PROCEDURE IF EXISTS sp_consultar_subcategoria//
CREATE PROCEDURE sp_consultar_subcategoria(IN p_id_subcategoria VARCHAR(30))
BEGIN
  SELECT s.*, c.nombre_categoria, c.tipo_categoria
  FROM subcategoria s
  JOIN categoria c ON c.id_categoria = s.id_categoria
  WHERE s.id_subcategoria = p_id_subcategoria;
END//

DROP PROCEDURE IF EXISTS sp_listar_subcategorias_por_categoria//
CREATE PROCEDURE sp_listar_subcategorias_por_categoria(IN p_id_categoria VARCHAR(30))
BEGIN
  SELECT *
  FROM subcategoria
  WHERE id_categoria = p_id_categoria
  ORDER BY es_defecto DESC, nombre_subcategoria ASC;
END//

DELIMITER ;