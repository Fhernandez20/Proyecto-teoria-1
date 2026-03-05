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
    id_detalle, id_presupuesto, id_subcategoria, monto_mensual, observaciones
  )
  VALUES (
    CONCAT('PD_', LEFT(REPLACE(UUID(),'-',''), 23)),
    p_id_presupuesto, p_id_subcategoria, p_monto_mensual, p_observaciones
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_presupuesto_detalle//
CREATE PROCEDURE sp_actualizar_presupuesto_detalle(
  IN p_id_detalle VARCHAR(30),
  IN p_monto_mensual DECIMAL(12,2),
  IN p_observaciones VARCHAR(200),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE presupuestodetalle
  SET monto_mensual = p_monto_mensual,
      observaciones = p_observaciones
  WHERE id_detalle = p_id_detalle;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_presupuesto_detalle//
CREATE PROCEDURE sp_eliminar_presupuesto_detalle(IN p_id_detalle VARCHAR(30))
BEGIN
  DELETE FROM presupuestodetalle WHERE id_detalle = p_id_detalle;
END//

DROP PROCEDURE IF EXISTS sp_consultar_presupuesto_detalle//
CREATE PROCEDURE sp_consultar_presupuesto_detalle(IN p_id_detalle VARCHAR(30))
BEGIN
  SELECT pd.*,
         s.nombre_subcategoria,
         c.nombre_categoria, c.tipo_categoria
  FROM presupuestodetalle pd
  JOIN subcategoria s ON s.id_subcategoria = pd.id_subcategoria
  JOIN categoria c ON c.id_categoria = s.id_categoria
  WHERE pd.id_detalle = p_id_detalle;
END//

DROP PROCEDURE IF EXISTS sp_listar_detalles_presupuesto//
CREATE PROCEDURE sp_listar_detalles_presupuesto(IN p_id_presupuesto VARCHAR(30))
BEGIN
  SELECT pd.*,
         s.nombre_subcategoria,
         c.nombre_categoria, c.tipo_categoria
  FROM presupuestodetalle pd
  JOIN subcategoria s ON s.id_subcategoria = pd.id_subcategoria
  JOIN categoria c ON c.id_categoria = s.id_categoria
  WHERE pd.id_presupuesto = p_id_presupuesto
  ORDER BY c.nombre_categoria, s.nombre_subcategoria;
END//

DELIMITER ;