USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_presupuesto_detalle//
CREATE PROCEDURE sp_insertar_presupuesto_detalle(
  IN p_id_presupuesto VARCHAR(30),
  IN p_id_subcategoria VARCHAR(30),
  IN p_monto_mensual DECIMAL(12,2),
  IN p_observaciones VARCHAR(200),
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO presupuestodetalle (
    id_detalle,
    id_presupuesto,
    id_subcategoria,
    monto_mensual,
    observaciones,
    creado_por,
    modificado_por
  )
  VALUES (
    CONCAT('DET_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_id_presupuesto,
    p_id_subcategoria,
    p_monto_mensual,
    p_observaciones,
    p_creado_por,
    p_creado_por
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_presupuesto_detalle//
CREATE PROCEDURE sp_actualizar_presupuesto_detalle(
  IN p_id_detalle VARCHAR(30),
  IN p_id_subcategoria VARCHAR(30),
  IN p_monto_mensual DECIMAL(12,2),
  IN p_observaciones VARCHAR(200),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE presupuestodetalle
  SET id_subcategoria = p_id_subcategoria,
      monto_mensual = p_monto_mensual,
      observaciones = p_observaciones,
      modificado_por = p_modificado_por
  WHERE id_detalle = p_id_detalle;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_presupuesto_detalle//
CREATE PROCEDURE sp_eliminar_presupuesto_detalle(IN p_id_detalle VARCHAR(30))
BEGIN
  DELETE FROM presupuestodetalle
  WHERE id_detalle = p_id_detalle;
END//

DROP PROCEDURE IF EXISTS sp_consultar_presupuesto_detalle//
CREATE PROCEDURE sp_consultar_presupuesto_detalle(IN p_id_detalle VARCHAR(30))
BEGIN
  SELECT
    pd.id_detalle,
    pd.id_presupuesto,
    pd.id_subcategoria,
    pd.monto_mensual,
    pd.observaciones,
    sc.nombre_subcategoria,
    c.nombre_categoria,
    pd.creado_por,
    pd.modificado_por,
    pd.creado_en,
    pd.modificado_en
  FROM presupuestodetalle pd
  INNER JOIN subcategoria sc
    ON pd.id_subcategoria = sc.id_subcategoria
  INNER JOIN categoria c
    ON sc.id_categoria = c.id_categoria
  WHERE pd.id_detalle = p_id_detalle;
END//

DROP PROCEDURE IF EXISTS sp_listar_detalles_presupuesto//
CREATE PROCEDURE sp_listar_detalles_presupuesto(IN p_id_presupuesto VARCHAR(30))
BEGIN
  SELECT
    pd.id_detalle,
    pd.id_presupuesto,
    pd.id_subcategoria,
    c.nombre_categoria,
    sc.nombre_subcategoria,
    pd.monto_mensual,
    pd.observaciones,
    pd.creado_por,
    pd.modificado_por,
    pd.creado_en,
    pd.modificado_en
  FROM presupuestodetalle pd
  INNER JOIN subcategoria sc
    ON pd.id_subcategoria = sc.id_subcategoria
  INNER JOIN categoria c
    ON sc.id_categoria = c.id_categoria
  WHERE pd.id_presupuesto = p_id_presupuesto
  ORDER BY c.nombre_categoria, sc.nombre_subcategoria;
END//

DELIMITER ;