CREATE TABLE `Usuario` (
  `id_usuario` varchar(30) PRIMARY KEY,
  `primer_nombre` varchar(30),
  `segundo_nombre` varchar(30),
  `primer_apellido` varchar(30),
  `segundo_apellido` varchar(30),
  `correo` varchar(100),
  `fecha_registro` datetime,
  `salario_mensual` decimal(12,2),
  `estado` bool,
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

CREATE TABLE `Presupuesto` (
  `id_presupuesto` varchar(30) PRIMARY KEY,
  `id_usuario` varchar(30),
  `nombre_descriptivo` varchar(60),
  `init_year` int,
  `init_month` int,
  `end_year` int,
  `end_month` int,
  `total_ingresos` decimal(12,2),
  `total_gastos` decimal(12,2),
  `total_ahorro` decimal(12,2),
  `fecha_creacion` datetime,
  `estado` varchar(15),
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

CREATE TABLE `Categoria` (
  `id_categoria` varchar(30) PRIMARY KEY,
  `id_usuario` varchar(30),
  `nombre_categoria` varchar(50),
  `descripcion` varchar(150),
  `tipo_categoria` varchar(10),
  `icono` varchar(50),
  `color_hex` varchar(10),
  `orden_presentacion` int,
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

CREATE TABLE `Subcategoria` (
  `id_subcategoria` varchar(30) PRIMARY KEY,
  `id_categoria` varchar(30),
  `nombre_subcategoria` varchar(50),
  `descripcion` varchar(150),
  `indicador_activo` bool,
  `es_defecto` bool,
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

CREATE TABLE `PresupuestoDetalle` (
  `id_detalle` varchar(30) PRIMARY KEY,
  `id_presupuesto` varchar(30),
  `id_subcategoria` varchar(30),
  `monto_mensual` decimal(12,2),
  `observaciones` varchar(200),
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

CREATE TABLE `ObligacionFija` (
  `id_obligacion` varchar(30) PRIMARY KEY,
  `id_usuario` varchar(30),
  `id_subcategoria` varchar(30),
  `nombre` varchar(80),
  `descripcion` varchar(200),
  `monto_fijo_mensual` decimal(12,2),
  `dia_vencimiento` int,
  `vigente` bool,
  `fecha_inicio` date,
  `fecha_fin` date,
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

CREATE TABLE `Transaccion` (
  `id_transaccion` varchar(30) PRIMARY KEY,
  `id_usuario` varchar(30),
  `id_presupuesto` varchar(30),
  `anio` int,
  `mes` int,
  `id_subcategoria` varchar(30),
  `id_obligacion` varchar(30),
  `tipo` varchar(10),
  `descripcion` varchar(200),
  `monto` decimal(12,2),
  `fecha` date,
  `metodo_pago` varchar(20),
  `num_factura` varchar(40),
  `observaciones` varchar(200),
  `fecha_registro` datetime,
  `creado_por` varchar(30),
  `modificado_por` varchar(30),
  `creado_en` datetime,
  `modificado_en` datetime
);

ALTER TABLE `Presupuesto` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id_usuario`);

ALTER TABLE `Categoria` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id_usuario`);

ALTER TABLE `Subcategoria` ADD FOREIGN KEY (`id_categoria`) REFERENCES `Categoria` (`id_categoria`);

ALTER TABLE `PresupuestoDetalle` ADD FOREIGN KEY (`id_presupuesto`) REFERENCES `Presupuesto` (`id_presupuesto`);

ALTER TABLE `PresupuestoDetalle` ADD FOREIGN KEY (`id_subcategoria`) REFERENCES `Subcategoria` (`id_subcategoria`);

ALTER TABLE `ObligacionFija` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id_usuario`);

ALTER TABLE `ObligacionFija` ADD FOREIGN KEY (`id_subcategoria`) REFERENCES `Subcategoria` (`id_subcategoria`);

ALTER TABLE `Transaccion` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id_usuario`);

ALTER TABLE `Transaccion` ADD FOREIGN KEY (`id_presupuesto`) REFERENCES `Presupuesto` (`id_presupuesto`);

ALTER TABLE `Transaccion` ADD FOREIGN KEY (`id_subcategoria`) REFERENCES `Subcategoria` (`id_subcategoria`);

ALTER TABLE `Transaccion` ADD FOREIGN KEY (`id_obligacion`) REFERENCES `ObligacionFija` (`id_obligacion`);
