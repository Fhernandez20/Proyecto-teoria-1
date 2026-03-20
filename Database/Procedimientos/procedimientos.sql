USE presupuesto_personal;
DELIMITER //
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_crear_presupuesto_completo//
CREATE PROCEDURE sp_crear_presupuesto_completo(
  IN p_id_usuario VARCHAR(30),
  IN p_nombre VARCHAR(60),
  IN p_descripcion VARCHAR(200),
  IN p_periodo_inicio DATE,
  IN p_periodo_fin DATE,
  IN p_lista_subcategorias_json LONGTEXT,
  IN p_creado_por VARCHAR(30)
)
BEGIN
  DECLARE v_id_presupuesto VARCHAR(30);
  DECLARE v_ini_y INT;
  DECLARE v_ini_m INT;
  DECLARE v_fin_y INT;
  DECLARE v_fin_m INT;
  DECLARE v_i INT DEFAULT 0;
  DECLARE v_total INT DEFAULT 0;
  DECLARE v_id_subcategoria VARCHAR(30);
  DECLARE v_monto_mensual DECIMAL(12,2);
  DECLARE v_tipo_categoria VARCHAR(10);
  DECLARE v_total_ingresos DECIMAL(12,2) DEFAULT 0.00;
  DECLARE v_total_gastos DECIMAL(12,2) DEFAULT 0.00;
  DECLARE v_total_ahorro DECIMAL(12,2) DEFAULT 0.00;

  IF p_periodo_inicio IS NULL OR p_periodo_fin IS NULL THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Las fechas de inicio y fin son obligatorias';
  END IF;

  IF p_periodo_fin < p_periodo_inicio THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La fecha fin no puede ser menor que la fecha inicio';
  END IF;

  IF p_lista_subcategorias_json IS NULL OR JSON_LENGTH(p_lista_subcategorias_json) = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Debe enviar al menos una subcategoria en el JSON';
  END IF;

  SET v_id_presupuesto = CONCAT('PRE_', LEFT(REPLACE(UUID(),'-',''), 22));
  SET v_ini_y = YEAR(p_periodo_inicio);
  SET v_ini_m = MONTH(p_periodo_inicio);
  SET v_fin_y = YEAR(p_periodo_fin);
  SET v_fin_m = MONTH(p_periodo_fin);

  START TRANSACTION;

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
    v_id_presupuesto,
    p_id_usuario,
    p_nombre,
    v_ini_y,
    v_ini_m,
    v_fin_y,
    v_fin_m,
    0.00,
    0.00,
    0.00,
    NOW(),
    'activo',
    p_creado_por,
    p_creado_por
  );

  SET v_total = JSON_LENGTH(p_lista_subcategorias_json);

  WHILE v_i < v_total DO
    SET v_id_subcategoria = JSON_UNQUOTE(
      JSON_EXTRACT(p_lista_subcategorias_json, CONCAT('$[', v_i, '].id_subcategoria'))
    );

    SET v_monto_mensual = CAST(
      JSON_UNQUOTE(
        JSON_EXTRACT(p_lista_subcategorias_json, CONCAT('$[', v_i, '].monto_mensual'))
      ) AS DECIMAL(12,2)
    );

    IF v_id_subcategoria IS NULL OR v_id_subcategoria = '' THEN
      ROLLBACK;
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Una subcategoria del JSON es invalida';
    END IF;

    IF NOT EXISTS (
      SELECT 1
      FROM subcategoria
      WHERE id_subcategoria = v_id_subcategoria
    ) THEN
      ROLLBACK;
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Una subcategoria enviada no existe';
    END IF;

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
      v_id_presupuesto,
      v_id_subcategoria,
      v_monto_mensual,
      p_descripcion,
      p_creado_por,
      p_creado_por
    );

    SELECT c.tipo_categoria
    INTO v_tipo_categoria
    FROM subcategoria s
    INNER JOIN categoria c
      ON s.id_categoria = c.id_categoria
    WHERE s.id_subcategoria = v_id_subcategoria
    LIMIT 1;

    IF v_tipo_categoria = 'ingreso' THEN
      SET v_total_ingresos = v_total_ingresos + IFNULL(v_monto_mensual,0);
    ELSEIF v_tipo_categoria = 'gasto' THEN
      SET v_total_gastos = v_total_gastos + IFNULL(v_monto_mensual,0);
    ELSEIF v_tipo_categoria = 'ahorro' THEN
      SET v_total_ahorro = v_total_ahorro + IFNULL(v_monto_mensual,0);
    END IF;

    SET v_i = v_i + 1;
  END WHILE;

  UPDATE presupuesto
  SET total_ingresos = v_total_ingresos,
      total_gastos = v_total_gastos,
      total_ahorro = v_total_ahorro,
      modificado_por = p_creado_por
  WHERE id_presupuesto = v_id_presupuesto;

  COMMIT;

  SELECT *
  FROM presupuesto
  WHERE id_presupuesto = v_id_presupuesto;
