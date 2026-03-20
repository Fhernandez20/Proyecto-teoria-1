USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_obligacion//
CREATE PROCEDURE sp_insertar_obligacion(
  IN p_id_usuario VARCHAR(30),
  IN p_id_subcategoria VARCHAR(30),
  IN p_nombre VARCHAR(80),
  IN p_descripcion VARCHAR(200),
  IN p_monto_fijo_mensual DECIMAL(12,2),
  IN p_dia_vencimiento INT,
  IN p_vigente BOOL,
  IN p_fecha_inicio DATE,
  IN p_fecha_fin DATE,
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO obligacionfija (
    id_obligacion,
    id_usuario,
    id_subcategoria,
    nombre,
    descripcion,
    monto_fijo_mensual,
    dia_vencimiento,
    vigente,
    fecha_inicio,
    fecha_fin,
    creado_por,
    modificado_por
  )
  VALUES (
    CONCAT('OBL_', LEFT(REPLACE(UUID(),'-',''), 22)),
    p_id_usuario,
    p_id_subcategoria,
    p_nombre,
    p_descripcion,
    p_monto_fijo_mensual,
    p_dia_vencimiento,
    p_vigente,
    p_fecha_inicio,
    p_fecha_fin,
    p_creado_por,
    p_creado_por
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_obligacion//
CREATE PROCEDURE sp_actualizar_obligacion(
  IN p_id_obligacion VARCHAR(30),
  IN p_id_subcategoria VARCHAR(30),
  IN p_nombre VARCHAR(80),
  IN p_descripcion VARCHAR(200),
  IN p_monto_fijo_mensual DECIMAL(12,2),
  IN p_dia_vencimiento INT,
  IN p_vigente BOOL,
  IN p_fecha_inicio DATE,
  IN p_fecha_fin DATE,
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE obligacionfija
  SET id_subcategoria = p_id_subcategoria,
      nombre = p_nombre,
      descripcion = p_descripcion,
      monto_fijo_mensual = p_monto_fijo_mensual,
      dia_vencimiento = p_dia_vencimiento,
      vigente = p_vigente,
      fecha_inicio = p_fecha_inicio,
      fecha_fin = p_fecha_fin,
      modificado_por = p_modificado_por
  WHERE id_obligacion = p_id_obligacion;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_obligacion//
CREATE PROCEDURE sp_eliminar_obligacion(IN p_id_obligacion VARCHAR(30))
BEGIN
  IF EXISTS (
    SELECT 1
    FROM obligacionfija_transaccion
    WHERE id_obligacion = p_id_obligacion
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede eliminar: la obligación tiene transacciones asociadas';
  END IF;

  DELETE FROM obligacionfija
  WHERE id_obligacion = p_id_obligacion;
END//

DROP PROCEDURE IF EXISTS sp_consultar_obligacion//
CREATE PROCEDURE sp_consultar_obligacion(IN p_id_obligacion VARCHAR(30))
BEGIN
  SELECT
    o.id_obligacion,
    o.id_usuario,
    o.id_subcategoria,
    o.nombre,
    o.descripcion,
    o.monto_fijo_mensual,
    o.dia_vencimiento,
    o.vigente,
    o.fecha_inicio,
    o.fecha_fin,
    sc.nombre_subcategoria,
    c.nombre_categoria,
    o.creado_por,
    o.modificado_por,
    o.creado_en,
    o.modificado_en
  FROM obligacionfija o
  INNER JOIN subcategoria sc
    ON o.id_subcategoria = sc.id_subcategoria
  INNER JOIN categoria c
    ON sc.id_categoria = c.id_categoria
  WHERE o.id_obligacion = p_id_obligacion;
END//

DROP PROCEDURE IF EXISTS sp_listar_obligaciones_usuario//
CREATE PROCEDURE sp_listar_obligaciones_usuario(
  IN p_id_usuario VARCHAR(30),
  IN p_vigente BOOL
)
BEGIN
  SELECT
    o.id_obligacion,
    o.nombre,
    o.descripcion,
    o.monto_fijo_mensual,
    o.dia_vencimiento,
    o.vigente,
    o.fecha_inicio,
    o.fecha_fin,
    c.nombre_categoria,
    sc.nombre_subcategoria,
    o.creado_por,
    o.modificado_por,
    o.creado_en,
    o.modificado_en
  FROM obligacionfija o
  INNER JOIN subcategoria sc
    ON o.id_subcategoria = sc.id_subcategoria
  INNER JOIN categoria c
    ON sc.id_categoria = c.id_categoria
  WHERE o.id_usuario = p_id_usuario
    AND (p_vigente IS NULL OR o.vigente = p_vigente)
  ORDER BY o.nombre;
END//

DELIMITER ;