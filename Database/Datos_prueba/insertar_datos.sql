USE presupuesto_personal;

-- =========================================================
-- DATOS DE PRUEBA REALISTAS - 2 MESES COMPLETOS
-- Usuario: ejemplo único para enero y febrero 2026
-- =========================================================

-- ---------------------------------------------------------
-- 1) USUARIO
-- ---------------------------------------------------------
CALL sp_insertar_usuario(
  'Fernando',
  NULL,
  'Hernandez',
  NULL,
  'fernando.hernandez.presupuesto@gmail.com',
  18000.00,
  1,
  'admin'
);

SET @id_usuario = (
  SELECT id_usuario
  FROM usuario
  WHERE correo = 'fernando.hernandez.presupuesto@gmail.com'
  ORDER BY fecha_registro DESC
  LIMIT 1
);

-- ---------------------------------------------------------
-- 2) CATEGORIAS
-- El trigger crea automáticamente subcategoría "General"
-- ---------------------------------------------------------
CALL sp_insertar_categoria('Ingresos', 'Entradas de dinero del usuario', 'ingreso', @id_usuario, 'admin');
CALL sp_insertar_categoria('Alimentacion', 'Gastos de comida y compras del hogar', 'gasto', @id_usuario, 'admin');
CALL sp_insertar_categoria('Vivienda y Servicios', 'Pagos del hogar y servicios básicos', 'gasto', @id_usuario, 'admin');
CALL sp_insertar_categoria('Transporte', 'Gastos de movilidad', 'gasto', @id_usuario, 'admin');
CALL sp_insertar_categoria('Salud', 'Gastos médicos y farmacia', 'gasto', @id_usuario, 'admin');
CALL sp_insertar_categoria('Entretenimiento', 'Gastos recreativos', 'gasto', @id_usuario, 'admin');
CALL sp_insertar_categoria('Ahorro', 'Apartados y metas de ahorro', 'ahorro', @id_usuario, 'admin');

SET @cat_ingresos = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Ingresos'
  LIMIT 1
);

SET @cat_alimentacion = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Alimentacion'
  LIMIT 1
);

SET @cat_vivienda = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Vivienda y Servicios'
  LIMIT 1
);

SET @cat_transporte = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Transporte'
  LIMIT 1
);

SET @cat_salud = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Salud'
  LIMIT 1
);

SET @cat_entretenimiento = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Entretenimiento'
  LIMIT 1
);

SET @cat_ahorro = (
  SELECT id_categoria
  FROM categoria
  WHERE id_usuario = @id_usuario
    AND nombre_categoria = 'Ahorro'
  LIMIT 1
);

-- ---------------------------------------------------------
-- 3) SUBCATEGORIAS
-- sp_insertar_subcategoria(
--   p_id_categoria, p_nombre, p_descripcion,
--   p_indicador_activo, p_es_defecto, p_creado_por
-- )
-- ---------------------------------------------------------
CALL sp_insertar_subcategoria(@cat_ingresos, 'Salario Base', 'Ingreso fijo mensual por salario', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_ingresos, 'Ingreso Extra', 'Ingresos ocasionales o bonos', 1, 0, 'admin');

CALL sp_insertar_subcategoria(@cat_alimentacion, 'Supermercado', 'Compras de alimentos y hogar', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_alimentacion, 'Restaurantes', 'Comidas fuera de casa', 1, 0, 'admin');

CALL sp_insertar_subcategoria(@cat_vivienda, 'Alquiler', 'Pago mensual de vivienda', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_vivienda, 'Internet', 'Servicio de internet del hogar', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_vivienda, 'Energia Electrica', 'Pago de luz', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_vivienda, 'Agua', 'Pago de agua potable', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_vivienda, 'Streaming', 'Suscripciones mensuales de entretenimiento', 1, 0, 'admin');

CALL sp_insertar_subcategoria(@cat_transporte, 'Combustible', 'Carga de combustible', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_transporte, 'Taxi o Bus', 'Movilidad diaria', 1, 0, 'admin');

CALL sp_insertar_subcategoria(@cat_salud, 'Farmacia', 'Compra de medicamentos', 1, 0, 'admin');
CALL sp_insertar_subcategoria(@cat_salud, 'Consulta Medica', 'Consultas o chequeos', 1, 0, 'admin');

