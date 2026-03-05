USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_transaccion//
CREATE PROCEDURE sp_insertar_transaccion(
  IN p_id_usuario VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  IN p_id_subcategoria VARCHAR(30),
  IN p_id_obligacion VARCHAR(30), 
  IN p_tipo VARCHAR(10),
  IN p_descripcion VARCHAR(200),
  IN p_monto DECIMAL(12,2),
  IN p_fecha DATE,
  IN p_metodo_pago VARCHAR(20),
  IN p_num_factura VARCHAR(40),
  IN p_observaciones VARCHAR(200),
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO transaccion (
    id_transaccion, id_usuario, id_presupuesto,
    anio, mes, id_subcategoria, tipo, descripcion, monto,
    fecha, metodo_pago, num_factura, observaciones, fecha_registro
  )
  VALUES (
    CONCAT('T_', LEFT(REPLACE(UUID(),'-',''), 25)),
    p_id_usuario, p_id_presupuesto,
    p_anio, p_mes, p_id_subcategoria, p_tipo, p_descripcion, p_monto,
    p_fecha, p_metodo_pago, p_num_factura, p_observaciones, NOW()
  );

  IF p_id_obligacion IS NOT NULL AND p_id_obligacion <> '' THEN
    INSERT INTO obligacionfija_transaccion (id_relacion, id_obligacion, id_transaccion)
    VALUES (
      CONCAT('REL_', LEFT(REPLACE(UUID(),'-',''), 22)),
      p_id_obligacion,
      (SELECT id_transaccion FROM transaccion ORDER BY fecha_registro DESC LIMIT 1)
    );
  END IF;
END//

DROP PROCEDURE IF EXISTS sp_actualizar_transaccion//
CREATE PROCEDURE sp_actualizar_transaccion(
  IN p_id_transaccion VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  IN p_descripcion VARCHAR(200),
  IN p_monto DECIMAL(12,2),
  IN p_fecha DATE,
  IN p_metodo_pago VARCHAR(20),
  IN p_num_factura VARCHAR(40),
  IN p_observaciones VARCHAR(200),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE transaccion
  SET anio = p_anio,
      mes = p_mes,
      descripcion = p_descripcion,
      monto = p_monto,
      fecha = p_fecha,
      metodo_pago = p_metodo_pago,
      num_factura = p_num_factura,
      observaciones = p_observaciones
  WHERE id_transaccion = p_id_transaccion;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_transaccion//
CREATE PROCEDURE sp_eliminar_transaccion(IN p_id_transaccion VARCHAR(30))
BEGIN
  DELETE FROM obligacionfija_transaccion WHERE id_transaccion = p_id_transaccion;

  DELETE FROM transaccion WHERE id_transaccion = p_id_transaccion;
END//

DROP PROCEDURE IF EXISTS sp_consultar_transaccion//
CREATE PROCEDURE sp_consultar_transaccion(IN p_id_transaccion VARCHAR(30))
BEGIN
  SELECT t.*,
         s.nombre_subcategoria,
         c.nombre_categoria
  FROM transaccion t
  JOIN subcategoria s ON s.id_subcategoria = t.id_subcategoria
  JOIN categoria c ON c.id_categoria = s.id_categoria
  WHERE t.id_transaccion = p_id_transaccion;
END//

DROP PROCEDURE IF EXISTS sp_listar_transacciones_presupuesto//
CREATE PROCEDURE sp_listar_transacciones_presupuesto(
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  IN p_tipo VARCHAR(10)
)
BEGIN
  SELECT *
  FROM transaccion
  WHERE id_presupuesto = p_id_presupuesto
    AND (p_anio IS NULL OR anio = p_anio)
    AND (p_mes IS NULL OR mes = p_mes)
    AND (p_tipo IS NULL OR p_tipo = '' OR tipo = p_tipo)
  ORDER BY fecha DESC, fecha_registro DESC;
END//

DELIMITER ;