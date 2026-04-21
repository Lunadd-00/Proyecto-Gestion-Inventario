#  Sistema de Gestión de Inventarios

Sistema web para la **gestión, control y monitoreo de inventarios de insumos de emergencia** desarrollado para el **Departamento de Gestión de Riesgos de la Municipalidad de Escazú**.

El sistema permite administrar ítems, registrar movimientos de inventario, generar alertas y visualizar métricas que apoyan la toma de decisiones en situaciones de emergencia.

---

##  Autores

- Abby Camila Chavarría Bolaños  
- Luna Delgado Durango  
- Jimena Barrantes Arguedas  
- Erick José Chaves Delgado  

---

##  Descripción

Actualmente, el Departamento de Gestión de Riesgos de la Municipalidad de Escazú administra una gran cantidad de insumos críticos utilizados en la atención de emergencias, tales como:

- Equipos de emergencia  
- Suministros médicos  
- Herramientas especializadas  
- Materiales de respuesta rápida  

El control de estos inventarios se realiza mediante métodos manuales o sistemas poco integrados, lo cual genera problemas como:

- Falta de trazabilidad de insumos  
- Riesgo de vencimiento de productos  
- Dificultad para controlar existencias  
- Limitaciones en la toma de decisiones  

Para solucionar esto, se propone el desarrollo de un sistema que automatice el control, seguimiento y análisis del inventario.

---

##  Arquitectura del Sistema

El proyecto sigue una arquitectura basada en el patrón:

**MVC (Model - View - Controller)**

### Componentes principales:

- **Model:** Representación de las entidades del sistema  
- **View:** Interfaces desarrolladas con Thymeleaf y Bootstrap  
- **Controller:** Manejo de las peticiones HTTP  
- **Repository (DAO):** Acceso a datos en MySQL  
- **Service:** Lógica de negocio  

---

##  Requerimientos del Sistema

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

---

##  Historias de Usuario Implementadas (Segundo Avance - 50%)

Para el primer avance del proyecto se desarrolló aproximadamente el **50% de las historias de usuario (HU-01 a HU-10)**.

| Integrante | Historias de Usuario Implementadas |
|-------------|------------------------------------|
|  Abby Chavarría | **HU-01** – Inicio de sesión de administrador<br>**HU-02** – Inicio de sesión de colaborador<br>**HU-03** – Gestión de usuarios |
|  Luna Delgado | **HU-04** – Gestión de categorías<br>**HU-05** – Gestión de proveedores |
|  Erick Chaves | **HU-06** – Registro de ítems<br>**HU-07** – Edición de ítems<br>**HU-08** – Visualización de inventario |
|  Jimena Barrantes | **HU-09** – Entradas de inventario<br>**HU-10** – Salidas de inventario<br>Validación de stock insuficiente<br>Actualización de stock |

---

## 🚀 Historias de Usuario Implementadas (Avance Final)

En el avance final se implementaron funcionalidades enfocadas en **visualización de datos, reportes, seguridad y experiencia de usuario**.

| Integrante | Historias de Usuario Implementadas |
|-------------|------------------------------------|
|  Luna Delgado | **HU-15** – Dashboard con gráficas<br>**HU-16** – Métricas de inventario (total, por vencer, stock bajo)<br>**HU-17** – Reportes PDF (junto con Erick)<br>**HU-18** – Reportes Excel (junto con Erick) |
|  Erick Chaves | **HU-17** – Reportes PDF<br>**HU-18** – Reportes Excel<br>**HU-20** – Seguridad con Spring Security y respaldo de datos |
|  Equipo Completo | **HU-19** – Interfaz responsive y amigable |

###  Implementaciones Clave

- Dashboard con gráficas dinámicas  
- Generación de reportes en PDF y Excel  
- Seguridad con Spring Security (autenticación y autorización)  
- Cifrado de contraseñas  
- Envío de notificaciones por correo  
- Diseño responsive  
- Respaldo automático de la base de datos  

---

##  Progreso del Proyecto

| Módulo | Estado |
|------|------|
| Autenticación |  Completado |
| Gestión de Usuarios |  Completado |
| Categorías |  Completado |
| Proveedores |  Completado |
| Inventario |  Completado |
| Movimientos de inventario |  Completado |
| Reportes |  Completado |
| Dashboard |  Completado |

---
