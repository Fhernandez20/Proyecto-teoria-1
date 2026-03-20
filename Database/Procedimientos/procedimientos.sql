USE presupuesto_personal;
DELIMITER //

-- -------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_crear_presupuesto_completo//
CREATE PROCEDURE sp_crear_presupuesto_completo(
  IN p_id_usuario VARCHAR(30),
  IN p_nombre VARCHAR(60),
  IN p_descripcion VARCHAR(200),
  IN p_periodo_inicio VARCHAR(7),
  IN p_periodo_fin VARCHAR(7),
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
  DECLARE v_total INT;
  DECLARE v_id_subcategoria VARCHAR(30);
  DECLARE v_monto_mensual DECIMAL(12,2);

  SET v_id_presupuesto = CONCAT('P_', LEFT(REPLACE(UUID(),'-',''), 24));

  SET v_ini_y = CAST(SUBSTRING(p_periodo_inicio,1,4) AS UNSIGNED);
  SET v_ini_m = CAST(SUBSTRING(p_periodo_inicio,6,2) AS UNSIGNED);
  SET v_fin_y = CAST(SUBSTRING(p_periodo_fin,1,4) AS UNSIGNED);
  SET v_fin_m = CAST(SUBSTRING(p_periodo_fin,6,2) AS UNSIGNED);

  INSERT INTO presupuesto (
    id_presupuesto, id_usuario, nombre_descriptivo,
    init_year, init_month, end_year, end_month,
    total_ingresos, total_gastos, total_ahorro,
    fecha_creacion, estado
  )
  VALUES (
    v_id_presupuesto, p_id_usuario, p_nombre,
    v_ini_y, v_ini_m, v_fin_y, v_fin_m,
    0, 0, 0,
    NOW(), 'activo'
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

    INSERT INTO presupuestodetalle (
      id_detalle, id_presupuesto, id_subcategoria, monto_mensual, observaciones
    )
    VALUES (
      CONCAT('PD_', LEFT(REPLACE(UUID(),'-',''), 23)),
      v_id_presupuesto,
      v_id_subcategoria,
      v_monto_mensual,
      NULL
    );

    SET v_i = v_i + 1;
  END WHILE;
END//

-- -------------------------------------------------------------
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
  IF p_mes < 1 OR p_mes > 12 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El mes debe estar entre 1 y 12';
  END IF;

  IF p_anio < 2000 OR p_anio > 2100 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El anio es invalido';
  END IF;

  IF fn_validar_vigencia_presupuesto(p_fecha, p_id_presupuesto) = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La fecha no esta dentro de la vigencia del presupuesto';
  END IF;

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
    fecha_registro
  )
  VALUES (
    CONCAT('T_', LEFT(REPLACE(UUID(),'-',''), 25)),
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
    NOW()
  );
END//

-- -------------------------------------------------------------
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

  -- ingresos
  SELECT IFNULL(SUM(monto),0)
  INTO p_total_ingresos
  FROM transaccion
  WHERE id_usuario = p_id_usuario
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes
    AND tipo = 'ingreso';

  -- gastos
  SELECT IFNULL(SUM(monto),0)
  INTO p_total_gastos
  FROM transaccion
  WHERE id_usuario = p_id_usuario
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes
    AND tipo = 'gasto';

  -- ahorros
  SELECT IFNULL(SUM(monto),0)
  INTO p_total_ahorros
  FROM transaccion
  WHERE id_usuario = p_id_usuario
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes
    AND tipo = 'ahorro';

  -- balance final
  SET p_balance_final = p_total_ingresos - p_total_gastos;

END//

-- -------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_calcular_monto_ejecutado_mes//
CREATE PROCEDURE sp_calcular_monto_ejecutado_mes(
  IN p_id_subcategoria VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  OUT p_monto_ejecutado DECIMAL(12,2)
)
BEGIN

  SELECT IFNULL(SUM(monto),0)
  INTO p_monto_ejecutado
  FROM transaccion
  WHERE id_subcategoria = p_id_subcategoria
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes;

END//

-- -------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_calcular_porcentaje_ejecucion_mes//
CREATE PROCEDURE sp_calcular_porcentaje_ejecucion_mes(
  IN p_id_subcategoria VARCHAR(30),
  IN p_id_presupuesto VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  OUT p_porcentaje DECIMAL(12,2)
)
BEGIN
  DECLARE v_monto_ejecutado DECIMAL(12,2);
  DECLARE v_monto_presupuestado DECIMAL(12,2);

  SELECT IFNULL(SUM(monto),0)
  INTO v_monto_ejecutado
  FROM transaccion
  WHERE id_subcategoria = p_id_subcategoria
    AND id_presupuesto = p_id_presupuesto
    AND anio = p_anio
    AND mes = p_mes;

  SELECT IFNULL(monto_mensual,0)
  INTO v_monto_presupuestado
  FROM presupuestodetalle
  WHERE id_subcategoria = p_id_subcategoria
    AND id_presupuesto = p_id_presupuesto
  LIMIT 1;

  IF v_monto_presupuestado = 0 THEN
    SET p_porcentaje = 0;
  ELSE
    SET p_porcentaje = (v_monto_ejecutado / v_monto_presupuestado) * 100;
  END IF;

END//

-- -------------------------------------------------------------
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

  SELECT IFNULL(SUM(pd.monto_mensual),0)
  INTO p_monto_presupuestado
  FROM presupuestodetalle pd
  INNER JOIN subcategoria s
    ON pd.id_subcategoria = s.id_subcategoria
  WHERE s.id_categoria = p_id_categoria
    AND pd.id_presupuesto = p_id_presupuesto;

  SELECT IFNULL(SUM(t.monto),0)
  INTO p_monto_ejecutado
  FROM transaccion t
  INNER JOIN subcategoria s
    ON t.id_subcategoria = s.id_subcategoria
  WHERE s.id_categoria = p_id_categoria
    AND t.id_presupuesto = p_id_presupuesto
    AND t.anio = p_anio
    AND t.mes = p_mes;

  IF p_monto_presupuestado = 0 THEN
    SET p_porcentaje = 0;
  ELSE
    SET p_porcentaje = (p_monto_ejecutado / p_monto_presupuestado) * 100;
  END IF;

END//

-- -------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_cerrar_presupuesto//
CREATE PROCEDURE sp_cerrar_presupuesto(
  IN p_id_presupuesto VARCHAR(30),
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  DECLARE v_fecha_fin DATE;

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
  SET estado = 'cerrado'
  WHERE id_presupuesto = p_id_presupuesto;

END//

-- -------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_procesar_obligaciones_mes//
CREATE PROCEDURE sp_procesar_obligaciones_mes(
  IN p_id_usuario VARCHAR(30),
  IN p_anio INT,
  IN p_mes INT,
  IN p_id_presupuesto VARCHAR(30)
)
BEGIN

  SELECT 
    o.id_obligacion,
    o.nombre,
    o.monto_fijo_mensual,
    o.dia_vencimiento,
    fn_dias_hasta_vencimiento(o.id_obligacion) AS dias_restantes
  FROM obligacionfija o
  WHERE o.id_usuario = p_id_usuario
    AND o.vigente = 1
    AND o.fecha_inicio <= LAST_DAY(STR_TO_DATE(CONCAT(p_anio, '-', LPAD(p_mes,2,'0'), '-01'), '%Y-%m-%d'))
    AND (o.fecha_fin IS NULL OR o.fecha_fin >= STR_TO_DATE(CONCAT(p_anio, '-', LPAD(p_mes,2,'0'), '-01'), '%Y-%m-%d'))
  ORDER BY o.dia_vencimiento ASC;

END//

DELIMITER ;