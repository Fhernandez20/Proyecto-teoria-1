# Sistema de Presupuesto Personal

**Autor:** Fernando Hernández  
**Asignatura:** Fundamentos de Bases de Datos  
**Motor de BD:** MariaDB  
**Backend:** Java 17 (consola) — VS Code  

---

## Descripción

Sistema completo de gestión de presupuesto personal que permite planificar, controlar y analizar finanzas personales. El usuario puede gestionar ingresos, gastos, obligaciones fijas y metas de ahorro mediante presupuestos mensuales con reportería en PDF.

---

## Requisitos previos

| Herramienta | Versión recomendada |
|---|---|
| Java (JDK) | 17 |
| MariaDB | 10.6 o superior |
| VS Code | Última versión |
| Extension Pack for Java (VS Code) | Última versión |

---

## Estructura del repositorio

```
Proyecto/
├── README.md
├── Database/
│   ├── DDL/
│   │   └── 01_crear_tablas.sql          # Creación de tablas
│   ├── Procedimientos/
│   │   ├── crud_usuario.sql
│   │   ├── crud_categoria.sql
│   │   ├── crud_subcategoria.sql
│   │   ├── crud_presupuesto.sql
│   │   ├── crud_presupuestodetalle.sql
│   │   ├── crud_obligacion.sql
│   │   ├── crud_transaccion.sql
│   │   └── procedimientos.sql           # Lógica de negocio
│   ├── Funciones/
│   │   └── funciones.sql                # 10 funciones del sistema
│   ├── Triggers/
│   │   └── triggers.sql                 # Trigger subcategoría por defecto
│   └── Datos_prueba/
│       └── insertar_datos.sql           # 2 meses completos de datos
├── Docs/
│   ├── ERD.png                          # Diagrama Entidad-Relación
│   ├── DBML proyecto TBD 1 arreglado.dbml
│   └── DBML proyecto TBD 1 arreglado.sql
└── presupuesto-personal/                # Aplicación Java
    ├── src/
    │   ├── app/Main.java
    │   ├── db/ConexionDB.java
    │   ├── menu/                        # Menús de consola
    │   ├── service/                     # Lógica de negocio Java
    │   └── util/InputHelper.java
    ├── lib/
    │   ├── mariadb-java-client-3.5.7.jar
    │   ├── openpdf-1.3.43.jar           # Generación de PDF
    │   ├── jfreechart-1.5.3.jar         # Gráficos en reportes
    │   └── jcommon-1.0.24.jar
    └── .vscode/settings.json
```

---

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/Fhernandez20/Proyecto-teoria-1.git
cd Proyecto-teoria-1
```

### 2. Crear la base de datos en MariaDB

Abre HeidiSQL, DBeaver o cualquier cliente MariaDB y ejecuta los scripts en este orden:

```sql
-- Paso 1: Crear la base de datos
CREATE DATABASE presupuesto_personal;
USE presupuesto_personal;

-- Paso 2: Crear las tablas
SOURCE Database/DDL/01_crear_tablas.sql;

-- Paso 3: Crear funciones
SOURCE Database/Funciones/funciones.sql;

-- Paso 4: Crear triggers
SOURCE Database/Triggers/triggers.sql;

-- Paso 5: Crear procedimientos CRUD
SOURCE Database/Procedimientos/crud_usuario.sql;
SOURCE Database/Procedimientos/crud_categoria.sql;
SOURCE Database/Procedimientos/crud_subcategoria.sql;
SOURCE Database/Procedimientos/crud_presupuesto.sql;
SOURCE Database/Procedimientos/crud_presupuestodetalle.sql;
SOURCE Database/Procedimientos/crud_obligacion.sql;
SOURCE Database/Procedimientos/crud_transaccion.sql;

-- Paso 6: Crear procedimientos de negocio
SOURCE Database/Procedimientos/procedimientos.sql;

-- Paso 7: Insertar datos de prueba
SOURCE Database/Datos_prueba/insertar_datos.sql;
```

### 3. Configurar la conexión

Abre el archivo `presupuesto-personal/src/db/ConexionDB.java` y ajusta los datos de conexión:

```java
private static final String URL      = "jdbc:mariadb://127.0.0.1:3306/presupuesto_personal";
private static final String USER     = "root";
private static final String PASSWORD = "tu_contraseña";
```

### 4. Abrir en VS Code

```bash
code presupuesto-personal/
```

Asegúrate de tener instalada la extensión **Extension Pack for Java**. VS Code detectará automáticamente los `.jar` en la carpeta `lib/`.

### 5. Correr el sistema

Abre `src/app/Main.java` y presiona **Run** (▶) o ejecuta desde la terminal:

```bash
cd presupuesto-personal
java -cp "bin;lib/*" app.Main
```

---

## Uso del sistema

Al iniciar verás el menú principal:

```
╔══════════════════════════════════════════════╗
║       SISTEMA DE PRESUPUESTO PERSONAL        ║
╠══════════════════════════════════════════════╣
║   GESTION                                    ║
║   1. Usuarios                                ║
║   2. Categorias                              ║
║   3. Subcategorias                           ║
║   4. Presupuestos                            ║
║   5. Obligaciones Fijas                      ║
║   6. Transacciones                           ║
║                                              ║
║   ANALISIS                                   ║
║   7. Reportes y Consultas                    ║
║                                              ║
║   0. Salir                                   ║
╚══════════════════════════════════════════════╝
```
## Base de datos

### Entidades principales

| Tabla | Descripción |
|---|---|
| `usuario` | Personas que usan el sistema |
| `presupuesto` | Plan financiero por periodo |
| `categoria` | Clasificación principal (ingreso/gasto/ahorro) |
| `subcategoria` | Clasificación detallada dentro de cada categoría |
| `presupuestodetalle` | Monto asignado por subcategoría en el presupuesto |
| `obligacionfija` | Pagos recurrentes mensuales |
| `transaccion` | Movimientos financieros reales |
| `obligacionfija_transaccion` | Relación entre obligaciones y sus pagos |

### Procedimientos almacenados

- **35 procedimientos CRUD** (5 por cada entidad)
- **8 procedimientos de lógica de negocio** (balance, cumplimiento, cierre de presupuesto, etc.)

### Funciones

10 funciones escalares para cálculos financieros: montos ejecutados, porcentajes de ejecución, balances, proyecciones y promedios.

### Triggers

- `trg_categoria_crear_subcategoria` — Crea automáticamente la subcategoría "General" cada vez que se inserta una categoría nueva.

### Campos de auditoría

Todas las tablas incluyen: `creado_por`, `modificado_por`, `creado_en`, `modificado_en`.

---

## Datos de prueba

El script `insertar_datos.sql` genera **2 meses completos** (Enero y Febrero 2026) con:

- 1 usuario con salario de L. 18,000
- 7 categorías (ingresos, alimentación, vivienda, transporte, salud, entretenimiento, ahorro)
- +20 subcategorías
- 5 obligaciones fijas (alquiler, internet, energía, agua, streaming)
- +35 transacciones distribuidas realistamente

---

## Tecnologías utilizadas

- **MariaDB** — Motor de base de datos relacional
- **Java 17** — Backend de aplicación de consola
- **OpenPDF 1.3.43** — Generación de reportes PDF
- **JFreeChart 1.5.3** — Gráficos estadísticos en PDF
- **MariaDB JDBC 3.5.7** — Conector Java-MariaDB
- **VS Code** — IDE de desarrollo

---

*Proyecto Final — Fundamentos de Bases de Datos*
