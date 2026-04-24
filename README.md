# Portal Estudiantil

Sistema web integral para la gestión académica de una institución educativa. Permite a administradores, docentes, encargados y estudiantes interactuar con información académica, comunicarse, gestionar asistencias, calificaciones, evaluaciones, eventos y soporte, entre otros módulos.

---

## Tabla de Contenidos

1. [Descripción](#descripción)
2. [Características Principales](#características-principales)
3. [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
5. [Estructura de Carpetas](#estructura-de-carpetas)
6. [Requisitos Previos](#requisitos-previos)
7. [Instalación y Configuración](#instalación-y-configuración)
8. [Ejecución del Proyecto](#ejecución-del-proyecto)
9. [Base de Datos](#base-de-datos)
10. [Módulos Funcionales](#módulos-funcionales)
11. [Seguridad y Roles](#seguridad-y-roles)
12. [Recuperación de Contraseña](#recuperación-de-contraseña)
13. [Historias de Usuario](#historias-de-usuario)
14. [Contribución](#contribución)
15. [Licencia](#licencia)

---

## Descripción

**Portal Estudiantil** es una aplicación web desarrollada con **Spring Boot 3.3.6** y **Thymeleaf**, pensada para centralizar la gestión académica y administrativa de una institución educativa. El sistema provee vistas diferenciadas por rol (Administrador, Docente, Encargado y Estudiante) y cubre procesos como matrícula, control de asistencia, registro de calificaciones, reportes académicos, mensajería interna, soporte técnico y predicción de rendimiento académico.

## Características Principales

- Autenticación y autorización mediante **Spring Security** con roles diferenciados.
- Gestión de usuarios (estudiantes, docentes, encargados y administradores).
- Registro y consulta de **asistencias** (pase de lista).
- Administración de **calificaciones** y **evaluaciones**.
- Generación de **reportes académicos** en PDF (Flying Saucer + OpenPDF).
- **Mensajería interna** entre usuarios del portal.
- Módulo de **tickets/soporte** con categorías, adjuntos y evaluación de atención.
- **Calendario** de eventos institucionales y **gestión de noticias**.
- **Aprendizaje personalizado** con recursos de aprendizaje.
- **Predicción de rendimiento académico** con vistas para Admin, Docente y Encargado.
- **FeedBack 360** para evaluación cruzada.
- **Estadísticas** específicas por rol.
- **Recuperación de contraseña** vía correo electrónico.
- Gestión de información personal del usuario (direcciones, teléfonos, correos).
- Soporte de ubicaciones (Provincia, Cantón, Distrito).

## Tecnologías Utilizadas

| Categoría | Tecnología |
|-----------|------------|
| Lenguaje | Java 17 / 23 |
| Framework | Spring Boot 3.3.6 |
| Vista | Thymeleaf + Thymeleaf Extras Spring Security 6 |
| ORM | Spring Data JPA / Hibernate |
| Base de Datos | MariaDB 10+ |
| Seguridad | Spring Security + BCrypt |
| Correo | Spring Boot Starter Mail (SMTP Gmail) |
| PDF | Flying Saucer (openpdf) + OpenPDF + iText |
| Frontend | Bootstrap 5.3, FontAwesome 6.7, jQuery 3.7, Popper.js |
| Utilidades | Lombok |
| Build | Maven |
| IDE recomendado | Apache NetBeans / IntelliJ IDEA |

## Arquitectura del Proyecto

La aplicación sigue una arquitectura clásica en capas, inspirada en el patrón **MVC** de Spring:

- **Controller** — Maneja peticiones HTTP y coordina las vistas.
- **Service** — Contiene la lógica de negocio.
- **Repository** — Acceso a datos vía Spring Data JPA.
- **Domain** — Entidades JPA y objetos de dominio.
- **Security** — Configuración de seguridad, manejadores de login y detalles de usuario.
- **Templates (Thymeleaf)** — Capa de presentación.
- **Static** — Recursos estáticos (CSS, JS, imágenes).

## Estructura de Carpetas

```
Portal_Estudiantil/
├── Diseño Sistemas/
│   ├── MariaDB/                 # Scripts SQL para MariaDB (creación, inserts, alters)
│   └── Oracle Antiguo/          # Scripts SQL legados de Oracle
├── Historias_Usuario/
│   ├── Historias_De_Usuario_Portal_Estudiantil.pdf
│   └── Historias_De_Usuario_Portal_Estudiantil.xlsx
└── ProyectoPortalEstudiantil/
    ├── pom.xml
    ├── nbactions.xml
    └── src/
        └── main/
            ├── java/PortalEstudiantil/ProyectoPortalEstudiantil/
            │   ├── ProyectoPortalEstudiantilApplication.java
            │   ├── Controller/        # Controladores MVC
            │   ├── Service/           # Lógica de negocio
            │   ├── Repository/        # Repositorios JPA
            │   ├── Domain/            # Entidades JPA
            │   └── Security/          # Configuración de seguridad
            └── resources/
                ├── application.properties
                ├── static/            # CSS, JS, imágenes
                └── templates/         # Vistas Thymeleaf
```

## Requisitos Previos

Antes de ejecutar el proyecto asegúrese de tener instalado:

- **JDK 17 o superior** (el proyecto está configurado con Java 23 y release 17).
- **Maven 3.8+**.
- **MariaDB 10+** escuchando en el puerto `3307` (configurable en `application.properties`).
- Una cuenta SMTP válida para el envío de correos (por defecto se usa Gmail con contraseña de aplicación).
- IDE compatible: **NetBeans**, **IntelliJ IDEA** o **Eclipse**.

## Instalación y Configuración

1. **Clonar el repositorio:**

   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd Portal_Estudiantil/ProyectoPortalEstudiantil
   ```

2. **Configurar la base de datos:**

   Ejecutar los scripts ubicados en `Diseño Sistemas/MariaDB/` en el siguiente orden:

   1. `1.CrearUsuario.sql`
   2. `2.CrearTablas.sql`
   3. `3.CrearProcesos.sql`
   4. `4.Procedimientos restablecer contraseña.sql`
   5. `5.Insert Estados.sql`
   6. `6.Insert TipoUsuario.sql`
   7. `7.Insert Provincias etc.sql`
   8. `8.Insert Usuario.sql`
   9. `9.Insert Direccion Usuario.sql`
   10. `10.Paso Establecer contraseña creada por Java.sql`

   Scripts adicionales (`Alter tabla Telefono.sql`, `Alter table EncargadoEstudiante.sql`, `Update_Inserts.sql`, etc.) deben aplicarse si se requieren ajustes posteriores al esquema inicial.

3. **Configurar `application.properties`:**

   Revisar el archivo `ProyectoPortalEstudiantil/src/main/resources/application.properties` y ajustar:

   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3307/PORTAL_ESCOLAR
   spring.datasource.username=portal_escolar
   spring.datasource.password=portal123

   spring.mail.username=tu_correo@gmail.com
   spring.mail.password=tu_password_de_aplicacion

   app.base-url=http://localhost:8080
   app.uploads.path=C:/ruta/local/uploads
   app.upload.dir=C:/ruta/local/uploads/mensajes/
   ```

   > **Importante:** No versionar credenciales reales. Use variables de entorno o un archivo `application-local.properties` para su entorno local.

4. **Instalar dependencias y compilar:**

   ```bash
   mvn clean install
   ```

## Ejecución del Proyecto

Desde la raíz del módulo `ProyectoPortalEstudiantil`:

```bash
mvn spring-boot:run
```

O bien, empaquetando y ejecutando:

```bash
mvn clean package
java -jar target/ProyectoPortalEstudiantil-1.jar
```

Al iniciar, la aplicación intentará conectarse a MariaDB y mostrará en consola:

```
✅ CONEXIÓN A MARIADB OK
```

Acceda al portal en: **http://localhost:8080**

## Base de Datos

- Motor: **MariaDB** (puerto local por defecto `3307`, base de datos `PORTAL_ESCOLAR`).
- Dialecto JPA: `org.hibernate.dialect.MariaDBDialect`.
- Estrategia de DDL: `none` (el esquema se administra vía scripts SQL, no por Hibernate).
- Los scripts `.sql` completos están en `Diseño Sistemas/MariaDB/`.
- Usuario inicial de base de datos: `portal_escolar` / `portal123` (cambiar en producción).

## Módulos Funcionales

El portal se organiza en los siguientes módulos principales:

- **Login y Autenticación** — Inicio de sesión con Spring Security.
- **Información Personal** — Gestión de datos personales, direcciones, teléfonos y correos.
- **Usuarios** — CRUD de usuarios, encargados y tipos de usuario.
- **Gestión Académica** — Administración de aulas, materias, secciones, horarios, periodos y matrículas.
- **Asistencias** — Pase de lista, detalle y listado de asistencias.
- **Calificaciones** — Registro y modificación de notas.
- **Evaluaciones** — Listado y modificación de evaluaciones.
- **Reportes Académicos** — Generación en PDF de reportes por estudiante/periodo.
- **Mensajería** — Bandeja, conversaciones y envío de nuevos mensajes.
- **Calendario y Eventos** — Gestión y visualización de eventos institucionales.
- **Noticias** — Publicación y administración de noticias.
- **Soporte / Tickets** — Creación de tickets con categorías, adjuntos y evaluación del servicio.
- **Aprendizaje Personalizado** — Recursos formativos dirigidos al estudiante.
- **Predicción Académica** — Vistas específicas para Admin, Docente y Encargado.
- **FeedBack 360** — Retroalimentación cruzada entre usuarios.
- **Estadísticas** — Paneles para Administrador y Encargado.

## Seguridad y Roles

- Autenticación basada en formulario con **Spring Security**.
- Contraseñas hasheadas con **BCrypt**.
- Manejadores personalizados para éxito (`LoginSuccessHandler`) y fallo (`LoginFailureHandler`) de login.
- `PortalUserDetails` implementa `UserDetails` para exponer información del usuario autenticado.
- Roles típicos: `ADMINISTRADOR`, `DOCENTE`, `ENCARGADO`, `ESTUDIANTE` (definidos en `TipoUsuario`).

## Recuperación de Contraseña

El sistema permite restablecer la contraseña mediante un enlace enviado al correo registrado del usuario:

1. El usuario solicita recuperación en `/recuperar-password`.
2. Se genera un token y se envía por correo (SMTP Gmail).
3. El usuario accede al enlace y define una nueva contraseña en `/reset-password`.
4. La nueva contraseña se almacena hasheada con BCrypt.

## Historias de Usuario

Las historias de usuario del proyecto están documentadas en:

- `Historias_Usuario/Historias_De_Usuario_Portal_Estudiantil.pdf`
- `Historias_Usuario/Historias_De_Usuario_Portal_Estudiantil.xlsx`

Estos documentos detallan los requisitos funcionales por rol y son la referencia principal para validar el alcance del sistema.

## Contribución

1. Cree una rama a partir de `main`:
   ```bash
   git checkout -b feature/nombre-de-la-funcionalidad
   ```
2. Realice commits atómicos y descriptivos.
3. Verifique que la aplicación compila y se ejecuta correctamente.
4. Abra un Pull Request describiendo los cambios y referencias a las historias de usuario relacionadas.

## Licencia

Este proyecto es de uso académico/institucional. Cualquier uso externo requiere autorización explícita del equipo de desarrollo.

---

**Autor(es):** Equipo de desarrollo Portal Estudiantil  
**Versión actual:** 1.0
