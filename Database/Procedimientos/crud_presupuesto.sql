USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_presupuesto//
CREATE PROCEDURE sp_insertar_presupuesto(
  IN p_id_usuario VARCHAR(30),
  IN p_nombre VARCHAR(60),
  IN p_descripcion VARCHAR(200),
  IN p_periodo_inicio DATE,
  IN p_periodo_fin DATE,
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
    estado,
    creado_por,
    modificado_por
  )
  VALUES (
    CONCAT('PRE_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_id_usuario,
    p_nombre,
    YEAR(p_periodo_inicio),
    MONTH(p_periodo_inicio),
    YEAR(p_periodo_fin),
    MONTH(p_periodo_fin),
    0.00,
    0.00,
    0.00,
    NOW(),
    'activo',
    p_creado_por,
    p_creado_por
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_presupuesto//
CREATE PROCEDURE sp_actualizar_presupuesto(
  IN p_id_presupuesto VARCHAR(30),
  IN p_nombre VARCHAR(60),
  IN p_descripcion VARCHAR(200),
  IN p_periodo_inicio DATE,
  IN p_periodo_fin DATE,
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE presupuesto
  SET nombre_descriptivo = p_nombre,
      init_year = YEAR(p_periodo_inicio),
      init_month = MONTH(p_periodo_inicio),
      end_year = YEAR(p_periodo_fin),
      end_month = MONTH(p_periodo_fin),
      modificado_por = p_modificado_por
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
      SET MESSAGE_TEXT = 'No se puede eliminar el presupuesto porque tiene transacciones asociadas';
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

DROP PROCEDURE IF EXISTS sp_listar_presupuestos_usuario//
CREATE PROCEDURE sp_listar_presupuestos_usuario(
  IN p_id_usuario VARCHAR(30),
  IN p_estado VARCHAR(15)
)
BEGIN
  SELECT *
  FROM presupuesto
  WHERE id_usuario = p_id_usuario
    AND (p_estado IS NULL OR p_estado = '' OR estado = p_estado)
  ORDER BY init_year DESC, init_month DESC;
END//

DELIMITER ;