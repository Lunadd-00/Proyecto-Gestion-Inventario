
# Sistema de Gestión de Inventarios

Sistema web para la gestión, control y monitoreo de inventarios de insumos de emergencia desarrollado para el Departamento de Gestión de Riesgos de la Municipalidad de Escazú.

El sistema permite administrar ítems, registrar movimientos de inventario, generar alertas y visualizar métricas que apoyen la toma de decisiones ante situaciones de emergencia.


## Autores

- Abby Camila Chavarría Bolaños
- Luna Delgado Durango
- Jimena Barrantes Arguedas
- Erick José Chaves Delgado
  

## Descripción

Actualmente el Departamento de Gestión de Riesgos de la Municipalidad de Escazú administra una gran cantidad de insumos críticos utilizados en la atención de emergencias, tales como:

- Equipos de emergencia
- Suministros médicos
- Herramientas especializadas
- Materiales de respuesta rápida

El control de estos inventarios se realiza mediante métodos manuales o sistemas poco integrados, lo cual genera problemas como:

- Falta de trazabilidad de insumos
- Riesgo de vencimiento de productos
- Dificultad para controlar existencias
- Limitaciones en la toma de decisiones

Para solucionar esto se propone el desarrollo de un Sistema de Gestión de Inventarios que permita automatizar el control, seguimiento y análisis de los insumos.
## Arquitectura del Sistema

El proyecto sigue una arquitectura basada en el patrón:

- MVC (Model - View - Controller)

Además se utilizan principios de diseño que facilitan la mantenibilidad del código. Componentes principales:

- Model: Representación de las entidades del sistema
- View: Interfaces desarrolladas con Thymeleaf y Bootstrap
- Controller: Manejo de las peticiones HTTP
- DAO / Repository: Acceso a datos en MySQL
- Service: Lógica de negocio
## Requerimientos del Sistema

### Requerimientos Funcionales

| Funcionalidad | Descripción |
|---------------|-------------|
| Gestión de Ítems | CRUD de ítems con o sin caducidad |
| Movimientos de Inventario | Entradas, salidas y transferencias |
| Alertas de Vencimiento | Notificaciones por correo |
| Dashboard | Visualización de métricas |
| Gestión de Categorías | Clasificación de insumos |
| Gestión de Proveedores | Administración de proveedores |
| Alertas de Stock Bajo | Notificación de niveles mínimos |
| Reportes | Exportación en PDF y Excel |
| Gestión de Usuarios | Roles Administrador / Colaborador |
| Búsqueda Avanzada | Filtros múltiples |

### Requerimientos No Funcionales

| Categoría | Descripción |
|-----------|-------------|
| Usabilidad | Interfaz amigable alineada con el libro de marca |
| Seguridad | Contraseñas cifradas y validación de sesiones |
| Mantenibilidad | Código estructurado y documentado |
| Portabilidad | Compatible con navegadores modernos |
| Respaldo | Backup automático de la base de datos |
## Historias de Usuario Implementadas (50%)

Para el primer avance del proyecto se desarrolló aproximadamente el 50% de las historias de usuario, abarcando de la primera a la decima.

| Integrante | Historias de Usuario Implementadas |
|-------------|------------------------------------|
| Abby Chavarría | **HU-01** – Inicio de sesión de administrador **HU-02** – Inicio de sesión de colaborador **HU-03** – Gestión de usuarios (crear, editar, desactivar y asignar roles) |
| Luna Delgado | **HU-04** – Gestión de categorías (crear, editar, eliminar) **HU-05** – Gestión de proveedores (registro y visualización) |
| Erick Chaves | **HU-06** – Registro de ítems con o sin fecha de caducidad **HU-07** – Edición de ítems **HU-08** – Visualización y filtrado de inventario |
| Jimena Barrantes | **HU-09** – Registro de entradas de inventario **HU-10** – Registro de salidas de inventario Validación de stock insuficiente Actualización de stock a cero |

## Progreso del Proyecto

| Módulo | Estado |
|------|------|
| Autenticación |  Completado |
| Gestión de Usuarios |  Completado |
| Categorías |  Completado |
| Proveedores |  Completado |
| Inventario |  En desarrollo |
| Movimientos de inventario |  En desarrollo |
| Reportes |  Pendiente |
| Dashboard |  Pendiente |
