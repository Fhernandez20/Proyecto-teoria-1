USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_transaccion//
CREATE PROCEDURE sp_insertar_transaccion(
  IN p_id_usuario VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  IN p_id_subcategoria VARCHAR(30),
  IN p_tipo VARCHAR(10),
  IN p_descripcion VARCHAR(200),
  IN p_monto DECIMAL(12,2),
  IN p_fecha DATE,
  IN p_metodo_pago VARCHAR(20),
  IN p_num_factura VARCHAR(40),
  IN p_observaciones VARCHAR(200),
  IN p_id_obligacion VARCHAR(30),
  IN p_creado_por VARCHAR(30)
)
BEGIN
  DECLARE v_id_transaccion VARCHAR(30);

  SET v_id_transaccion = CONCAT('TRA_', LEFT(REPLACE(UUID(),'-',''), 22));

  INSERT INTO transaccion (
    id_transaccion,
    id_usuario,
    id_presupuesto,
    anio,
    mes,
    id_subcategoria,
    tipo,
    descripcion,
    monto,
    fecha,
    metodo_pago,
    num_factura,
    observaciones,
    fecha_registro,
    creado_por,
    modificado_por
  )
  VALUES (
    v_id_transaccion,
    p_id_usuario,
    p_id_presupuesto,
    p_anio,
    p_mes,
    p_id_subcategoria,
    p_tipo,
    p_descripcion,
    p_monto,
    p_fecha,
    p_metodo_pago,
    p_num_factura,
    p_observaciones,
    NOW(),
    p_creado_por,
    p_creado_por
  );

  IF p_id_obligacion IS NOT NULL AND p_id_obligacion <> '' THEN
    INSERT INTO obligacionfija_transaccion (
      id_relacion,
      id_obligacion,
      id_transaccion,
      creado_por,
      modificado_por
    )
    VALUES (
      CONCAT('REL_', LEFT(REPLACE(UUID(),'-',''), 22)),
      p_id_obligacion,
      v_id_transaccion,
      p_creado_por,
      p_creado_por
    );
  END IF;
END//

DROP PROCEDURE IF EXISTS sp_actualizar_transaccion//
CREATE PROCEDURE sp_actualizar_transaccion(
  IN p_id_transaccion VARCHAR(30),
  IN p_id_subcategoria VARCHAR(30),
  IN p_tipo VARCHAR(10),
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
  SET id_subcategoria = p_id_subcategoria,
      tipo = p_tipo,
      descripcion = p_descripcion,
      monto = p_monto,
      fecha = p_fecha,
      metodo_pago = p_metodo_pago,
      num_factura = p_num_factura,
      observaciones = p_observaciones,
      modificado_por = p_modificado_por
  WHERE id_transaccion = p_id_transaccion;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_transaccion//
CREATE PROCEDURE sp_eliminar_transaccion(IN p_id_transaccion VARCHAR(30))
BEGIN
  DELETE FROM obligacionfija_transaccion
  WHERE id_transaccion = p_id_transaccion;

  DELETE FROM transaccion
  WHERE id_transaccion = p_id_transaccion;
END//

DROP PROCEDURE IF EXISTS sp_consultar_transaccion//
CREATE PROCEDURE sp_consultar_transaccion(IN p_id_transaccion VARCHAR(30))
BEGIN
  SELECT
    t.id_transaccion,
    t.id_usuario,
    t.id_presupuesto,
    t.anio,
    t.mes,
    t.id_subcategoria,
    t.tipo,
    t.descripcion,
    t.monto,
    t.fecha,
    t.metodo_pago,
    t.num_factura,
    t.observaciones,
    t.fecha_registro,
    c.nombre_categoria,
    sc.nombre_subcategoria,
    oft.id_obligacion,
    o.nombre AS nombre_obligacion,
    t.creado_por,
    t.modificado_por,
    t.creado_en,
    t.modificado_en
  FROM transaccion t
  INNER JOIN subcategoria sc
    ON t.id_subcategoria = sc.id_subcategoria
  INNER JOIN categoria c
    ON sc.id_categoria = c.id_categoria
  LEFT JOIN obligacionfija_transaccion oft
    ON t.id_transaccion = oft.id_transaccion
  LEFT JOIN obligacionfija o
    ON oft.id_obligacion = o.id_obligacion
  WHERE t.id_transaccion = p_id_transaccion;
END//

DROP PROCEDURE IF EXISTS sp_listar_transacciones_usuario//
CREATE PROCEDURE sp_listar_transacciones_usuario(
  IN p_id_usuario VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT
)
BEGIN
  SELECT
    t.id_transaccion,
    t.id_presupuesto,
    t.anio,
    t.mes,
    t.tipo,
    t.descripcion,
    t.monto,
    t.fecha,
    t.metodo_pago,
    t.num_factura,
    c.nombre_categoria,
    sc.nombre_subcategoria,
    oft.id_obligacion,
    o.nombre AS nombre_obligacion,
    t.creado_por,
    t.modificado_por,
    t.creado_en,
    t.modificado_en
  FROM transaccion t
  INNER JOIN subcategoria sc
    ON t.id_subcategoria = sc.id_subcategoria
  INNER JOIN categoria c
    ON sc.id_categoria = c.id_categoria
  LEFT JOIN obligacionfija_transaccion oft
    ON t.id_transaccion = oft.id_transaccion
  LEFT JOIN obligacionfija o
    ON oft.id_obligacion = o.id_obligacion
  WHERE t.id_usuario = p_id_usuario
    AND (p_anio IS NULL OR t.anio = p_anio)
    AND (p_mes IS NULL OR t.mes = p_mes)
  ORDER BY t.fecha DESC, t.id_transaccion DESC;
END//

DELIMITER ;