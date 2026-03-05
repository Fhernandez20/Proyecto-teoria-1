USE presupuesto_personal;
DELIMITER //

-- TRIGGER 1: Crear subcategoría por defecto al insertar categoría
DROP TRIGGER IF EXISTS trg_categoria_crea_subcategoria_defecto//
CREATE TRIGGER trg_categoria_crea_subcategoria_defecto
AFTER INSERT ON categoria
FOR EACH ROW
BEGIN
  -- Evita duplicado si el usuario inserta algo raro
  IF NOT EXISTS (
    SELECT 1 FROM subcategoria 
    WHERE id_categoria = NEW.id_categoria AND es_defecto = 1
  ) THEN
    INSERT INTO subcategoria (
      id_subcategoria, id_categoria, nombre_subcategoria, descripcion, indicador_activo, es_defecto
    )
    VALUES (
      CONCAT('SUB_', NEW.id_categoria, '_GEN'),
      NEW.id_categoria,
      'General',
      CONCAT('Subcategoría por defecto de ', NEW.nombre_categoria),
      1,
      1
    );
  END IF;
END//

-- TRIGGER 2: Auto-asignar anio/mes si vienen NULL (basado en fecha)
DROP TRIGGER IF EXISTS trg_transaccion_autofill_anio_mes//
CREATE TRIGGER trg_transaccion_autofill_anio_mes
BEFORE INSERT ON transaccion
FOR EACH ROW
BEGIN
  IF NEW.fecha IS NOT NULL THEN
    IF NEW.anio IS NULL OR NEW.anio = 0 THEN
      SET NEW.anio = YEAR(NEW.fecha);
    END IF;
    IF NEW.mes IS NULL OR NEW.mes = 0 THEN
      SET NEW.mes = MONTH(NEW.fecha);
    END IF;
  END IF;
END//

-- TRIGGER 3: Validar que anio/mes estén dentro de la vigencia del presupuesto
DROP TRIGGER IF EXISTS trg_transaccion_validar_periodo_presupuesto//
CREATE TRIGGER trg_transaccion_validar_periodo_presupuesto
BEFORE INSERT ON transaccion
FOR EACH ROW
BEGIN
  DECLARE v_start INT;
  DECLARE v_end INT;
  DECLARE v_cur INT;

  SELECT (init_year*100 + init_month), (end_year*100 + end_month)
  INTO v_start, v_end
  FROM presupuesto
  WHERE id_presupuesto = NEW.id_presupuesto;

  SET v_cur = (NEW.anio*100 + NEW.mes);

  IF v_cur < v_start OR v_cur > v_end THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La transacción está fuera del período del presupuesto';
  END IF;
END//

-- TRIGGER 4: Prevenir eliminación de subcategoría si está en uso
DROP TRIGGER IF EXISTS trg_subcategoria_prevenir_delete_en_uso//
CREATE TRIGGER trg_subcategoria_prevenir_delete_en_uso
BEFORE DELETE ON subcategoria
FOR EACH ROW
BEGIN
  IF EXISTS (SELECT 1 FROM presupuestodetalle WHERE id_subcategoria = OLD.id_subcategoria) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: subcategoría usada en PresupuestoDetalle';
  END IF;

  IF EXISTS (SELECT 1 FROM transaccion WHERE id_subcategoria = OLD.id_subcategoria) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: subcategoría usada en Transaccion';
  END IF;
END//

DELIMITER ;