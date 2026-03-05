USE presupuesto_personal;
DELIMITER //

DROP PROCEDURE IF EXISTS sp_insertar_obligacion//
CREATE PROCEDURE sp_insertar_obligacion(
  IN p_id_usuario VARCHAR(30),
  IN p_id_subcategoria VARCHAR(30),
  IN p_nombre VARCHAR(80),
  IN p_descripcion VARCHAR(200),
  IN p_monto DECIMAL(12,2),
  IN p_dia_vencimiento INT,
  IN p_fecha_inicio DATE,
  IN p_fecha_fin DATE,
  IN p_creado_por VARCHAR(30)
)
BEGIN
  INSERT INTO obligacionfija (
    id_obligacion, id_usuario, id_subcategoria,
    nombre, descripcion, monto_fijo_mensual,
    dia_vencimiento, vigente, fecha_inicio, fecha_fin
  )
  VALUES (
    CONCAT('OF_', LEFT(REPLACE(UUID(),'-',''), 24)),
    p_id_usuario, p_id_subcategoria,
    p_nombre, p_descripcion, p_monto,
    p_dia_vencimiento, 1, p_fecha_inicio, p_fecha_fin
  );
END//

DROP PROCEDURE IF EXISTS sp_actualizar_obligacion//
CREATE PROCEDURE sp_actualizar_obligacion(
  IN p_id_obligacion VARCHAR(30),
  IN p_nombre VARCHAR(80),
  IN p_descripcion VARCHAR(200),
  IN p_monto DECIMAL(12,2),
  IN p_dia_vencimiento INT,
  IN p_fecha_fin DATE,
  IN p_activo BOOL,
  IN p_modificado_por VARCHAR(30)
)
BEGIN
  UPDATE obligacionfija
  SET nombre = p_nombre,
      descripcion = p_descripcion,
      monto_fijo_mensual = p_monto,
      dia_vencimiento = p_dia_vencimiento,
      fecha_fin = p_fecha_fin,
      vigente = p_activo
  WHERE id_obligacion = p_id_obligacion;
END//

DROP PROCEDURE IF EXISTS sp_eliminar_obligacion//
CREATE PROCEDURE sp_eliminar_obligacion(IN p_id_obligacion VARCHAR(30))
BEGIN
  UPDATE obligacionfija
  SET vigente = 0
  WHERE id_obligacion = p_id_obligacion;
END//

DROP PROCEDURE IF EXISTS sp_consultar_obligacion//
CREATE PROCEDURE sp_consultar_obligacion(IN p_id_obligacion VARCHAR(30))
BEGIN
  SELECT o.*,
         s.nombre_subcategoria,
         c.nombre_categoria
  FROM obligacionfija o
  JOIN subcategoria s ON s.id_subcategoria = o.id_subcategoria
  JOIN categoria c ON c.id_categoria = s.id_categoria
  WHERE o.id_obligacion = p_id_obligacion;
END//

DROP PROCEDURE IF EXISTS sp_listar_obligaciones_usuario//
CREATE PROCEDURE sp_listar_obligaciones_usuario(
  IN p_id_usuario VARCHAR(30),
  IN p_activo BOOL
)
BEGIN
  SELECT *
  FROM obligacionfija
  WHERE id_usuario = p_id_usuario
    AND (p_activo IS NULL OR vigente = p_activo)
  ORDER BY vigente DESC, nombre ASC;
END//

DELIMITER ;