END//
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_registrar_transaccion_completa//
CREATE PROCEDURE sp_registrar_transaccion_completa(
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
  IN p_creado_por VARCHAR(30)
)
BEGIN
  DECLARE v_tipo_categoria VARCHAR(10);
  DECLARE v_id_transaccion VARCHAR(30);

  IF p_mes < 1 OR p_mes > 12 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El mes debe estar entre 1 y 12';
  END IF;

  IF p_anio < 2000 OR p_anio > 2100 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El anio es invalido';
  END IF;

  IF p_fecha IS NULL THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La fecha de la transaccion es obligatoria';
  END IF;

  IF p_anio <> YEAR(p_fecha) OR p_mes <> MONTH(p_fecha) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El anio y mes no coinciden con la fecha de la transaccion';
  END IF;

  IF fn_validar_vigencia_presupuesto(p_fecha, p_id_presupuesto) = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La fecha no esta dentro de la vigencia del presupuesto';
  END IF;

  SELECT c.tipo_categoria
  INTO v_tipo_categoria
  FROM subcategoria s
  INNER JOIN categoria c
    ON s.id_categoria = c.id_categoria
  WHERE s.id_subcategoria = p_id_subcategoria
  LIMIT 1;

  IF v_tipo_categoria IS NULL THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La subcategoria no existe';
  END IF;

  IF v_tipo_categoria <> p_tipo THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El tipo de transaccion no coincide con el tipo de la categoria';
  END IF;

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
    NULL,
    NULL,
    NOW(),
    p_creado_por,
    p_creado_por
  );

  SELECT *
  FROM transaccion
  WHERE id_transaccion = v_id_transaccion;
END//
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_procesar_obligaciones_mes//
CREATE PROCEDURE sp_procesar_obligaciones_mes(
  IN p_id_usuario VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  IN p_id_presupuesto VARCHAR(30)
)
BEGIN
  DECLARE v_fecha_inicio_mes DATE;
  DECLARE v_fecha_fin_mes DATE;

  SET v_fecha_inicio_mes = STR_TO_DATE(
    CONCAT(p_anio, '-', LPAD(p_mes,2,'0'), '-01'),
    '%Y-%m-%d'
  );

  SET v_fecha_fin_mes = LAST_DAY(v_fecha_inicio_mes);

  IF fn_validar_vigencia_presupuesto(v_fecha_inicio_mes, p_id_presupuesto) = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El mes consultado no esta dentro de la vigencia del presupuesto';
  END IF;

  SELECT 
    o.id_obligacion,
    o.nombre,
    o.descripcion,
    o.monto_fijo_mensual,
    o.dia_vencimiento,
    o.fecha_inicio,
    o.fecha_fin,
    c.nombre_categoria,
    s.nombre_subcategoria,
    fn_dias_hasta_vencimiento(o.id_obligacion) AS dias_restantes
  FROM obligacionfija o
  INNER JOIN subcategoria s
    ON o.id_subcategoria = s.id_subcategoria
  INNER JOIN categoria c
    ON s.id_categoria = c.id_categoria
  WHERE o.id_usuario = p_id_usuario
    AND o.vigente = 1
    AND o.fecha_inicio <= v_fecha_fin_mes
    AND (o.fecha_fin IS NULL OR o.fecha_fin >= v_fecha_inicio_mes)
  ORDER BY o.dia_vencimiento ASC, o.nombre;
