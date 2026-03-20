USE presupuesto_personal;

DROP TRIGGER IF EXISTS trg_categoria_crear_subcategoria;
DELIMITER //

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
    es_defecto,
    creado_por,
    modificado_por
  )
  VALUES(
    CONCAT('SUB_', LEFT(REPLACE(UUID(),'-',''), 22)),
    NEW.id_categoria,
    'General',
    'Subcategoria por defecto',
    1,
    1,
    NEW.creado_por,
    NEW.modificado_por
  );
END//

DELIMITER ;