CALL sp_insertar_subcategoria(@cat_entretenimiento, 'Salidas', 'Cine, café o recreación', 1, 0, 'admin');

CALL sp_insertar_subcategoria(@cat_ahorro, 'Fondo de Emergencia', 'Dinero apartado para ahorro', 1, 0, 'admin');

SET @sub_salario = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_ingresos
    AND nombre_subcategoria = 'Salario Base'
  LIMIT 1
);

SET @sub_ingreso_extra = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_ingresos
    AND nombre_subcategoria = 'Ingreso Extra'
  LIMIT 1
);

SET @sub_supermercado = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_alimentacion
    AND nombre_subcategoria = 'Supermercado'
  LIMIT 1
);

SET @sub_restaurantes = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_alimentacion
    AND nombre_subcategoria = 'Restaurantes'
  LIMIT 1
);

SET @sub_alquiler = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_vivienda
    AND nombre_subcategoria = 'Alquiler'
  LIMIT 1
);

SET @sub_internet = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_vivienda
    AND nombre_subcategoria = 'Internet'
  LIMIT 1
);

SET @sub_energia = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_vivienda
    AND nombre_subcategoria = 'Energia Electrica'
  LIMIT 1
);

SET @sub_agua = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_vivienda
    AND nombre_subcategoria = 'Agua'
  LIMIT 1
);

SET @sub_streaming = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_vivienda
    AND nombre_subcategoria = 'Streaming'
  LIMIT 1
);

SET @sub_combustible = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_transporte
    AND nombre_subcategoria = 'Combustible'
  LIMIT 1
);

SET @sub_taxi_bus = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_transporte
    AND nombre_subcategoria = 'Taxi o Bus'
  LIMIT 1
);

SET @sub_farmacia = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_salud
    AND nombre_subcategoria = 'Farmacia'
  LIMIT 1
);

SET @sub_consulta = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_salud
    AND nombre_subcategoria = 'Consulta Medica'
  LIMIT 1
);

SET @sub_salidas = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_entretenimiento
    AND nombre_subcategoria = 'Salidas'
  LIMIT 1
);

SET @sub_fondo = (
  SELECT id_subcategoria
  FROM subcategoria
  WHERE id_categoria = @cat_ahorro
    AND nombre_subcategoria = 'Fondo de Emergencia'
  LIMIT 1
);

-- ---------------------------------------------------------
-- 4) PRESUPUESTO COMPLETO DE DOS MESES
-- Usa el procedimiento de lógica de negocio con JSON
-- ---------------------------------------------------------
CALL sp_crear_presupuesto_completo(
  @id_usuario,
  'Presupuesto Personal Enero-Febrero 2026',
  'Presupuesto de prueba con datos realistas para dos meses',
  '2026-01-01',
  '2026-02-28',
  JSON_ARRAY(
    JSON_OBJECT('id_subcategoria', @sub_salario, 'monto_mensual', 18000.00),
    JSON_OBJECT('id_subcategoria', @sub_ingreso_extra, 'monto_mensual', 2000.00),
    JSON_OBJECT('id_subcategoria', @sub_supermercado, 'monto_mensual', 4200.00),
    JSON_OBJECT('id_subcategoria', @sub_restaurantes, 'monto_mensual', 1200.00),
    JSON_OBJECT('id_subcategoria', @sub_alquiler, 'monto_mensual', 6000.00),
    JSON_OBJECT('id_subcategoria', @sub_internet, 'monto_mensual', 850.00),
    JSON_OBJECT('id_subcategoria', @sub_energia, 'monto_mensual', 1100.00),
    JSON_OBJECT('id_subcategoria', @sub_agua, 'monto_mensual', 350.00),
    JSON_OBJECT('id_subcategoria', @sub_streaming, 'monto_mensual', 250.00),
    JSON_OBJECT('id_subcategoria', @sub_combustible, 'monto_mensual', 1800.00),
    JSON_OBJECT('id_subcategoria', @sub_taxi_bus, 'monto_mensual', 700.00),
    JSON_OBJECT('id_subcategoria', @sub_farmacia, 'monto_mensual', 600.00),
    JSON_OBJECT('id_subcategoria', @sub_consulta, 'monto_mensual', 500.00),
    JSON_OBJECT('id_subcategoria', @sub_salidas, 'monto_mensual', 800.00),
    JSON_OBJECT('id_subcategoria', @sub_fondo, 'monto_mensual', 2000.00)
  ),
  'admin'
);

