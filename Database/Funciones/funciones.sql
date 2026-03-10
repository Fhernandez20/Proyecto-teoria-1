USE presupuesto_personal;
DELIMITER //

-- 1
DROP FUNCTION IF EXISTS fn_calcular_monto_ejecutado//
CREATE FUNCTION fn_calcular_monto_ejecutado(
    p_id_subcategoria VARCHAR(30),
    p_anio INT,
    p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
    RETURN (
        SELECT IFNULL(SUM(monto),0)
        FROM transaccion
        WHERE id_subcategoria = p_id_subcategoria
        AND YEAR(fecha_transaccion) = p_anio
        AND MONTH(fecha_transaccion) = p_mes
    );
END//

-- 2
DROP FUNCTION IF EXISTS fn_calcular_porcentaje_ejecutado//
CREATE FUNCTION fn_calcular_porcentaje_ejecutado(
    p_id_subcategoria VARCHAR(30),
    p_id_presupuesto VARCHAR(30),
    p_anio INT,
    p_mes INT
)
RETURNS DECIMAL(6,2)
DETERMINISTIC
BEGIN
    RETURN (
        SELECT IFNULL(
            (SUM(t.monto) / pd.monto_asignado) * 100
        ,0)
        FROM presupuestodetalle pd
        LEFT JOIN transaccion t 
        ON pd.id_subcategoria = t.id_subcategoria
        AND YEAR(t.fecha_transaccion) = p_anio
        AND MONTH(t.fecha_transaccion) = p_mes
        WHERE pd.id_subcategoria = p_id_subcategoria
        AND pd.id_presupuesto = p_id_presupuesto
    );
END//

-- 3
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
    RETURN (
        SELECT 
        IFNULL(pd.monto_asignado,0) -
        IFNULL(SUM(t.monto),0)
        FROM presupuestodetalle pd
        LEFT JOIN transaccion t
        ON pd.id_subcategoria = t.id_subcategoria
        AND YEAR(t.fecha_transaccion) = p_anio
        AND MONTH(t.fecha_transaccion) = p_mes
        WHERE pd.id_presupuesto = p_id_presupuesto
        AND pd.id_subcategoria = p_id_subcategoria
    );
END//

-- 4
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
    RETURN (
        SELECT IFNULL(SUM(pd.monto_asignado),0)
        FROM presupuestodetalle pd
        JOIN subcategoria s 
        ON pd.id_subcategoria = s.id_subcategoria
        WHERE s.id_categoria = p_id_categoria
        AND pd.id_presupuesto = p_id_presupuesto
    );
END//

-- 5
DROP FUNCTION IF EXISTS fn_obtener_total_ejecutado_categoria_mes//
CREATE FUNCTION fn_obtener_total_ejecutado_categoria_mes(
    p_id_categoria VARCHAR(30),
    p_anio INT,
    p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
    RETURN (
        SELECT IFNULL(SUM(t.monto),0)
        FROM transaccion t
        JOIN subcategoria s
        ON t.id_subcategoria = s.id_subcategoria
        WHERE s.id_categoria = p_id_categoria
        AND YEAR(t.fecha_transaccion) = p_anio
        AND MONTH(t.fecha_transaccion) = p_mes
    );
END//

-- 6
DROP FUNCTION IF EXISTS fn_dias_hasta_vencimiento//
CREATE FUNCTION fn_dias_hasta_vencimiento(
    p_id_obligacion VARCHAR(30)
)
RETURNS INT
DETERMINISTIC
BEGIN
    RETURN (
        SELECT DATEDIFF(fecha_vencimiento, CURDATE())
        FROM obligacionfija
        WHERE id_obligacion = p_id_obligacion
    );
END//

-- 7
DROP FUNCTION IF EXISTS fn_validar_vigencia_presupuesto//
CREATE FUNCTION fn_validar_vigencia_presupuesto(
    p_fecha DATE,
    p_id_presupuesto VARCHAR(30)
)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    RETURN (
        SELECT 
        p_fecha BETWEEN 
        STR_TO_DATE(CONCAT(init_year,'-',init_month,'-01'),'%Y-%m-%d')
        AND
        LAST_DAY(STR_TO_DATE(CONCAT(end_year,'-',end_month,'-01'),'%Y-%m-%d'))
        FROM presupuesto
        WHERE id_presupuesto = p_id_presupuesto
    );
END//

-- 8
DROP FUNCTION IF EXISTS fn_obtener_categoria_por_subcategoria//
CREATE FUNCTION fn_obtener_categoria_por_subcategoria(
    p_id_subcategoria VARCHAR(30)
)
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    RETURN (
        SELECT id_categoria
        FROM subcategoria
        WHERE id_subcategoria = p_id_subcategoria
    );
END//

-- 9
DROP FUNCTION IF EXISTS fn_calcular_proyeccion_gasto_mensual//
CREATE FUNCTION fn_calcular_proyeccion_gasto_mensual(
    p_id_subcategoria VARCHAR(30),
    p_anio INT,
    p_mes INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
    RETURN (
        SELECT 
        IFNULL(SUM(monto),0) *
        DAY(LAST_DAY(CURDATE())) /
        DAY(CURDATE())
        FROM transaccion
        WHERE id_subcategoria = p_id_subcategoria
        AND YEAR(fecha_transaccion) = p_anio
        AND MONTH(fecha_transaccion) = p_mes
    );
END//

-- 10
DROP FUNCTION IF EXISTS fn_obtener_promedio_gasto_subcategoria//
CREATE FUNCTION fn_obtener_promedio_gasto_subcategoria(
    p_id_usuario VARCHAR(30),
    p_id_subcategoria VARCHAR(30),
    p_cantidad_meses INT
)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
    RETURN (
        SELECT IFNULL(AVG(monto),0)
        FROM transaccion
        WHERE id_usuario = p_id_usuario
        AND id_subcategoria = p_id_subcategoria
        ORDER BY fecha_transaccion DESC
        LIMIT p_cantidad_meses
    );
END//

DELIMITER ;