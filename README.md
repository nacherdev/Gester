# Gester
Este proyecto es una aplicación Android diseñada para gestionar
usuarios, servicios y citas mediante una base de datos MySQL remota.
Implementa una arquitectura robusta utilizando el patrón DAO y una
base de datos relacional con integridad referencial.
## 🚀 Características
* **Gestión de Usuarios:** Registro de clientes con datos personales y
DNI.
* **Catálogo de Servicios:** Administración de servicios con duración
y precio.
* **Sistema de Citas:** Vinculación de usuarios y servicios con
control de fecha, hora y estado.
* **Arquitectura DAO:** Separación clara entre la lógica de negocio y
el acceso a datos.
* **Integridad Referencial:** Uso de Foreign Keys (Claves Foráneas)
para asegurar la consistencia de los datos.
## ️ Tecnologías Utilizadas
* **Java:** Lenguaje principal del desarrollo.
* **Android Studio:** Entorno de desarrollo (IDE).
* **MySQL:** Motor de base de datos relacional.
* **JDBC:** Conector para la comunicación entre Java y MySQL.
* **Git/GitHub:** Control de versiones.
## 📊 Estructura de la Base de Datos
El esquema se compone de tres tablas principales relacionadas:
| Tabla | Descripción |
| :--- | :--- |
| **usuarios** | Almacena id, nombre, apellidos, DNI y fecha de
nacimiento. |
| **servicios** | Almacena id, nombre del servicio, duración y precio.
|
| **citas** | Tabla relacional con FK a usuarios y servicios, además
de fecha y hora. |
## ️ Arquitectura de Paquetes

```
com.example.gester
├── dao # Contiene la clase Dao.java con la lógica SQL (JOINs,
Try-with-resources).
├── models # Clases POJO (Usuario, Servicio, Cita).
└── ui # Actividades y fragmentos de la interfaz de usuario.
```
## 💻 Ejemplo de Consulta Optimizada
El proyecto utiliza `INNER JOIN` para recuperar información completa
en una sola petición al servidor, mejorando el rendimiento:
```sql
SELECT c.*, u.nombre, s.nombre_servicio
FROM citas c
JOIN usuarios u ON c.id_usuario = u.id
JOIN servicios s ON c.id_servicio = s.id;
```

---
*Desarrollado como parte del aprendizaje en desarrollo de aplicaciones
Android y gestión de bases de datos.*
