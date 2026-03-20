USE presupuesto_personal;
DELIMITER //

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_calcular_monto_ejecutado//
CREATE FUNCTION fn_calcular_monto_ejecutado(
  p_id_subcategoria VARCHAR(30),
  p_id_presupuesto VARCHAR(30),
  p_anio INT,
  p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_total DECIMAL(12,2);

  SELECT IFNULL(SUM(t.monto),0)
  INTO v_total
  FROM transaccion t
  WHERE t.id_subcategoria = p_id_subcategoria
    AND t.id_presupuesto = p_id_presupuesto
    AND t.anio = p_anio
    AND t.mes = p_mes;

  RETURN v_total;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_calcular_porcentaje_ejecutado//
CREATE FUNCTION fn_calcular_porcentaje_ejecutado(
  p_id_subcategoria VARCHAR(30),
  p_id_presupuesto VARCHAR(30),
  p_anio INT,
  p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_ejecutado DECIMAL(12,2);
  DECLARE v_presup DECIMAL(12,2);

  SET v_ejecutado = fn_calcular_monto_ejecutado(
    p_id_subcategoria,
    p_id_presupuesto,
    p_anio,
    p_mes
  );

  SELECT IFNULL(pd.monto_mensual,0)
  INTO v_presup
  FROM presupuestodetalle pd
  WHERE pd.id_presupuesto = p_id_presupuesto
    AND pd.id_subcategoria = p_id_subcategoria
  LIMIT 1;

  IF v_presup = 0 THEN
    RETURN 0;
  END IF;

  RETURN (v_ejecutado / v_presup) * 100;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_obtener_balance_subcategoria//
CREATE FUNCTION fn_obtener_balance_subcategoria(
  p_id_presupuesto VARCHAR(30),
  p_id_subcategoria VARCHAR(30),
  p_anio INT,
  p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_ejecutado DECIMAL(12,2);
  DECLARE v_presup DECIMAL(12,2);

  SET v_ejecutado = fn_calcular_monto_ejecutado(
    p_id_subcategoria,
    p_id_presupuesto,
    p_anio,
    p_mes
  );

  SELECT IFNULL(pd.monto_mensual,0)
  INTO v_presup
  FROM presupuestodetalle pd
  WHERE pd.id_presupuesto = p_id_presupuesto
    AND pd.id_subcategoria = p_id_subcategoria
  LIMIT 1;

  RETURN (v_presup - v_ejecutado);
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_obtener_total_categoria_mes//
CREATE FUNCTION fn_obtener_total_categoria_mes(
  p_id_categoria VARCHAR(30),
  p_id_presupuesto VARCHAR(30),
  p_anio INT,
  p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_total DECIMAL(12,2);

  SELECT IFNULL(SUM(pd.monto_mensual),0)
  INTO v_total
  FROM presupuestodetalle pd
  INNER JOIN subcategoria s
    ON s.id_subcategoria = pd.id_subcategoria
  WHERE pd.id_presupuesto = p_id_presupuesto
    AND s.id_categoria = p_id_categoria;

  RETURN v_total;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_obtener_total_ejecutado_categoria_mes//
CREATE FUNCTION fn_obtener_total_ejecutado_categoria_mes(
  p_id_categoria VARCHAR(30),
  p_id_presupuesto VARCHAR(30),
  p_anio INT,
  p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_total DECIMAL(12,2);

  SELECT IFNULL(SUM(t.monto),0)
  INTO v_total
  FROM transaccion t
  INNER JOIN subcategoria s
    ON s.id_subcategoria = t.id_subcategoria
  WHERE s.id_categoria = p_id_categoria
    AND t.id_presupuesto = p_id_presupuesto
    AND t.anio = p_anio
    AND t.mes = p_mes;

  RETURN v_total;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_dias_hasta_vencimiento//
CREATE FUNCTION fn_dias_hasta_vencimiento(
  p_id_obligacion VARCHAR(30)
)
RETURNS INT
DETERMINISTIC
BEGIN
  DECLARE v_dia INT;
  DECLARE v_hoy DATE;
  DECLARE v_target DATE;

  SET v_hoy = CURDATE();

  SELECT o.dia_vencimiento
  INTO v_dia
  FROM obligacionfija o
  WHERE o.id_obligacion = p_id_obligacion
  LIMIT 1;

  SET v_target = STR_TO_DATE(
    CONCAT(YEAR(v_hoy), '-', LPAD(MONTH(v_hoy),2,'0'), '-', LPAD(v_dia,2,'0')),
    '%Y-%m-%d'
  );

  RETURN DATEDIFF(v_target, v_hoy);
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_validar_vigencia_presupuesto//
CREATE FUNCTION fn_validar_vigencia_presupuesto(
  p_fecha DATE,
  p_id_presupuesto VARCHAR(30)
)
RETURNS TINYINT
DETERMINISTIC
BEGIN
  DECLARE v_ini DATE;
  DECLARE v_fin DATE;

  SELECT
    STR_TO_DATE(CONCAT(p.init_year,'-',LPAD(p.init_month,2,'0'),'-01'), '%Y-%m-%d'),
    LAST_DAY(STR_TO_DATE(CONCAT(p.end_year,'-',LPAD(p.end_month,2,'0'),'-01'), '%Y-%m-%d'))
  INTO v_ini, v_fin
  FROM presupuesto p
  WHERE p.id_presupuesto = p_id_presupuesto
  LIMIT 1;

  IF v_ini IS NULL OR v_fin IS NULL THEN
    RETURN 0;
  END IF;

  IF p_fecha BETWEEN v_ini AND v_fin THEN
    RETURN 1;
  END IF;

  RETURN 0;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_obtener_categoria_por_subcategoria//
CREATE FUNCTION fn_obtener_categoria_por_subcategoria(
  p_id_subcategoria VARCHAR(30)
)
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
  DECLARE v_cat VARCHAR(30);

  SELECT s.id_categoria
  INTO v_cat
  FROM subcategoria s
  WHERE s.id_subcategoria = p_id_subcategoria
  LIMIT 1;

  RETURN v_cat;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_calcular_proyeccion_gasto_mensual//
CREATE FUNCTION fn_calcular_proyeccion_gasto_mensual(
  p_id_subcategoria VARCHAR(30),
  p_id_presupuesto VARCHAR(30),
  p_anio INT,
  p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_ejecutado DECIMAL(12,2);
  DECLARE v_hoy DATE;
  DECLARE v_dias_mes INT;
  DECLARE v_dia_actual INT;

  SET v_hoy = CURDATE();

  SET v_ejecutado = fn_calcular_monto_ejecutado(
    p_id_subcategoria,
    p_id_presupuesto,
    p_anio,
    p_mes
  );

  SET v_dias_mes = DAY(
    LAST_DAY(
      STR_TO_DATE(CONCAT(p_anio,'-',LPAD(p_mes,2,'0'),'-01'), '%Y-%m-%d')
    )
  );

  IF YEAR(v_hoy) = p_anio AND MONTH(v_hoy) = p_mes THEN
    SET v_dia_actual = DAY(v_hoy);
  ELSE
    SET v_dia_actual = 15;
  END IF;

  IF v_dia_actual = 0 THEN
    RETURN 0;
  END IF;

  RETURN (v_ejecutado / v_dia_actual) * v_dias_mes;
END//

-- -------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_obtener_promedio_gasto_subcategoria//
CREATE FUNCTION fn_obtener_promedio_gasto_subcategoria(
  p_id_usuario VARCHAR(30),
  p_id_subcategoria VARCHAR(30),
  p_cantidad_meses INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
  DECLARE v_prom DECIMAL(12,2);

  SELECT IFNULL(AVG(x.total_mes),0)
  INTO v_prom
  FROM (
    SELECT SUM(t.monto) AS total_mes
    FROM transaccion t
    WHERE t.id_usuario = p_id_usuario
      AND t.id_subcategoria = p_id_subcategoria
      AND t.tipo = 'gasto'
    GROUP BY t.anio, t.mes
    ORDER BY t.anio DESC, t.mes DESC
    LIMIT p_cantidad_meses
  ) x;

  RETURN v_prom;
END//

DELIMITER ;
