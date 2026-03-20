# Proyecto Teoría de Base de Datos 1

**Autor:** Fernando Hernández  
**Motor de Base de Datos:** MariaDB  

---

## Descripción

Este proyecto corresponde al diseño e implementación de la base de datos para un **Sistema de Presupuesto Personal**.

El sistema permite gestionar ingresos, gastos, ahorros y obligaciones financieras mediante presupuestos mensuales.

El proyecto incluye modelado, implementación en base de datos y lógica de negocio.

---

## Entidades Principales

- Usuario  
- Presupuesto  
- Categoria  
- Subcategoria  
- PresupuestoDetalle  
- ObligacionFija  
- Transaccion  
- ObligacionFija_Transaccion  

---

## Implementación de Base de Datos

La base de datos fue implementada utilizando **MariaDB** y administrada mediante **DBeaver**.

Se utilizaron:

- Claves primarias y foráneas  
- Consultas con `INNER JOIN`  
- Manejo de datos por año y mes  

---

## Auditoría del Sistema

Se implementó un sistema de auditoría en las principales tablas:

- `creado_por`  
- `modificado_por`  
- `creado_en`  
- `modificado_en`  

Permite controlar quién crea y modifica los registros y cuándo se realizan los cambios.

---

## Procedimientos Almacenados (CRUD)

Se implementaron procedimientos CRUD para:

### Usuario
- sp_insertar_usuario  
- sp_actualizar_usuario  
- sp_eliminar_usuario  
- sp_consultar_usuario  
- sp_listar_usuarios  

### Categoria
- sp_insertar_categoria  
- sp_actualizar_categoria  
- sp_eliminar_categoria  
- sp_consultar_categoria  
- sp_listar_categorias  

### Subcategoria
- sp_insertar_subcategoria  
- sp_actualizar_subcategoria  
- sp_eliminar_subcategoria  
- sp_consultar_subcategoria  
- sp_listar_subcategorias_por_categoria  

### Presupuesto
- sp_insertar_presupuesto  
- sp_actualizar_presupuesto  
- sp_eliminar_presupuesto  
- sp_consultar_presupuesto  
- sp_listar_presupuestos_usuario  

### PresupuestoDetalle
- sp_insertar_presupuesto_detalle  
- sp_actualizar_presupuesto_detalle  
- sp_eliminar_presupuesto_detalle  
- sp_consultar_presupuesto_detalle  
- sp_listar_detalles_presupuesto  

### ObligacionFija
- sp_insertar_obligacion  
- sp_actualizar_obligacion  
- sp_eliminar_obligacion  
- sp_consultar_obligacion  
- sp_listar_obligaciones_usuario  

### Transaccion
- sp_insertar_transaccion  
- sp_actualizar_transaccion  
- sp_eliminar_transaccion  
- sp_consultar_transaccion  
- sp_listar_transacciones_presupuesto  

---

## Lógica de Negocio

Se implementaron procedimientos adicionales:

- sp_crear_presupuesto_completo  
- sp_registrar_transaccion_completa  
- sp_procesar_obligaciones_mes  
- sp_calcular_balance_mensual  
- sp_calcular_porcentaje_ejecucion_mes  
- sp_cerrar_presupuesto  

---

## Funciones

Se desarrollaron funciones para:

- Cálculo de montos ejecutados  
- Cálculo de porcentajes  
- Validación de vigencia del presupuesto  
- Proyección de gastos  
- Cálculo por categoría y subcategoría  

---

## Triggers

Se implementaron triggers para:

- Creación automática de subcategoría por defecto  
- Automatización de auditoría  

---

## Datos de Prueba

Se generaron datos de prueba para:

- Enero 2026  
- Febrero 2026  

Incluyendo ingresos, gastos y ahorros.

---

## Archivos del Proyecto

- `.dbml` → modelo  
- `.sql` → tablas  
- `procedimientos.sql`  
- `funciones.sql`  
- `triggers.sql`  
- `insertar_datos.sql`  

---

## Orden de Ejecución

1. Crear base de datos  
2. Ejecutar tablas  
3. Ejecutar funciones  
4. Ejecutar triggers  
5. Ejecutar procedimientos  
6. Ejecutar datos de prueba  

---

## Estado Actual del Proyecto

- Modelo ERD  
- Base de datos implementada  
- CRUD completo  
- Funciones SQL  
- Triggers  
- Lógica de negocio  
- Datos de prueba funcionales  

---

## Autor

Fernando Hernández  
