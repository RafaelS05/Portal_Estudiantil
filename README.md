# Portal Estudiantil

A comprehensive web-based academic management system built with Spring Boot. Portal Estudiantil centralizes academic operations for educational institutions, providing dedicated interfaces for administrators, teachers, guardians, and students.

---

## Features

- **Role-based access control** — Four distinct roles: Administrator, Teacher (Docente), Guardian (Encargado), and Student (Estudiante), each with a tailored dashboard and permissions.
- **Academic management** — Manage classrooms, subjects, sections, schedules, academic periods, and student enrollments from a single hub.
- **Attendance & grades** — Track daily attendance (pase de lista) and manage grades and evaluations with decimal precision.
- **PDF reports** — Generate academic reports as PDF documents using Flying Saucer and OpenPDF.
- **Internal messaging** — Built-in messaging system with file attachment support for communication between portal users.
- **Support ticketing** — Categorized support ticket system with status tracking, attachments, and satisfaction evaluations.
- **Event calendar & news** — Manage institutional events and publish news items visible across roles.
- **Academic prediction** — Role-specific views for monitoring and predicting academic performance.
- **360° Feedback** — Cross-evaluation feedback system between users.
- **Statistics dashboards** — Analytics views for administrators and guardians.
- **Personalized learning resources** — Attach and manage learning materials per student or section.
- **Password recovery** — Secure email-based password reset via Gmail SMTP.
- **Account security** — BCrypt password hashing, account lockout mechanism, and Spring Security RBAC.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.6 |
| Security | Spring Security 6 |
| ORM | Spring Data JPA / Hibernate |
| Templating | Thymeleaf 3 + Spring Security extras |
| Database | MariaDB 10+ |
| PDF Generation | Flying Saucer 9.4.0 + OpenPDF 2.2.2 |
| Frontend | Bootstrap 5.3.3, jQuery 3.7.1, Font Awesome 6.7.2 |
| Build | Maven 3.8+ |
| Utilities | Lombok |

---

## Prerequisites

Before running the application, make sure you have the following installed:

- **JDK 17+**
- **Maven 3.8+**
- **MariaDB 10+** (running on port `3307` by default)
- A **Gmail account** with an [app-specific password](https://support.google.com/accounts/answer/185833) configured for SMTP

---

## Database Setup

1. Start your MariaDB server.
2. Create the database and user:

```sql
CREATE DATABASE PORTAL_ESCOLAR;
CREATE USER 'portal_escolar'@'localhost' IDENTIFIED BY 'portal123';
GRANT ALL PRIVILEGES ON PORTAL_ESCOLAR.* TO 'portal_escolar'@'localhost';
FLUSH PRIVILEGES;
```

3. Run the setup SQL scripts located in `Diseño Sistemas/MariaDB/` in order (10 primary setup scripts + additional ALTER/UPDATE scripts).

> ⚠️ **Important:** Change the default database password before deploying to any environment beyond local development.

---

## Configuration

Open `src/main/resources/application.properties` and update the following:

```properties
# Database
spring.datasource.url=jdbc:mariadb://localhost:3307/PORTAL_ESCOLAR
spring.datasource.username=portal_escolar
spring.datasource.password=portal123   # Change this!

# Base URL
app.base-url=http://localhost:8080

# File uploads (local or VPS path)
app.upload.path=/path/to/uploads/

# Gmail SMTP (for password reset emails)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

---

## Running the Application

```bash
# Navigate to the project folder
cd ProyectoPortalEstudiantil

# Build
mvn clean install

# Run
mvn spring-boot:run
```

Or package and run as a JAR:

```bash
mvn clean package
java -jar target/ProyectoPortalEstudiantil-1.jar
```

The application will be available at **http://localhost:8080**.

---

## Project Structure

```
Portal_Estudiantil/
├── Historias_Usuario/                  # User story documentation (PDF & XLSX)
├── Diseño Sistemas/MariaDB/            # Database setup SQL scripts
└── ProyectoPortalEstudiantil/          # Main Spring Boot application
    ├── pom.xml
    └── src/main/
        ├── java/PortalEstudiantil/ProyectoPortalEstudiantil/
        │   ├── ProyectoPortalEstudiantilApplication.java
        │   ├── Controller/             # 28 MVC controller classes
        │   ├── Service/                # 32 service classes (business logic)
        │   ├── Repository/             # Spring Data JPA repositories
        │   ├── Domain/                 # 28 JPA entity classes
        │   ├── Security/               # Spring Security config & handlers
        │   └── Config/                 # Web & file upload configuration
        └── resources/
            ├── application.properties
            ├── static/                 # CSS, JS, images
            └── templates/              # Thymeleaf HTML templates
                ├── layout/plantilla.html
                ├── login.html
                ├── index.html
                └── [module templates]/
```

---

## User Roles

| Role | Description |
|---|---|
| `ADMINISTRADOR` | Full system access: manages users, academic setup, reports, and statistics |
| `DOCENTE` | Manages attendance, grades, evaluations, and views assigned sections |
| `ENCARGADO` | Views student academic progress, attendance, and statistics |
| `ESTUDIANTE` | Views own grades, attendance, schedule, and learning resources |

---

## Key Modules

- **GestionAcademicaController** — Central hub for academic setup (classrooms, subjects, sections, schedules, periods, enrollments)
- **AsistenciaController** — Daily roll call and attendance history
- **CalificacionesController / EvaluacionController** — Grade entry and evaluation management
- **ReporteAcademicoController** — PDF academic report generation
- **MensajeriaController** — Internal messaging with attachments
- **TicketController** — Support ticketing workflow
- **PrediccionController** — Academic performance prediction
- **EstadisticasController** — Analytics and statistics dashboards
- **FeedBackController** — 360° feedback between users
- **PasswordResetController** — Email-based secure password recovery

---

## IDE

This project was developed with **Apache NetBeans**. The `nbactions.xml` file at the project root contains preconfigured NetBeans run/build actions. You can also import and run it with any Maven-compatible IDE (IntelliJ IDEA, Eclipse, VS Code).

---

## License

This project is intended for academic and institutional use. Please review your institution's policies before deploying in a production environment.
