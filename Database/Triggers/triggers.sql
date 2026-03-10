USE presupuesto_personal;
DELIMITER //

DROP TRIGGER IF EXISTS trg_categoria_crear_subcategoria//

CREATE TRIGGER trg_categoria_crear_subcategoria
AFTER INSERT ON categoria
FOR EACH ROW
BEGIN
  INSERT INTO subcategoria(
    id_subcategoria,
    id_categoria,
    nombre_subcategoria,
    descripcion,
    indicador_activo,
    es_defecto
  )
  VALUES(
    CONCAT('SUB_', NEW.id_categoria),
    NEW.id_categoria,
    'General',
    'Subcategoria por defecto',
    1,
    1
  );
END//

DELIMITER ;