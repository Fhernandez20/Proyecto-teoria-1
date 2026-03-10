USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_presupuesto//
CREATE PROCEDURE sp_insertar_presupuesto(
  IN p_id_usuario VARCHAR(30),
  IN p_nombre VARCHAR(60),
  IN p_descripcion VARCHAR(200),
  IN p_periodo_inicio VARCHAR(7),
  IN p_periodo_fin VARCHAR(7),
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO presupuesto (
    id_presupuesto,
    id_usuario,
    nombre_descriptivo,
    init_year,
    init_month,
    end_year,
    end_month,
    total_ingresos,
    total_gastos,
    total_ahorro,
    fecha_creacion,
    estado
  )
  VALUES (
    CONCAT('P_', LEFT(REPLACE(UUID(),'-',''), 24)),
    p_id_usuario,
    p_nombre,
    CAST(SUBSTRING(p_periodo_inicio,1,4) AS UNSIGNED),
    CAST(SUBSTRING(p_periodo_inicio,6,2) AS UNSIGNED),
    CAST(SUBSTRING(p_periodo_fin,1,4) AS UNSIGNED),
    CAST(SUBSTRING(p_periodo_fin,6,2) AS UNSIGNED),
    0,
    0,
    0,
    NOW(),
    'activo'
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_presupuesto//
CREATE PROCEDURE sp_actualizar_presupuesto(
  IN p_id_presupuesto VARCHAR(30),
  IN p_nombre VARCHAR(60),
  IN p_descripcion VARCHAR(200),
  IN p_periodo_inicio VARCHAR(7),
  IN p_periodo_fin VARCHAR(7),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE presupuesto
  SET nombre_descriptivo = p_nombre,
      init_year = CAST(SUBSTRING(p_periodo_inicio,1,4) AS UNSIGNED),
      init_month = CAST(SUBSTRING(p_periodo_inicio,6,2) AS UNSIGNED),
      end_year = CAST(SUBSTRING(p_periodo_fin,1,4) AS UNSIGNED),
      end_month = CAST(SUBSTRING(p_periodo_fin,6,2) AS UNSIGNED)
  WHERE id_presupuesto = p_id_presupuesto;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_presupuesto//
CREATE PROCEDURE sp_eliminar_presupuesto(IN p_id_presupuesto VARCHAR(30))
BEGIN
  IF EXISTS (
    SELECT 1
    FROM transaccion
    WHERE id_presupuesto = p_id_presupuesto
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: el presupuesto tiene transacciones asociadas';
  END IF;

  DELETE FROM presupuestodetalle
  WHERE id_presupuesto = p_id_presupuesto;

  DELETE FROM presupuesto
  WHERE id_presupuesto = p_id_presupuesto;
END//

DROP PROCEDURE IF EXISTS sp_consultar_presupuesto//
CREATE PROCEDURE sp_consultar_presupuesto(IN p_id_presupuesto VARCHAR(30))
BEGIN
  SELECT *
  FROM presupuesto
  WHERE id_presupuesto = p_id_presupuesto;
END//

DELIMITER ;