SET @id_presupuesto = (
  SELECT id_presupuesto
  FROM presupuesto
  WHERE id_usuario = @id_usuario
    AND nombre_descriptivo = 'Presupuesto Personal Enero-Febrero 2026'
  ORDER BY fecha_creacion DESC
  LIMIT 1
);

-- ---------------------------------------------------------
-- 5) OBLIGACIONES FIJAS
-- sp_insertar_obligacion(
--   p_id_usuario, p_id_subcategoria, p_nombre, p_descripcion,
--   p_monto_fijo_mensual, p_dia_vencimiento, p_vigente,
--   p_fecha_inicio, p_fecha_fin, p_creado_por
-- )
-- ---------------------------------------------------------
CALL sp_insertar_obligacion(
  @id_usuario, @sub_alquiler, 'Alquiler apartamento', 'Pago mensual de renta',
  6000.00, 5, 1, '2026-01-01', '2026-02-28', 'admin'
);

CALL sp_insertar_obligacion(
  @id_usuario, @sub_internet, 'Internet hogar', 'Servicio mensual de internet',
  850.00, 10, 1, '2026-01-01', '2026-02-28', 'admin'
);

CALL sp_insertar_obligacion(
  @id_usuario, @sub_energia, 'Energia electrica', 'Pago de luz mensual',
  1100.00, 15, 1, '2026-01-01', '2026-02-28', 'admin'
);

CALL sp_insertar_obligacion(
  @id_usuario, @sub_agua, 'Agua potable', 'Pago mensual de agua',
  350.00, 20, 1, '2026-01-01', '2026-02-28', 'admin'
);

CALL sp_insertar_obligacion(
  @id_usuario, @sub_streaming, 'Suscripcion streaming', 'Pago mensual de plataforma',
  250.00, 28, 1, '2026-01-01', '2026-02-28', 'admin'
);

SET @obl_alquiler = (
  SELECT id_obligacion
  FROM obligacionfija
  WHERE id_usuario = @id_usuario
    AND nombre = 'Alquiler apartamento'
  LIMIT 1
);

SET @obl_internet = (
  SELECT id_obligacion
  FROM obligacionfija
  WHERE id_usuario = @id_usuario
    AND nombre = 'Internet hogar'
  LIMIT 1
);

SET @obl_energia = (
  SELECT id_obligacion
  FROM obligacionfija
  WHERE id_usuario = @id_usuario
    AND nombre = 'Energia electrica'
  LIMIT 1
);

SET @obl_agua = (
  SELECT id_obligacion
  FROM obligacionfija
  WHERE id_usuario = @id_usuario
    AND nombre = 'Agua potable'
  LIMIT 1
);

SET @obl_streaming = (
  SELECT id_obligacion
  FROM obligacionfija
  WHERE id_usuario = @id_usuario
    AND nombre = 'Suscripcion streaming'
  LIMIT 1
);

-- =========================================================
-- 6) TRANSACCIONES ENERO 2026
-- Usa sp_registrar_transaccion_completa
-- =========================================================
CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_salario, 'ingreso',
  'Salario mensual enero', 18000.00, '2026-01-01', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_fondo, 'ahorro',
  'Apartado a fondo de emergencia', 1800.00, '2026-01-03', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_alquiler, 'gasto',
  'Pago de alquiler enero', 6000.00, '2026-01-05', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_supermercado, 'gasto',
  'Compra supermercado quincenal', 1325.50, '2026-01-07', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_taxi_bus, 'gasto',
  'Transporte urbano semana 1', 145.00, '2026-01-08', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_internet, 'gasto',
  'Pago de internet enero', 850.00, '2026-01-09', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_restaurantes, 'gasto',
  'Almuerzo fuera de casa', 220.00, '2026-01-11', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_combustible, 'gasto',
  'Recarga de combustible', 620.00, '2026-01-12', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_energia, 'gasto',
  'Pago de energia enero', 1085.00, '2026-01-15', 'banca_movil', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_supermercado, 'gasto',
  'Compra de supermercado media quincena', 980.75, '2026-01-16', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_farmacia, 'gasto',
  'Medicamentos y vitaminas', 215.00, '2026-01-18', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_agua, 'gasto',
  'Pago de agua enero', 340.00, '2026-01-19', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_taxi_bus, 'gasto',
  'Pasajes y transporte', 120.00, '2026-01-20', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_salidas, 'gasto',
  'Salida al cine', 310.00, '2026-01-22', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_supermercado, 'gasto',
  'Compra de supermercado fin de mes', 1410.20, '2026-01-24', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_restaurantes, 'gasto',
  'Cena casual', 185.00, '2026-01-25', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_ingreso_extra, 'ingreso',
  'Trabajo freelance pequeño', 1250.00, '2026-01-26', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_streaming, 'gasto',
  'Pago de suscripcion streaming enero', 250.00, '2026-01-27', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_combustible, 'gasto',
  'Recarga de combustible cierre de mes', 540.00, '2026-01-28', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 1, @sub_consulta, 'gasto',
  'Consulta medica general', 400.00, '2026-01-29', 'efectivo', 'admin'
);