END//
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_calcular_balance_mensual//
CREATE PROCEDURE sp_calcular_balance_mensual(
  IN p_id_usuario VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  OUT p_total_ingresos DECIMAL(12,2),
  OUT p_total_gastos DECIMAL(12,2),
  OUT p_total_ahorros DECIMAL(12,2),
  OUT p_balance_final DECIMAL(12,2)
)
BEGIN
  SELECT IFNULL(SUM(monto),0)
  INTO p_total_ingresos
  FROM transaccion
  WHERE id_usuario = p_id_usuario
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes
    AND tipo = 'ingreso';

  SELECT IFNULL(SUM(monto),0)
  INTO p_total_gastos
  FROM transaccion
  WHERE id_usuario = p_id_usuario
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes
    AND tipo = 'gasto';

  SELECT IFNULL(SUM(monto),0)
  INTO p_total_ahorros
  FROM transaccion
  WHERE id_usuario = p_id_usuario
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes
    AND tipo = 'ahorro';

  SET p_balance_final = p_total_ingresos - p_total_gastos - p_total_ahorros;
END//
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_calcular_monto_ejecutado_mes//
CREATE PROCEDURE sp_calcular_monto_ejecutado_mes(
  IN p_id_subcategoria VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  OUT p_monto_ejecutado DECIMAL(12,2)
)
BEGIN
  SELECT IFNULL(SUM(t.monto),0)
  INTO p_monto_ejecutado
  FROM transaccion t
  WHERE t.id_subcategoria = p_id_subcategoria
    AND t.id_presupuesto = p_id_presupuesto
    AND t.anio = p_anio
    AND t.mes = p_mes;
END//

-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_calcular_porcentaje_ejecucion_mes//
CREATE PROCEDURE sp_calcular_porcentaje_ejecucion_mes(
  IN p_id_subcategoria VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  OUT p_porcentaje DECIMAL(12,2)
)
BEGIN
  SET p_porcentaje = fn_calcular_porcentaje_ejecutado(
    p_id_subcategoria,
    p_id_presupuesto,
    p_anio,
    p_mes
  );
END//

-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_cerrar_presupuesto//
CREATE PROCEDURE sp_cerrar_presupuesto(
  IN p_id_presupuesto VARCHAR(30),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  DECLARE v_fecha_fin DATE;
  DECLARE v_total_ejecutado DECIMAL(12,2);

  SELECT LAST_DAY(
           STR_TO_DATE(
             CONCAT(end_year, '-', LPAD(end_month, 2, '0'), '-01'),
             '%Y-%m-%d'
           )
         )
  INTO v_fecha_fin
  FROM presupuesto
  WHERE id_presupuesto = p_id_presupuesto
  LIMIT 1;

  IF v_fecha_fin IS NULL THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El presupuesto no existe';
  END IF;

  IF CURDATE() <= v_fecha_fin THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El presupuesto aun no puede cerrarse';
  END IF;

  UPDATE presupuesto
  SET estado = 'cerrado',
      modificado_por = p_modificado_por
  WHERE id_presupuesto = p_id_presupuesto;

  SELECT IFNULL(SUM(monto),0)
  INTO v_total_ejecutado
  FROM transaccion
  WHERE id_presupuesto = p_id_presupuesto;

  SELECT
    p.id_presupuesto,
    p.nombre_descriptivo,
    p.estado,
    p.total_ingresos,
    p.total_gastos,
    p.total_ahorro,
    v_total_ejecutado AS total_ejecutado,
    p.modificado_por,
    p.modificado_en
  FROM presupuesto p
  WHERE p.id_presupuesto = p_id_presupuesto;
END//

-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_obtener_resumen_categoria_mes//
CREATE PROCEDURE sp_obtener_resumen_categoria_mes(
  IN p_id_categoria VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  OUT p_monto_presupuestado DECIMAL(12,2),
  OUT p_monto_ejecutado DECIMAL(12,2),
  OUT p_porcentaje DECIMAL(12,2)
)
BEGIN
  SET p_monto_presupuestado = fn_obtener_total_categoria_mes(
    p_id_categoria,
    p_id_presupuesto,
    p_anio,
    p_mes
  );

  SET p_monto_ejecutado = fn_obtener_total_ejecutado_categoria_mes(
    p_id_categoria,
    p_anio,
    p_mes
  );

  IF p_monto_presupuestado = 0 THEN
    SET p_porcentaje = 0;
  ELSE
    SET p_porcentaje = (p_monto_ejecutado / p_monto_presupuestado) * 100;
  END IF;
END//

DELIMITER ;
