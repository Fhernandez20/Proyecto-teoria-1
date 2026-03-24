# Sistema de Presupuesto Personal
 
**Autor:** Fernando Hernández  
**Asignatura:** Fundamentos de Bases de Datos  
**Motor de BD:** MariaDB  
**Backend / Frontend:** Java 17 — Aplicación de consola  
 
---
 
## Descripción
 
Sistema completo de gestión de presupuesto personal desarrollado en Java con arquitectura de 3 capas. Permite gestionar ingresos, gastos, obligaciones fijas y metas de ahorro, con generación automática de reportes en PDF con gráficos estadísticos.
 
---
 
## Arquitectura del sistema
 
```
┌─────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN        │
│    Frontend — Menús de consola      │
│  MenuPrincipal, MenuUsuarios,       │
│  MenuCategorias, MenuTransacciones  │
│  MenuReportes, etc.                 │
└────────────────┬────────────────────┘
                 │ llama
┌────────────────▼────────────────────┐
│         CAPA DE NEGOCIO             │
│    Backend — Services Java          │
│  UsuarioService, CategoriaService,  │
│  TransaccionService, ReporteService │
│  (solo CallableStatement, sin SQL)  │
└────────────────┬────────────────────┘
                 │ invoca stored procedures
┌────────────────▼────────────────────┐
│         CAPA DE DATOS               │
│    MariaDB — Stored Procedures      │
│  sp_insertar_usuario,               │
│  sp_calcular_balance_mensual, etc.  │
└─────────────────────────────────────┘
```
 
---
 
## Frontend — Menús de consola
 
### Descripción
 
La capa de presentación son menús interactivos de consola implementados en Java. Cada módulo tiene su propio menú con las 5 operaciones CRUD y opciones adicionales.
 
### Estructura
 
```
src/
├── app/
│   └── Main.java              # Punto de entrada del sistema
├── menu/
│   ├── MenuPrincipal.java     # Menú principal con los 7 módulos
│   ├── MenuUsuarios.java      # CRUD de usuarios
│   ├── MenuCategorias.java    # CRUD de categorías
│   ├── MenuSubcategorias.java # CRUD de subcategorías
│   ├── MenuPresupuestos.java  # CRUD de presupuestos
│   ├── MenuObligaciones.java  # CRUD de obligaciones fijas
│   ├── MenuTransacciones.java # CRUD de transacciones
│   └── MenuReportes.java      # Generación de los 6 reportes PDF
└── util/
    └── InputHelper.java       # Utilidad para lectura de datos del usuario
```
 
### Menú principal
 
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
 
### Módulos disponibles
 
Cada módulo tiene estas opciones:
 
| Opción | Acción |
|---|---|
| 1 | Insertar registro nuevo |
| 2 | Listar todos los registros |
| 3 | Consultar por ID |
| 4 | Actualizar registro |
| 5 | Eliminar / Desactivar |
| 0 | Volver al menú principal |
 
### Módulo de Reportes (opción 7)
 
El sistema pide solo el **ID de usuario** y detecta automáticamente el presupuesto activo. Genera el PDF y lo abre automáticamente.
 
```
╔══════════════════════════════════════════════╗
║         MODULO DE REPORTES                   ║
╠══════════════════════════════════════════════╣
║  1. Balance Mensual (Ingresos/Gastos/Ahorro) ║
║  2. Distribucion de Gastos por Categoria     ║
║  3. Cumplimiento de Presupuesto              ║
║  4. Tendencia de Gastos (multi-mes)          ║
║  5. Estado de Obligaciones Fijas             ║
║  6. Progreso de Metas de Ahorro              ║
╚══════════════════════════════════════════════╝
```
 
Los PDFs se guardan automáticamente en:
```
Backend/reportes/reporte1_balance_2026_1.pdf
Backend/reportes/reporte2_gastos_2026_1.pdf
...
```
 
---