-- =========================================================
-- 7) TRANSACCIONES FEBRERO 2026
-- =========================================================
CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_salario, 'ingreso',
  'Salario mensual febrero', 18000.00, '2026-02-01', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_fondo, 'ahorro',
  'Apartado a fondo de emergencia', 2000.00, '2026-02-04', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_alquiler, 'gasto',
  'Pago de alquiler febrero', 6000.00, '2026-02-05', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_supermercado, 'gasto',
  'Compra supermercado inicio de mes', 1195.30, '2026-02-06', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_taxi_bus, 'gasto',
  'Pasajes semana 1', 135.00, '2026-02-08', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_internet, 'gasto',
  'Pago de internet febrero', 850.00, '2026-02-10', 'transferencia', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_restaurantes, 'gasto',
  'Almuerzo de fin de semana', 260.00, '2026-02-13', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_supermercado, 'gasto',
  'Compra supermercado media quincena', 870.40, '2026-02-14', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_energia, 'gasto',
  'Pago de energia febrero', 995.00, '2026-02-16', 'banca_movil', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_combustible, 'gasto',
  'Recarga de combustible', 590.00, '2026-02-17', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_agua, 'gasto',
  'Pago de agua febrero', 355.00, '2026-02-20', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_farmacia, 'gasto',
  'Compra de farmacia', 180.00, '2026-02-21', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_salidas, 'gasto',
  'Salida con amigos', 420.00, '2026-02-22', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_supermercado, 'gasto',
  'Compra de supermercado fin de mes', 1465.80, '2026-02-24', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_restaurantes, 'gasto',
  'Cena casual febrero', 210.00, '2026-02-25', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_ingreso_extra, 'ingreso',
  'Venta de articulo usado', 900.00, '2026-02-26', 'efectivo', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_streaming, 'gasto',
  'Pago de suscripcion streaming febrero', 250.00, '2026-02-27', 'tarjeta', 'admin'
);

CALL sp_registrar_transaccion_completa(
  @id_usuario, @id_presupuesto, 2026, 2, @sub_taxi_bus, 'gasto',
  'Pasajes y transporte cierre de mes', 160.00, '2026-02-28', 'efectivo', 'admin'
);

-- =========================================================
-- 8) CONSULTAS DE VERIFICACION
-- =========================================================
SELECT 'USUARIO CREADO' AS info, @id_usuario AS id_usuario;
SELECT 'PRESUPUESTO CREADO' AS info, @id_presupuesto AS id_presupuesto;

SELECT anio, mes, tipo, COUNT(*) AS cantidad_transacciones, SUM(monto) AS total
FROM transaccion
WHERE id_presupuesto = @id_presupuesto
GROUP BY anio, mes, tipo
ORDER BY anio, mes, tipo;

SELECT
  t.anio,
  t.mes,
  t.fecha,
  t.descripcion,
  t.tipo,
  t.monto,
  s.nombre_subcategoria
FROM transaccion t
INNER JOIN subcategoria s
  ON s.id_subcategoria = t.id_subcategoria
WHERE t.id_presupuesto = @id_presupuesto
ORDER BY t.fecha, t.fecha_registro;
