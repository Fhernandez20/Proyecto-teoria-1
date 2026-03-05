# Proyecto Teoría de Base de Datos 1

**Autor:** Fernando Hernández  
**Motor de Base de Datos:** MariaDB  

---

## Descripción

Este proyecto corresponde al diseño e implementación de la base de datos para un **Sistema de Presupuesto Personal**.

El objetivo del sistema es permitir a los usuarios gestionar sus finanzas personales mediante el registro de presupuestos, ingresos, gastos y obligaciones financieras.

El proyecto incluye:

- Diseño del **modelo entidad–relación (ERD)**
- Modelo de base de datos utilizando **DBML**
- Implementación de tablas en **MariaDB**
- Implementación de **procedimientos almacenados CRUD**
- Organización estructurada del proyecto en GitHub

---

## Entidades Principales

Las principales entidades del sistema son:

- Usuario  
- Presupuesto  
- Categoria  
- Subcategoria  
- PresupuestoDetalle  
- ObligacionFija  
- Transaccion  
- ObligacionFija_Transaccion  

Estas entidades permiten estructurar la información financiera del usuario y mantener relaciones entre presupuestos, transacciones y categorías.

---

## Implementación de Base de Datos

La base de datos fue implementada utilizando **MariaDB** y administrada mediante **DBeaver**.


---

## Procedimientos Almacenados

Se implementaron procedimientos almacenados para las operaciones **CRUD (Create, Read, Update, Delete)** de las principales tablas del sistema.

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

## Archivos del Modelo

- `.dbml` → Modelo diseñado en **dbdiagram.io**  
- `.sql` → Script de creación de tablas compatible con **MariaDB**  
- `ERD` → Diagrama del modelo entidad-relación  
- `Procedimientos` → Procedimientos almacenados CRUD  
- `Funciones` → Funciones SQL del sistema  
- `Triggers` → Automatización de procesos en la base de datos  
- `Datos de prueba` → Inserciones de datos para pruebas del sistema  

---

## Estado Actual del Proyecto

Actualmente el proyecto incluye:

- Diseño del modelo entidad–relación
- Modelo en DBML
- Implementación de tablas en MariaDB
- Relaciones y claves foráneas
- Procedimientos almacenados CRUD
- Organización estructurada del repositorio en GitHub

---

## Autor

Fernando Hernández
